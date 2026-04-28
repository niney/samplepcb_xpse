# Wiki Compile Log

## 2026-04-29 — 증분 컴파일

- **분석 커밋 범위**: 6903133..eba88b1 (9개 커밋, 2026-04-22 ~ 2026-04-29)
- **토픽 업데이트**: 3개
  - pcb-parts: API Surface 에 `_savePart`/`_saveParts`/`_searchExternalBatch`/`_searchMultiSourceFirstHit` 추가, `(service_type, part_name)` UNIQUE 제약 + DataIntegrityViolationException 폴백 / 동일 데이터 시 `lastModifiedDate` touch 의사결정 추가
  - external-integration: 멀티 소스 검색 모드 3종 (병렬 / first-hit / 외부 일괄 batch) 흐름 정리, `_searchMultiSourceFirstHit` / `_searchExternalBatch` 엔드포인트 추가, partName 단위 에러 격리 정책 반영
  - sp-estimate: 필수 협력사(Digikey 6035 / UniKeyIC 6036) PED+PEI 자동 보장(`ensureRequiredPartners`), 외부 시세 자동 동기화(`syncExternalSelectedPrices`, 24h TTL, `external_synced_at` 컬럼) 의사결정 + Gotchas 추가, `SelectedPriceCalculator`/`SelectedPriceVO` 헬퍼 등록
- **컨셉 업데이트**: 1개
  - multi-level-caching: 연결 토픽에 `sp-estimate` 추가, instance 2건 추가 (lastModifiedDate touch / external_synced_at TTL 가드)
- **신규 토픽/컨셉**: 없음
- **변경 소스**: 12개 (PcbPartsResource, PcbPartsService, PcbPartsRepository, PcbPartsMultiSearchService, PcbPartsMultiSearchServiceTest, PcbPartsExternalBatchResult, SpEstimateService, SpPartnerEstimateItem, SpPartnerEstimateItemRepository, SelectedPriceCalculator, SelectedPriceVO, alter_sp_pcb_parts_unique_key.sql, alter_sp_partner_estimate_item_external_synced_at.sql)

## 2026-04-15 — 증분 컴파일

- **커밋 분석**: c6fe828 (ES 캐시 우선 전략)
- **토픽 업데이트**: 3개
  - pcb-parts: 다중 소스 검색 흐름에 ES 캐시 우선 전략 추가, Key Decisions #4 확장, #8(이중 캐싱) 신규
  - external-integration: 멀티 소스 검색 흐름 업데이트, Key Decision #7(캐시 전략) 이중 캐시로 확장
  - infrastructure: ApplicationProperties에 ExternalCache 추가, CacheConfig 갱신
- **신규 컨셉**: 1개
  - multi-level-caching: Caffeine + ES TTL 이중 캐싱 전략
- **신규 메타데이터**: schema.md 초기 생성
- **변경 소스**: 6개 (ApplicationProperties, CacheConfig, PcbPartsMultiSearchService, DigikeySubService, application.yaml, PcbPartsMultiSearchServiceTest)

## 2026-04-14 — 최초 컴파일

- **모드**: codebase
- **토픽**: 8개 생성
  - pcb-parts (22 소스)
  - pcb-search-index (22 소스)
  - sp-estimate (30 소스)
  - sp-bom (17 소스)
  - sp-order (30 소스)
  - external-integration (23 소스)
  - security (6 소스)
  - infrastructure (26 소스)
- **Java 파일**: 154개 분석
- **생성 파일**: INDEX.md, CONTEXT.md, 8개 토픽 문서
