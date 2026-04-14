# 주문 관리 (sp-order)

## Purpose

samplepcb_xpse 프로젝트의 주문 관리 모듈은 두 가지 독립적인 주문 체계를 제공한다.

1. **G5 쇼핑몰 주문 (G5ShopOrder)** -- 그누보드5(G5) 쇼핑몰에서 발생한 고객 주문을 조회한다. 기존 G5 쇼핑몰 테이블(`g5_shop_order`, `g5_shop_cart`)을 읽기 전용으로 참조하며, 주문 생성/변경은 G5 쪽에서 처리한다.
2. **협력사 발주 (SpPartnerOrder)** -- 내부 견적서(`sp_estimate_document`)를 기반으로 협력사에게 부품 발주를 생성하고 관리한다. 견적 항목(BOM)별로 어떤 협력사에게 어떤 가격으로 발주했는지를 추적하는 것이 핵심이다.

두 체계는 서로 직접 참조하지 않지만, 공통적으로 `g5_shop_item`(아이템)과 `g5_member`(회원) 엔티티를 공유한다.

---

## Architecture

### 계층 구조

```
Resource (REST Controller)
  +-- G5ShopOrderResource        : /api/g5ShopOrders
  +-- SpPartnerOrderResource     : /api/spPartnerOrders
      |
Service
  +-- G5ShopOrderService         : G5 주문 검색 / 상세 조회
  +-- SpOrderService             : SP 주문(장바구니 기반) 검색
  +-- SpPartnerOrderService      : 협력사 발주 CRUD 및 상세 조회
      |
Repository
  +-- G5ShopOrderRepository / G5ShopOrderRepositoryCustom
  +-- G5ShopCartRepository / G5ShopCartRepositoryCustom / G5ShopCartRepositoryImpl
  +-- SpPartnerOrderDocumentRepository
  +-- SpPartnerOrderItemRepository
  +-- SpEstimateDocumentRepository / SpEstimateDocumentRepositoryCustom
  +-- SpEstimateItemRepository
  +-- SpPartnerEstimateDocumentRepository
  +-- SpPartnerEstimateItemRepository
```

### G5 쇼핑몰 주문 흐름

`G5ShopOrderResource` --> `G5ShopOrderService` --> `G5ShopOrderRepository`(QueryDSL 커스텀)

- 읽기 전용(`@Transactional(readOnly = true)`)으로만 동작한다.
- `G5ShopOrderSearchParam`의 `odStatus`, `mbId`, `caId`(기본값 `"41"`) 조건과 자유 검색어(`q`), 검색 필드(`field`)를 지원한다.
- 상세 조회 시 주문 PK(`odId`) 또는 아이템 ID(`itId`) 두 경로를 제공한다.

### SP 주문 검색 흐름 (장바구니 기반)

`SpPartnerOrderResource._search` --> `SpOrderService.search` --> `G5ShopCartRepository`(QueryDSL 커스텀)

- `G5ShopCart` 테이블을 직접 검색하여 장바구니 단위 주문 목록을 반환한다.
- QueryDSL(`G5ShopCartRepositoryImpl`)로 `G5ShopCart`, `G5ShopItem`, `G5Member`를 LEFT JOIN 후 조건 검색한다.
- ID 목록을 먼저 조회한 뒤 본 쿼리에서 `fetchJoin`하는 2단계 페이징 최적화를 적용한다.

### 협력사 발주 흐름

`SpPartnerOrderResource` --> `SpPartnerOrderService`

- **조회**: 발주서 ID 또는 아이템 ID(`itId`)로 상세 조회. 하나의 `itId`에 여러 협력사 발주서가 존재할 수 있으므로 `List<SpPartnerOrderDetailDTO>`를 반환한다.
- **생성**: `POST /_batch`로 `SpPartnerOrderItemCreateDTO` 리스트를 받아 일괄 생성한다. `EstimateItem`을 JOIN FETCH로 벌크 조회하고, Document 캐싱 upsert + Item 일괄 저장 패턴을 사용한다.
- **가격 계산**: 발주 생성 후 `calculateAndSetOrderPrice()`가 협력사 견적 항목(`SpPartnerEstimateItem`)의 `selectedPrice` JSON에서 `unitPrice * qty`를 합산하여 `SpPartnerOrderDocument.orderPrice`에 기록한다.

