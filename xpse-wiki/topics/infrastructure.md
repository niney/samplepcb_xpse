# 설정/인프라 (infrastructure)

## Purpose

`samplepcb_xpse` 프로젝트는 PCB 부품 검색 및 견적 관리를 위한 Spring Boot 4.1.0-SNAPSHOT 기반 백엔드 애플리케이션이다. Elasticsearch를 핵심 검색 엔진으로 사용하고, MySQL을 관계형 데이터 저장소로 활용하며, 외부 부품 API(DigiKey, UniKeyIC)와의 연동, JWT 기반 인증, Caffeine 캐시, QueryDSL 타입세이프 쿼리 등의 인프라를 구성한다. Java 21 toolchain 위에서 동작하며, 포트 8081에서 서비스를 제공한다.

## Architecture

### 핵심 설정 클래스 구조

프로젝트의 인프라 설정은 `kr.co.samplepcb.xpse.config` 패키지 아래 5개 `@Configuration` 클래스로 구성된다.

#### 1. `ApplicationProperties` -- 외부 설정 바인딩

`@ConfigurationProperties(prefix = "application")` 으로 `application.yaml`의 `application:` 하위 속성을 타입 안전하게 바인딩한다. 내부 정적 클래스로 다음 설정 그룹을 관리한다:

| 내부 클래스 | YAML 키 | 역할 |
|---|---|---|
| `Jwt` | `application.jwt.secret` | JWT 서명 비밀키 |
| `SpLinserver` | `application.splinserver.serverUrl` | SP Lin 서버 URL |
| `MlServer` | `application.mlServer.serverUrl` | ML 서버 URL |
| `Digikey` | `application.digikey.*` | DigiKey API 인증 정보 (`baseUrl`, `clientId`, `clientSecret`) |
| `UniKeyIC` | `application.unikeyic.*` | UniKeyIC API 인증 정보 (`baseUrl`, `apiKey`, `exchangeRate`) |

`CorsConfiguration` 역시 이 클래스에서 `application.cors.*` 설정으로 관리된다.

#### 2. `ElasticsearchConfig` -- Elasticsearch 인덱스 자동 초기화

`@PostConstruct`의 `initializeElasticsearch()` 메서드에서 `classpath:mapping/*.txt` 패턴으로 모든 매핑 파일을 스캔한다. 파일 이름(확장자 제외)을 인덱스 이름으로 사용하여 `CoolElasticUtils.processIndex()`를 호출한다. 이 과정에서:

- 인덱스가 이미 존재하면 엔티티 클래스 기반으로 매핑만 업데이트한다.
- 인덱스가 없으면 매핑 파일의 JSON(첫 줄의 `PUT` 명령어 제외)에서 settings와 mappings를 파싱하여 새 인덱스를 생성한다.

엔티티 클래스 탐색은 `kr.co.samplepcb.xpse.domain` 패키지를 재귀 스캔하여 `@Document(indexName = "...")` 어노테이션의 인덱스 이름이 일치하는 클래스를 찾는다.

#### 3. `CacheConfig` -- Caffeine 캐시 설정

`@EnableCaching` 활성화 후 `CaffeineCacheManager`를 비동기 모드(`setAsyncCacheMode(true)`)로 구성한다. 두 개의 캐시 영역을 등록한다:

| 캐시 이름 | 상수 | 최대 크기 | TTL | 용도 |
|---|---|---|---|---|
| `searchResults` | `CacheConfig.SEARCH_RESULTS` | 500 | 30분 | 검색 결과 캐싱 |
| `productDetails` | `CacheConfig.PRODUCT_DETAILS` | 1,000 | 1시간 | 제품 상세 정보 캐싱 |

두 캐시 모두 `recordStats()`가 활성화되어 히트율 등 통계 수집이 가능하다.

#### 4. `SecurityConfig` -- Spring Security 및 JWT 인증

