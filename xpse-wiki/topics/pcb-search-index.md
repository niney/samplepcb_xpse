# PCB 검색 인덱스 (pcb-search-index)

## Purpose

PCB 검색 인덱스 모듈은 PCB 관련 데이터를 Elasticsearch에 색인하고, 이를 통한 고속 검색 기능을 제공하는 핵심 모듈이다. 크게 세 가지 도메인으로 나뉜다:

- **PcbKind (PCB 종류)**: PCB 부품의 분류 체계를 관리한다. 엑셀 파일 업로드를 통해 대량 색인하며, 대/중/소 분류(target 1, 2, 3)를 제외한 나머지 분류(target 4 이상)를 색인 대상으로 한다.
- **PcbColumn (PCB 컬럼)**: PCB 관련 컬럼(속성) 명칭을 관리하고, Google Universal Sentence Encoder 기반의 벡터 유사도 검색을 지원한다. 문장 목록을 입력받아 가장 유사한 컬럼을 매칭하는 시맨틱 검색이 핵심 기능이다.
- **PcbItem (PCB 아이템)**: PCB 개별 항목(부품명 등)을 색인하고 정확 매칭 검색을 제공한다. Digikey 카테고리 데이터의 자동 색인 기능도 포함한다.

## Architecture

### 계층 구조

```
Resource (REST Controller)
    |
Service (비즈니스 로직)
    |
Repository (ElasticsearchRepository)
    |
Document (Elasticsearch 도메인 객체)
```

### 핵심 클래스 맵

| 계층 | PcbKind | PcbColumn | PcbItem |
|------|---------|-----------|---------|
| Resource | `PcbKindResource` | `PcbColumnResource` | `PcbItemResource` |
| Service | `PcbKindService` | `PcbColumnService` | `PcbItemService` |
| Repository | `PcbKindSearchRepository` | `PcbColumnSearchRepository` | `PcbItemSearchRepository` |
| Document | `PcbKindSearch` | `PcbColumnSearch` | `PcbItemSearch` |
| 필드 상수 | (없음) | `PcbColumnSearchField` | `PcbItemSearchField` |
| ViewModel | (없음) | `PcbColumnSearchVM` | (없음) |

### 상속 구조

- `SearchBase` : `_class` 필드를 보유한 공통 기반 클래스
  - `PcbColumnSearch` : `SearchBase`를 상속
- `PcbKindSearch` : `Persistable<String>` 인터페이스를 구현 (Spring Data의 새 엔티티 판별용)
- `PcbItemSearch` : 별도 상속 없음
- `PcbUnitSearch` : Elasticsearch Document 어노테이션 없이 범용 8개 필드(`field1`~`field8`)를 정의하는 임베디드 도큐먼트 구조체

### Elasticsearch 인덱스명

`ElasticIndexName` 클래스에서 상수로 관리한다:

| 상수 | 인덱스명 | 용도 |
|------|----------|------|
| `PCB_KIND` | `pcbkind` | PCB 종류 분류 |
| `PCB_COLUMN` | `pcbcolumn` | PCB 컬럼/속성명 |
| `PCB_ITEM` | `pcbitem` | PCB 아이템 항목 |
| `PCB_PARTS` | `pcbparts` | PCB 부품 (이 모듈 범위 밖) |
| `NON_DIGIKEY_PARTS` | `nondigikeyparts` | 비-Digikey 부품 (이 모듈 범위 밖) |

## Talks To

### 내부 의존성

| 서비스 | 의존 대상 | 설명 |
|--------|-----------|------|
| `PcbKindService` | `ExcelSubService` | 엑셀 파일 셀 값 읽기 유틸리티 |
| `PcbKindService` | `PcbPartsSubService` | 종류명 변경 시 관련 부품 데이터 일괄 업데이트 (`updateKindAllByGroup`) |
| `PcbColumnService` | `GoogleTensorService` | 문장 벡터 인코딩 (Universal Sentence Encoder) |
| `PcbColumnService` | `ElasticsearchClient` | Elasticsearch 저수준 클라이언트 (`msearch` 멀티 검색용) |
| `PcbColumnService` | `ElasticsearchOperations` | Spring Data Elasticsearch 고수준 검색 연산 |
| `PcbItemService` | `DigikeySubService` | Digikey API에서 카테고리 데이터 조회 |

