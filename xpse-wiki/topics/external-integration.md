# 외부 부품 API 연동 (external-integration)

## Purpose

samplepcb_xpse 프로젝트는 다양한 외부 전자부품 데이터 소스와 연동하여, 단일 검색 인터페이스를 통해 여러 공급업체의 부품 정보를 통합 검색하고 내부 Elasticsearch/JPA 저장소에 색인하는 기능을 제공한다. 연동 대상은 다음 네 가지이다:

1. **Digi-Key** -- 글로벌 전자부품 유통사. OAuth2 Client Credentials 인증 기반 REST API (v4)를 통해 부품 상세 조회, 키워드 검색, 카테고리 조회를 수행한다.
2. **UniKeyIC** -- 중국 기반 전자부품 공급사. API Key 인증 기반 REST API로 부품번호 정확매칭 검색을 수행한다.
3. **IC114** -- 국내 전자부품 유통사. 엑셀 파일 업로드를 통해 저항/콘덴서 등 수동부품 데이터를 일괄 색인한다.
4. **Google Universal Sentence Encoder (USE)** -- 자체 linserver를 경유하여 문장 임베딩 벡터를 생성한다. 부품 설명의 유사도 분석에 활용된다.

이 연동 계층의 핵심 목표는 외부 API 응답을 내부 도메인 모델(`PcbPartsSearch`)로 정규화하고, Elasticsearch 인덱스(`pcbparts`)와 JPA 엔티티(`PcbParts`)에 이중 저장하여 향후 검색 성능과 데이터 일관성을 확보하는 것이다.

---

## Architecture

### 계층 구조

```
Controller (PcbPartsResource)
    |
    +-- Service 계층
    |     +-- PcbPartsMultiSearchService     -- 멀티 소스 병렬 검색 오케스트레이터
    |     +-- PcbPartsService                -- 색인/검색 핵심 로직
    |     +-- PcbPartsIC114Service           -- IC114 엑셀 임포트
    |
    +-- Sub-Service 계층 (service/common/sub/)
    |     +-- DigikeySubService              -- Digi-Key API 호출 (WebClient)
    |     +-- DigikeyPartsParserSubService   -- Digi-Key 응답 파싱 -> PcbPartsSearch
    |     +-- UniKeyICSubService             -- UniKeyIC API 호출 (WebClient)
    |     +-- UniKeyICPartsParserSubService  -- UniKeyIC 응답 파싱 -> PcbPartsSearch
    |     +-- GoogleTensorService            -- Sentence Encoder 호출
    |     +-- ExcelSubService                -- 엑셀 셀 값 읽기 유틸
    |     +-- PcbPartsConvertSubService      -- PcbPartsSearch <-> PcbParts 엔티티 변환
    |     +-- PcbPartsSubService             -- ES/JPA 일괄 업데이트
    |     +-- DataExtractorSubService        -- partsPackageOnly.json 기반 패키지 크기 추출
    |
    +-- Utility 계층
    |     +-- DigikeyUtils                   -- Digi-Key 카테고리에서 단어 추출
    |     +-- PcbPartsUtils                  -- 검색어 파싱, 단위 변환, 기본 가격 생성
    |
    +-- 설정
          +-- ApplicationProperties          -- 외부 서비스 URL/인증정보
          +-- CacheConfig                    -- Caffeine 캐시 설정
```

### 멀티 소스 검색 모드 (3가지)

`PcbPartsMultiSearchService` 는 사용 시나리오별로 세 가지 검색 모드를 제공한다.

**1. `searchMultiSource()` — 병렬 통합 검색 (`Mono.zip`)**

```
[자체(samplepcb/eleparts)]              [Digi-Key]                    [UniKeyIC]
  1. PART_NAME.keyword 완전일치           0. ES 캐시 확인 (TTL 내)        0. ES 캐시 확인 (TTL 내)
     -> searchType="exact"                  → 히트 시 API 생략               → 히트 시 API 생략
  2. 파싱 ES 키워드 검색                  1. getProductDetails           1. searchByPartNumber
     -> searchType="keyword"                 완전일치                       정확매칭
  3. part name 일반 텍스트 검색              -> searchType="exact"          -> searchType="exact"
     -> searchType="keyword"            2. searchByKeyword 폴백
                                            -> searchType="keyword"

                    === Mono.zip 병렬 완료 후 합산 응답 ===
```