Stateless 세션 정책(`SessionCreationPolicy.STATELESS`)으로 구성되며, CSRF는 비활성화된다. `JwtAuthenticationFilter`가 `UsernamePasswordAuthenticationFilter` 앞에 등록되어 `Authorization: Bearer <token>` 헤더에서 JWT를 추출, 검증한다.

**인증 흐름:**

1. `JwtAuthenticationFilter.doFilterInternal()` -- 요청 헤더에서 Bearer 토큰을 추출한다.
2. `JwtTokenProvider.validateToken()` -- HMAC-SHA 키로 토큰 유효성을 검증한다.
3. `JwtTokenProvider.getAuthentication()` -- Claims에서 `sub`, `mbName`, `mbLevel`, `mbNo`를 추출하여 `JwtUserPrincipal`을 생성한다.
4. `mbLevel >= 10` 이면 `ROLE_ADMIN` 권한이 추가로 부여된다.

**선택적 인증 모델:** `authorizeHttpRequests`에서 모든 요청을 `permitAll()`로 설정하되, 컨트롤러 메서드에 `@JwtAuth` 어노테이션이 있거나 파라미터에 `@AuthenticationPrincipal JwtUserPrincipal`이 있으면 필터가 인증을 강제한다. 이 방식으로 인증이 필요한 엔드포인트만 선택적으로 보호한다.

CORS 설정은 `ApplicationProperties`의 `cors` 속성에서 읽어 `/api/**` 경로에만 적용된다.

#### 5. `QueryDslConfig` -- QueryDSL JPA 설정

`EntityManager`를 주입받아 `JPAQueryFactory` Bean을 생성한다. QueryDSL 5.1.0 (Jakarta 분류자)을 사용하며, 어노테이션 프로세서로 Q-타입 클래스를 컴파일 시점에 생성한다.

#### 6. `WebConfigurer`

`WebMvcConfigurer` 인터페이스를 구현하지만 현재 별도 커스터마이징 없이 빈 상태이다. 향후 인터셉터, 포매터, 리소스 핸들러 등의 확장 포인트로 사용된다.

### 유틸리티 클래스

#### `CoolElasticUtils`

Elasticsearch 인덱스 관리 및 검색 결과 처리의 핵심 유틸리티이다.

- `processIndex()` -- 인덱스 존재 여부에 따라 생성 또는 매핑 업데이트를 수행한다.
- `findEntityClassForIndex()` -- 클래스패스를 스캔하여 인덱스 이름에 해당하는 `@Document` 엔티티를 탐색한다.
- `createNewIndex()` -- 매핑 파일의 첫 줄(`PUT` 명령)을 건너뛰고 JSON을 파싱하여 settings와 mappings로 인덱스를 생성한다.
- `getSourceWithHighlight()` -- 검색 결과를 Map으로 변환하며, `_score`, `_id`, `_index`, `highlight` 메타데이터를 포함한다. 1,000건 이상 시 `parallelStream()`으로 병렬 처리한다.
- `createHighlightQuery()` -- 지정된 필드명 집합으로 `HighlightQuery`를 생성한다(`PcbPartsSearch` 클래스 기반).

#### `DocIdGenerator`

`SecureRandom` 기반으로 15바이트 난수를 생성한 뒤 URL-safe Base64로 인코딩하여 ES 스타일 20자 ID를 만든다. 싱글턴 `SecureRandom` 인스턴스를 사용하며, private 생성자로 인스턴스화를 방지한다.

#### `CoolStringUtils`

문자열에서 숫자를 추출하는 유틸리티이다.

- `extractAndRoundNumber()` -- 문자열에서 숫자와 소수점만 추출하여 반올림한 `int` 값을 반환한다.
- `extractNumericValue()` -- 문자열에서 숫자만 추출하여 `Integer`를 반환한다.

### 공통 POJO/Adapter

#### `SearchParam`

Swagger `@Schema` 어노테이션이 적용된 기본 검색 파라미터 클래스로, `q`(검색어)와 `field`(검색 필드)를 포함한다.

#### `PagingAdapter`

