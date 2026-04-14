# BOM 문서 관리 (sp-bom)

## Purpose

BOM(Bill of Materials) 문서를 회원 단위로 관리하는 모듈이다. 프론트엔드에서 파싱한 BOM 파일(엑셀, CSV 등)의 내용을 JSON 형태로 서버에 저장하고, 이를 검색/조회/수정/삭제할 수 있는 CRUD API를 제공한다.

핵심 기능:
- 회원별 BOM 문서의 저장 및 중복 감지 (SHA-256 `contentHash` 기반)
- 파일명, 해시, 확장자 타입 등 다양한 조건의 동적 검색
- 동일 해시의 BOM이 이미 존재하면 신규 생성 대신 기존 문서를 갱신하는 upsert 로직

## Architecture

### 계층 구조

```
SpBomDocumentResource (REST Controller)
    |
    v
SpBomDocumentService (비즈니스 로직)
    |
    +--- SpBomDocumentMapper (MapStruct 매퍼: Entity <-> DTO 변환)
    |
    v
SpBomDocumentRepository (JPA + QueryDSL)
    |-- JpaRepository<SpBomDocument, Long>       (기본 CRUD)
    |-- SpBomDocumentRepositoryCustom             (커스텀 인터페이스)
    +-- SpBomDocumentRepositoryImpl               (QueryDSL 구현체)
```

### 주요 클래스 목록

| 계층 | 클래스/인터페이스 | 역할 |
|------|-------------------|------|
| Resource | `SpBomDocumentResource` | REST 엔드포인트 (`/api/spBomDocuments`) |
| Service | `SpBomDocumentService` | 저장, 조회, 삭제, 검색 로직 |
| Mapper | `SpBomDocumentMapper` | `SpBomDocument` <-> `SpBomDocumentCreateDTO` / `SpBomDocumentDetailDTO` 변환 |
| Entity | `SpBomDocument` | JPA 엔티티 (테이블: `sp_bom_document`) |
| Entity | `SpFile` | 파일 첨부 엔티티 (테이블: `sp_file`), 범용 참조 구조 |
| Repository | `SpBomDocumentRepository` | `JpaRepository` + 커스텀 리포지토리 |
| Repository | `SpBomDocumentRepositoryCustom` | 동적 검색용 인터페이스 |
| Repository | `SpBomDocumentRepositoryImpl` | QueryDSL `JPAQueryFactory`를 이용한 동적 쿼리 구현 |
| Repository | `SpFileRepository` | `SpFile` 기본 CRUD 및 참조 기반 조회 |
| DTO | `SpBomDocumentCreateDTO` | 저장/수정 요청 바디 |
| DTO | `SpBomDocumentDetailDTO` | 단건 상세 응답 (items, fileInfo 포함) |
| DTO | `SpBomDocumentListDTO` | 목록 응답 (items, fileInfo 제외, 경량) |
| DTO | `SpBomDocumentSearchParam` | 검색 파라미터 (`SearchParam` 상속) |
| 공통 | `SearchParam` | 기본 검색 파라미터 (q, field) |
| 공통 | `PagingAdapter` | `CCPagingResult` 페이징 응답 어댑터 |

## Talks To

### 의존하는 모듈/라이브러리

| 대상 | 설명 |
|------|------|
| **coolib** (`CCResult`, `CCObjectResult`, `CCPagingResult`) | 공통 응답 래퍼 라이브러리. 모든 API 응답이 이 포맷을 따른다. |
| **Spring Security + JWT** (`JwtAuth`, `JwtUserPrincipal`) | 모든 엔드포인트에 JWT 인증 적용. `principal.getSub()`로 회원 ID(`mbId`)를 추출한다. |
| **QueryDSL** (`JPAQueryFactory`, `QSpBomDocument`, `BooleanBuilder`) | 동적 검색 쿼리 빌드. |
| **MapStruct** (`@Mapper(componentModel = "spring")`) | 엔티티-DTO 간 매핑 자동 생성. JSON 직렬화/역직렬화 커스텀 메서드 포함. |
| **Jackson** (`tools.jackson.databind.ObjectMapper`) | `SpBomDocumentMapper` 내에서 `fileInfo`, `items` 필드의 JSON 문자열 파싱에 사용. |
| **SpFile / SpFileRepository** | 범용 파일 첨부 테이블. `refType` + `refId`로 다형 참조하며, BOM 문서에 원본 파일을 연결할 때 사용 가능. |

