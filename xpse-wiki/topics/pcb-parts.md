# PCB 부품 (pcb-parts)

## Purpose

PCB 부품 모듈은 다양한 소스(자체 데이터, Eleparts, IC114, Digikey, UniKeyIC)에서 전자부품 정보를 수집하고, Elasticsearch 색인과 JPA(RDB) 양쪽에 이중 저장하여 통합 검색 기능을 제공하는 핵심 도메인이다. 부품의 전기적 사양(저항, 커패시터, 인덕터, 전압, 전류, 와트, 오차범위 등)을 정규화된 형태로 파싱하여 색인하고, 사양 기반의 지능형 검색과 외부 API 연동 검색을 지원한다.

주요 의존 컴포넌트:
- **Elasticsearch**: 부품 검색 인덱스(`pcbparts`, `nondigikeyparts`)를 통한 전문 검색
- **JPA/RDB**: `sp_pcb_parts` 계열 테이블을 통한 영속 데이터 관리
- **Digikey API**: 외부 부품 정보 조회 및 색인 (`DigikeySubService`, `DigikeyPartsParserSubService`)
- **UniKeyIC API**: 외부 부품 정보 조회 및 색인 (`UniKeyICSubService`, `UniKeyICPartsParserSubService`)
- **Apache POI**: 엑셀 파일 기반 대량 부품 데이터 업로드 처리

---

## Architecture

### 계층 구조

```
PcbPartsResource (REST Controller)
  |
  +-- PcbPartsService (핵심 서비스)
  |     +-- PcbPartsConvertSubService (ES <-> JPA 변환)
  |     +-- DigikeySubService / DigikeyPartsParserSubService
  |     +-- UniKeyICPartsParserSubService
  |     +-- ExcelSubService
  |     +-- DataExtractorSubService
  |
  +-- PcbPartsMultiSearchService (다중 소스 병렬 검색)
  |     +-- DigikeySubService / DigikeyPartsParserSubService
  |     +-- UniKeyICSubService / UniKeyICPartsParserSubService
  |     +-- PcbPartsService (색인 위임)
  |
  +-- PcbPartsIC114Service (IC114 엑셀 업로드 전용)
  |     +-- ExcelSubService
  |     +-- PcbPartsConvertSubService
  |
  +-- PcbPartsSubService (카테고리 일괄 변경)
```

### 주요 파일

| 파일 | 역할 |
|------|------|
| `PcbPartsResource.java` | REST API 진입점. `/api/pcbParts` 경로의 모든 엔드포인트 정의 |
| `PcbPartsService.java` | 핵심 비즈니스 로직. 검색, 색인, Eleparts 업로드, Digikey/UniKeyIC 연동 |
| `PcbPartsMultiSearchService.java` | 자체(samplepcb) + Digikey + UniKeyIC 3개 소스 검색. `searchMultiSource` (병렬), `searchMultiSourceFirstHit` (순차/조기종료), `searchExternalBatch` (Digikey+UniKeyIC 병렬 일괄) 세 모드 제공 |
| `PcbPartsIC114Service.java` | IC114 형식 엑셀 파일 파싱 및 색인 (저항/콘덴서 카테고리 자동 분류) |
| `PcbPartsSubService.java` | ES + DB 동기화 카테고리 일괄 변경 (`updateKindAllByGroup`) |
| `PcbPartsConvertSubService.java` | `PcbPartsSearch`(ES) <-> `PcbParts`(JPA) 간 양방향 변환 |
| `PcbPartsUtils.java` | 부품 사양 문자열 파싱 유틸리티 (저항값, 커패시터, 전압 등 정규식 기반 추출) |
| `PcbPartsSearchField.java` | ES 필드명 상수 정의 및 keyword 필드 매핑 |
| `PcbPartsSearchVM.java` | 검색 조건 View Model (API 요청 파라미터 바인딩) |

### 진입점