---

## Talks To

| 대상 모듈/엔티티 | 관계 | 설명 |
|---|---|---|
| `G5ShopItem` (`g5_shop_item`) | 참조 | 주문 아이템 정보 (제조사, 모델, 브랜드, 가격, 이미지 등) |
| `G5Member` (`g5_member`) | 참조 | 주문자/협력사 회원 정보 (이름, 연락처, 레벨, 은행 정보 등) |
| `SpEstimateDocument` (`sp_estimate_document`) | FK | 협력사 발주서가 참조하는 견적서 |
| `SpEstimateItem` (`sp_estimate_item`) | FK | 협력사 발주 항목이 참조하는 견적 항목(BOM 라인) |
| `SpPartnerEstimateDocument` | 읽기 | 발주 가격 계산 시 협력사 견적 문서 참조 |
| `SpPartnerEstimateItem` | 읽기 | 발주 가격 계산 시 `selectedPrice` JSON 파싱 |
| `SpEstimateService` | 호출 | `searchWithPartnerOrders()` 위임 -- 견적서 + 발주서 통합 검색 |
| JWT 인증 (`JwtAuth`, `JwtUserPrincipal`) | 보안 | 모든 API가 Bearer 토큰 인증 필요. `mbLevel == 10`이면 관리자, 아니면 `mbNo` 기반 협력사 필터 적용 |

---

## API Surface

### G5 쇼핑몰 주문 API (`/api/g5ShopOrders`)

| 메서드 | 경로 | 설명 | 요청 파라미터 / 바디 | 응답 |
|---|---|---|---|---|
| `GET` | `/_search` | 주문 목록 페이징 검색 | `Pageable` + `G5ShopOrderSearchParam`(`odStatus`, `mbId`, `caId`, `q`, `field`) | `CCPagingResult<G5ShopOrderListDTO>` |
| `GET` | `/{odId}` | 주문 상세 조회 (PK) | Path: `odId` (Long) | `CCObjectResult<G5ShopOrderDetailDTO>` |
| `GET` | `/byItId/{itId}` | 주문 상세 조회 (아이템 ID) | Path: `itId` (String) | `CCObjectResult<G5ShopOrderDetailDTO>` |

### 협력사 발주 API (`/api/spPartnerOrders`)

| 메서드 | 경로 | 설명 | 요청 파라미터 / 바디 | 응답 |
|---|---|---|---|---|
| `GET` | `/_search` | SP 주문(장바구니) 목록 검색 | `Pageable` + `SpOrderSearchParam`(`ctStatus`, `caId`, `q`, `field`) | `CCPagingResult<SpOrderListDTO>` |
| `GET` | `/_searchWithPartnerOrders` | 견적서 + 협력사 발주서 통합 검색 | `Pageable` + `SpEstimateSearchParam` + JWT principal | `CCPagingResult<SpEstimateListDTO>` |
| `GET` | `/{orderDocId}` | 협력사 발주서 상세 조회 (발주서 ID) | Path: `orderDocId` (Long) | `CCObjectResult<SpPartnerOrderDetailDTO>` |
| `GET` | `/byItId/{itId}` | 협력사 발주서 상세 조회 (아이템 ID) | Path: `itId` (String) + JWT principal | `CCObjectResult<List<SpPartnerOrderDetailDTO>>` |
| `POST` | `/_batch` | 협력사 발주 다중 생성 | Body: `List<SpPartnerOrderItemCreateDTO>` | `CCResult` |

### SpPartnerOrderItemCreateDTO 필드

| 필드명 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `estimateItemId` | `Long` | 필수 | 견적 항목 ID |
| `mbNo` | `int` | 필수 | 파트너 회원번호 |
| `selectedPrice` | `String` | 선택 | 선택된 가격 (JSON text) |
| `status` | `String` | 선택 | 발주 상태 (미입력 시 `"발주접수"`) |
| `memo` | `String` | 선택 | 메모 |
| `dateCode` | `String` | 선택 | 데이트 코드 |
| `deliveryDate` | `Date` | 선택 | 납기일 |

---

## Data

### G5 쇼핑몰 주문 테이블