Spring `Page`/`Pageable` 결과를 `CCPagingResult<T>` 형식으로 변환하는 정적 어댑터이다. `currentPage`는 1-based로 변환되며(`pageable.getPageNumber() + 1`), `totalCount`, `offset`, `size`, `data` 필드를 설정한다. 선택적으로 검색 쿼리 문자열 `q`를 포함할 수 있다.

#### `SearchBase`

모든 ES 검색 도큐먼트의 기반 클래스로, `_class` 필드를 가진다. Spring Data Elasticsearch가 역직렬화 시 사용하는 타입 힌트 필드이다.

#### `ElasticIndexName`

Elasticsearch 인덱스 이름 상수를 정의한다:

| 상수 | 값 | 설명 |
|---|---|---|
| `PCB_PARTS` | `pcbparts` | PCB 부품 인덱스 |
| `PCB_KIND` | `pcbkind` | PCB 종류/카테고리 인덱스 |
| `PCB_COLUMN` | `pcbcolumn` | PCB 컬럼 인덱스 (벡터 검색 포함) |
| `PCB_ITEM` | `pcbitem` | PCB 아이템 인덱스 |
| `NON_DIGIKEY_PARTS` | `nondigikeyparts` | DigiKey 외 부품 인덱스 |

## Talks To

### Elasticsearch (핵심 검색 엔진)

- **연결:** `spring.elasticsearch.uris: http://localhost:9200` (기본)
- **Spring Data Elasticsearch** 기반으로 `ElasticsearchOperations`를 통해 인덱스 관리 및 쿼리를 수행한다.
- 애플리케이션 시작 시 5개 인덱스(`pcbparts`, `pcbkind`, `pcbcolumn`, `pcbitem`, `nondigikeyparts`)를 자동 생성/갱신한다.
- 디버깅을 위해 `co.elastic.clients: DEBUG`, `elasticsearch.client.elc: TRACE` 레벨 로깅이 설정되어 있다.

### MySQL (관계형 데이터베이스)

- **연결:** `spring.datasource.url: jdbc:mysql://www.samplepcb.co.kr:3306/hyoh9150` (dev/prod 프로파일)
- **드라이버:** `com.mysql.jdbc.Driver` (mysql-connector-java 5.1.49)
- **커넥션 풀:** HikariCP (`maximum-pool-size: 10`, `minimum-idle: 5`, `idle-timeout: 30000`, `connection-timeout: 20000`)
- **JPA 설정:** `ddl-auto: none` (스키마 자동 생성 없음), `open-in-view: false`, `MySQLDialect`
- QueryDSL 5.1.0 (Jakarta)을 통한 타입 안전 쿼리 지원

### DigiKey API (외부 부품 정보)

- **Base URL:** `https://api.digikey.com`
- **인증:** `clientId` + `clientSecret` 기반 OAuth

### UniKeyIC API (외부 부품 정보)

- **Base URL:** `https://openapi.unikeyic.com`
- **인증:** API Key 방식
- **환율:** `exchangeRate` 설정 (기본값 1350)

### SP Lin 서버

- **개발:** `http://localhost:8098`
- **운영:** `https://lin.easypcb.co.kr`

### ML 서버

- **개발:** `http://localhost:8099`
- **운영:** `https://ml.easypcb.co.kr`

## API Surface

### Swagger/OpenAPI

- **API 문서:** `/v3/api-docs`
- **Swagger UI:** `/swagger-ui.html`
- 태그와 오퍼레이션은 알파벳순 정렬, 기본적으로 접힌 상태(`doc-expansion: none`)
- 기본 미디어 타입: `application/json`

### 파일 업로드 제한

- `max-file-size: 50MB`
- `max-request-size: 100MB`

### 페이징 설정

- 1-based 페이지 번호 (`one-indexed-parameters: true`)
- 기본 페이지 크기: 10
- 최대 페이지 크기: 1,000,000,000 (사실상 무제한)

### 인증 엔드포인트 보호