- REST Controller: `PcbPartsResource` (`/api/pcbParts`)
- 내부 서비스 호출: `PcbPartsService.search()`, `PcbPartsService.indexingByDigikey()`, `PcbPartsMultiSearchService.searchMultiSource()`

---

## Talks To

### 내부 의존성

| 대상 서비스 | 용도 | 호출 패턴 |
|-------------|------|-----------|
| `DigikeySubService` | Digikey API 호출 (`getProductDetails`, `searchByKeyword`) | 비동기 WebClient (`Mono`) |
| `DigikeyPartsParserSubService` | Digikey 응답 JSON을 `PcbPartsSearch`로 파싱 | 동기 호출 |
| `UniKeyICSubService` | UniKeyIC API 호출 (`searchByPartNumber`) | 비동기 WebClient (`Mono`) |
| `UniKeyICPartsParserSubService` | UniKeyIC 응답을 `PcbPartsSearch` 리스트로 파싱 | 동기 호출 |
| `ExcelSubService` | 엑셀 셀 값 읽기 유틸리티 | 동기 호출 |
| `DataExtractorSubService` | 검색어에서 사이즈 값 추출 (`extractSizeFromTitle`) | 동기 호출 |
| `PcbPartsConvertSubService` | ES 문서 <-> JPA 엔티티 변환 | 동기 호출 |
| `PcbKindSearchRepository` | 부품 카테고리(kind) 존재 여부 검증 | Spring Data ES |

### 외부 API 통신

| 외부 시스템 | 프로토콜 | 용도 |
|-------------|----------|------|
| **Digikey API** | HTTP (WebClient, 비동기) | 부품번호로 상세정보 조회, 키워드 검색 |
| **UniKeyIC API** | HTTP (WebClient, 비동기) | 부품번호로 검색 |

### 저장소 통신

| 저장소 | Repository | 용도 |
|--------|------------|------|
| Elasticsearch | `PcbPartsSearchRepository` | 부품 검색 문서 CRUD |
| Elasticsearch | `NonDigikeyPartsSearchRepository` | Digikey 비해당 부품 기록 |
| Elasticsearch | `PcbKindSearchRepository` | 카테고리 정보 검증 |
| JPA/RDB | `PcbPartsRepository` | 부품 엔티티 영속화 |

### 통신 패턴 (다중 소스 검색)

`PcbPartsMultiSearchService.searchMultiSource()`는 `Mono.zip()`으로 3개 소스를 병렬 실행한다:

```
[자체(samplepcb) ES]           [Digikey]                        [UniKeyIC]
  1. keyword 완전일치            0. ES 캐시 확인 (TTL 내 데이터)    0. ES 캐시 확인 (TTL 내 데이터)
  2. 파싱 ES 검색                  → 히트 시 API 호출 생략            → 히트 시 API 호출 생략
  3. partName 텍스트 검색        1. getProductDetails             1. searchByPartNumber
                                 2. searchByKeyword 폴백            (정확매칭)
         |                            |                          |
         +------- Mono.zip으로 병렬 합류 후 통합 응답 -------+
```

---

## API Surface

기본 경로: `@RequestMapping("/api/pcbParts")`

### 검색 API

| 메서드 | 경로 | 설명 | 주요 파라미터 |
|--------|------|------|--------------|
| `GET` | `/_search` | 다양한 조건으로 PCB 부품 검색. 파싱 검색(`qf=parsing`) 지원 | `Pageable`, `QueryParam(q, qf)`, `PcbPartsSearchVM`, `referencePrefix` |
| `GET` | `/_searchExactMatch` | 부품명 + 제조사명 정확 매칭 검색. ES에 없으면 Digikey 조회 후 색인 | `partName`, `manufacturerName` |
| `GET` | `/_searchById` | 부품 ID로 검색 | `id` |
| `GET` | `/_searchCandidateByDigikey` | Digikey 후보 부품 검색 | `partNumber`, `referencePrefix` |
| `GET` | `/_searchMultiSource` | 3개 소스(자체/Digikey/UniKeyIC) 병렬 통합 검색 | `searchWord`, `referencePrefix` |

