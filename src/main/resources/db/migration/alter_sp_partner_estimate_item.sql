-- sp_partner_estimate_item 테이블에 주문 관련 컬럼 추가
ALTER TABLE sp_partner_estimate_item
    ADD COLUMN status VARCHAR(30) DEFAULT '협력사 견적요청' AFTER selected_price,
    ADD COLUMN memo TEXT NULL AFTER status;