**2. `searchMultiSourceFirstHit()` — 순차 검색 + 조기 종료**

자체 → Digi-Key → UniKeyIC 순서로 순차 검색하며, 결과가 존재하는 첫 번째 소스에서 즉시 종료. 각 단계에 `onErrorResume` 으로 빈 결과 폴백을 적용해 한 소스 장애가 다음 소스 시도를 막지 않도록 격리한다. 응답 구조는 `searchMultiSource` 와 동일하나 히트한 소스 필드만 채워지고 나머지는 `null` 이다.

**3. `searchExternalBatch()` — Digi-Key + UniKeyIC 병렬 일괄**

여러 `partName` 을 한 번에 외부 공급사에서 조회한다. 두 트랙(Digi-Key / UniKeyIC) 은 `Mono.zip` 으로 병렬, 각 트랙 내부는 `Flux.concatMap` 으로 입력 순서를 보존하며 순차 호출한다. Digi-Key 는 `searchDigikeyExactOnly()` 가 사용되어 ES 캐시 → `getProductDetails` 정확매칭만 수행하고 `searchByKeyword` 폴백이 없다 (`partName` 이 정확한 부품번호라는 가정, 키워드 노이즈/추가 API 호출 회피). `partName` 단위 `onErrorResume` 으로 1개 실패가 트랙 전체를 중단시키지 않는다.

### ES 캐시 우선 전략

Digi-Key와 UniKeyIC 채널에서 외부 API 호출 전에 **ES 캐시 확인**을 수행한다. `PcbPartsMultiSearchService.findFreshCachedResults()`가 `pcbparts` 인덱스에서 `serviceType + partName.keyword` 조건으로 기존 색인 데이터를 조회하고, `lastModifiedDate`가 TTL(`application.external-cache.ttl-hours`, 기본 24시간) 이내이면 캐시 히트로 판정하여 외부 API 호출을 완전히 생략한다.

이 전략은 두 레벨 캐싱의 일부이다:
- **Caffeine 인메모리 캐시** (`searchResults`, 500건/30분): `DigikeySubService.searchByKeyword()`의 `@Cacheable`로 동일 키워드 반복 호출을 인메모리에서 처리한다.
- **ES 색인 캐시** (TTL 24시간): Caffeine 캐시 만료 후에도 ES에 색인된 데이터가 TTL 이내이면 API 호출을 생략한다.

### 데이터 저장 전략 (이중 저장)

외부 API 결과를 색인할 때, 항상 두 저장소에 동시 저장한다:
- **Elasticsearch** (`pcbparts` 인덱스) -- `PcbPartsSearchRepository.saveAll()` / `save()`
- **JPA (RDBMS)** -- `PcbPartsRepository.save()` / `saveAll()`

변환은 `PcbPartsConvertSubService.toEntity()`가 담당하며, `DocIdGenerator.generate()`로 양쪽에서 공유하는 `docId`를 생성한다.

---

## Talks To

### 1. Digi-Key API v4

| 항목 | 상세 |
|------|------|
| **서비스 클래스** | `DigikeySubService` |
| **기본 URL** | `application.digikey.baseUrl` (설정 파일) |
| **인증 방식** | OAuth2 Client Credentials (`/v1/oauth2/token`). `clientId`와 `clientSecret`를 Base64 인코딩하여 Basic 헤더로 토큰 발급. 토큰은 파일 시스템(`${app.storage.path}/digikey/token.json`)에 영구 저장하고, 만료 시간을 확인하여 자동 갱신한다. |
| **헤더** | `X-DIGIKEY-Client-Id`, `X-DIGIKEY-Locale-Currency: KRW`, `X-DIGIKEY-Locale-Site: KR`, `Authorization: Bearer <token>` |
| **호출 엔드포인트** | (1) `GET /products/v4/search/{partNumber}/productdetails` -- 부품번호 정확매칭 상세 조회 |
|                    | (2) `POST /products/v4/search/keyword` -- 키워드 검색 (body: `Keywords`, `Limit`, `Offset`, `FilterOptionsRequest.CategoryFilter`) |
|                    | (3) `GET /products/v4/search/categories` -- 카테고리 목록 조회 |
| **응답 파싱** | `DigikeyPartsParserSubService` -- `parseProduct()` (단건), `parseAllProducts()` (복수건), `parseProductsFirst()` (첫 번째 건) |
| **캐시** | Caffeine 비동기 캐시. `searchResults` (500건, 30분), `productDetails` (1000건, 1시간) |
| **카테고리 필터 매핑** | `referencePrefix` "R" -> CategoryId 2 (저항), "C" -> CategoryId 3 (커패시터), "I" -> CategoryId 4 (인덕터) |