#### `g5_shop_order` (엔티티: `G5ShopOrder`)

G5 쇼핑몰의 주문 마스터 테이블. PK는 `od_id` (long).

| 주요 컬럼 | 타입 | 설명 |
|---|---|---|
| `od_id` | BIGINT PK | 주문 ID |
| `mb_id` | VARCHAR | 회원 ID (FK -> `g5_member.mb_id`) |
| `od_name` | VARCHAR(100) | 주문자명 |
| `od_email` | VARCHAR(100) | 주문자 이메일 |
| `od_tel` / `od_hp` | VARCHAR(20) | 전화번호 / 휴대폰 |
| `od_zip1`, `od_zip2`, `od_addr1` ~ `od_addr3`, `od_addr_jibeon` | VARCHAR | 주문자 주소 |
| `od_b_name`, `od_b_tel`, `od_b_hp`, `od_b_zip1` ~ `od_b_addr_jibeon` | VARCHAR | 배송지 주소 |
| `od_cart_count` | INT | 장바구니 수량 |
| `od_cart_price` | INT | 장바구니 금액 |
| `od_send_cost` / `od_send_cost2` | INT | 배송비 |
| `od_receipt_price` | INT | 입금 금액 |
| `od_cancel_price` | INT | 취소 금액 |
| `od_refund_price` | INT | 환불 금액 |
| `od_status` | VARCHAR | 주문 상태 |
| `od_settle_case` | VARCHAR | 결제 방법 |
| `od_pg` / `od_tno` / `od_app_no` | VARCHAR | PG사 / 거래번호 / 승인번호 |
| `od_delivery_company` / `od_invoice` | VARCHAR | 택배사 / 송장번호 |
| `od_time` | DATETIME | 주문 시간 |
| `od_memo` | TEXT | 고객 메모 |
| `od_shop_memo` | TEXT | 관리자 메모 |
| `od_mod_history` | TEXT | 수정 이력 |
| `od_1` ~ `od_17` | VARCHAR(60) | 여분 확장 필드 |

연관 관계:
- `@ManyToOne` -> `G5Member` (via `mb_id`)
- `@OneToMany` -> `G5ShopCart` (mappedBy `shopOrder`)

#### `g5_shop_cart` (엔티티: `G5ShopCart`)

장바구니/주문 상품 항목 테이블. PK는 `ct_id` (int, auto_increment).

| 주요 컬럼 | 타입 | 설명 |
|---|---|---|
| `ct_id` | INT PK | 장바구니 ID |
| `od_id` | BIGINT NOT NULL | 주문 ID (FK -> `g5_shop_order.od_id`) |
| `mb_id` | VARCHAR | 회원 ID |
| `it_id` | VARCHAR(20) | 아이템 ID (FK -> `g5_shop_item.it_id`) |
| `it_name` | VARCHAR | 아이템명 |
| `ct_status` | VARCHAR | 장바구니 상태 |
| `ct_price` | INT | 가격 |
| `ct_qty` | INT | 수량 |
| `ct_option` | VARCHAR | 옵션 |
| `ct_point` | INT | 포인트 |
| `ct_send_cost` | INT | 배송비 |
| `ct_time` | DATETIME | 등록 시간 |
| `it_sc_type` / `it_sc_method` / `it_sc_price` | INT | 배송 관련 설정 |

연관 관계:
- `@ManyToOne(LAZY)` -> `G5ShopOrder` (via `od_id`)
- `@ManyToOne(LAZY)` -> `G5ShopItem` (via `it_id`)
- `@ManyToOne(LAZY)` -> `G5Member` (via `mb_id`)

#### `g5_shop_order_address` (엔티티: `G5ShopOrderAddress`)

배송지 주소록 테이블. PK는 `ad_id` (int, auto_increment).

| 주요 컬럼 | 타입 | 설명 |
|---|---|---|
| `ad_id` | INT PK | 주소 ID |
| `mb_id` | VARCHAR | 회원 ID |
| `ad_subject` | VARCHAR | 주소 제목 |
| `ad_default` | INT | 기본 주소 여부 |
| `ad_name` ~ `ad_jibeon` | VARCHAR | 수령인명, 전화번호, 주소 |

#### `g5_shop_order_data` (엔티티: `G5ShopOrderData`)

