-- ============================================
-- sp_pcb_parts: (service_type, part_name) UNIQUE INDEX 추가
--
-- 목적:
--   외부 공급사(Digikey/UniKeyIC) 색인 시 동일 (service_type, part_name) 쌍의
--   중복 row 가 생기지 않도록 DB level 에서 보장한다.
--   ES race condition 으로 동시 insert 시도가 들어와도 RDB UNIQUE 가 차단하여
--   데이터 정합성을 유지한다.
--
-- MySQL 5.1.45 환경 고려사항:
--   - InnoDB 인덱스 prefix 한계: 767 bytes
--   - utf8 charset 기준 (3 bytes/char): service_type(50) + part_name(150) = 600 bytes 로 안전
--   - 정확매칭은 prefix 길이 안에서만 보장되지만, 부품번호는 보통 짧아 실무상 문제 없음
--
-- 사전 점검 (수동 실행):
--   다음 쿼리로 기존 중복 row 가 있는지 먼저 확인. 1건 이상이면 ALTER 가 실패하므로
--   중복 row 정리 후 ALTER 수행할 것.
--
--   SELECT service_type, part_name, COUNT(*) AS cnt
--   FROM sp_pcb_parts
--   GROUP BY service_type, part_name
--   HAVING cnt > 1;
-- ============================================

ALTER TABLE sp_pcb_parts
    ADD UNIQUE INDEX uk_sp_pcb_parts_servicetype_partname (service_type(50), part_name(150));