#### Digi-Key 응답 필드 매핑 (`DigikeyPartsParserSubService`)

**기본 정보:**

| Digi-Key 응답 키 | 내부 필드 (`PcbPartsSearch`) |
|---|---|
| `ManufacturerProductNumber` | `partName` |
| `Description.ProductDescription` | `description` |
| `Manufacturer.Name` | `manufacturerName` |
| `PhotoUrl` | `photoUrl` |
| `DatasheetUrl` | `datasheetUrl` |
| `Category.Name` | `largeCategory` |
| `Category.ChildCategories[0].Name` | `mediumCategory` |
| `Category.ChildCategories[0].ChildCategories[0].Name` | `smallCategory` |

**파라미터 매핑 (ParameterId -> 필드):**

| ParameterId | 내부 필드 | 설명 |
|---|---|---|
| `2` | `watt` | 전력 |
| `3` | `tolerance` | 허용오차 |
| `2085` | `ohm` | 저항값 |
| `2049` | `condenser` | 정전용량 |
| `14` | `voltage` | 전압 |
| `714` | `current` | 전류 |
| `2087` | `inductor` | 인덕턴스 |
| `16` | `packaging` | 패키징 (공백 유지) |
| `46` | `size` | 크기 (공백 유지) |
| `252` | `temperature` | 온도 (공백 유지) |

`PRESERVE_WHITESPACE_PARAMS` 집합(16, 46, 252)에 속하는 파라미터는 공백을 유지하고, 그 외 파라미터는 `replaceAll("\\s+", "")`로 공백을 제거한 뒤 `PcbPartsUtils.parsingToPcbUnitSearch()`로 단위를 파싱한다.

**가격 정보:**

`ProductVariations` 배열의 각 항목에서:
- `DigiKeyProductNumber` -> `PcbPartsPriceSearch.sku`
- `PackageType.Name` -> `PcbPartsPriceSearch.pkg`
- `MinimumOrderQuantity` -> `PcbPartsPriceSearch.moq`
- `QuantityAvailableforPackageType` -> `PcbPartsPriceSearch.stock`
- `StandardPricing[].BreakQuantity` -> `PcbPartsPriceStepSearch.breakQuantity`
- `StandardPricing[].UnitPrice` -> `PcbPartsPriceStepSearch.unitPrice`

**serviceType**: 항상 `PcbPkgType.DIGIKEY` (`"digikey"`)로 설정된다.

---

### 2. UniKeyIC API

| 항목 | 상세 |
|------|------|
| **서비스 클래스** | `UniKeyICSubService` |
| **기본 URL** | `application.unikeyic.baseUrl` (설정 파일) |
| **인증 방식** | `Authorization` 헤더에 `application.unikeyic.apiKey` 값을 직접 설정 |
| **호출 엔드포인트** | `POST /search-v1/products/get-single-goods-usd` (body: `{"pro_sno": "<partNumber>"}`) |
| **응답 파싱** | `UniKeyICPartsParserSubService.parseProducts()` |
| **환율 처리** | `application.unikeyic.exchangeRate` (기본값 1350). USD 가격에 환율을 곱하여 KRW 정수 단가로 변환한다. |
| **성공 판단** | `err_code`가 `"Com:Success"`인 경우에만 파싱 진행 |

#### UniKeyIC 응답 필드 매핑 (`UniKeyICPartsParserSubService`)

| UniKeyIC 응답 키 | 내부 필드 (`PcbPartsSearch`) |
|---|---|
| `data.products[].pro_sno` | `partName` |
| `data.products[].std_mfr_name` | `manufacturerName` |
| `data.products[].short_desc` | `description` |
| `data.products[].dc` | `dateCode` |
| `data.products[].img_url` | `photoUrl` |
| `data.products[].datasheet_url` | `datasheetUrl` |
| `data.products[].cate_name` | `largeCategory` |
| `data.products[].package` | `partsPackaging` |
| `data.products[].moq` | `moq` |
| `data.products[].sku` | `PcbPartsPriceSearch.sku` |
| `data.products[].stock` | `PcbPartsPriceSearch.stock` |
| `data.products[].calc_sale_usd_price[]` | `PcbPartsPriceStepSearch.unitPrice` (USD * exchangeRate) |
| `data.products[].nums[]` | `PcbPartsPriceStepSearch.breakQuantity` |