### 호출 관계 요약

- `SpBomDocumentResource` -> `SpBomDocumentService` (모든 요청 위임)
- `SpBomDocumentService` -> `SpBomDocumentRepository` (데이터 접근)
- `SpBomDocumentService` -> `SpBomDocumentMapper` (DTO 변환)
- `SpBomDocumentRepositoryImpl` -> `JPAQueryFactory` (QueryDSL 동적 쿼리)

## API Surface

기본 경로: `/api/spBomDocuments`
인증: 모든 엔드포인트에 `@JwtAuth` + Bearer Token 필요

### 엔드포인트 목록

#### 1. BOM 문서 검색

```
GET /api/spBomDocuments/_search
```

**파라미터 (Query String):**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `page` | int | 아니오 | 페이지 번호 (0-based, Spring Pageable) |
| `size` | int | 아니오 | 페이지 크기 |
| `q` | String | 아니오 | 검색어. `field` 미지정 시 `fileName`, `contentHash` 모두에서 LIKE 검색 |
| `field` | String | 아니오 | 검색 대상 필드: `fileName`, `contentHash`, `type` |
| `fileName` | String | 아니오 | 파일명 LIKE 검색 (독립 필터) |
| `contentHash` | String | 아니오 | 콘텐츠 해시 일치 검색 (독립 필터) |
| `type` | String | 아니오 | `file`이면 `fileInfo`가 존재하는 문서만 필터링 |

**응답:** `CCPagingResult<SpBomDocumentListDTO>`

응답 내 `SpBomDocumentListDTO` 필드: `id`, `mbId`, `fileName`, `contentHash`, `createdAt`, `updatedAt`

**정렬:** `createdAt` 내림차순 고정

#### 2. BOM 문서 단건 조회

```
GET /api/spBomDocuments/{id}
```

**파라미터:**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `id` | Long | 예 | 문서 ID (Path Variable) |

**응답:** `CCObjectResult<SpBomDocumentDetailDTO>`

응답 내 `SpBomDocumentDetailDTO` 필드: `id`, `mbId`, `fileName`, `contentHash`, `fileInfo` (JSON Object), `items` (JSON Object/Array), `createdAt`, `updatedAt`

**권한 검증:** 조회 대상 문서의 `mbId`와 인증된 사용자의 `sub`가 일치해야 한다. 불일치 시 `dataNotFound` 반환.

#### 3. BOM 문서 저장 (Create / Update)

```
POST /api/spBomDocuments
```

**요청 바디 (`SpBomDocumentCreateDTO`):**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `id` | Long | 아니오 | 지정 시 해당 ID의 문서를 수정 |
| `fileName` | String | 예 (신규 시) | 원본 파일명 |
| `contentHash` | String | 아니오 | SHA-256 해시. 중복 감지에 사용 |
| `fileInfo` | Object | 아니오 | 파일 메타 정보 (JSON) |
| `items` | Object | 예 (신규 시) | BOM 아이템 데이터 (JSON) |

**저장 로직 (3단계 분기):**

1. `id`가 존재하면 -> 해당 문서를 ID로 조회하여 업데이트
2. `id`가 없고 `contentHash`가 존재하면 -> `mbId` + `contentHash`로 기존 문서 조회. 존재하면 업데이트 (upsert)
3. 위 두 경우 모두 해당하지 않으면 -> 신규 문서 생성

