# 견적 관리 (sp-estimate)

## Purpose

견적 관리 모듈은 SamplePCB 플랫폼에서 PCB 부품에 대한 **견적서(Estimate Document)** 생성, 조회, 수정, 삭제와 **협력사 견적(Partner Estimate)** 관리를 담당한다. 핵심 업무 흐름은 다음과 같다:

1. 관리자가 상품(`G5ShopItem`) + 장바구니(`G5ShopCart`) + 견적서(`SpEstimateDocument`) + 견적 항목(`SpEstimateItem`)을 일괄 생성한다.
2. 각 견적 항목에 대해 여러 협력사가 견적을 제출한다(`SpPartnerEstimateItem`).
3. 관리자가 협력사 견적 중 하나를 선택하여 최종 확정한다(`selectedPartnerEstimateItem`).
4. 주문이 발생하면 확정 가격(`confirmedPrice`)이 자동으로 채워진다.

이 모듈은 운영자(관리자) 시점과 협력사 시점 양쪽 모두에 API를 제공하며, 협력사 측에는 마진율 등 민감 정보를 노출하지 않는다.

## Architecture

### 계층 구조

```
SpEstimateResource (/api/spEstimates)        -- 관리자용 견적 REST API
SpPartnerEstimateResource (/api/spPartnerEstimates) -- 협력사용 견적 REST API
        |
        v
SpEstimateService                             -- 비즈니스 로직 (단일 서비스)
        |
        v
SpEstimateDocumentRepository (+ Custom/Impl)  -- 견적서 조회 (QueryDSL)
SpEstimateItemRepository                      -- 견적 항목 CRUD
SpPartnerEstimateDocumentRepository            -- 협력사 견적서 CRUD
SpPartnerEstimateItemRepository (+ Custom/Impl)-- 협력사 견적 항목 조회 (QueryDSL)
SpFileRepository                               -- 첨부파일 CRUD
SpEstimateMapper                               -- Entity <-> DTO 변환
```

### 핵심 클래스

| 계층 | 클래스 | 역할 |
|------|--------|------|
| Resource | `SpEstimateResource` | 관리자용 견적서 CRUD, 협력사 견적 선택, 통합 검색 |
| Resource | `SpPartnerEstimateResource` | 협력사용 견적 CRUD, 견적서 조회/수정, 상태 변경 |
| Service | `SpEstimateService` | 모든 견적 비즈니스 로직을 단일 서비스로 처리 |
| Entity | `SpEstimateDocument` | 견적서 문서 (테이블 `sp_estimate_document`) |
| Entity | `SpEstimateItem` | 견적서 부품 항목 (테이블 `sp_estimate_item`) |
| Entity | `SpPartnerEstimateDocument` | 협력사 견적서 (테이블 `sp_partner_estimate_document`) |
| Entity | `SpPartnerEstimateItem` | 협력사 견적 항목 (테이블 `sp_partner_estimate_item`) |
| Repository | `SpEstimateDocumentRepository` | `JpaRepository` + `SpEstimateDocumentRepositoryCustom` (QueryDSL) |
| Repository | `SpPartnerEstimateItemRepositoryImpl` | QueryDSL 기반 동적 검색 |
| Mapper | `SpEstimateMapper` | Entity-DTO 변환 매퍼 |

### 보안

- 모든 엔드포인트에 `@JwtAuth` 어노테이션이 적용되어 JWT 인증이 필수이다.
- `_searchWithPartners` 엔드포인트에서 `mbLevel != 10`인 사용자(비관리자)는 자동으로 `partnerMbNo` 필터가 적용되어 본인의 견적만 조회할 수 있다.
- 협력사용 API(`SpPartnerEstimateResource`)에서는 마진율(`globalMarginRate`)이 노출되지 않는다.

## Talks To