### 외부 의존성

| 대상 | 프로토콜 | 설명 |
|------|----------|------|
| **Elasticsearch** | HTTP (REST) | 문서 색인, 검색, 멀티 검색 수행 |
| **Google Tensor (LinServer)** | HTTP (WebClient) | 문장을 512차원 벡터로 인코딩. `ApplicationProperties.spLinserver.serverUrl`로 URL 설정. 엔드포인트: `GET /encode` (단일), `POST /encode` (배치) |
| **Digikey API** | HTTP (WebClient) | PCB 부품 카테고리 정보 조회 |

## API Surface

### PcbKind API (`/api/pcbKind`)

| 메서드 | 경로 | 설명 | 파라미터 |
|--------|------|------|----------|
| `POST` | `/_uploadItemFile` | 엑셀 파일 업로드 후 PCB 종류 재인덱싱 | `file` (MultipartFile) |

**동작 상세**: `reindexAllByFile()` 메서드가 엑셀의 모든 시트를 순회하며, 시트명에서 숫자를 추출하여 `target` 값으로 사용한다. target 1, 2, 3(대/중/소 분류)은 건너뛰고, target 4 이상만 색인한다. 각 행의 컬럼 0은 `id`, 컬럼 1은 `itemName`으로 매핑된다. 기존 동일 `itemName`+`target` 조합이 있으면 중복 색인을 방지한다. ID가 존재하는 문서의 `itemName`이 변경된 경우, `PcbPartsSubService.updateKindAllByGroup()`을 호출하여 관련 부품 데이터의 종류명도 일괄 변경한다.

### PcbColumn API (`/api/pcbColumn`)

| 메서드 | 경로 | 설명 | 파라미터 |
|--------|------|------|----------|
| `GET` | `/_search` | PCB 컬럼 검색 | `colName`, `target`, `page`, `size` (Pageable) |
| `POST` | `/_searchSentenceList` | 문장 목록 기반 시맨틱 유사도 검색 | `PcbSentenceVM` (JSON Body): `queryColumnNameList` |
| `POST` | `/_indexing` | PCB 컬럼 색인 | `colName`, `target` |

**검색 동작 상세**:
- `search()`: `PcbColumnSearchRepository.createSearchQuery()`에서 `Criteria` 기반 쿼리를 생성한다. `colName`과 `target` 필드에 대해 조건을 구성하고, 하이라이트를 적용한다. 응답에는 `target`과 `colName` 필드만 소스 필터링하여 반환한다.
- `searchSentenceList()`: 입력된 문장 목록(`queryColumnNameList`)을 `GoogleTensorService.postEncodedSentences()`로 벡터화한 뒤, Elasticsearch의 `msearch` API로 코사인 유사도 기반 `script_score` 쿼리를 일괄 실행한다. 각 문장당 상위 1개 결과를 반환한다. 유사도 점수가 1.4 초과인 경우만 유효한 매칭으로 간주하며, `(score / 2.0) * 100`으로 백분율 변환한다(최대 100%). 빈 `queryColName` 비율에 따라 평균 점수를 감소시켜 전체 매칭 품질(`averageScore`)을 산출한다.

**색인 동작 상세**:
- `indexing()`: `colName.keyword` 필드로 정확히 동일한 컬럼이 있는지 중복 확인 후, `GoogleTensorService.getEncodedSentence()`로 벡터를 생성하여 `colNameVector` 필드에 저장한다.

### PcbItem API (`/api/pcbItem`)

