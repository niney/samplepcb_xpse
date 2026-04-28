-- ============================================
-- sp_partner_estimate_item: external_synced_at 컬럼 추가
--
-- 목적:
--   외부 공급사(Digikey/UniKeyIC) selectedPrice 자동 동기화 시
--   TTL 가드(24h) 안에 들어온 재호출은 외부 API 호출/UPDATE 를 스킵하기 위함.
--   ES 캐시(application.yaml: ttl-hours=24) 와 동일 주기로 통일.
-- ============================================

ALTER TABLE sp_partner_estimate_item
    ADD COLUMN external_synced_at DATETIME NULL;