가격 배열 `calc_sale_usd_price`와 수량 배열 `nums`를 인덱스 매칭으로 가격 단계를 생성한다. `calc_sale_usd_price` 항목이 `Number`이면 직접 환율을 곱하고, `Map`이면 첫 번째 `Number` 타입 값을 찾아 사용한다.

**serviceType**: 항상 `PcbPkgType.UNIKEYIC` (`"unikeyic"`)로 설정된다. 유통사명(`distributor`)은 `"UniKeyIC"`로 하드코딩된다.

---

### 3. IC114 (엑셀 파일 임포트)

| 항목 | 상세 |
|------|------|
| **서비스 클래스** | `PcbPartsIC114Service` |
| **입력 방식** | 엑셀 파일 (`.xlsx`) 업로드. 최대 1GB(`MAX_FILE_SIZE_BYTES`) |
| **청크 처리** | `EXCEL_CHUNK_SIZE = 3000` 행 단위로 분할 처리 |
| **카테고리 추출** | 파일명에서 6자리 숫자 코드 추출 -> `RESISTANCE_CATEGORIES` / `CAPACITOR_CATEGORIES` 맵으로 카테고리명 결정 |

#### IC114 엑셀 컬럼 매핑

| 컬럼 인덱스 | 엑셀 필드 | 내부 필드 (`PcbPartsSearch`) |
|---|---|---|
| 0 | 카테고리 코드 | `mediumCategory` (저항/콘덴서 자동 판별), `smallCategory` |
| 1 | 부품명 | `partName` |
| 2 | 약칭 | `productName` |
| 3 | 가격 | `prices` (via `PcbPartsUtils.createDefaultPrices()`) |
| 4 | 최소수량 | `moq` |
| 5 | 단위 | `partsPackaging` |
| 6 | 제조사 | `manufacturerName` |
| 7 | 설명 | `description` |
| 8 | 재고 | (읽기는 하나 현재 미사용) |
| 9 | 와트 | `watt` (단위 파싱) |
| 10 | 허용오차 | `tolerance` (단위 파싱) |
| 11 | 저항값 | `ohm` (단위 파싱) |
| 12 | 크기 | `size` |
| 13 | 정전용량 | `condenser` (단위 파싱) |
| 14 | 전압 | `voltage` (단위 파싱) |
| 15 | 온도 | `temperature` |

**카테고리 코드 예시:**
- `030101` -> "1/4W J 5% 저항" (저항, `mediumCategory = "저항"`)
- `030303` -> "전해 콘덴서 85C" (콘덴서, `mediumCategory = "콘덴서"`)

모든 IC114 데이터의 `serviceType`은 `PcbPkgType.SAMPLEPCB` (`"samplepcb"`), `largeCategory`는 `"수동부품"`으로 고정된다.

---

### 4. Google Universal Sentence Encoder (linserver)

| 항목 | 상세 |
|------|------|
| **서비스 클래스** | `GoogleTensorService` |
| **기본 URL** | `application.spLinserver.serverUrl` (설정 파일) |
| **인증** | 없음 (내부 서버) |
| **엔드포인트** | `GET /encode?sentence=<text>` (단일 문장) / `POST /encode` (body: `{"sentences": [...]}`, 다중 문장) |
| **응답 형식** | 단일: `CCObjectResult<List<Double>>` (벡터), 다중: `CCObjectResult<List<List<Double>>>` (벡터 배열) |

---

### 5. ML Server (별도 Digi-Key 프록시)

| 항목 | 상세 |
|------|------|
| **호출 위치** | `PcbPartsService.searchDigikeyProductDetails()` |
| **기본 URL** | `application.mlServer.serverUrl` |
| **엔드포인트** | `GET /api/digikeyProductDetails?partNumber=<partNumber>` |
| **용도** | ML 서버를 경유하여 Digi-Key 제품 상세 정보를 조회하는 별도 경로 |

---

## API Surface

모든 엔드포인트는 `/api/pcbParts` 경로 하위에 위치한다 (`PcbPartsResource`).

### 외부 연동 관련 엔드포인트