| 메서드 | 경로 | 설명 | 파라미터 |
|--------|------|------|----------|
| `POST` | `/_indexing` | PCB 아이템 색인 | `itemName`, `target` |
| `GET` | `/_search` | PCB 아이템 검색 | `itemName`, `target` |
| `GET` | `/_digikeyCategoryIndexing` | Digikey 카테고리 자동 색인 | (없음) |

**동작 상세**:
- `indexing()`: `target` 유무에 따라 `itemName` 단독 또는 `itemName`+`target` 조합으로 중복 검사 후 색인한다.
- `search()`: 동일한 분기 로직으로 기존 아이템을 조회한다.
- `digikeyCategoryIndexing()`: `DigikeySubService.getCategories()`로 카테고리 데이터를 조회한 뒤, `DigikeyUtils.extractWords()`로 단어를 추출하여 `target=14`로 일괄 색인한다.

## Data

### pcbkind 인덱스

**매핑 파일**: `src/main/resources/mapping/pcbkind.txt`

| 필드 | 타입 | 분석기/기타 | 설명 |
|------|------|-------------|------|
| `id` | (자동) | `@Id` | 문서 고유 식별자 |
| `pId` | `keyword` | - | 부모 ID |
| `itemName` | `text` (주) / `keyword` (서브) / `keyword` (normalize) | `ngram_analyzer_case_insensitive` (주), `keyword_normalizer` (normalize) | 종류명. ngram 텍스트 검색 + 정확 매칭 + 대소문자 무시 정규화 매칭 지원 |
| `target` | `keyword` | - | 분류 레벨 구분 (정수이나 keyword로 저장) |
| `writeDate` | `date` | `epoch_millis` | 생성일 (`@CreatedDate`) |
| `lastModifiedDate` | `date` | `epoch_millis` | 수정일 (`@LastModifiedDate`) |
| `displayName` | `text` / `keyword` / `keyword` (normalize) | `ngram_analyzer_case_insensitive`, `keyword_normalizer` | 표시명 |
| `etc1`, `etc2`, `etc3` | `text` / `keyword` / `keyword` (normalize) | `ngram_analyzer_case_insensitive`, `keyword_normalizer` | 기타 필드 1~3 |

**분석기 설정**:
- `ngram_tokenizer`: min_gram=2, max_gram=2 (바이그램)
- `ngram_analyzer_case_insensitive`: ngram_tokenizer + lowercase 필터
- `keyword_normalizer`: lowercase 필터 적용 커스텀 normalizer
- `nori`: nori_tokenizer (한국어 형태소 분석기, 매핑에 정의되어 있으나 필드에 직접 사용하지 않음)

**인덱스 설정**: 샤드 1개, 레플리카 0개, `max_result_window`=1,000,000,000

### pcbcolumn 인덱스

**매핑 파일**: `src/main/resources/mapping/pcbcolumn.txt`

| 필드 | 타입 | 분석기/기타 | 설명 |
|------|------|-------------|------|
| `id` | (자동) | `@Id` | 문서 고유 식별자 |
| `colName` | `text` (주) / `keyword` (서브) | `ngram_analyzer_case_insensitive` (주) | 컬럼명. ngram 텍스트 검색 + 정확 매칭 지원 |
| `target` | `keyword` | - | 대상 구분 |
| `colNameVector` | `dense_vector` | dims=512 | Google Universal Sentence Encoder로 생성된 512차원 임베딩 벡터 |
| `_class` | (상속) | `SearchBase` | Spring Data Elasticsearch 타입 메타데이터 |

**분석기 설정**: pcbkind와 동일한 ngram 구성 (min_gram=2, max_gram=2)

**인덱스 설정**: 샤드 1개, 레플리카 0개

### pcbitem 인덱스

**매핑 파일**: `src/main/resources/mapping/pcbitem.txt`

