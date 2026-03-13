-- sp_partner_estimate_document 테이블 생성
CREATE TABLE sp_partner_estimate_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estimate_document_id BIGINT NOT NULL,
    mb_no INT NOT NULL,
    status VARCHAR(30) DEFAULT '협력사 견적요청',
    estimate_price INT NULL,
    memo TEXT NULL,
    write_date DATETIME NOT NULL,
    modify_date DATETIME NOT NULL,
    CONSTRAINT uk_sp_partner_estimate_doc UNIQUE (estimate_document_id, mb_no),
    CONSTRAINT fk_sp_partner_estimate_doc_document FOREIGN KEY (estimate_document_id) REFERENCES sp_estimate_document(id),
    CONSTRAINT fk_sp_partner_estimate_doc_member FOREIGN KEY (mb_no) REFERENCES g5_member(mb_no)
);

-- sp_partner_estimate_item: UK 변경 (estimate_item_id, mb_no) → (estimate_item_id, partner_estimate_document_id)
-- 기존 UK 제거
ALTER TABLE sp_partner_estimate_item DROP INDEX uk_sp_partner_estimate_item;

-- partner_estimate_document_id 컬럼 추가
ALTER TABLE sp_partner_estimate_item
    ADD COLUMN partner_estimate_document_id BIGINT NULL AFTER estimate_item_id;

-- 새 UK 추가
ALTER TABLE sp_partner_estimate_item
    ADD CONSTRAINT uk_sp_partner_estimate_item UNIQUE (estimate_item_id, partner_estimate_document_id);

-- FK 추가
ALTER TABLE sp_partner_estimate_item
    ADD CONSTRAINT fk_sp_partner_estimate_item_partner_doc
        FOREIGN KEY (partner_estimate_document_id) REFERENCES sp_partner_estimate_document(id);