| HTTP 메서드 | 경로 | 설명 | 관련 외부 소스 |
|---|---|---|---|
| `GET` | `/_indexingByDigikey?partNumber=` | Digi-Key 부품번호로 단건 인덱싱. NonDigikeyParts 블랙리스트 확인 후 호출 | Digi-Key |
| `POST` | `/_indexingByDigikey` (body: `List<String>`) | Digi-Key 부품번호 복수건 일괄 인덱싱 (병렬도 5) | Digi-Key |
| `GET` | `/_searchCandidateByDigikey?partNumber=&referencePrefix=` | Digi-Key 키워드 검색 후 첫 번째 결과 반환 (후보 검색) | Digi-Key |
| `GET` | `/_searchByUniKeyIC?partNumber=` | UniKeyIC 부품번호 검색 및 색인 | UniKeyIC |
| `GET` | `/_searchMultiSource?searchWord=&referencePrefix=` | 자체 ES + Digi-Key + UniKeyIC 3소스 병렬 통합 검색 | 전체 |
| `GET` | `/_searchMultiSourceFirstHit?searchWord=&referencePrefix=` | 자체 → Digi-Key → UniKeyIC 순차 검색, 결과 존재 시 조기 종료 (소스별 에러 격리) | 전체 |
| `POST` | `/_searchExternalBatch` (body: `List<String> partNames`) | Digi-Key + UniKeyIC 병렬 일괄 검색. ES 캐시 우선, Digi-Key 는 정확매칭 전용 (키워드 폴백 없음) | Digi-Key, UniKeyIC |
| `POST` | `/_uploadItemFileByIC114` (multipart: `file`) | IC114 엑셀 단일 파일 업로드 인덱싱 | IC114 |
| `POST` | `/_uploadItemFilesByIC114` (multipart: `files`) | IC114 엑셀 다중 파일 업로드 인덱싱 | IC114 |
| `GET` | `/_searchExactMatch?partName=&manufacturerName=` | 부품명+제조사 정확매칭 (내부 ES + Digi-Key 폴백) | Digi-Key |

### 응답 구조

**멀티 소스 검색 응답 (`PcbPartsMultiSearchResult`):**

```json
{
  "samplepcb": {
    "searchType": "exact|keyword",
    "items": [...]
  },
  "digikey": {
    "searchType": "exact|keyword",
    "items": [...]
  },
  "unikeyic": {
    "searchType": "exact|keyword",
    "items": [...]
  }
}
```

`searchType` 필드는 결과가 정확매칭(`"exact"`)인지 키워드 검색(`"keyword"`)인지를 나타낸다.

---

## Data

### Elasticsearch 인덱스

| 인덱스명 | 도메인 클래스 | 용도 |
|---|---|---|
| `pcbparts` (`ElasticIndexName.PCB_PARTS`) | `PcbPartsSearch` | 모든 소스의 통합 부품 데이터 |
| `nondigikeyparts` (`ElasticIndexName.NON_DIGIKEY_PARTS`) | `NonDigikeyPartsSearch` | Digi-Key에서 찾을 수 없는 부품번호 블랙리스트 |

### `nondigikeyparts` 인덱스 상세

Digi-Key API 호출 시 응답이 실패하면 해당 부품번호를 `NonDigikeyPartsSearch`로 저장하여, 이후 동일 부품번호에 대한 불필요한 API 호출을 방지한다.

**매핑 (`nondigikeyparts.txt`):**
- `partName`: ngram(6) 분석기 + keyword + normalize 서브필드
- `manufacturerName`: ngram(4) 분석기 + keyword + normalize 서브필드
- normalizer: `keyword_normalizer` (lowercase 필터)
- 분석기 구성: `ngram_tokenizer6` (min_gram=6, max_gram=6), `ngram_tokenizer4` (min_gram=4, max_gram=4)

조회는 `NonDigikeyPartsSearchRepository.findByPartNameKeyword()`로 `partName.keyword` 완전일치 검색한다.

### serviceType 구분 (`PcbPkgType`)

| 열거값 | 문자열 | 데이터 소스 |
|---|---|---|
| `DIGIKEY` | `"digikey"` | Digi-Key API |
| `SAMPLEPCB` | `"samplepcb"` | IC114 엑셀 / 자체 데이터 |
| `ELEPARTS` | `"eleparts"` | Eleparts 엑셀 업로드 |
| `UNIKEYIC` | `"unikeyic"` | UniKeyIC API |
| `UNKNOWN` | `"unknown"` | 미분류 |