| 필드 | 타입 | 분석기/기타 | 설명 |
|------|------|-------------|------|
| `id` | (자동) | `@Id` | 문서 고유 식별자 |
| `itemName` | `keyword` (주, lowercase normalizer) / `text` (서브, suffix="text") | `lowercase` normalizer (주), `ngram_analyzer_case_insensitive` (서브) | 아이템명. 기본은 keyword로 정확 매칭, 서브 필드 `itemName.text`로 ngram 부분 검색 지원 |
| `target` | `keyword` | - | 대상 구분 |

**분석기 설정**: ngram 구성 동일 (min_gram=2, max_gram=2), `keyword_normalizer` (lowercase)

**인덱스 설정**: 샤드 1개, 레플리카 0개, mappings 본문은 비어 있음 (Spring Data 어노테이션 기반 자동 매핑에 의존)

### PcbUnitSearch (범용 필드 구조체)

특정 인덱스에 매핑되지 않는 범용 도큐먼트 구조로, `field1`~`field8`까지 8개의 동일 구조 필드를 가진다. `@JsonInclude(JsonInclude.Include.NON_NULL)`로 null 필드는 직렬화에서 제외한다.

각 필드의 멀티필드 구성:
- 주 필드: `text` 타입, `samplepcb_analyzer` 사용
- `.keyword` 서브필드: `text` 타입, `keyword_lowercase_analyzer` 사용
- `.ngram` 서브필드: `text` 타입, `ngram_analyzer_case_insensitive` 사용

### 필드 상수 클래스

**`PcbColumnSearchField`**:
| 상수 | 값 | 용도 |
|------|----|------|
| `ID` | `"id"` | 문서 ID |
| `COL_NAME` | `"colName"` | 컬럼명 텍스트 검색 |
| `COL_NAME_KEYWORD` | `"colName.keyword"` | 컬럼명 정확 매칭 (중복 검사) |
| `TARGET` | `"target"` | 대상 구분 필터 |
| `COL_NAME_VECTOR` | `"colNameVector"` | 벡터 유사도 스크립트에서 참조 |
| `GL_SCORE` | `"glScore"` | (용도 미상, 코드에서 미사용) |

**`PcbItemSearchField`**:
| 상수 | 값 | 용도 |
|------|----|------|
| `ITEM_NAME` | `"itemName"` | 아이템명 |
| `TARGET` | `"target"` | 대상 구분 |
| `_SCORE` | `"_score"` | Elasticsearch 관련성 점수 |

### ViewModel / POJO

**`PcbColumnSearchVM`**: 컬럼 검색 조건 및 결과를 담는 뷰모델. `id`, `colName`, `target`, `queryScore` (매칭 점수), `queryColName` (검색에 사용된 원본 쿼리 컬럼명)을 포함한다.

**`PcbSentenceVM`**: 문장 목록 검색 요청/응답 모델. `queryColumnNameList` (검색할 문장 목록), `pcbColumnSearchList` (매칭 결과 목록), `averageScore` (전체 평균 매칭 점수)를 포함한다.

## Key Decisions

### 바이그램(Bigram) ngram 토크나이저 채택

모든 인덱스에서 `min_gram=2`, `max_gram=2`의 ngram 토크나이저를 사용한다. 이는 PCB 부품명, 컬럼명 등이 영문 약어와 숫자 조합인 경우가 많아 2글자 단위의 부분 매칭이 효과적이기 때문이다. `token_chars`가 빈 배열이므로 공백, 특수문자 등 모든 문자를 토큰에 포함한다.

### 멀티필드(Multi-field) 전략

대부분의 텍스트 필드에 멀티필드를 적용하여 단일 필드로 여러 검색 시나리오를 지원한다:
- **주 필드 (text)**: ngram 기반 부분 매칭 검색용
- **`.keyword` 서브필드**: 정확 매칭, 집계, 정렬용
- **`.normalize` 서브필드**: 대소문자를 무시한 정확 매칭용 (pcbkind)

