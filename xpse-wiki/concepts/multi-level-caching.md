---
concept: 다층 캐싱 전략 (Caffeine + ES TTL)
last_compiled: 2026-04-15
topics_connected: [pcb-parts, external-integration, infrastructure]
status: active
---

# 다층 캐싱 전략 (Caffeine + ES TTL)

## Pattern

외부 부품 API(Digi-Key, UniKeyIC) 호출 비용과 지연을 줄이기 위해, 인메모리 캐시와 검색 인덱스 캐시를 조합하는 이중 캐싱 전략이 반복적으로 적용된다. 핵심은 **ES 인덱스 자체를 장기 캐시로 활용**하는 점이다 — 외부 API에서 가져온 부품 데이터를 ES에 색인할 때, 그 색인 데이터가 단순 저장이 아니라 다음 검색 요청의 캐시 소스로 재활용된다.

이 패턴은 일반적인 인메모리 캐시(Caffeine)와는 다른 시간 스케일에서 작동한다. Caffeine 캐시는 30분 TTL로 "같은 검색어의 반복 호출"을 최적화하고, ES 캐시는 24시간 TTL로 "비슷한 시간대의 다른 검색 경로에서 동일 부품을 다시 만나는 경우"를 최적화한다. 서로 다른 시간 창에서 서로 다른 종류의 중복 호출을 차단하는 보완적 구조이다.

## Instances

- **2026-04-15** in [pcb-parts](../topics/pcb-parts.md): `PcbPartsMultiSearchService.findFreshCachedResults()`가 Digikey/UniKeyIC 검색 전에 ES 인덱스를 캐시로 확인한다. `lastModifiedDate` 기반으로 24시간 TTL을 판별하며, 캐시 히트 시 외부 API 호출을 완전히 생략한다.
- **2026-04-15** in [external-integration](../topics/external-integration.md): `DigikeySubService.searchByKeyword()`에 `@Cacheable(searchResults)` 적용으로 Caffeine 인메모리 캐시(500건/30분)가 동일 키워드 반복 호출을 처리한다. ES TTL 캐시는 Caffeine 만료 후에도 API 호출을 방지한다.
- **2026-04-15** in [infrastructure](../topics/infrastructure.md): `CacheConfig`에서 Caffeine 비동기 캐시 관리, `ApplicationProperties.ExternalCache.ttlHours`에서 ES 캐시 TTL 제어. 두 캐시 계층의 설정이 서로 다른 설정 클래스에 분산되어 있다.

## What This Means

ES를 캐시 레이어로 활용하는 설계는 "이중 저장"의 부산물이 아니라 의도된 최적화이다. 외부 API 호출 비용(금전적 비용 + 지연)이 높은 부품 도메인에서, 이미 색인된 데이터를 재활용하는 것은 자연스러운 선택이다. 다만 이 전략은 두 가지 전제에 의존한다:

1. **부품 데이터의 변경 빈도가 낮다** — 24시간 TTL이 적절한 것은 부품 스펙/가격이 자주 바뀌지 않기 때문이다. 실시간 재고/가격이 중요해지면 TTL을 줄여야 한다.
2. **ES 색인 데이터가 API 응답과 동일한 정보를 담고 있다** — 파싱 과정에서 정보 손실이 발생하면 캐시 히트 시 API 직접 호출 대비 정보가 부족해질 수 있다.

향후 캐시 레벨 추가(예: Redis)보다는, 현재 두 레벨의 TTL 조정과 캐시 키 설계를 최적화하는 방향이 복잡도 대비 효과적이다.

## Sources

- [pcb-parts](../topics/pcb-parts.md)
- [external-integration](../topics/external-integration.md)
- [infrastructure](../topics/infrastructure.md)