### 정적 데이터 리소스

| 파일 | 용도 | 사용처 |
|---|---|---|
| `partsPackageOnly.json` | 359개 패키지 크기 코드 목록 (0402, 0603, SOT-23, QFP, DIP 등). 숫자형/문자형으로 분류하여 부품 제목에서 패키지 크기를 추출 | `DataExtractorSubService` |
| `manufacturers.json` | Digi-Key 제조사 목록 (`Id`/`Name` 쌍). 제조사 ID 매핑에 활용 | 참조 데이터 |
| `mapping/nondigikeyparts.txt` | `nondigikeyparts` Elasticsearch 인덱스 매핑 정의 (PUT 요청 본문) | 인덱스 초기 설정 |

### 엔티티 변환 (`PcbPartsConvertSubService`)

`toEntity()` 메서드가 `PcbPartsSearch` (ES 문서) -> `PcbParts` (JPA 엔티티) 변환을 수행한다. 주요 처리 사항:
- `docId` 자동 생성: `DocIdGenerator.generate()` (ES 문서 ID와 JPA 엔티티를 연결하는 키)
- `PcbUnitSearch` 타입 필드 (`watt`, `tolerance`, `ohm`, `condenser`, `voltage`, `current`, `inductor`)는 JSON 문자열로 직렬화하여 JPA에 저장
- `prices` -> `PcbPartsPrice` + `PcbPartsPriceStep` 자식 엔티티 변환
- `images` -> `PcbPartsImage` 엔티티 변환
- `specs` -> `PcbPartsSpec` 엔티티 변환

---

## Key Decisions

### 1. 병렬 리액티브 검색 아키텍처

`PcbPartsMultiSearchService`는 `Mono.zip()`으로 세 소스를 동시에 호출하되, 자체 ES 검색은 블로킹 호출이므로 `Mono.fromCallable()`로 감싸서 리액티브 파이프라인에 편입시킨다. Digi-Key와 UniKeyIC는 `WebClient` 기반 논블로킹 호출이다.

배치/순차 모드는 별도 메서드로 분리되어 있다: `searchMultiSourceFirstHit()` 은 첫 히트에서 종료하여 외부 API 호출 비용을 절감하고, `searchExternalBatch()` 는 부품 리스트 단위 외부 시세 조회 (sp-estimate 의 `syncExternalSelectedPrices` 등)를 위해 `Flux.concatMap` 으로 입력 순서를 보존하면서 두 공급사 트랙을 병렬 실행한다. 배치 모드는 Digi-Key 키워드 폴백을 의도적으로 제거(`searchDigikeyExactOnly`) 하여 부품번호 가정 하의 노이즈를 차단한다.

### 2. NonDigikeyParts 블랙리스트 전략

Digi-Key 인덱싱 시 API 응답이 실패하면 해당 부품번호를 `nondigikeyparts` 인덱스에 저장하여, 동일 부품번호에 대한 반복적인 API 호출을 차단한다. 이후 `_indexingByDigikey` 호출 시 `searchNonDigikeyParts()`로 블랙리스트를 먼저 확인한다.

### 3. 이중 저장 (ES + JPA)

모든 외부 데이터는 Elasticsearch와 RDBMS(JPA) 양쪽에 저장된다. ES는 전문 검색/필터링용, JPA는 관계형 데이터 및 견적 시스템 연계용이다.

### 4. 벌크 인덱싱 최적화

`PcbPartsService.bulkIndexParts()`는 `serviceType + partName` 기준으로 ES 벌크 조회를 먼저 수행하고, 기존 데이터가 있으면 업데이트, 없으면 신규 생성한다. ES 저장은 `saveAll()`로 일괄 처리하여 N+1 문제를 방지한다.

### 5. 검색어 파싱 기반 ES 쿼리 구축

`PcbPartsUtils.parseString()`이 입력 검색어를 정규식으로 분석하여 전기적 특성(와트, 옴, 볼트, 패럿 등)별로 분류한다. `referencePrefix` ("R", "C")에 따라 패턴을 다르게 적용한다:
- `"R"` (저항): R 표기법 (2R2, 1K2 등) 인식 및 변환
- `"C"` (커패시터): 축약 단위 (22p, 33n 등) -> 전체 단위 (22pF, 33nF)로 확장