| 대상 | 관계 | 설명 |
|------|------|------|
| `G5ShopItem` | FK (`it_id`) | 견적서는 쇼핑몰 상품과 1:1 관계. 견적서 생성 시 상품을 upsert 한다. |
| `G5ShopCart` | FK (`it_id`) | 견적서 생성 시 장바구니도 함께 upsert 한다. 주문 여부 판단에 사용된다. |
| `G5ShopOrder` | 간접 참조 | `ShopCart.shopOrder`를 통해 주문 여부를 확인한다 (`isOrdered()`). |
| `G5Member` | FK (`mb_no`) | 협력사 회원 정보. 견적 항목/문서에서 회원 정보를 조인하여 반환한다. |
| `PcbParts` (`sp_pcb_parts`) | FK (`pcb_part_doc_id` -> `doc_id`) | 견적 항목이 참조하는 PCB 부품 마스터 데이터. |
| `SpFile` (`sp_file`) | 다형성 참조 (`ref_type` + `ref_id`) | 견적서 및 협력사 견적 항목의 첨부파일. `ref_type`으로 `estimate_document`, `partner_estimate_item`을 구분한다. |

## API Surface

### 관리자용 견적 API (`/api/spEstimates`)

| HTTP | 경로 | 메서드 | 설명 |
|------|------|--------|------|
| `POST` | `/api/spEstimates` | `create()` | 견적서 생성/수정 (상품 + 장바구니 + 견적서 + 견적항목 일괄 upsert) |
| `GET` | `/api/spEstimates/{id}` | `getDetail()` | 견적서 상세 조회 (PK 기준) |
| `GET` | `/api/spEstimates/byItId/{itId}` | `getDetailByItId()` | 견적서 상세 조회 (아이템 ID 기준) |
| `GET` | `/api/spEstimates/_search` | `search()` | 견적서 목록 검색 (페이징) |
| `DELETE` | `/api/spEstimates/{id}` | `delete()` | 견적서 삭제 (cascade + 파일 포함) |
| `POST` | `/api/spEstimates/{id}/status` | `updateStatus()` | 견적서 상태 변경 (`G5ShopItem.it24`에도 동기화) |
| `GET` | `/api/spEstimates/{estimateItemId}/partnerEstimateItems` | `getPartnerEstimateItems()` | 견적 항목의 협력사 견적 목록 조회 |
| `POST` | `/api/spEstimates/{estimateItemId}/partnerEstimateItems` | `createPartnerEstimateItem()` | 협력사 견적 항목 등록/수정 |
| `POST` | `/api/spEstimates/items/{estimateItemId}/selectPartner` | `selectPartnerEstimateItem()` | 협력사 견적 단건 선택 |
| `POST` | `/api/spEstimates/items/_batch/selectPartner` | `selectPartnerEstimateItems()` | 협력사 견적 다중 선택 (일괄) |
| `GET` | `/api/spEstimates/_searchWithPartners` | `searchWithEstimate()` | 견적서 + 협력사 견적서 통합 검색 |

### 협력사용 견적 API (`/api/spPartnerEstimates`)

| HTTP | 경로 | 메서드 | 설명 |
|------|------|--------|------|
| `GET` | `/api/spPartnerEstimates/{estimateItemId}/{mbNo}` | `getDetail()` | 협력사 견적 상세 조회 (견적항목 ID + 회원번호) |
| `GET` | `/api/spPartnerEstimates/_search` | `search()` | 협력사 견적 목록 검색 |
| `GET` | `/api/spPartnerEstimates/estimates/_search` | `searchEstimateDocs()` | 협력사가 참여한 견적서 목록 검색 |
| `GET` | `/api/spPartnerEstimates/estimateDocuments/_search` | `searchEstimateDocuments()` | 협력사에 배정된 견적서 목록 조회 (마진율 미노출) |
| `GET` | `/api/spPartnerEstimates/estimateDocuments/{id}` | `getEstimateDocumentDetail()` | 견적서 상세 (전체 협력사 조회) |
| `POST` | `/api/spPartnerEstimates/estimateDocuments/{id}` | `updateEstimateDocumentDetail()` | 협력사 견적서 상세 수정 (문서 + 항목 레벨) |
| `GET` | `/api/spPartnerEstimates/partnerEstimateDocuments/{pedId}` | `getPartnerEstimateDocumentDetail()` | 협력사 견적서 상세 조회 (PED ID 기준) |
| `POST` | `/api/spPartnerEstimates/partnerEstimateDocuments/{pedId}` | `updatePartnerEstimateDocumentDetail()` | 협력사 견적서 상세 수정 (PED ID 기준) |
| `POST` | `/api/spPartnerEstimates/partnerEstimateDocuments/{pedId}/status` | `updatePartnerEstimateDocStatus()` | 협력사 견적서 상태 변경 |
| `POST` | `/api/spPartnerEstimates` | `create()` | 협력사 견적 단건 생성 |
| `POST` | `/api/spPartnerEstimates/_batch` | `createBatch()` | 협력사 견적 다중 생성 (일괄) |
| `POST` | `/api/spPartnerEstimates/_batch/delete` | `deleteBatch()` | 협력사 견적 다중 삭제 |