모든 URL은 기본적으로 `permitAll()`이지만, 컨트롤러에서 `@JwtAuth` 어노테이션 또는 `@AuthenticationPrincipal JwtUserPrincipal` 파라미터를 사용하는 엔드포인트만 JWT 인증이 강제된다. 인증 실패 시 HTTP 401과 함께 `{"error":"Unauthorized","message":"JWT token is required"}` JSON 응답을 반환한다.

## Data

### Elasticsearch 인덱스 매핑 상세

모든 인덱스는 `number_of_shards: 1`, `number_of_replicas: 0`으로 설정된다 (단일 노드 환경).

#### `pcbparts` (PCB 부품)

커스텀 분석기 구성:

| 분석기 | 토크나이저 | 필터 | 용도 |
|---|---|---|---|
| `ngram_analyzer` | `ngram_tokenizer` (2-gram) | 없음 | 기본 N-gram 분석 |
| `ngram_analyzer_case_insensitive` | `ngram_tokenizer` (2-gram) | `lowercase` | 대소문자 무시 2-gram |
| `ngram_analyzer4_case_insensitive` | `ngram_tokenizer4` (4-gram) | `lowercase` | 제조사명 검색용 |
| `ngram_analyzer6_case_insensitive` | `ngram_tokenizer6` (6-gram) | `lowercase` | 부품명 검색용 |
| `keyword_lowercase_analyzer` | `keyword` | `lowercase` | 전체 키워드 소문자 비교 |

주요 필드 매핑:

- `partName` -- `ngram_analyzer6_case_insensitive` 분석기, `keyword`/`normalize` 서브필드
- `manufacturerName` -- `ngram_analyzer4_case_insensitive` 분석기, `keyword`/`normalize` 서브필드

#### `pcbkind` (PCB 종류)

- `nori` 분석기 포함 (한국어 형태소 분석)
- `max_result_window: 1,000,000,000` (대량 결과 윈도우)
- `pId` -- `keyword` 타입 (부모 ID)
- `itemName` -- `ngram_analyzer_case_insensitive` 분석기

#### `pcbcolumn` (PCB 컬럼)

- `colNameVector` -- `dense_vector` 타입, `dims: 512` (벡터 유사도 검색용, ML 서버 연동)

#### `pcbitem` (PCB 아이템)

- `ngram_analyzer_case_insensitive` 분석기
- `keyword_normalizer` 정규화기
- 명시적 필드 매핑 없이 동적 매핑 사용

#### `nondigikeyparts` (DigiKey 외 부품)

- `pcbparts`와 동일한 분석기 및 매핑 구조
- `partName`, `manufacturerName` 동일한 N-gram 설정

### 매핑 파일 형식

`src/main/resources/mapping/*.txt` 파일은 Kibana Dev Tools 형식(`PUT indexname` 으로 시작)을 사용한다. `CoolElasticUtils.readFileContentFirstIgnore()`가 첫 줄을 건너뛰고 JSON 본문만 파싱한다.

### 응답 페이징 형식

`PagingAdapter`를 통해 `CCPagingResult<T>` 형식으로 통일된 응답을 제공한다:

```json
{
  "result": true,
  "q": "검색어",
  "currentPage": 1,
  "offset": 0,
  "size": 10,
  "totalCount": 100,
  "data": [...]
}
```

## Key Decisions

1. **Stateless JWT + 선택적 인증 모델:** Spring Security의 `permitAll()` 전략 위에 커스텀 `@JwtAuth` 어노테이션 기반 선택적 강제 인증을 구현했다. 필터가 `HandlerMapping`을 조회하여 대상 메서드의 어노테이션을 런타임에 검사하므로, 컨트롤러 개발자가 어노테이션 하나로 인증 요구 여부를 제어할 수 있다.

2. **Elasticsearch 인덱스 자동 관리:** 매핑 파일을 `classpath:mapping/*.txt`에 Kibana 형식으로 관리하고, 애플리케이션 기동 시 자동으로 인덱스를 생성하거나 매핑을 갱신한다. 엔티티 클래스 탐색은 리플렉션 기반 패키지 스캐닝으로 수행한다.