### 6. OAuth2 토큰 파일 영구화

`DigikeySubService`는 Digi-Key OAuth2 토큰을 파일 시스템(`${app.storage.path}/digikey/token.json`)에 직렬화하여, 애플리케이션 재시작 후에도 유효한 토큰을 재사용한다. WebClient 필터에서 API 경로가 `/oauth2/token`이 아닌 경우 자동으로 유효한 토큰을 가져와 `Authorization: Bearer` 헤더에 주입한다.

### 7. 이중 캐시 전략 (Caffeine + ES TTL)

외부 API 호출 최적화를 위해 두 레벨의 캐싱이 적용된다:

**Caffeine 인메모리 캐시:**
- `searchResults`: 최대 500건, 30분 만료 — `DigikeySubService.searchByKeyword()`에 `@Cacheable` 적용
- 캐시 키: `#keyword + '_' + #limit + '_' + #offset + '_' + #parsedKeywordsStr`

**ES 색인 기반 캐시 (TTL 24시간):**
- `PcbPartsMultiSearchService.findFreshCachedResults()`가 `pcbparts` 인덱스에서 `serviceType + partName.keyword` 조건으로 기존 색인 데이터를 조회
- `lastModifiedDate`가 `application.external-cache.ttl-hours`(기본 24시간) 이내이면 캐시 히트
- Caffeine 캐시 만료 후에도 ES에 색인된 데이터가 TTL 이내이면 외부 API 호출을 생략하여 비용과 지연을 절감

### 8. USD -> KRW 환율 변환

UniKeyIC의 USD 단가를 KRW로 변환할 때 `application.unikeyic.exchangeRate` (기본값 1350)를 곱하여 `int` 정수로 반올림한다. 환율 변경 시 설정 파일만 수정하면 된다.

---

## Gotchas

### 1. IC114 엑셀 파싱에서 카테고리 판별 순서

`PcbPartsIC114Service.excelIndexingByIC114()` 에서 카테고리 코드를 먼저 `RESISTANCE_CATEGORIES`에서 찾고, 없으면 `CAPACITOR_CATEGORIES`에서 찾는다. 따라서 두 맵에 동일 코드가 존재할 경우 항상 저항으로 분류된다. 또한 어떤 카테고리에도 매핑되지 않는 코드의 경우 `mediumCategory`가 `"콘덴서"`로 남아 있게 되므로 주의가 필요하다 (초기값으로 `"저항"`을 먼저 설정하고, 저항 카테고리에서 못 찾으면 `"콘덴서"`로 덮어쓰는 로직).

### 2. NonDigikeyParts 블랙리스트는 만료가 없음

한 번 `nondigikeyparts` 인덱스에 등록된 부품번호는 수동으로 삭제하지 않는 한 영구적으로 Digi-Key 검색에서 제외된다. Digi-Key 카탈로그에 나중에 추가된 부품도 차단될 수 있다.

### 3. Digi-Key WebClient 필터의 토큰 자동 주입

`DigikeySubService.init()`에서 `WebClient.Builder`에 등록하는 `ExchangeFilterFunction`은 요청 경로에 `/oauth2/token`이 포함되지 않은 모든 요청에 Bearer 토큰을 주입한다. 따라서 동일 `WebClient.Builder` 빈을 다른 서비스에서 공유하면 의도치 않게 Digi-Key 토큰이 주입될 수 있다.

### 4. UniKeyIC 가격 맵 파싱의 불안정성

`UniKeyICPartsParserSubService.parsePriceSteps()`에서 `calc_sale_usd_price` 항목이 `Map` 타입인 경우 `findPriceValue()`가 맵의 첫 번째 `Number` 값을 반환한다. 맵 순회 순서가 보장되지 않으므로 예상과 다른 값이 선택될 가능성이 있다.

### 5. 멀티 소스 검색의 에러 핸들링

`searchMultiSource()`에서 개별 소스 검색이 실패하면 `onErrorReturn`으로 빈 결과를 반환한다. 즉, 한 소스가 장애를 일으켜도 전체 응답은 성공으로 반환되며, 클라이언트는 실패한 소스의 `items`가 빈 리스트인지 실제로 결과가 없는 것인지 구분할 수 없다.

`searchMultiSourceFirstHit()` 와 `searchExternalBatch()` 도 동일 정책을 따르되, 후자는 `partName` 단위 `onErrorResume` 으로 더 잘게 격리되어 있다 — 한 부품의 외부 API 호출 실패가 같은 트랙의 다른 부품 조회를 막지 않는다.