**응답:** `CCObjectResult<SpBomDocumentDetailDTO>`

#### 4. BOM 문서 삭제

```
DELETE /api/spBomDocuments/{id}
```

**파라미터:**

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `id` | Long | 예 | 삭제할 문서 ID (Path Variable) |

**응답:** `CCResult` (성공 시 `result: true`)

**권한 검증:** 삭제 대상 문서의 `mbId`와 인증된 사용자의 `sub`가 일치해야 한다.

### 내부 전용 메서드

| 메서드 | 위치 | 설명 |
|--------|------|------|
| `getByContentHash(String mbId, String contentHash)` | `SpBomDocumentService` | 해시 기반 문서 조회. API 엔드포인트 없음, 서비스 내부 호출용. |

## Data

### 테이블: `sp_bom_document`

| 컬럼 | 타입 | 제약 조건 | 설명 |
|------|------|-----------|------|
| `id` | BIGINT (AUTO_INCREMENT) | PK | 문서 고유 ID |
| `mb_id` | VARCHAR(100) | NOT NULL | 회원 ID (JWT `sub` 값) |
| `file_name` | VARCHAR(255) | NOT NULL | 원본 파일명 |
| `content_hash` | VARCHAR(64) | NULLABLE | SHA-256 해시값 |
| `file_info` | TEXT | NULLABLE | 파일 메타 정보 (JSON 문자열) |
| `items` | LONGTEXT | NOT NULL | BOM 아이템 데이터 (JSON 문자열) |
| `created_at` | DATETIME | NOT NULL | 생성 일시 |
| `updated_at` | DATETIME | NOT NULL | 수정 일시 |

**인덱스:**
- `idx_mb_id`: `mb_id` 단일 인덱스
- `uq_mb_content`: `mb_id` + `content_hash` 유니크 제약 (동일 회원의 동일 해시 문서 중복 방지)

### 테이블: `sp_file` (관련 테이블)

| 컬럼 | 타입 | 제약 조건 | 설명 |
|------|------|-----------|------|
| `id` | BIGINT (AUTO_INCREMENT) | PK | 파일 고유 ID |
| `ref_type` | VARCHAR(50) | NOT NULL | 참조 대상 타입 (다형 참조) |
| `ref_id` | BIGINT | NOT NULL | 참조 대상 ID |
| `upload_file_name` | VARCHAR(255) | NOT NULL | 업로드된 파일명 (서버 저장명) |
| `origin_file_name` | VARCHAR(255) | NOT NULL | 원본 파일명 |
| `path_token` | VARCHAR(500) | NOT NULL | 파일 접근 경로 토큰 |
| `size` | BIGINT | NOT NULL | 파일 크기 (bytes) |
| `write_date` | DATETIME | NOT NULL | 파일 기록 일시 |

**인덱스:**
- `idx_sp_file_ref`: `ref_type` + `ref_id` 복합 인덱스

`SpFile`은 `refType`과 `refId` 조합으로 다양한 엔티티에 파일을 연결하는 범용 첨부 파일 테이블이다. BOM 문서에 원본 파일을 연결할 때 `refType`에 BOM 관련 타입 문자열, `refId`에 `SpBomDocument.id`를 지정하여 사용할 수 있다.

### DTO 구조

```
SpBomDocumentCreateDTO (저장 요청)
  - id: Long (수정 시)
  - fileName: String
  - contentHash: String
  - fileInfo: Object   <-- JSON 자유 형식
  - items: Object      <-- JSON 자유 형식

SpBomDocumentDetailDTO (상세 응답)
  - id, mbId, fileName, contentHash
  - fileInfo: Object   <-- DB의 JSON 문자열을 파싱한 객체
  - items: Object      <-- DB의 JSON 문자열을 파싱한 객체
  - createdAt, updatedAt

SpBomDocumentListDTO (목록 응답 - 경량)
  - id, mbId, fileName, contentHash
  - createdAt, updatedAt
  (fileInfo, items 제외)
```

