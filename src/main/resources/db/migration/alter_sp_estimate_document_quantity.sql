-- sp_estimate_document: 세트수량, 예비수량 컬럼 추가
ALTER TABLE sp_estimate_document
    ADD COLUMN set_quantity   INT DEFAULT NULL AFTER global_margin_rate,
    ADD COLUMN spare_quantity INT DEFAULT NULL AFTER set_quantity;