PG 결제 데이터 테이블. 복합키 `(od_id, cart_id)`.

| 주요 컬럼 | 타입 | 설명 |
|---|---|---|
| `od_id` | BIGINT PK | 주문 ID |
| `cart_id` | BIGINT PK | 장바구니 ID |
| `mb_id` | VARCHAR(20) | 회원 ID |
| `dt_pg` | VARCHAR | PG사 |
| `dt_data` | TEXT | PG 응답 데이터 |
| `dt_time` | DATETIME | 처리 시간 |

복합키 클래스: `G5ShopOrderDataId` (`Serializable` 구현, `equals`/`hashCode` 재정의)

#### `g5_shop_order_delete` (엔티티: `G5ShopOrderDelete`)

삭제된 주문 보관 테이블. PK는 `de_id` (int, auto_increment).

| 주요 컬럼 | 타입 | 설명 |
|---|---|---|
| `de_id` | INT PK | 삭제 ID |
| `de_key` | VARCHAR | 삭제 키 |
| `de_data` | LONGTEXT | 삭제된 주문 데이터 |
| `mb_id` | VARCHAR(20) | 삭제 실행 회원 |
| `de_ip` | VARCHAR | 삭제 IP |
| `de_datetime` | DATETIME | 삭제 시간 |

### 협력사 발주 테이블

#### `sp_partner_order_document` (엔티티: `SpPartnerOrderDocument`)

협력사 발주서 헤더 테이블. 하나의 견적서(`sp_estimate_document`) + 하나의 협력사(`mb_no`) 조합당 하나의 발주서가 존재한다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | BIGINT PK (AUTO_INCREMENT) | 발주서 ID |
| `estimate_document_id` | BIGINT NOT NULL | FK -> `sp_estimate_document(id)` |
| `mb_no` | INT NOT NULL | FK -> `g5_member(mb_no)` -- 협력사 회원번호 |
| `status` | VARCHAR(30) | 발주 상태 (기본값: `"발주접수"`) |
| `order_price` | INT | 발주 총금액 (`unitPrice * qty` 합산) |
| `memo` | TEXT | 메모 |
| `delivery_date` | DATETIME | 납기일 |
| `write_date` | DATETIME NOT NULL | 작성일 |
| `modify_date` | DATETIME NOT NULL | 수정일 |

제약조건:
- UNIQUE: `uk_sp_partner_order_doc (estimate_document_id, mb_no)` -- 견적서당 협력사 1건 보장
- FK: `fk_sp_partner_order_doc_document` -> `sp_estimate_document(id)`
- FK: `fk_sp_partner_order_doc_member` -> `g5_member(mb_no)`

연관 관계:
- `@ManyToOne(LAZY)` -> `SpEstimateDocument`
- `@ManyToOne(LAZY)` -> `G5Member` (read-only)
- `@OneToMany(cascade=ALL)` -> `SpPartnerOrderItem` (mappedBy `partnerOrderDocument`)

#### `sp_partner_order_item` (엔티티: `SpPartnerOrderItem`)

협력사 발주 항목 테이블. 견적 항목(BOM 라인)별로 발주 상태를 추적한다.

| 컬럼 | 타입 | 설명 |
|---|---|---|
| `id` | BIGINT PK (AUTO_INCREMENT) | 발주 항목 ID |
| `estimate_item_id` | BIGINT NOT NULL | FK -> `sp_estimate_item(id)` |
| `partner_order_document_id` | BIGINT | FK -> `sp_partner_order_document(id)` |
| `mb_no` | INT NOT NULL | FK -> `g5_member(mb_no)` -- 협력사 회원번호 |
| `selected_price` | TEXT | 선택된 가격 (JSON text) |
| `status` | VARCHAR(30) | 항목별 발주 상태 |
| `memo` | TEXT | 메모 |
| `write_date` | DATETIME NOT NULL | 작성일 |
| `date_code` | VARCHAR(100) | 데이트 코드 |
| `delivery_date` | DATETIME | 납기일 |
| `modify_date` | DATETIME NOT NULL | 수정일 |