### 색인 API

| 메서드 | 경로 | 설명 | 주요 파라미터 |
|--------|------|------|--------------|
| `POST` | `/_savePart` | 단일 `PcbPartsSearch` 저장. `(serviceType, partName)` 기준 upsert | `PcbPartsSearch` (JSON Body) |
| `POST` | `/_saveParts` | 다중 `PcbPartsSearch` 일괄 저장. `serviceType` 별 그룹 벌크 처리 | `List<PcbPartsSearch>` (JSON Body) |
| `POST` | `/_uploadItemFileByEleparts` | Eleparts 형식 엑셀 파일 업로드 및 색인 | `file` (MultipartFile) |
| `POST` | `/_uploadItemFileByIC114` | IC114 형식 엑셀 파일 단일 업로드 및 색인 | `file` (MultipartFile) |
| `POST` | `/_uploadItemFilesByIC114` | IC114 형식 엑셀 파일 다중 업로드 및 색인 | `files` (MultipartFile[]) |
| `GET` | `/_indexingByDigikey` | Digikey 부품번호로 단건 색인 | `partNumber` |
| `POST` | `/_indexingByDigikey` | Digikey 부품번호 리스트 일괄 색인 (동시성 5) | `List<String> partNumbers` (JSON Body) |
| `GET` | `/_searchByUniKeyIC` | UniKeyIC API로 검색 후 ES에 색인 | `partNumber` |

### 외부 공급사 검색 API

| 메서드 | 경로 | 설명 | 주요 파라미터 |
|--------|------|------|--------------|
| `POST` | `/_searchExternalBatch` | 여러 partName 으로 Digikey + UniKeyIC 를 병렬 일괄 조회 (ES 캐시 우선, 키워드 폴백 없음) | `List<String> partNames` (JSON Body) |
| `GET` | `/_searchMultiSourceFirstHit` | 자체 → Digikey → UniKeyIC 순차 검색, 결과 존재 시 조기 종료 (소스별 에러 격리) | `searchWord`, `referencePrefix` |

### 반환 타입

- 동기 API: `CCResult` (또는 하위 타입 `CCObjectResult`, `CCPagingResult`)
- 비동기 API: `Mono<CCResult>`

### 다중 소스 검색 응답 구조 (`PcbPartsMultiSearchResult`)

```json
{
  "samplepcb": { "searchType": "exact|keyword", "items": [...] },
  "digikey":   { "searchType": "exact|keyword", "items": [...] },
  "unikeyic":  { "searchType": "exact|keyword", "items": [...] }
}
```

---

## Data

### JPA 엔티티 (RDB 테이블)

#### `sp_pcb_parts` (엔티티: `PcbParts`)