3. **N-gram 다단계 토크나이저:** 부품명(6-gram), 제조사명(4-gram), 일반 필드(2-gram)로 검색 정밀도를 분리했다. 긴 식별자에는 높은 gram, 짧은 일반 텍스트에는 낮은 gram을 사용하여 검색 품질과 인덱스 크기를 절충한다.

4. **비동기 Caffeine 캐시:** `setAsyncCacheMode(true)`로 비동기 캐시를 활성화하여 WebFlux/WebClient 기반 외부 API 호출 결과를 논블로킹으로 캐싱한다.

5. **벡터 검색 지원:** `pcbcolumn` 인덱스에 `dense_vector(512)` 필드를 설정하여 ML 서버에서 생성한 임베딩 벡터를 활용한 유사도 검색을 지원한다.

6. **검색 결과 병렬 처리:** `CoolElasticUtils.getSourceWithHighlight()`에서 1,000건 이상 결과에 대해 `parallelStream()`을 적용하여 대량 결과의 Map 변환 성능을 최적화한다.

7. **ES 스타일 ID 생성:** `DocIdGenerator`로 `SecureRandom` 기반 20자 URL-safe Base64 ID를 생성한다. ES 내부 ID 포맷과 일관성을 유지한다.

8. **Spring Boot 4.1.0-SNAPSHOT:** 스냅샷 버전을 사용하며, `https://repo.spring.io/snapshot` 저장소를 추가로 등록한다.

9. **cool-library 로컬 JAR:** `libs/cool-library-1.4.6.jar`를 `implementation files()`로 직접 포함한다. Maven Central에 없는 자체 라이브러리이다.

## Gotchas

1. **매핑 파일 첫 줄 규약:** `mapping/*.txt` 파일의 첫 줄은 반드시 `PUT indexname` 형식이어야 한다. `CoolElasticUtils.readFileContentFirstIgnore()`가 무조건 첫 줄을 건너뛰므로, 이 규약이 깨지면 JSON 파싱이 실패한다.

2. **인덱스-엔티티 매핑 의존성:** 매핑 파일명과 `@Document(indexName = "...")` 어노테이션 값이 정확히 일치해야 한다. 불일치 시 `findEntityClassForIndex()`가 null을 반환하고, 기존 인덱스의 매핑 업데이트가 실행되지 않는다.

3. **`max-page-size: 1,000,000,000`:** 페이징 최대 크기가 사실상 무제한이므로, 클라이언트가 매우 큰 페이지 크기를 요청하면 OOM이나 성능 문제가 발생할 수 있다. `pcbkind`의 `max_result_window`도 동일하게 10억으로 설정되어 있다.

4. **Replicas 0 설정:** 모든 ES 인덱스가 `number_of_replicas: 0`이므로, 노드 장애 시 데이터 유실 위험이 있다. 운영 환경에서 클러스터 구성 시 변경이 필요하다.

5. **MySQL 드라이버 버전:** `mysql-connector-java:5.1.49`와 `com.mysql.jdbc.Driver`(레거시 클래스명)를 사용한다. 최신 MySQL 8.x 연결 시 `com.mysql.cj.jdbc.Driver`와 최신 커넥터로 교체가 권장된다.

6. **`open-in-view: false`:** JPA의 OSIV가 비활성화되어 있으므로, 서비스 레이어 밖에서 지연 로딩이 동작하지 않는다. 필요한 연관 데이터는 반드시 트랜잭션 내에서 로드해야 한다.

7. **CORS `allowed-origins: "*"`:** 기본 설정에서 모든 출처를 허용한다. 운영 환경에서는 제한된 도메인으로 변경해야 한다.

8. **`parallelStream()` 임계값:** `getSourceWithHighlight()`의 1,000건 병렬 처리 임계값은 하드코딩되어 있다. 서버 환경에 따라 최적 임계값이 다를 수 있으며, ForkJoinPool의 기본 스레드 수에 의존한다.

