-- ============================================
-- sp_estimate_document: 견적서 (g5_shop_item과 1:1)
-- ============================================
CREATE TABLE sp_estimate_document (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    it_id                 VARCHAR(20)   NOT NULL,
    status                VARCHAR(30)   DEFAULT NULL,
    expected_delivery     VARCHAR(100)  DEFAULT NULL,
    shipping_fee          INT           DEFAULT NULL,
    management_fee        INT           DEFAULT NULL,
    total_amount          INT           DEFAULT NULL,
    final_amount          INT           DEFAULT NULL,
    write_date            DATETIME      DEFAULT NULL,
    modify_date           DATETIME      DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE INDEX uk_sp_estimate_document_it_id (it_id),
    CONSTRAINT fk_sp_estimate_document_it_id FOREIGN KEY (it_id) REFERENCES g5_shop_item (it_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- sp_file: 공통 첨부파일 (다형성 ref_type + ref_id)
-- ============================================
CREATE TABLE sp_file (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    ref_type              VARCHAR(50)   NOT NULL,
    ref_id                BIGINT        NOT NULL,
    upload_file_name      VARCHAR(255)  NOT NULL,
    origin_file_name      VARCHAR(255)  NOT NULL,
    path_token            VARCHAR(500)  NOT NULL,
    size                  BIGINT        NOT NULL DEFAULT 0,
    write_date            DATETIME      NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_sp_file_ref (ref_type, ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- sp_estimate_item: 견적서 부품 항목
-- ============================================
CREATE TABLE sp_estimate_item (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    estimate_document_id  BIGINT        NOT NULL,
    pcb_part_doc_id       VARCHAR(20)   NOT NULL,
    qty                   INT           NOT NULL DEFAULT 0,
    analysis_meta         TEXT,
    selected_price        TEXT,
    selected_partner_estimate_item_id BIGINT DEFAULT NULL,
    write_date            DATETIME      NOT NULL,
    modify_date           DATETIME      NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_sp_estimate_item_document_id (estimate_document_id),
    INDEX idx_sp_estimate_item_pcb_part_doc_id (pcb_part_doc_id),
    CONSTRAINT fk_sp_estimate_item_document FOREIGN KEY (estimate_document_id) REFERENCES sp_estimate_document (id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_estimate_item_pcb_part FOREIGN KEY (pcb_part_doc_id) REFERENCES sp_pcb_parts (doc_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- sp_partner_estimate_item: 협력사 견적 항목
-- ============================================
CREATE TABLE sp_partner_estimate_item (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    estimate_item_id      BIGINT        NOT NULL,
    mb_no                 INT           NOT NULL,
    selected_price        TEXT,
    write_date            DATETIME      NOT NULL,
    modify_date           DATETIME      NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_sp_partner_estimate_item_estimate_item_id (estimate_item_id),
    INDEX idx_sp_partner_estimate_item_mb_no (mb_no),
    UNIQUE INDEX uk_sp_partner_estimate_item (estimate_item_id, mb_no),
    CONSTRAINT fk_sp_partner_estimate_item_estimate FOREIGN KEY (estimate_item_id) REFERENCES sp_estimate_item (id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_partner_estimate_item_member FOREIGN KEY (mb_no) REFERENCES g5_member (mb_no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- selected_partner_estimate_item_id FK는 sp_partner_estimate_item 생성 후 추가
ALTER TABLE sp_estimate_item
    ADD CONSTRAINT fk_sp_estimate_item_selected_partner
    FOREIGN KEY (selected_partner_estimate_item_id) REFERENCES sp_partner_estimate_item (id) ON DELETE SET NULL;