부품 마스터 테이블. `docId` 컬럼으로 ES 문서 ID와 연결된다.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK, AUTO) | 내부 시퀀스 ID |
| `doc_id` | `VARCHAR(20)` (UNIQUE, NOT NULL) | ES 문서 ID (`DocIdGenerator` 자동 생성) |
| `write_date` | `DATETIME` | 작성일시 |
| `last_modified_date` | `DATETIME` | 최종 수정일시 |
| `service_type` | `VARCHAR(100)` | 데이터 출처 (`samplepcb`, `eleparts`, `digikey`, `unikeyic`) |
| `sub_service_type` | `VARCHAR(100)` | 서브 서비스 타입 |
| `large_category` | `VARCHAR(255)` | 대분류 (예: `Passive Components`, `수동부품`) |
| `medium_category` | `VARCHAR(255)` | 중분류 (예: `Capacitors`, `Resistors`, `저항`, `콘덴서`) |
| `small_category` | `VARCHAR(255)` | 소분류 |
| `part_name` | `VARCHAR(255)` | 부품명 |
| `description` | `TEXT` | 설명 |
| `manufacturer_name` | `VARCHAR(255)` | 제조사명 |
| `parts_packaging` | `VARCHAR(255)` | 포장 단위 |
| `packaging` | `TEXT` | 포장 상세 (JSON) |
| `moq` | `INT` | 최소 주문 수량 |
| `price` | `INT` | 최저 단가 (breakQuantity=1 기준) |
| `watt` | `TEXT` | 와트 (JSON, `PcbUnitSearch` 형태) |
| `tolerance` | `TEXT` | 오차범위 (JSON) |
| `ohm` | `TEXT` | 저항값 (JSON) |
| `condenser` | `TEXT` | 커패시터값 (JSON) |
| `voltage` | `TEXT` | 전압 (JSON) |
| `temperature` | `VARCHAR(255)` | 온도 |
| `size` | `VARCHAR(255)` | 사이즈 |
| `current_val` | `TEXT` | 전류 (JSON) |
| `inductor` | `TEXT` | 인덕터값 (JSON) |
| `product_name` | `VARCHAR(255)` | 제품명 |
| `photo_url` | `TEXT` | 사진 URL |
| `datasheet_url` | `TEXT` | 데이터시트 URL |
| `memo` | `TEXT` | 메모 |
| `offer_name` | `VARCHAR(255)` | 공급업체명 |
| `date_code` | `VARCHAR(100)` | 날짜 코드 |
| `member_id` | `VARCHAR(255)` | 회원 ID |
| `manager_phone_number` | `VARCHAR(50)` | 담당자 전화번호 |
| `manager_name` | `VARCHAR(255)` | 담당자 이름 |
| `manager_email` | `VARCHAR(255)` | 담당자 이메일 |
| `contents` | `TEXT` | 내용 |
| `status` | `INT` | 상태 (0: 미승인, 1: 승인) |

하위 컬렉션에 `@BatchSize(size = 100)` 적용으로 N+1 쿼리 문제를 해소한다.

**유니크 제약조건:** `uk_sp_pcb_parts_servicetype_partname (service_type(50), part_name(150))` — 동일 `(service_type, part_name)` 쌍의 중복 행을 DB 레벨에서 차단. ES race condition 으로 동시 insert 가 들어와도 RDB UNIQUE 가 마지막 방어선 역할을 한다 (마이그레이션: `alter_sp_pcb_parts_unique_key.sql`).

#### `sp_pcb_parts_price` (엔티티: `PcbPartsPrice`)

부품별 유통사 가격 정보.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK) | |
| `parts_id` | `BIGINT` (FK -> `sp_pcb_parts`) | |
| `distributor` | `VARCHAR(255)` | 유통사 이름 |
| `sku` | `VARCHAR(255)` | SKU |
| `stock` | `INT` | 재고 수량 |
| `moq` | `INT` | 최소 주문 수량 |
| `pkg` | `VARCHAR(100)` | 패키지 타입 |
| `updated_date` | `DATETIME` | 업데이트 일시 |

#### `sp_pcb_parts_price_step` (엔티티: `PcbPartsPriceStep`)

수량 구간별 단가 (Volume Pricing).

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK) | |
| `price_id` | `BIGINT` (FK -> `sp_pcb_parts_price`) | |
| `break_quantity` | `INT` | 구간 시작 수량 |
| `unit_price` | `INT` | 해당 구간 단가 |

`@OrderBy("breakQuantity ASC")` 정렬이 적용되어 있다.

#### `sp_pcb_parts_image` (엔티티: `PcbPartsImage`)

부품 이미지 정보.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK) | |
| `parts_id` | `BIGINT` (FK -> `sp_pcb_parts`) | |
| `upload_file_name` | `VARCHAR(255)` | 업로드된 파일명 |
| `origin_file_name` | `VARCHAR(255)` | 원본 파일명 |
| `path_token` | `VARCHAR(255)` | 경로 토큰 |
| `size` | `VARCHAR(50)` | 파일 크기 |

#### `sp_pcb_parts_spec` (엔티티: `PcbPartsSpec`)