제약조건:
- UNIQUE: `uk_sp_partner_order_item (estimate_item_id, partner_order_document_id)` -- 견적 항목 + 발주서 조합당 1건
- FK: `fk_sp_partner_order_item_estimate` -> `sp_estimate_item(id)`
- FK: `fk_sp_partner_order_item_partner_doc` -> `sp_partner_order_document(id)`
- FK: `fk_sp_partner_order_item_member` -> `g5_member(mb_no)`

### 레거시 마이그레이션 SQL (`alter_sp_partner_order.sql`)

기존 `sp_partner_order` 테이블에 대한 스키마 변경:
- `status` 기본값을 `'협력사 견적요청'`으로 변경
- `price`, `forwarder`, `shipping`, `tracking`, `estimate_file1_subj`, `estimate_file1`, `memo` 컬럼을 NULL 허용으로 변경
- `sp_estimate_item.qty`를 `INT NULL`로 변경
- `uk_sp_partner_order_it_partner (it_id, partner_mb_no)` 유니크 인덱스 추가
- `g5_shop_item(it_id)` 및 `g5_member(mb_no)` FK 추가 (CASCADE DELETE)

### DTO 구조

| DTO 클래스 | 용도 | 주요 필드 |
|---|---|---|
| `G5ShopOrderListDTO` | G5 주문 목록 | `odId`, `mbId`, `odName`, `odStatus`, `odCartPrice`, `odReceiptPrice`, `odDeliveryCompany`, `odInvoice`, `odTime`, `itId`(첫 번째 cart), 회원 정보 |
| `G5ShopOrderDetailDTO` | G5 주문 상세 | 주문자/배송지 주소, 결제 정보, PG 정보, 택배 정보, `List<CartItem>`, 회원 정보 |
| `G5ShopOrderDetailDTO.CartItem` | 장바구니 항목 | `ctId`, `itId`, `itName`, `ctStatus`, `ctPrice`, `ctQty`, `ctOption`, `ctPoint`, `ctSendCost` |
| `SpOrderListDTO` | SP 장바구니 주문 목록 | `ctId`, `odId`, `itId`, `itName`, `ctPrice`, `ctStatus`, `ctQty`, 아이템 정보(제조사/모델/브랜드/가격/ETA/견적상태), 회원 정보 |
| `SpPartnerOrderDetailDTO` | 협력사 발주서 상세 | 견적서 ID/상태, 발주서 ID/상태/총금액/납기일, `List<ItemDTO>` |
| `SpPartnerOrderDetailDTO.ItemDTO` | 발주 항목 상세 | `estimateItemId`, `pcbPartDocId`, `qty`, `analysisMeta`(JSON), `selectedPrice`(JSON), 부품 정보, 발주 항목 정보(`orderItemId`, `orderStatus`, `orderMemo`, `orderDateCode` 등) |
| `SpPartnerOrderItemCreateDTO` | 발주 항목 생성 요청 | `estimateItemId`, `mbNo`, `selectedPrice`, `status`, `memo`, `dateCode`, `deliveryDate` |

### 검색 파라미터

| 파라미터 클래스 | 상위 클래스 | 고유 필드 | 설명 |
|---|---|---|---|
| `SearchParam` | -- | `q`, `field` | 기본 검색 파라미터 |
| `G5ShopOrderSearchParam` | `SearchParam` | `odStatus`, `mbId`, `caId`(기본 `"41"`) | G5 주문 검색 |
| `SpOrderSearchParam` | `SearchParam` | `ctStatus`, `caId` | SP 장바구니 주문 검색 |

---

## Key Decisions

1. **G5 주문은 읽기 전용**: G5ShopOrder 관련 모든 서비스 메서드에 `@Transactional(readOnly = true)`가 적용되어 있다. 주문 생성/수정/삭제는 G5 쇼핑몰 플랫폼에서 처리하며, 이 시스템은 조회만 담당한다.

2. **Document-Item 2단 구조**: 협력사 발주는 `SpPartnerOrderDocument`(발주서 헤더) + `SpPartnerOrderItem`(발주 항목) 2단 구조이다. 하나의 견적서 + 하나의 협력사 조합에 하나의 Document가 대응하고(`UNIQUE(estimate_document_id, mb_no)`), 그 아래 견적 항목(BOM 라인)별 Item이 매달린다.