### 요청/응답 DTO

**생성/수정 요청:**
- `SpEstimateCreateDTO` -- 견적서 일괄 생성. 내부에 `EstimateItemDTO` (부품 항목), `FileDTO` (첨부파일) 리스트 포함. `toItemCreateDTO()`로 `SpItemCreateDTO`를 위임 생성.
- `SpPartnerEstimateItemCreateDTO` -- 협력사 견적 항목 생성. `estimateItemId`, `mbNo`, `selectedPrice` (JSON text), `status`, `memo`.
- `SpPartnerEstimateDocUpdateDTO` -- 협력사 견적서 수정. 문서 레벨(`estimatePrice`, `status`, `memo`, `deliveryDate`) + 항목 레벨(`ItemUpdateDTO` 리스트).
- `SpEstimatePartnerSelectionDTO` -- 협력사 견적 다중 선택 요청. `estimateItemId` + `partnerEstimateItemId` 쌍.

**응답:**
- `SpEstimateDetailDTO` -- 견적서 상세. 하위에 `EstimateItemDTO`, `SelectedPartnerEstimateItemDTO`, `PcbPartDTO`, `PcbPartPriceDTO`, `PcbPartPriceStepDTO`, `PcbPartImageDTO`, `PcbPartSpecDTO`, `FileDTO` 포함.
- `SpEstimateListDTO` -- 견적서 목록. 하위에 `PartnerEstimateDTO` (협력사 견적서), `PartnerOrderDTO` (협력사 발주서) 리스트 포함.
- `SpPartnerEstimateDocDetailDTO` -- 협력사용 견적서 상세 (flat 구조). `ItemDTO`가 `sp_estimate_item` + `pcb_parts` + `sp_partner_estimate_item`을 하나로 합친 flat 뷰를 제공.
- `SpPartnerEstimateDocListDTO` -- 파트너별 견적서 목록. 항목 수, 협력사 견적 항목 수를 서브쿼리로 집계.
- `SpPartnerEstimateItemListDTO` / `SpPartnerEstimateItemDetailDTO` -- 협력사 견적 항목 목록/상세. 파트너 회원 정보(ID, 이름, 닉네임, 이메일, 휴대폰)를 조인.

**검색 파라미터:**
- `SpEstimateSearchParam` -- `itId`, `status`, `partnerMbNo` (JWT에서 자동 세팅, hidden).
- `SpPartnerEstimateItemSearchParam` -- `estimateItemId`, `partnerEstimateDocumentId`, `mbNo`, `status`.
- 공통 부모: `SearchParam` (`q` 검색어 등 공통 필드 포함).

**기본 DTO:**
- `SpEstimateItemBaseDTO` -- 견적 항목의 공통 필드 (`id`, `pcbPartDocId`, `qty`, `selectedPartnerEstimateItemId`). `SpEstimateCreateDTO.EstimateItemDTO`와 `SpEstimateDetailDTO.EstimateItemDTO`가 이를 상속한다.

## Data

### 엔티티 관계도 (ER)

