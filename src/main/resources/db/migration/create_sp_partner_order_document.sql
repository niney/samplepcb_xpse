-- sp_partner_order_document 테이블 생성
CREATE TABLE sp_partner_order_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estimate_document_id BIGINT NOT NULL,
    mb_no INT NOT NULL,
    status VARCHAR(30) NULL,
    order_price INT NULL,
    memo TEXT NULL,
    delivery_date DATETIME NULL,
    write_date DATETIME NOT NULL,
    modify_date DATETIME NOT NULL,
    CONSTRAINT uk_sp_partner_order_doc UNIQUE (estimate_document_id, mb_no),
    CONSTRAINT fk_sp_partner_order_doc_document FOREIGN KEY (estimate_document_id) REFERENCES sp_estimate_document(id),
    CONSTRAINT fk_sp_partner_order_doc_member FOREIGN KEY (mb_no) REFERENCES g5_member(mb_no)
);

-- sp_partner_order_item 테이블 생성
CREATE TABLE sp_partner_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    estimate_item_id BIGINT NOT NULL,
    partner_order_document_id BIGINT NULL,
    mb_no INT NOT NULL,
    selected_price TEXT NULL,
    status VARCHAR(30) NULL,
    memo TEXT NULL,
    write_date DATETIME NOT NULL,
    date_code VARCHAR(100) NULL,
    delivery_date DATETIME NULL,
    modify_date DATETIME NOT NULL,
    CONSTRAINT uk_sp_partner_order_item UNIQUE (estimate_item_id, partner_order_document_id),
    CONSTRAINT fk_sp_partner_order_item_estimate FOREIGN KEY (estimate_item_id) REFERENCES sp_estimate_item(id),
    CONSTRAINT fk_sp_partner_order_item_partner_doc FOREIGN KEY (partner_order_document_id) REFERENCES sp_partner_order_document(id),
    CONSTRAINT fk_sp_partner_order_item_member FOREIGN KEY (mb_no) REFERENCES g5_member(mb_no)
);