3. **배치 생성 시 Document 캐싱 upsert**: `createPartnerOrderBatch()` 에서 `docKey = estimateDocumentId + "_" + mbNo` 로 `HashMap` 캐싱하여, 동일 견적서+협력사 조합의 Document를 한 번만 조회/생성한다. 이미 존재하면 기존 것을 재사용하고, 없으면 새로 생성 후 캐시에 넣는다.

4. **가격 자동 계산**: 발주 생성 후 `calculateAndSetOrderPrice()`가 호출되어, 해당 협력사의 견적 항목(`SpPartnerEstimateItem.selectedPrice`)에서 `unitPrice * qty`를 파싱/합산하여 `orderPrice`에 기록한다. `selectedPrice`는 JSON 형식이며 `SelectedPrice` record(`unitPrice: Integer`, `qty: Integer`)로 역직렬화한다.

5. **QueryDSL 2단계 페이징**: `G5ShopCartRepositoryImpl`에서 먼저 조건에 맞는 ID 목록만 페이징 조회한 뒤, 해당 ID로 `fetchJoin` 본 쿼리를 실행한다. 이는 OneToMany 관계에서 페이징 + fetchJoin 시 발생하는 N+1 문제를 회피하는 패턴이다.

6. **권한 기반 데이터 필터링**: `SpPartnerOrderResource`에서 `JwtUserPrincipal.mbLevel != 10`(비관리자)이면 해당 사용자의 `mbNo`로 데이터를 필터링한다. 관리자(`mbLevel == 10`)는 전체 데이터에 접근 가능하다.

7. **기본 상태값**: `SpPartnerOrderService.DEFAULT_STATUS`는 `"발주접수"`이다. 항목 생성 시 `status`가 비어있으면 이 값이 적용된다. 레거시 `sp_partner_order` 테이블의 기본 상태는 `'협력사 견적요청'`으로, 두 테이블의 기본 상태가 다르다.

8. **SpOrderListDTO에서 견적 상태 매핑**: `SpOrderListDTO.from()`에서 `G5ShopItem.getIt24()`를 `itemEstimateStatus`로 매핑한다. `it_24`는 G5 확장 필드로, 견적 상태를 저장하는 용도로 활용되고 있다.

---

## Gotchas

1. **G5ShopOrderSearchParam.caId 기본값**: `caId`의 기본값이 `"41"`로 하드코딩되어 있다. 카테고리를 명시하지 않으면 항상 `caId = "41"` 조건이 적용되므로, 전체 카테고리 검색 시 `caId`를 빈 문자열이나 null로 명시해야 한다.

2. **selectedPrice JSON 파싱 실패 시 BusinessException**: `calculateAndSetOrderPrice()` 에서 `selectedPrice`가 null/blank이거나 `unitPrice`/`qty`가 없으면 `BusinessException`이 발생하여 전체 배치 트랜잭션이 롤백된다. 부분 실패 허용이 필요하면 별도 처리가 필요하다.

3. **G5 테이블과 SP 테이블의 ID 체계 차이**: G5 테이블은 `mb_id`(문자열)로 회원을 식별하고, SP 협력사 발주 테이블은 `mb_no`(정수)로 식별한다. 두 체계 간 조인 시 `g5_member` 테이블의 `mb_id`와 `mb_no` 양쪽 컬럼을 사용해야 한다.

4. **G5ShopOrderDataId 복합키**: `G5ShopOrderData`는 `@IdClass(G5ShopOrderDataId.class)`를 사용하는 복합 기본키 구조이다. JPA에서 이 엔티티를 조회할 때 반드시 `G5ShopOrderDataId` 인스턴스를 사용해야 한다.

5. **SpPartnerOrderDocument의 orderPrice 지연 계산**: `orderPrice`는 발주 생성 직후에 계산되지만, 이후 견적 항목 변경 시 자동으로 재계산되지 않는다. 견적 가격이 변경되면 발주서의 `orderPrice`와 불일치가 발생할 수 있다.

6. **레거시 sp_partner_order 테이블 병존**: `alter_sp_partner_order.sql`은 기존 `sp_partner_order` 테이블을 변경하는 마이그레이션이다. 현재 코드는 새로운 `sp_partner_order_document` + `sp_partner_order_item` 구조를 사용하므로, 레거시 테이블과의 데이터 마이그레이션 상태를 확인해야 한다.

