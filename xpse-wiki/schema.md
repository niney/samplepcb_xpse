# Wiki Schema

This file defines the structure and conventions for this knowledge base wiki. It is generated on first compile and co-evolved between human and LLM on subsequent runs.

**Human:** You can edit this file to rename topics, merge them, add conventions, or change the article structure. The compiler will respect your changes on the next run.

**Compiler:** Read this file before classifying sources. Follow its conventions. Add new topics here when discovered. Never remove topics without human approval.

## Topics

- pcb-parts: 다소스 부품 데이터 수집, ES+JPA 이중 저장, 통합 검색 (Digikey, UniKeyIC, IC114)
- pcb-search-index: Kind/Column/Item/Unit 분류 체계, Elasticsearch 인덱스 관리, 벡터 유사도 검색
- sp-estimate: 견적서 생성, 협력사 견적 제출/선택, 주문 확정 업무 흐름
- sp-bom: BOM 문서 회원별 CRUD, contentHash 기반 upsert
- sp-order: G5 쇼핑몰 주문 조회, 협력사 발주 CRUD
- external-integration: Digi-Key, UniKeyIC, IC114, Google USE 연동, 병렬 검색, 이중 저장
- security: JWT 검증 전용 무상태 인증, opt-in @JwtAuth 어노테이션 모델
- infrastructure: Elasticsearch, MySQL, Caffeine Cache, QueryDSL, Spring Security 설정

## Concepts

- multi-level-caching: Caffeine 인메모리 캐시 + ES 색인 TTL 캐시 + (sp-estimate) PEI 단위 24h 동기화 가드의 다층 캐시 전략 — connects [pcb-parts, external-integration, infrastructure, sp-estimate]

## Article Structure

Each topic article follows this format (from `.wiki-compiler.json` `article_sections`):
- **Purpose** [coverage] -- 이 모듈/서비스가 하는 일과 의존하는 컴포넌트
- **Architecture** [coverage] -- 주요 파일, 구조, 진입점
- **Talks To** [coverage] -- 의존성, 통신 패턴, 서비스 간 호출
- **API Surface** [coverage] -- 엔드포인트, 외부 노출 함수 또는 인터페이스
- **Data** [coverage] -- 테이블, 컬렉션, 큐, 캐시, 소유 상태
- **Key Decisions** [coverage] -- 이렇게 구축한 이유, ADR 및 README 기반
- **Gotchas** [coverage] -- 알려진 이슈, 엣지 케이스, 장애 모드
- **Sources** -- 기여 파일에 대한 역링크

Coverage tags: `[coverage: high -- N sources]`, `[coverage: medium -- N sources]`, `[coverage: low -- N sources]`

## Naming Conventions

- Topic slugs: lowercase-kebab-case (e.g., `pcb-parts`, `sp-estimate`)
- Files: `{topic-slug}.md` in `topics/`
- Concept files: `{concept-slug}.md` in `concepts/`
- Dates: YYYY-MM-DD format everywhere
- Links: Markdown `[text](path)` with relative paths

## Cross-Reference Rules

- Topics that share 3+ sources should reference each other in their Purpose or Key Decisions sections
- Decisions that affect multiple topics get noted in each relevant topic's Key Decisions section
- When a gotcha applies to multiple topics, include it in each with a note

## Evolution Log

- 2026-04-14: Initial schema implied from first compile — 8 topics created
- 2026-04-15: Schema file generated. Added concept `multi-level-caching` from ES cache-first strategy changes
- 2026-04-29: `multi-level-caching` 컨셉에 sp-estimate 연결 추가 (sp_partner_estimate_item.external_synced_at 24h TTL 가드가 ES 캐시 위에 또 한 겹 캐시 레이어로 동작). 토픽/컨셉 신규 추가 없음.