부품 사양 속성 (Digikey 등에서 가져온 구조화된 스펙).

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK) | |
| `parts_id` | `BIGINT` (FK -> `sp_pcb_parts`) | |
| `display_value` | `VARCHAR(255)` | 표시 값 |
| `attr_group` | `VARCHAR(255)` | 속성 그룹 |
| `attr_name` | `VARCHAR(255)` | 속성 이름 |
| `attr_shortname` | `VARCHAR(255)` | 속성 약칭 |

### Elasticsearch 인덱스

#### `pcbparts` (문서: `PcbPartsSearch`)

부품 검색을 위한 ES 인덱스. 다양한 커스텀 분석기(analyzer)를 사용한다.

주요 필드 매핑:

| 필드 | ES 타입 | 분석기 | 설명 |
|------|---------|--------|------|
| `partName` | `Text` (main) + `keyword` + `normalize` + `ngram4` | `ngram_analyzer6_case_insensitive` | 부품명. 다중 필드(MultiField) 구성으로 완전일치, 부분일치, 정규화 검색 모두 지원 |
| `serviceType` | `Keyword` | `keyword_normalizer` | 데이터 출처 구분 |
| `largeCategory`, `mediumCategory`, `smallCategory` | `Text` + `keyword` + `normalize` | `nori` (한국어) | 3단계 카테고리 |
| `manufacturerName` | `Text` + `keyword` + `normalize` | `ngram_analyzer4_case_insensitive` | 제조사명 |
| `price` | `Integer` | - | 최저 단가 (READ_ONLY, `setPrices()` 시 자동 계산) |
| `prices` | `Nested` (`PcbPartsPriceSearch`) | - | 유통사별 가격 정보 |
| `watt`, `ohm`, `condenser`, `voltage`, `current`, `inductor` | `Object` (`PcbUnitSearch`) | - | 전기적 사양. 5개 서브필드(`field1`~`field5`)로 분해 저장 |
| `tolerance` | `Text` + `keyword` + `ngram` | `samplepcb_analyzer` | 오차범위 |
| `size`, `temperature` | `Text` + `keyword` + `ngram` | `samplepcb_analyzer` | 크기, 온도 |
| `specs` | `Nested` (`PcbPartSpec`) | - | 구조화된 사양 속성 (Digikey 등) |
| `images` | `Nested` (`PcbImageVM`) | - | 이미지 정보 |

`PcbPartsSearch`는 `Persistable<String>`을 구현하여 `id` 기반 신규/기존 판별 로직을 제공한다.

#### `nondigikeyparts` (문서: `NonDigikeyPartsSearch`)

Digikey에서 조회 실패한 부품번호를 기록하여 중복 호출을 방지하는 블랙리스트 인덱스.

| 필드 | ES 타입 | 설명 |
|------|---------|------|
| `partName` | `Text` (`ngram_analyzer6_case_insensitive`) + `keyword` + `normalize` + `ngram4` | 조회 실패 부품명 |

### 가격 자동 계산 로직

`PcbPartsSearch.setPrices()` 호출 시, `breakQuantity == 1`인 단가 중 최솟값을 `price` 필드에 자동 세팅한다. 이 필드는 검색 정렬에 사용된다 (`Sort.Direction.ASC`).

### 서비스 타입 (`PcbPkgType`)

| 값 | 설명 |
|----|------|
| `samplepcb` | 자체 등록 부품 (IC114 포함) |
| `eleparts` | Eleparts 엑셀 업로드 부품 |
| `digikey` | Digikey API 연동 부품 |
| `unikeyic` | UniKeyIC API 연동 부품 |

---

## Key Decisions

### 1. ES + JPA 이중 저장 전략

모든 부품 데이터는 Elasticsearch와 RDB(JPA) 양쪽에 저장된다. ES는 전문 검색과 사양 기반 필터링에 사용되고, RDB는 관계형 데이터 정합성과 트랜잭션 보장에 사용된다. `PcbPartsConvertSubService.toEntity()` / `toEntities()`가 양 방향 변환을 담당하며, `docId`로 두 저장소 간 문서를 연결한다.

