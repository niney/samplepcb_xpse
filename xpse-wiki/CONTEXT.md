# samplepcb_xpse 위키 네비게이션 가이드

> 이 파일은 LLM이 코드베이스를 탐색할 때 참조하는 가이드입니다.
> CLAUDE.md에서 이 파일을 참조하면 대화 시작 시 프로젝트 전체 구조를 빠르게 파악할 수 있습니다.

## 프로젝트 개요

samplepcb_xpse는 PCB 부품 검색 및 견적/발주 관리를 위한 Spring Boot 백엔드 서비스입니다.
다수의 외부 부품 API(Digi-Key, UniKeyIC, IC114)에서 데이터를 수집하고, Elasticsearch와 MySQL에 이중 저장하여 통합 검색을 제공합니다.

## 패키지 구조

```
kr.co.samplepcb.xpse
├── config/          → Elasticsearch, Cache, Security, QueryDSL, Web 설정
├── domain/
│   ├── document/    → Elasticsearch 문서 (PcbPartsSearch, PcbKindSearch 등)
│   └── entity/      → JPA 엔티티 (PcbParts, SpEstimateDocument, G5ShopOrder 등)
├── exception/       → 예외 처리
├── mapper/          → MapStruct 매퍼
├── pojo/            → DTO, ViewModel, 검색 필드 상수
├── repository/      → ES Repository + JPA Repository (QueryDSL 커스텀 포함)
├── resource/        → REST 컨트롤러 (11개)
├── security/        → JWT 인증 필터 및 토큰 프로바이더
├── service/         → 비즈니스 로직
│   └── common/sub/  → 공통 서브 서비스 (파싱, 변환, 엑셀)
└── util/            → 유틸리티 (Elasticsearch, 문자열, ID 생성)
```

## 토픽별 빠른 참조

질문이나 작업의 맥락에 따라 적절한 위키 토픽을 참조하세요:

| 이런 질문/작업이라면 | 이 토픽을 보세요 |
|---------------------|-----------------|
| 부품 검색, 가격, 스펙, 멀티소스 검색 | [pcb-parts](topics/pcb-parts.md) |
| Kind/Column/Item 분류, 벡터 검색, 인덱스 매핑 | [pcb-search-index](topics/pcb-search-index.md) |
| 견적서 생성/수정, 협력사 견적, 견적 상태 흐름 | [sp-estimate](topics/sp-estimate.md) |
| BOM 문서 업로드, contentHash, 파일 관리 | [sp-bom](topics/sp-bom.md) |
| 주문 조회, 장바구니, 협력사 발주 | [sp-order](topics/sp-order.md) |
| Digikey/UniKeyIC/IC114 API, 데이터 파싱, 병렬 검색 | [external-integration](topics/external-integration.md) |
| JWT 인증, @JwtAuth, 권한 체크, CORS | [security](topics/security.md) |
| ES 설정, 캐시, DB 연결, 프로파일, 매핑 파일 | [infrastructure](topics/infrastructure.md) |

## 핵심 데이터 흐름

### 부품 검색 흐름
```
사용자 요청 → PcbPartsResource → PcbPartsService
  ├── ES 검색 (PcbPartsSearch)
  └── 멀티소스 검색 (PcbPartsMultiSearchService)
       ├── Digi-Key API (WebClient)
       ├── UniKeyIC API (WebClient)
       └── IC114 (ExcelSubService)
       → Mono.zip 병렬 실행 → ES + JPA 이중 저장
```

### 견적 → 발주 흐름
```
견적서 생성 (SpEstimateDocument)
  → 협력사 선택/배정 (SpPartnerEstimateDocument)
  → 협력사 견적 제출 (SpPartnerEstimateItem)
  → 견적 확정 (confirmedPrice)
  → 발주 생성 (SpPartnerOrderDocument)
```

## 인증 모델

- **기본: 인증 불필요** — 대부분의 검색 API는 공개
- **opt-in 인증** — `@JwtAuth` 어노테이션 또는 `@AuthenticationPrincipal JwtUserPrincipal` 파라미터가 있는 엔드포인트만 JWT 검증
- **관리자 판별** — `mbLevel >= 10`이면 `ROLE_ADMIN` 부여

## 주의사항 (공통)

1. **ES + JPA 이중 저장**: 트랜잭션 경계가 다르므로 데이터 불일치 가능
2. **Mono.block() 사용**: WebFlux의 비동기 호출을 동기 블로킹으로 처리하는 코드 존재
3. **레거시 G5 테이블**: `g5_shop_*` 테이블은 외부 쇼핑몰 스키마로 읽기 전용 사용
4. **매핑 파일 규약**: `src/main/resources/mapping/*.txt` 첫 줄은 인덱스 설정, 이후 필드 매핑
5. **프로파일**: `dev`(로컬), `prod`(운영) — ES/DB/API URL이 다름
6. **이중 캐싱**: Caffeine(30분) + ES 색인 TTL(24시간) 이중 캐시 — [multi-level-caching](concepts/multi-level-caching.md) 참조

## Stats
Compiled: 2026-04-29 | Topics: 8 | Concepts: 1 | Sources: 165+ | Auto-updates on session start