### JSON 변환 흐름

`SpBomDocumentMapper`가 `@Named` 커스텀 메서드로 양방향 변환을 처리한다:

- **저장 시** (`toJsonString`): DTO의 `Object` 타입 필드를 JSON 문자열로 직렬화하여 엔티티의 `String` 필드에 저장. 이미 JSON 문자열인 경우(`{...}` 또는 `[...]`) 그대로 통과시킨다.
- **조회 시** (`parseJson`): 엔티티의 JSON 문자열을 `Object`(Map 또는 List)로 역직렬화하여 DTO에 담아 응답. 파싱 실패 시 원본 문자열을 그대로 반환한다.

## Key Decisions

### 1. contentHash 기반 upsert 전략

동일 회원이 같은 BOM 파일을 다시 업로드하면 `contentHash` (SHA-256)로 기존 문서를 찾아 갱신한다. 이를 통해 클라이언트가 명시적으로 문서 ID를 관리하지 않아도 중복 생성을 방지할 수 있다. DB 레벨에서도 `uq_mb_content` 유니크 제약으로 동일 회원의 해시 중복을 원천 차단한다.

### 2. JSON을 LONGTEXT로 저장

`items` 필드는 BOM의 부품 목록 전체를 JSON 형태로 `LONGTEXT` 컬럼에 저장한다. 정규화된 별도 테이블 대신 비정형 JSON 저장을 선택한 이유는 BOM 데이터의 스키마가 파일마다 다를 수 있고, 개별 아이템에 대한 서버 측 쿼리 요구가 낮기 때문으로 보인다.

### 3. 목록/상세 DTO 분리

목록 조회(`SpBomDocumentListDTO`)에서는 대용량인 `items`와 `fileInfo` 필드를 제외하여 응답 페이로드를 경량화했다. 상세 조회(`SpBomDocumentDetailDTO`)에서만 전체 JSON 데이터를 반환한다.

### 4. QueryDSL 동적 검색

정적 쿼리 메서드 대신 `SpBomDocumentRepositoryImpl`에서 `BooleanBuilder`로 조건을 동적 조합한다. `q` + `field` 조합 검색, `fileName` 필터, `contentHash` 필터, `type` 필터를 유연하게 처리한다.

### 5. 회원 격리 (Tenant Isolation)

모든 조회/수정/삭제 연산에서 JWT에서 추출한 `mbId`와 문서의 `mbId`를 비교하여, 다른 회원의 문서에 접근하는 것을 차단한다. 이는 컨트롤러가 아닌 서비스 계층에서 수행된다.

### 6. 수정 시 null 무시 전략

`SpBomDocumentMapper.updateEntity()`에 `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)`를 적용하여, 부분 업데이트(PATCH 의미론)를 지원한다. 클라이언트가 보내지 않은 필드는 기존 값이 유지된다.

## Gotchas

### 1. items 컬럼의 LONGTEXT 크기 제한

`items`가 `LONGTEXT`로 선언되어 최대 약 4GB까지 저장 가능하지만, 실제로는 HTTP 요청 바디 크기 제한, JSON 파싱 메모리, 응답 직렬화 시간 등이 병목이 될 수 있다. 매우 큰 BOM 파일의 경우 별도 처리가 필요할 수 있다.

### 2. fileInfo, items의 스키마 미정의

`fileInfo`와 `items`는 `Object` 타입으로 받아 JSON 문자열로 저장하므로, 서버 측에서 내용 검증(validation)이 없다. 클라이언트가 잘못된 구조를 보내도 그대로 저장된다.

### 3. 목록 검색 시 items 직접 매핑

`SpBomDocumentService.search()` 메서드에서 목록 DTO 변환 시 MapStruct 매퍼를 사용하지 않고 수동으로 `SpBomDocumentListDTO`에 값을 세팅하고 있다. `items`와 `fileInfo`를 제외하기 위한 의도적 선택이지만, 필드가 추가되면 매핑 누락이 발생할 수 있다.