### 2. 사양 값의 PcbUnitSearch 5-필드 분해 구조

저항(ohm), 커패시터(condenser), 전압(voltage) 등의 전기적 사양은 `PcbUnitSearch` 객체의 `field1`~`field5`로 분해 저장된다. 이를 통해 단위 변환 없이도 다양한 표기법(예: `1K`, `1000ohm`, `1kohm`)으로 검색이 가능하다. 각 필드에 대해 `.keyword` suffix가 붙은 정확 매칭 필드도 생성된다.

### 3. 검색 폴백 체인

검색은 단계적 폴백 전략을 사용한다:
1. **완전일치** (`partName.keyword`) -> 2. **파싱 검색** (사양 단위별 분해 후 조건 조합) -> 3. **텍스트 검색** (`partName` 기본 분석기)
결과가 없으면 Digikey 키워드 검색으로 외부 폴백한다.

### 4. 다중 소스 병렬 검색 (`Mono.zip`) + ES 캐시 우선 전략

`PcbPartsMultiSearchService`는 자체 ES, Digikey, UniKeyIC 3개 소스를 `Mono.zip()`으로 병렬 실행한다. 자체 ES는 블로킹 호출이므로 `Mono.fromCallable()`로 감싸서 리액티브 파이프라인에 통합한다. 각 소스의 결과에 `searchType`("exact" / "keyword")을 태깅하여 클라이언트가 결과 유형을 구분할 수 있게 한다.

Digikey와 UniKeyIC 채널에는 **ES 캐시 우선 전략**이 적용된다. `findFreshCachedResults()`가 `pcbparts` 인덱스에서 `serviceType + partName.keyword` 조건으로 기존 색인 데이터를 조회하고, `lastModifiedDate`가 설정된 TTL(`application.external-cache.ttl-hours`, 기본 24시간) 이내이면 캐시 히트로 판정하여 외부 API 호출을 완전히 생략한다. 캐시 미스 시에만 외부 API를 호출하고 결과를 ES에 색인하여 다음 요청의 캐시로 활용한다.

### 5. IC114 카테고리 자동 매핑

`PcbPartsIC114Service`는 6자리 카테고리 코드(예: `030101`)를 정적 맵(`RESISTANCE_CATEGORIES`, `CAPACITOR_CATEGORIES`)을 통해 한국어 카테고리명으로 자동 변환한다. 파일명에서 코드를 추출하여 대분류(`수동부품`), 중분류(`저항`/`콘덴서`), 소분류를 자동 설정한다.

### 6. referencePrefix 기반 검색 컨텍스트

검색어 파싱 시 `referencePrefix` 값(`R`, `C`, `L`)에 따라 다른 패턴과 기본값이 적용된다:
- `R` (저항): 확장된 저항 표기법(R 표기, 범위 표기) 인식, 저항값+사이즈 필수, 오차범위 기본값 `10%`
- `C` (커패시터): 축약 커패시터 단위(`22p`, `33n`) 인식, 커패시터값 필수, 전압 기본값 `25V`
- `L` (인덕터): 인덕터값+사이즈 필수

### 7. 외부 API 결과 벌크 색인 최적화

`bulkIndexParts()`는 serviceType + partName 기준으로 ES 벌크 조회 후, JPA docId 기반 벌크 조회를 수행하여 기존 데이터 유무를 일괄 판별한다. 이를 통해 개별 존재 확인 쿼리의 N+1 문제를 회피한다. ES 저장도 `saveAll()`로 일괄 처리한다.

**동일 데이터 재색인 스킵 + lastModifiedDate touch:** `SKIP_IDENTICAL_INDEX = true` 조건에서, 기존 ES 문서와 신규 데이터의 `description` + `manufacturerName` 이 모두 동일하면 RDB save 와 본문 재색인을 건너뛴다. 단, ES 캐시(`findFreshCachedResults`)가 사용하는 `lastModifiedDate` 만은 `new Date()` 로 갱신하여 외부 캐시 TTL(24h) 이 만료되지 않도록 유지한다 (`PcbPartsService.bulkIndexParts()`).