단, `PcbItemSearch`의 `itemName`은 주 필드가 `keyword`(lowercase normalizer)이고 서브 필드가 `text`(ngram)인 역전된 구조를 가진다. 이는 아이템명의 정확 매칭이 기본이고, 부분 검색은 보조적으로 사용되는 도메인 특성을 반영한다.

### 벡터 유사도 검색 (PcbColumn)

`PcbColumnSearch`에만 `dense_vector` (512차원) 필드가 존재한다. Elasticsearch의 `script_score` 쿼리에서 `cosineSimilarity` 함수를 사용하며, Elasticsearch의 `cosineSimilarity`는 [-1, 1] 범위를 반환하므로 +1.0을 더해 [0, 2] 범위로 변환한다. 스코어 1.4 초과를 유효 매칭 임계값으로 사용한다 (코사인 유사도 0.4 이상에 해당).

### Persistable 인터페이스 (PcbKind)

`PcbKindSearch`만 `Persistable<String>` 인터페이스를 구현한다. `isNew()`가 `id == null`을 반환하여, Spring Data Elasticsearch가 `save()` 호출 시 신규 생성(index)과 업데이트(update)를 올바르게 구분하도록 한다. 엑셀 업로드 시 기존 ID의 문서를 업데이트하는 시나리오가 있기 때문에 필요하다.

### 중복 방지 전략

세 인덱스 모두 색인 전 중복 검사를 수행하지만 방식이 다르다:
- **PcbKind**: `findByItemNameKeywordAndTarget()`으로 `itemName.normalize` + `target` 조합으로 검사 (대소문자 무시 정확 매칭)
- **PcbColumn**: `colName.keyword` 필드로 `Criteria.is()` 정확 매칭 후 `count()` 검사
- **PcbItem**: `findByItemName()` 또는 `findByItemNameAndTarget()`으로 keyword 정확 매칭 검사 (target 유무에 따라 분기)

### 레플리카 0 설정

모든 인덱스가 `number_of_replicas: 0`으로 설정되어 있다. 이는 단일 노드 개발/운영 환경을 전제로 하며, 프로덕션 HA 환경에서는 조정이 필요하다.

## Gotchas

### pcbkind 인덱스의 max_result_window

`max_result_window`가 1,000,000,000(10억)으로 설정되어 있다. 이는 기본값(10,000)을 크게 초과하며, 깊은 페이지네이션을 허용하지만 대량 데이터 환경에서 메모리 문제를 유발할 수 있다. `search_after` 또는 `scroll` API 사용을 고려해야 한다.

### pcbitem 매핑 파일의 빈 mappings

`pcbitem.txt` 매핑 파일의 `"mappings": {}`가 비어 있다. 실제 필드 매핑은 `PcbItemSearch` 클래스의 Spring Data Elasticsearch 어노테이션(`@MultiField`, `@Field`)에 의해 자동 생성된다. 매핑 파일과 어노테이션 사이의 불일치가 발생할 수 있으므로 주의해야 한다.

### PcbColumnSearchField.GL_SCORE 미사용

`PcbColumnSearchField.GL_SCORE` 상수가 정의되어 있으나 현재 코드에서 사용처가 없다. 과거 기능의 잔재이거나 향후 사용을 위해 남겨둔 것으로 보인다.

### 벡터 유사도 임계값 하드코딩

`PcbColumnService.searchSentenceListScore()`에서 유효 매칭 판별 임계값 `1.4`와 점수 변환 공식 `(score / 2.0) * 100`이 하드코딩되어 있다. 이 값들은 설정 파일로 외부화하는 것이 바람직하다.

### target 필드의 타입 불일치

`PcbKindSearch`, `PcbColumnSearch`, `PcbItemSearch` 모두 `target` 필드를 Java `Integer` 타입으로 선언하지만, Elasticsearch 매핑에서는 `keyword` 타입으로 저장한다. `@Field(type = FieldType.Keyword)` 어노테이션이 적용되어 있어 숫자가 문자열로 저장된다. 범위 검색이 필요한 경우 문제가 될 수 있다.