7. **G5ShopOrder의 od_1 ~ od_17 확장 필드**: 그누보드 확장 컬럼으로, 각 프로젝트마다 용도가 다를 수 있다. 현재 코드에서는 DTO 매핑에 사용되지 않지만, 직접 쿼리 시 해당 필드에 비즈니스 데이터가 들어있을 수 있다.

8. **fetchJoin 페이징 트릭의 한계**: `G5ShopCartRepositoryImpl`의 2단계 페이징은 결과 수가 적을 때 효과적이지만, `ids` 리스트가 매우 클 경우 `IN` 절의 크기 제한에 걸릴 수 있다. 실제로는 `pageable.getPageSize()`로 제한되므로 일반적인 사용에서는 문제가 없다.

---

## Sources

| 파일 | 경로 |
|---|---|
| `G5ShopOrderResource` | `src/main/java/kr/co/samplepcb/xpse/resource/G5ShopOrderResource.java` |
| `SpPartnerOrderResource` | `src/main/java/kr/co/samplepcb/xpse/resource/SpPartnerOrderResource.java` |
| `G5ShopOrderService` | `src/main/java/kr/co/samplepcb/xpse/service/G5ShopOrderService.java` |
| `SpOrderService` | `src/main/java/kr/co/samplepcb/xpse/service/SpOrderService.java` |
| `SpPartnerOrderService` | `src/main/java/kr/co/samplepcb/xpse/service/SpPartnerOrderService.java` |
| `G5ShopOrder` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopOrder.java` |
| `G5ShopOrderAddress` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopOrderAddress.java` |
| `G5ShopOrderData` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopOrderData.java` |
| `G5ShopOrderDataId` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopOrderDataId.java` |
| `G5ShopOrderDelete` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopOrderDelete.java` |
| `G5ShopCart` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopCart.java` |
| `G5ShopItem` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5ShopItem.java` |
| `G5Member` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/G5Member.java` |
| `SpPartnerOrderDocument` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpPartnerOrderDocument.java` |
| `SpPartnerOrderItem` | `src/main/java/kr/co/samplepcb/xpse/domain/entity/SpPartnerOrderItem.java` |
| `G5ShopOrderRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/G5ShopOrderRepository.java` |
| `G5ShopCartRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/G5ShopCartRepository.java` |
| `G5ShopCartRepositoryCustom` | `src/main/java/kr/co/samplepcb/xpse/repository/G5ShopCartRepositoryCustom.java` |
| `G5ShopCartRepositoryImpl` | `src/main/java/kr/co/samplepcb/xpse/repository/G5ShopCartRepositoryImpl.java` |
| `G5ShopItemRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/G5ShopItemRepository.java` |
| `SpPartnerOrderDocumentRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/SpPartnerOrderDocumentRepository.java` |
| `SpPartnerOrderItemRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/SpPartnerOrderItemRepository.java` |
| `SpEstimateDocumentRepository` | `src/main/java/kr/co/samplepcb/xpse/repository/SpEstimateDocumentRepository.java` |
| `SpOrderSearchParam` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpOrderSearchParam.java` |
| `SpOrderListDTO` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpOrderListDTO.java` |
| `G5ShopOrderSearchParam` | `src/main/java/kr/co/samplepcb/xpse/pojo/G5ShopOrderSearchParam.java` |
| `G5ShopOrderListDTO` | `src/main/java/kr/co/samplepcb/xpse/pojo/G5ShopOrderListDTO.java` |
| `G5ShopOrderDetailDTO` | `src/main/java/kr/co/samplepcb/xpse/pojo/G5ShopOrderDetailDTO.java` |
| `SpPartnerOrderDetailDTO` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerOrderDetailDTO.java` |
| `SpPartnerOrderItemCreateDTO` | `src/main/java/kr/co/samplepcb/xpse/pojo/SpPartnerOrderItemCreateDTO.java` |
| DDL: 발주 테이블 생성 | `src/main/resources/db/migration/create_sp_partner_order_document.sql` |
| DDL: 레거시 발주 변경 | `src/main/resources/db/migration/alter_sp_partner_order.sql` |