9. **비밀키 관리:** `application.yaml`에 JWT secret, DigiKey clientSecret, UniKeyIC apiKey 등 민감 정보가 평문으로 기재되어 있다. 환경 변수나 Vault 등 외부 시크릿 관리 도입이 권장된다.

10. **`createHighlightQuery()`의 하드코딩:** 하이라이트 쿼리 생성 시 `PcbPartsSearch.class`가 하드코딩되어 있어, 다른 인덱스의 하이라이트 쿼리에는 이 메서드를 직접 사용할 수 없다.

## Sources

| 파일 | 역할 |
|---|---|
| `src/main/java/kr/co/samplepcb/xpse/config/ElasticsearchConfig.java` | ES 인덱스 자동 초기화 |
| `src/main/java/kr/co/samplepcb/xpse/config/CacheConfig.java` | Caffeine 비동기 캐시 설정 |
| `src/main/java/kr/co/samplepcb/xpse/config/QueryDslConfig.java` | QueryDSL `JPAQueryFactory` 빈 등록 |
| `src/main/java/kr/co/samplepcb/xpse/config/SecurityConfig.java` | Spring Security 필터 체인 및 CORS |
| `src/main/java/kr/co/samplepcb/xpse/config/WebConfigurer.java` | WebMvc 확장 포인트 (현재 미사용) |
| `src/main/java/kr/co/samplepcb/xpse/config/ApplicationProperties.java` | 외부 설정 바인딩 (`@ConfigurationProperties`) |
| `src/main/java/kr/co/samplepcb/xpse/security/JwtTokenProvider.java` | JWT 토큰 검증 및 Authentication 생성 |
| `src/main/java/kr/co/samplepcb/xpse/security/JwtAuthenticationFilter.java` | 요청별 JWT 인증 필터 |
| `src/main/java/kr/co/samplepcb/xpse/security/JwtAuth.java` | 선택적 인증 강제 어노테이션 |
| `src/main/java/kr/co/samplepcb/xpse/security/JwtUserPrincipal.java` | JWT Claims 기반 사용자 정보 |
| `src/main/java/kr/co/samplepcb/xpse/util/CoolElasticUtils.java` | ES 인덱스 관리 및 검색 결과 변환 유틸리티 |
| `src/main/java/kr/co/samplepcb/xpse/util/CoolStringUtils.java` | 문자열 숫자 추출 유틸리티 |
| `src/main/java/kr/co/samplepcb/xpse/util/DocIdGenerator.java` | ES 스타일 20자 문서 ID 생성기 |
| `src/main/java/kr/co/samplepcb/xpse/pojo/ElasticIndexName.java` | ES 인덱스명 상수 정의 |
| `src/main/java/kr/co/samplepcb/xpse/domain/document/SearchBase.java` | ES 도큐먼트 기반 클래스 |
| `src/main/java/kr/co/samplepcb/xpse/pojo/adapter/PagingAdapter.java` | Spring Page -> CCPagingResult 변환 |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SearchParam.java` | 검색 파라미터 POJO |
| `src/main/resources/application.yaml` | 공통 애플리케이션 설정 |
| `src/main/resources/application-dev.yaml` | 개발 환경 프로파일 |
| `src/main/resources/application-prod.yaml` | 운영 환경 프로파일 |
| `src/main/resources/mapping/pcbparts.txt` | pcbparts 인덱스 매핑 정의 |
| `src/main/resources/mapping/pcbkind.txt` | pcbkind 인덱스 매핑 정의 |
| `src/main/resources/mapping/pcbcolumn.txt` | pcbcolumn 인덱스 매핑 정의 (벡터) |
| `src/main/resources/mapping/pcbitem.txt` | pcbitem 인덱스 매핑 정의 |
| `src/main/resources/mapping/nondigikeyparts.txt` | nondigikeyparts 인덱스 매핑 정의 |
| `build.gradle` | 빌드 설정 및 의존성 관리 |