```
G5ShopItem (g5_shop_item)
    |
    | it_id (1:1, UNIQUE)
    v
SpEstimateDocument (sp_estimate_document)
    |                                    \
    | 1:N (cascade ALL, orphanRemoval)    \ 1:N
    v                                      v
SpEstimateItem (sp_estimate_item)     SpPartnerEstimateDocument (sp_partner_estimate_document)
    |       \                              |
    |        \ selected_partner_           | 1:N (cascade ALL)
    |         \ estimate_item_id           v
    |          \ (FK, ON DELETE SET NULL)  SpPartnerEstimateItem (sp_partner_estimate_item)
    |           `-------------------------^
    |             1:N (cascade ALL, orphanRemoval)
    |
    | pcb_part_doc_id (FK)
    v
PcbParts (sp_pcb_parts)

SpFile (sp_file)  -- 다형성 참조 (ref_type + ref_id)
```

### 테이블 정의

#### `sp_estimate_document` -- 견적서

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK, AUTO_INCREMENT) | 견적서 ID |
| `it_id` | `VARCHAR(20)` (UNIQUE, NOT NULL) | 쇼핑몰 아이템 ID (`g5_shop_item.it_id` FK) |
| `status` | `VARCHAR(30)` | 견적 상태 |
| `expected_delivery` | `VARCHAR(100)` | 예상 납기 |
| `shipping_fee` | `INT` | 배송비 |
| `management_fee` | `INT` | 관리비 |
| `total_amount` | `INT` | 총액 |
| `final_amount` | `INT` | 최종 금액 |
| `memo` | `TEXT` | 메모 |
| `global_margin_rate` | `INT` | 글로벌 마진율 |
| `set_quantity` | `INT` | 세트 수량 (ALTER 추가) |
| `spare_quantity` | `INT` | 예비 수량 (ALTER 추가) |
| `write_date` | `DATETIME` | 작성일 |
| `modify_date` | `DATETIME` | 수정일 |

#### `sp_estimate_item` -- 견적 항목

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK, AUTO_INCREMENT) | 항목 ID |
| `estimate_document_id` | `BIGINT` (FK, NOT NULL) | 견적서 ID |
| `pcb_part_doc_id` | `VARCHAR(20)` (FK, NOT NULL) | PCB 부품 doc_id (`sp_pcb_parts.doc_id`) |
| `qty` | `INT` | 수량 |
| `analysis_meta` | `TEXT` | 분석 메타정보 (JSON) |
| `selected_price` | `TEXT` | 선택된 가격 (JSON) |
| `selected_partner_estimate_item_id` | `BIGINT` (FK, ON DELETE SET NULL) | 선택된 협력사 견적 항목 ID |
| `confirmed_price` | `TEXT` | 주문 확정 가격 (JSON, ALTER 추가) |
| `item_margin_rate` | `INT` | 항목별 마진율 (ALTER 추가) |
| `write_date` | `DATETIME` (NOT NULL) | 작성일 |
| `modify_date` | `DATETIME` (NOT NULL) | 수정일 |

#### `sp_partner_estimate_document` -- 협력사 견적서

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK, AUTO_INCREMENT) | 협력사 견적서 ID |
| `estimate_document_id` | `BIGINT` (FK, NOT NULL) | 원본 견적서 ID |
| `mb_no` | `INT` (FK, NOT NULL) | 협력사 회원번호 |
| `status` | `VARCHAR(30)` | 상태 (기본값: `협력사 견적요청`) |
| `estimate_price` | `INT` | 협력사 견적가 |
| `memo` | `TEXT` | 메모 |
| `delivery_date` | `DATETIME` | 납기일 (ALTER 추가) |
| `write_date` | `DATETIME` (NOT NULL) | 작성일 |
| `modify_date` | `DATETIME` (NOT NULL) | 수정일 |

**유니크 제약조건:** `uk_sp_partner_estimate_doc (estimate_document_id, mb_no)` -- 하나의 견적서에 대해 협력사당 하나의 견적서만 존재.

#### `sp_partner_estimate_item` -- 협력사 견적 항목

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK, AUTO_INCREMENT) | 협력사 견적 항목 ID |
| `estimate_item_id` | `BIGINT` (FK, NOT NULL) | 견적 항목 ID |
| `partner_estimate_document_id` | `BIGINT` (FK) | 협력사 견적서 ID (ALTER 추가) |
| `mb_no` | `INT` (FK, NOT NULL) | 협력사 회원번호 |
| `selected_price` | `TEXT` | 협력사 선택 가격 (JSON) |
| `status` | `VARCHAR(30)` | 상태 (기본값: `협력사 견적요청`, ALTER 추가) |
| `memo` | `TEXT` | 메모 (ALTER 추가) |
| `date_code` | `VARCHAR(100)` | Date Code (ALTER 추가) |
| `delivery_date` | `DATETIME` | 납기일 (ALTER 추가) |
| `write_date` | `DATETIME` (NOT NULL) | 작성일 |
| `modify_date` | `DATETIME` (NOT NULL) | 수정일 |

**유니크 제약조건:** 초기 `(estimate_item_id, mb_no)` -> 변경됨 `uk_sp_partner_estimate_item (estimate_item_id, partner_estimate_document_id)` -- 마이그레이션에서 UK가 변경되었다.

#### `sp_file` -- 공통 첨부파일

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | `BIGINT` (PK, AUTO_INCREMENT) | 파일 ID |
| `ref_type` | `VARCHAR(50)` (NOT NULL) | 참조 타입 (`estimate_document`, `partner_estimate_item`) |
| `ref_id` | `BIGINT` (NOT NULL) | 참조 대상 ID |
| `upload_file_name` | `VARCHAR(255)` (NOT NULL) | 업로드된 파일명 |
| `origin_file_name` | `VARCHAR(255)` (NOT NULL) | 원본 파일명 |
| `path_token` | `VARCHAR(500)` (NOT NULL) | 경로 토큰 |
| `size` | `BIGINT` (NOT NULL) | 파일 크기 (bytes) |
| `write_date` | `DATETIME` (NOT NULL) | 작성일 |

### 마이그레이션 이력

| 파일 | 내용 |
|------|------|
| `create_estimate_tables.sql` | `sp_estimate_document`, `sp_file`, `sp_estimate_item`, `sp_partner_estimate_item` 초기 생성. `selected_partner_estimate_item_id` FK 후행 추가. |
| `create_sp_partner_estimate_document.sql` | `sp_partner_estimate_document` 테이블 생성. `sp_partner_estimate_item`에 `partner_estimate_document_id` 컬럼 추가 및 UK 변경. |
| `alter_sp_partner_estimate_item.sql` | `sp_partner_estimate_item`에 `status`, `memo`, `date_code`, `delivery_date` 컬럼 추가. `sp_partner_estimate_document`에 `delivery_date` 추가. |
| `alter_sp_estimate_item_confirmed_price.sql` | `sp_estimate_item`에 `confirmed_price`, `item_margin_rate` 컬럼 추가. |
| `alter_sp_estimate_document_quantity.sql` | `sp_estimate_document`에 `set_quantity`, `spare_quantity` 컬럼 추가. |

### JPA 관계 매핑 요약

- `SpEstimateDocument` -> `SpEstimateItem`: `@OneToMany(cascade = ALL, orphanRemoval = true)`, `@OrderBy("id ASC")`
- `SpEstimateDocument` -> `SpPartnerEstimateDocument`: `@OneToMany(mappedBy = "estimateDocument")` (cascade 없음)
- `SpEstimateItem` -> `SpPartnerEstimateItem`: `@OneToMany(cascade = ALL, orphanRemoval = true)`
- `SpEstimateItem` -> `SpPartnerEstimateItem` (선택): `@ManyToOne(selectedPartnerEstimateItem)`, FK ON DELETE SET NULL
- `SpPartnerEstimateDocument` -> `SpPartnerEstimateItem`: `@OneToMany(cascade = ALL)`
- 모든 `@ManyToOne` 연관관계는 `fetch = FetchType.LAZY` 적용

## Key Decisions

### 1. 단일 서비스 패턴
`SpEstimateService`가 견적서와 협력사 견적 양쪽의 모든 비즈니스 로직을 담당한다. 두 Resource 클래스(`SpEstimateResource`, `SpPartnerEstimateResource`)가 동일한 서비스를 주입받아 사용한다. 이는 견적서와 협력사 견적 사이의 트랜잭션 일관성을 보장하기 위한 설계이다.

### 2. Upsert 패턴
견적서 생성(`create()`) 시 `itId` 기준으로 기존 데이터가 있으면 수정, 없으면 생성하는 upsert 패턴을 전반적으로 사용한다. 상품(`G5ShopItem`), 장바구니(`G5ShopCart`), 견적서(`SpEstimateDocument`), 견적 항목(`SpEstimateItem`) 모두 동일한 패턴을 따른다.

### 3. 협력사 견적 항목의 UK 변경
초기에는 `(estimate_item_id, mb_no)`가 유니크 키였으나, `sp_partner_estimate_document` 도입 이후 `(estimate_item_id, partner_estimate_document_id)`로 변경되었다. 이는 하나의 협력사가 동일한 견적 항목에 대해 서로 다른 문서 컨텍스트에서 견적을 제출할 수 있도록 설계를 확장한 것이다.

### 4. 확정 가격 자동 채움 (confirmedPrice)
주문이 발생한 견적서를 조회(`getDetail()`, `getDetailByItId()`)할 때, `confirmedPrice`가 비어 있는 항목에 대해 자동으로 가격을 채운다. 우선순위는: (1) 선택된 협력사 견적의 `selectedPrice`, (2) 견적 항목 자체의 `selectedPrice`. 이 로직은 `fillConfirmedPriceIfNeeded()` 메서드에 구현되어 있다.

### 5. 다형성 파일 참조
`sp_file` 테이블은 `ref_type` + `ref_id`의 다형성 패턴으로 여러 엔티티의 첨부파일을 관리한다. 현재 사용되는 `ref_type` 값은:
- `estimate_document` -- 견적서 첨부파일
- `partner_estimate_item` -- 협력사 견적 항목 첨부파일

### 6. 가격 데이터의 JSON 텍스트 저장
`selected_price`, `analysis_meta`, `confirmed_price` 등 가격 관련 필드는 `TEXT` 타입 컬럼에 JSON 문자열로 저장된다. 응답 DTO에서는 Jackson `ObjectMapper`로 파싱하여 `Object` 타입으로 반환하며, 파싱 실패 시 원본 문자열을 그대로 반환한다 (`parseJson()` 메서드).

### 7. 견적서 상태의 상품 동기화
`updateStatus()` 호출 시 견적서의 `status` 변경과 함께 대응하는 `G5ShopItem.it24` 필드에도 동일한 상태값을 동기화한다.

### 8. QueryDSL 기반 동적 검색
복잡한 목록 조회 및 검색은 `SpEstimateDocumentRepositoryImpl`과 `SpPartnerEstimateItemRepositoryImpl`에서 QueryDSL의 `BooleanBuilder`를 사용하여 동적 조건을 구성한다. `Projections.constructor()`를 통해 DTO를 직접 프로젝션한다.

### 9. 협력사 삭제 시 정리 로직
협력사 견적 항목 삭제(`deletePartnerOrderBatch()`) 시:
- 해당 항목을 `selectedPartnerEstimateItem`으로 참조하는 견적 항목의 참조를 `null`로 설정
- 해당 항목의 첨부파일 삭제
- 협력사 견적서 문서에 남은 항목이 없으면 문서 자체도 삭제

## Gotchas

### 1. 견적서와 상품의 강결합
견적서 생성 시 `G5ShopItem`과 `G5ShopCart`를 함께 upsert 한다. 견적서만 독립적으로 생성할 수 없으며, 반드시 쇼핑몰 상품 체계와 연동된다.

### 2. itId 자동 생성
`SpEstimateCreateDTO.itId`가 비어 있으면 `System.currentTimeMillis()`를 문자열로 사용하여 자동 생성한다. 이는 동시 요청 시 충돌 가능성이 있으므로 주의가 필요하다.

### 3. 첨부파일 전체 교체 방식
견적서 생성/수정 시 첨부파일은 기존 파일을 모두 삭제 후 새로 저장하는 방식이다 (`spFileRepository.deleteByRefTypeAndRefId()` 호출 후 `saveAll()`). 부분 수정이 아닌 전체 교체 방식이므로, 클라이언트는 수정 시에도 유지할 파일을 포함하여 전체 목록을 전송해야 한다.

### 4. Unique Key 변경 히스토리
`sp_partner_estimate_item` 테이블의 UK가 `(estimate_item_id, mb_no)`에서 `(estimate_item_id, partner_estimate_document_id)`로 변경되었지만, 서비스 코드에서는 여전히 `findByEstimateItemIdAndMbNo()` 메서드를 사용하는 곳이 있다 (예: `deletePartnerOrderBatch()`). UK 변경 이후에도 이 조합으로 조회가 가능하도록 로직이 유지되고 있다.

### 5. 기본 상태값
서비스에서 정의된 기본 상태값은 `협력사 견적접수`(`DEFAULT_STATUS` 상수)이고, DDL에서의 기본값은 `협력사 견적요청`이다. 두 값이 다르므로 데이터 생성 경로에 따라 초기 상태가 달라질 수 있다.

### 6. 트랜잭션 범위
대부분의 조회 메서드에 `@Transactional` 또는 `@Transactional(readOnly = true)`가 적용되어 있다. 특히 `getDetail()` 메서드는 `fillConfirmedPriceIfNeeded()`에서 쓰기가 발생할 수 있어 `readOnly`가 아닌 `@Transactional`이 적용되어 있다.

### 7. 협력사 견적서 자동 생성
`createPartnerEstimateItem()` 호출 시 해당 `(estimate_document_id, mb_no)` 조합의 `SpPartnerEstimateDocument`가 없으면 자동으로 생성된다. 협력사 견적 항목 생성이 곧 협력사 견적서 생성을 트리거할 수 있다.

### 8. 배치 삭제의 cascade 정리
`deletePartnerOrderBatch()`는 단순 삭제가 아니라 다음의 연쇄 정리를 수행한다: (1) `selectedPartnerEstimateItem` 참조 해제, (2) 첨부파일 삭제, (3) 항목 삭제, (4) 빈 문서 삭제. 이 중 하나라도 실패하면 트랜잭션이 롤백된다.

## Sources

| 파일 경로 |
|-----------|
| `src/main/java/kr/co/samplepcb/xpse/resource/SpEstimateResource.java` |
| `src/main/java/kr/co/samplepcb/xpse/resource/SpPartnerEstimateResource.java` |
| `src/main/java/kr/co/samplepcb/xpse/service/SpEstimateService.java` |
| `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpEstimateDocument.java` |
| `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpEstimateItem.java` |
| `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpPartnerEstimateDocument.java` |
| `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpPartnerEstimateItem.java` |
| `src/main/java/kr/co/samplepcb/xpse/repository/SpEstimateDocumentRepository.java` |
| `src/main/java/kr/co/samplepcb/xpse/repository/SpEstimateDocumentRepositoryCustom.java` |
| `src/main/java/kr/co/samplepcb/xpse/repository/SpPartnerEstimateDocumentRepository.java` |
| `src/main/java/kr/co/samplepcb/xpse/repository/SpPartnerEstimateItemRepositoryCustom.java` |
| `src/main/java/kr/co/samplepcb/xpse/repository/SpPartnerEstimateItemRepositoryImpl.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpEstimateCreateDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpEstimateDetailDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpEstimateListDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpEstimateSearchParam.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpEstimateItemBaseDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpEstimatePartnerSelectionDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateDocUpdateDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateDocListDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateDocDetailDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateItemCreateDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateItemDetailDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateItemListDTO.java` |
| `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerEstimateItemSearchParam.java` |
| `src/main/resources/db/migration/create_estimate_tables.sql` |
| `src/main/resources/db/migration/create_sp_partner_estimate_document.sql` |
| `src/main/resources/db/migration/alter_sp_partner_estimate_item.sql` |
| `src/main/resources/db/migration/alter_sp_estimate_item_confirmed_price.sql` |
| `src/main/resources/db/migration/alter_sp_estimate_document_quantity.sql` |