### 4. contentHash가 nullable

`contentHash`는 nullable이므로, 해시 없이 저장된 문서는 upsert 로직을 타지 않고 항상 신규 생성된다. 동일한 파일을 해시 없이 여러 번 저장하면 중복 문서가 쌓일 수 있다.

### 5. 삭제 시 SpFile 연계 처리 없음

`SpBomDocumentService.delete()`에서 `SpBomDocument`만 삭제하고, `SpFile` 테이블의 관련 레코드는 삭제하지 않는다. `SpFileRepository.deleteByRefTypeAndRefId()`가 존재하지만 현재 BOM 삭제 로직에서는 호출되지 않으므로, 첨부 파일을 연결한 경우 고아 레코드가 남을 수 있다.

### 6. type 검색의 이중 해석

`SpBomDocumentSearchParam.type` 필드가 두 가지 의미로 해석된다: (1) 값이 `"file"`이면 `fileInfo IS NOT NULL` 필터, (2) `q` + `field=type` 조합이면 파일 확장자 검색 (`fileName LIKE '%.확장자'`). 동일한 `type` 파라미터명이 서로 다른 맥락에서 다른 동작을 하므로 혼동 가능성이 있다.

### 7. Jackson 패키지 주의

`SpBomDocumentMapper`에서 사용하는 `ObjectMapper`의 import가 `tools.jackson.databind.ObjectMapper`이다. 표준 Jackson(`com.fasterxml.jackson`)이 아닌 별도 패키지를 사용하고 있으므로, 의존성 관리 시 주의가 필요하다.

### 8. 검색 정렬 고정

검색 API의 정렬 순서가 `createdAt DESC`로 하드코딩되어 있다. `Pageable`의 `sort` 파라미터가 무시된다.

## Sources

| 파일 | 경로 |
|------|------|
| `SpBomDocumentResource.java` | `src/main/java/kr/co/samplepcb/xpse/resource/SpBomDocumentResource.java` |
| `SpBomDocumentService.java` | `src/main/java/kr/co/samplepcb/xpse/service/SpBomDocumentService.java` |
| `SpBomDocument.java` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpBomDocument.java` |
| `SpFile.java` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpFile.java` |
| `SpBomDocumentRepository.java` | `src/main/java/kr/co/samplepcb/xpse/repository/SpBomDocumentRepository.java` |
| `SpBomDocumentRepositoryCustom.java` | `src/main/java/kr/co/samplepcb/xpse/repository/SpBomDocumentRepositoryCustom.java` |
| `SpBomDocumentRepositoryImpl.java` | `src/main/java/kr/co/samplepcb/xpse/repository/SpBomDocumentRepositoryImpl.java` |
| `SpFileRepository.java` | `src/main/java/kr/co/samplepcb/xpse/repository/SpFileRepository.java` |
| `SpBomDocumentMapper.java` | `src/main/java/kr/co/samplepcb/xpse/mapper/SpBomDocumentMapper.java` |
| `SpBomDocumentCreateDTO.java` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpBomDocumentCreateDTO.java` |
| `SpBomDocumentDetailDTO.java` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpBomDocumentDetailDTO.java` |
| `SpBomDocumentListDTO.java` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpBomDocumentListDTO.java` |
| `SpBomDocumentSearchParam.java` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpBomDocumentSearchParam.java` |
| `SearchParam.java` | `src/main/java/kr/co/samplepcb/xpse/pojo/SearchParam.java` |
| `PagingAdapter.java` | `src/main/java/kr/co/samplepcb/xpse/pojo/adapter/PagingAdapter.java` |
| `JwtAuth.java` | `src/main/java/kr/co/samplepcb/xpse/security/JwtAuth.java` |
| `JwtUserPrincipal.java` | `src/main/java/kr/co/samplepcb/xpse/security/JwtUserPrincipal.java` |