**`(service_type, part_name)` UNIQUE 제약 + DataIntegrityViolationException 폴백:** `uk_sp_pcb_parts_servicetype_partname` 인덱스가 RDB 레벨 정합성을 보장한다. ES 벌크 조회에서는 보이지 않던 row 가 동시 insert race 로 RDB 에 먼저 들어온 경우, `DataIntegrityViolationException` 을 catch 하여 `findByServiceTypeAndPartName` 으로 충돌 row 를 다시 조회한 뒤 update 경로로 폴백한다 (`PcbPartsService.bulkIndexParts()`).

### 8. Caffeine 캐시 + ES 캐시 이중 캐싱

외부 API 호출 최적화를 위해 두 레벨의 캐싱이 적용된다:
- **Caffeine 캐시** (`searchResults`, 500건/30분): `DigikeySubService.searchByKeyword()`에 `@Cacheable` 적용. 동일 키워드의 반복 호출을 인메모리에서 즉시 반환한다.
- **ES 캐시** (TTL 24시간): `PcbPartsMultiSearchService.findFreshCachedResults()`가 `pcbparts` 인덱스의 `lastModifiedDate` 기반으로 신선도를 판별한다. Caffeine 캐시 만료 후에도 ES에 색인된 데이터가 TTL 이내이면 API 호출을 생략한다.

### 9. NonDigikey 블랙리스트

Digikey API에서 조회 실패한 부품번호는 `nondigikeyparts` 인덱스에 기록된다. 이후 동일 부품번호로 색인 요청 시 API 호출을 건너뛴다 (`searchNonDigikeyParts()` -> 결과 있으면 `Mono.just(CCResult.dataNotFound())`).

---

## Gotchas

### 1. 이중 저장 동기화 불일치 가능성

ES와 RDB에 순차적으로 저장하므로, 중간에 예외가 발생하면 한쪽에만 저장될 수 있다. 트랜잭션이 ES 작업을 포함하지 않으므로 수동 복구가 필요할 수 있다.

### 2. 파싱 검색의 정규식 복잡도

`PcbPartsUtils.parseString()`의 정규식 패턴은 다양한 단위 표기법(ohm, Ω, kohm, mΩ, R 표기법 등)을 처리해야 하므로 매우 복잡하다. 새로운 표기법이 추가되면 패턴 충돌(overlap) 가능성이 있으며, 매칭 우선순위에 따라 결과가 달라질 수 있다.

### 3. 엑셀 파일 크기 제한

`MAX_FILE_SIZE_BYTES`는 1GB로 설정되어 있으며, `IOUtils.setByteArrayMaxOverride()`로 Apache POI의 기본 제한을 오버라이드한다. 대용량 파일 처리 시 메모리 부족이 발생할 수 있다. 청크 크기는 `EXCEL_CHUNK_SIZE = 3000`행 단위이다.

### 4. Digikey 일괄 색인의 동시성

`POST /_indexingByDigikey`에서 `Flux.fromIterable`의 동시성(concurrency)이 5로 제한되어 있다. Digikey API의 Rate Limit에 따라 이 값을 조정해야 할 수 있다.

### 5. 다중 소스 검색 중 자체 ES 검색의 serviceType 필터

`PcbPartsMultiSearchService.searchSamplepcb()`는 `serviceType`이 `samplepcb` 또는 `eleparts`인 문서만 검색한다. Digikey/UniKeyIC에서 색인된 문서는 자체 검색 결과에 포함되지 않으므로, 동일 부품이 여러 소스에서 중복 반환될 수 있다.

### 6. price 필드의 읽기 전용 계산

`PcbPartsSearch.price`는 `@JsonProperty(access = READ_ONLY)`로 직접 세팅이 불가능하다. `setPrices()`를 호출해야만 `breakQuantity == 1`인 최솟값으로 자동 계산된다. 가격 정보 없이 부품을 저장하면 `price`가 `null`이 되어 가격순 정렬에서 의도치 않은 위치에 놓일 수 있다.