### 6. Digi-Key 일괄 인덱싱 병렬도 제한

`POST /_indexingByDigikey`에서 `Flux.fromIterable(...).flatMap(..., 5)`로 병렬도가 5로 제한되어 있다. Digi-Key API의 Rate Limit을 고려한 설정이므로, 변경 시 API 제한에 주의해야 한다.

### 7. IC114 엑셀 청크 처리 시 메모리

`IOUtils.setByteArrayMaxOverride(1GB)`로 Apache POI의 바이트 배열 제한을 1GB까지 올린다. 대용량 엑셀 파일 처리 시 힙 메모리 부족이 발생할 수 있으므로 JVM 힙 설정에 주의가 필요하다.

### 8. parsingToPcbUnitSearch 단위 변환 정밀도

`PcbPartsUtils`의 단위 변환 로직은 정규식 기반이므로, 비표준 표기법(예: 공백이 포함된 단위, 여러 값이 슬래시로 구분된 경우)에서 파싱이 실패할 수 있다. 특히 `referencePrefix`가 `null`이면 축약 단위 인식이 비활성화된다.

---

## Sources

| 파일 경로 | 역할 |
|---|---|
| `src/main/java/kr/co/samplepcb/xpse/resource/PcbPartsResource.java` | REST 컨트롤러. 외부 연동 API 엔드포인트 정의 |
| `src/main/java/kr/co/samplepcb/xpse/service/PcbPartsMultiSearchService.java` | 멀티 소스 병렬 검색 오케스트레이터 |
| `src/main/java/kr/co/samplepcb/xpse/service/PcbPartsService.java` | 핵심 색인/검색 로직. 벌크 인덱싱, NonDigikeyParts 관리 |
| `src/main/java/kr/co/samplepcb/xpse/service/PcbPartsIC114Service.java` | IC114 엑셀 파일 임포트 서비스 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/DigikeySubService.java` | Digi-Key API 호출 (OAuth2, WebClient) |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/DigikeyPartsParserSubService.java` | Digi-Key 응답 파싱 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/UniKeyICSubService.java` | UniKeyIC API 호출 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/UniKeyICPartsParserSubService.java` | UniKeyIC 응답 파싱 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/GoogleTensorService.java` | Google USE 문장 인코딩 호출 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/DataExtractorSubService.java` | 패키지 크기 추출 (partsPackageOnly.json) |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/ExcelSubService.java` | 엑셀 셀 값 읽기 유틸 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/PcbPartsConvertSubService.java` | ES 문서 <-> JPA 엔티티 변환 |
| `src/main/java/kr/co/samplepcb/xpse/service/common/sub/PcbPartsSubService.java` | ES/JPA 일괄 업데이트 |
| `src/main/java/kr/co/samplepcb/xpse/util/DigikeyUtils.java` | Digi-Key 카테고리 단어 추출 유틸 |
| `src/main/java/kr/co/samplepcb/xpse/util/PcbPartsUtils.java` | 검색어 파싱, 단위 변환, 기본 가격 생성 |
| `src/main/java/kr/co/samplepcb/xpse/domain/document/NonDigikeyPartsSearch.java` | NonDigikeyParts ES 문서 도메인 |
| `src/main/java/kr/co/samplepcb/xpse/repository/NonDigikeyPartsSearchRepository.java` | NonDigikeyParts ES 리포지토리 |
| `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPkgType.java` | serviceType 열거형 |
| `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartsMultiSearchResult.java` | 멀티 소스 검색 응답 POJO |
| `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartsExternalBatchResult.java` | 외부 공급사 일괄 검색 응답 POJO (digikey/unikeyic 두 트랙) |
| `src/main/java/kr/co/samplepcb/xpse/config/ApplicationProperties.java` | 외부 서비스 URL/인증정보 설정 |
| `src/main/java/kr/co/samplepcb/xpse/config/CacheConfig.java` | Caffeine 캐시 설정 |
| `src/main/resources/mapping/nondigikeyparts.txt` | nondigikeyparts ES 인덱스 매핑 정의 |
| `src/main/resources/partsPackageOnly.json` | 패키지 크기 코드 목록 (359항목) |
| `src/main/resources/manufacturers.json` | Digi-Key 제조사 목록 |