### GoogleTensorService의 블로킹 호출

`PcbColumnService`에서 `Mono.block()`을 사용하여 리액티브 스트림을 동기적으로 블로킹한다. 서블릿 기반 Spring MVC 환경에서는 동작하지만, 향후 WebFlux 전환 시 이 패턴은 변경해야 한다.

### PcbKindService의 대소문자 무시 중복 검사

`findByItemNameKeywordAndTarget()`은 `itemName.normalize` 필드(lowercase normalizer 적용)로 검색하여 대소문자를 무시한 중복 검사를 수행한다. 반면 `PcbItemService`의 `findByItemName()`은 `itemName` 필드(keyword, lowercase normalizer)로 검색한다. 두 인덱스의 중복 검사 전략이 미묘하게 다르므로 혼동하지 않아야 한다.

### PcbColumnSearch의 colNameVector null 허용

`colNameVector` 필드는 `List<Double>` 타입이며 null이 허용된다. `GoogleTensorService` 호출이 실패하면 벡터 없이 색인될 수 있으며, 이 경우 `cosineSimilarity` 스크립트 쿼리 실행 시 오류가 발생할 수 있다.

## Sources

| 파일 | 경로 |
|------|------|
| `PcbKindResource` | `src/main/java/kr/co/samplepcb/xpse/resource/PcbKindResource.java` |
| `PcbColumnResource` | `src/main/java/kr/co/samplepcb/xpse/resource/PcbColumnResource.java` |
| `PcbItemResource` | `src/main/java/kr/co/samplepcb/xpse/resource/PcbItemResource.java` |
| `PcbKindService` | `src/main/java/kr/co/samplepcb/xpse/service/PcbKindService.java` |
| `PcbColumnService` | `src/main/java/kr/co/samplepcb/xpse/service/PcbColumnService.java` |
| `PcbItemService` | `src/main/java/kr/co/samplepcb/xpse/service/PcbItemService.java` |
| `PcbKindSearch` | `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbKindSearch.java` |
| `PcbColumnSearch` | `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbColumnSearch.java` |
| `PcbItemSearch` | `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbItemSearch.java` |
| `PcbUnitSearch` | `src/main/java/kr/co/samplepcb/xpse/domain/document/PcbUnitSearch.java` |
| `SearchBase` | `src/main/java/kr/co/samplepcb/xpse/domain/document/SearchBase.java` |
| `PcbKindSearchRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/PcbKindSearchRepository.java` |
| `PcbColumnSearchRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/PcbColumnSearchRepository.java` |
| `PcbItemSearchRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/PcbItemSearchRepository.java` |
| `PcbColumnSearchField` | `src/main/java/kr/co/samplepcb/xpse/pojo/PcbColumnSearchField.java` |
| `PcbItemSearchField` | `src/main/java/kr/co/samplepcb/xpse/pojo/PcbItemSearchField.java` |
| `PcbColumnSearchVM` | `src/main/java/kr/co/samplepcb/xpse/pojo/PcbColumnSearchVM.java` |
| `PcbSentenceVM` | `src/main/java/kr/co/samplepcb/xpse/pojo/PcbSentenceVM.java` |
| `ElasticIndexName` | `src/main/java/kr/co/samplepcb/xpse/pojo/ElasticIndexName.java` |
| `GoogleTensorService` | `src/main/java/kr/co/samplepcb/xpse/service/common/sub/GoogleTensorService.java` |
| pcbkind 매핑 | `src/main/resources/mapping/pcbkind.txt` |
| pcbcolumn 매핑 | `src/main/resources/mapping/pcbcolumn.txt` |
| pcbitem 매핑 | `src/main/resources/mapping/pcbitem.txt` |