### 7. manufacturers.json 정적 로딩

`PcbPartsService`는 정적 블록에서 `manufacturers.json`을 로드하여 제조사명 -> ID 매핑을 생성한다. 이 파일이 클래스패스에 없으면 매핑이 비어 있는 채로 시작되며, `searchExactMatch()`에서 Digikey 제조사 필터링이 동작하지 않는다.

### 8. IC114 카테고리 코드 정적 매핑 한계

`PcbPartsIC114Service`의 저항/콘덴서 카테고리 매핑은 정적 `Map`에 하드코딩되어 있다. 새로운 카테고리 코드가 추가되면 코드 변경 및 재배포가 필요하다. 매핑되지 않은 코드의 경우 `smallCategory`가 `null`이 된다.

---

## Sources

- `src/main/java/kr/co/samplepcb/xpse/resource/PcbPartsResource.java` - REST Controller
- `src/main/java/kr/co/samplepcb/xpse/service/PcbPartsService.java` - 핵심 서비스
- `src/main/java/kr/co/samplepcb/xpse/service/PcbPartsMultiSearchService.java` - 다중 소스 병렬 검색
- `src/main/java/kr/co/samplepcb/xpse/service/PcbPartsIC114Service.java` - IC114 엑셀 업로드
- `src/main/java/kr/co/samplepcb/xpse/domain/entity/PcbParts.java` - JPA 부품 엔티티
- `src/main/java/kr/co/samplepcb/xpse/domain/entity/PcbPartsPrice.java` - JPA 가격 엔티티
- `src/main/java/kr/co/samplepcb/xpse/domain/entity/PcbPartsPriceStep.java` - JPA 구간 단가 엔티티
- `src/main/java/kr/co/samplepcb/xpse/domain/entity/PcbPartsImage.java` - JPA 이미지 엔티티
- `src/main/java/kr/co/samplepcb/xpse/domain/entity/PcbPartsSpec.java` - JPA 사양 엔티티
- `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbPartsSearch.java` - ES 부품 검색 문서
- `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbPartsPriceSearch.java` - ES 가격 문서
- `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbPartsPriceStepSearch.java` - ES 구간 단가 문서
- `src/main/java/kr/co/samplepcb/xpse/domain/document/NonDigikeyPartsSearch.java` - ES NonDigikey 문서
- `src/main/java/kr/co/samplepcb/xpse/service/common/sub/PcbPartsSubService.java` - 카테고리 일괄 변경
- `src/main/java/kr/co/samplepcb/xpse/service/common/sub/PcbPartsConvertSubService.java` - ES/JPA 변환
- `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartsSearchField.java` - ES 필드명 상수
- `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartsSearchVM.java` - 검색 조건 View Model
- `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartSpec.java` - 부품 사양 속성 POJO
- `src/main/java/kr/co/samplepcb/xpse/util/PcbPartsUtils.java` - 부품 사양 파싱 유틸리티
- `src/main/java/kr/co/samplepcb/xpse/pojo/ElasticIndexName.java` - ES 인덱스명 상수
- `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPkgType.java` - 서비스 타입 Enum
- `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartsMultiSearchResult.java` - 다중 소스 검색 응답 POJO
- `src/main/java/kr/co/samplepcb/xpse/pojo/PcbPartsExternalBatchResult.java` - 외부 공급사 일괄 검색 응답 POJO
- `src/main/java/kr/co/samplepcb/xpse/repository/PcbPartsRepository.java` - JPA 리포지토리 (`findByServiceTypeAndPartName` 충돌 폴백)
- `src/main/resources/db/migration/alter_sp_pcb_parts_unique_key.sql` - `(service_type, part_name)` UNIQUE 인덱스 추가
- `src/test/java/kr/co/samplepcb/xpse/service/PcbPartsMultiSearchServiceTest.java` - 다중 소스 검색 ES 캐시 테스트
