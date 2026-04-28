# samplepcb_xpse Wiki

> Spring Boot 4.1 / Java 21 기반 PCB 부품 검색 및 견적 관리 시스템

Last compiled: 2026-04-29
Total topics: 8 | Total concepts: 1 | Total sources: 160+

## 토픽

### 핵심 도메인

| 토픽 | Also Known As | 설명 |
|------|--------------|------|
| [PCB 부품](topics/pcb-parts.md) | PcbParts, 부품 검색, multi-search | 다소스 부품 데이터 수집, ES+JPA 이중 저장, 통합 검색 (Digikey, UniKeyIC, IC114) |
| [PCB 검색 인덱스](topics/pcb-search-index.md) | PcbKind, PcbColumn, PcbItem, 분류 체계 | Kind/Column/Item/Unit 분류 체계, Elasticsearch 인덱스 관리, 벡터 유사도 검색 |
| [견적 관리](topics/sp-estimate.md) | SpEstimate, 견적서, partner estimate | 견적서 생성, 협력사 견적 제출/선택, 주문 확정 업무 흐름 |
| [BOM 문서 관리](topics/sp-bom.md) | SpBom, BOM 문서 | BOM 문서 회원별 CRUD, contentHash 기반 upsert |
| [주문 관리](topics/sp-order.md) | SpOrder, G5ShopOrder, 발주 | G5 쇼핑몰 주문 조회, 협력사 발주 CRUD |

### 외부 연동

| 토픽 | Also Known As | 설명 |
|------|--------------|------|
| [외부 부품 API 연동](topics/external-integration.md) | Digikey, UniKeyIC, IC114, API 연동 | Digi-Key, UniKeyIC, IC114, Google USE 연동, 병렬 검색, 이중 저장 |

### 인프라

| 토픽 | Also Known As | 설명 |
|------|--------------|------|
| [인증/인가](topics/security.md) | JWT, JwtAuth, Spring Security | JWT 검증 전용 무상태 인증, opt-in `@JwtAuth` 어노테이션 모델 |
| [설정/인프라](topics/infrastructure.md) | Config, ES설정, Cache, QueryDSL | Elasticsearch, MySQL, Caffeine Cache, QueryDSL, Spring Security 설정 |

## Concepts

| Concept | Connects | Last Updated |
|---------|----------|-------------|
| [다층 캐싱 전략](concepts/multi-level-caching.md) | pcb-parts, external-integration, infrastructure, sp-estimate | 2026-04-29 |

## 기술 스택

- **Framework**: Spring Boot 4.1.0-SNAPSHOT, Java 21
- **검색 엔진**: Elasticsearch (Spring Data Elasticsearch)
- **데이터베이스**: MySQL 5.x (Spring Data JPA, QueryDSL)
- **캐시**: Caffeine (Spring Cache) + ES 색인 TTL 캐시
- **보안**: JWT (jjwt 0.13), Spring Security
- **API 문서**: SpringDoc OpenAPI 3.0
- **매핑**: MapStruct 1.6
- **비동기**: WebFlux/WebClient (Mono.zip 병렬 검색)
- **파일 처리**: Apache POI 5.5 (엑셀)

## 주요 Elasticsearch 인덱스

| 인덱스 | 용도 |
|--------|------|
| `pcbparts` | PCB 부품 검색 (메인) |
| `pcbkind` | 부품 종류 분류 |
| `pcbcolumn` | 부품 컬럼/속성 분류 (벡터 검색 포함) |
| `pcbitem` | 부품 아이템 분류 |
| `nondigikeyparts` | Digikey 외 소스 부품 |

## 주요 JPA 테이블

| 테이블 | 용도 |
|--------|------|
| `sp_pcb_parts` | PCB 부품 마스터 |
| `sp_pcb_parts_price` | 부품 가격 |
| `sp_estimate_document` | 견적서 |
| `sp_estimate_item` | 견적 항목 |
| `sp_partner_estimate_document` | 협력사 견적서 |
| `sp_partner_order_document` | 협력사 발주서 |
| `sp_bom_document` | BOM 문서 |
| `g5_shop_order` | G5 쇼핑몰 주문 (레거시) |

## Recent Changes

- 2026-04-29: pcb-parts(외부 일괄검색·first-hit·UNIQUE 제약·lastModifiedDate touch), external-integration(searchExternalBatch + searchMultiSourceFirstHit), sp-estimate(필수 협력사 PED/PEI 자동 보장 + 외부 selectedPrice 24h 동기화) 업데이트. multi-level-caching 컨셉에 sp-estimate 인스턴스 추가.
- 2026-04-15: pcb-parts, external-integration, infrastructure 토픽 업데이트 — ES 캐시 우선 전략 반영. concept `multi-level-caching` 생성. schema.md 초기 생성.
- 2026-04-14: 최초 컴파일 — 8개 토픽 생성
