-- ============================================
-- pcb_parts: 메인 부품 테이블
-- ============================================
CREATE TABLE sp_pcb_parts (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    doc_id                VARCHAR(20)   NOT NULL,
    write_date            DATETIME      NOT NULL,
    last_modified_date    DATETIME      NOT NULL,
    service_type          VARCHAR(100)  DEFAULT NULL,
    sub_service_type      VARCHAR(100)  DEFAULT NULL,
    large_category        VARCHAR(255)  DEFAULT NULL,
    medium_category       VARCHAR(255)  DEFAULT NULL,
    small_category        VARCHAR(255)  DEFAULT NULL,
    part_name             VARCHAR(255)  DEFAULT NULL,
    description           TEXT,
    manufacturer_name     VARCHAR(255)  DEFAULT NULL,
    parts_packaging       VARCHAR(255)  DEFAULT NULL,
    packaging             TEXT,
    moq                   INT           DEFAULT NULL,
    price                 INT           DEFAULT NULL,
    memo                  TEXT,
    offer_name            VARCHAR(255)  DEFAULT NULL,
    date_code             VARCHAR(100)  DEFAULT NULL,
    member_id             VARCHAR(255)  DEFAULT NULL,
    manager_phone_number  VARCHAR(50)   DEFAULT NULL,
    manager_name          VARCHAR(255)  DEFAULT NULL,
    manager_email         VARCHAR(255)  DEFAULT NULL,
    contents              TEXT,
    status                INT           DEFAULT NULL,
    watt                  TEXT,
    tolerance             TEXT,
    ohm                   TEXT,
    condenser             TEXT,
    voltage               TEXT,
    temperature           VARCHAR(255)  DEFAULT NULL,
    size                  VARCHAR(255)  DEFAULT NULL,
    current_val           TEXT,
    inductor              TEXT,
    product_name          VARCHAR(255)  DEFAULT NULL,
    photo_url             TEXT,
    datasheet_url         TEXT,
    PRIMARY KEY (id),
    INDEX idx_sp_pcb_parts_service_type (service_type),
    INDEX idx_sp_pcb_parts_sub_service_type (sub_service_type),
    INDEX idx_sp_pcb_parts_part_name (part_name),
    INDEX idx_sp_pcb_parts_manufacturer (manufacturer_name),
    INDEX idx_sp_pcb_parts_member_id (member_id),
    INDEX idx_sp_pcb_parts_price (price),
    UNIQUE INDEX uk_sp_pcb_parts_doc_id (doc_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- pcb_parts_price: 유통사별 가격 정보
-- ============================================
CREATE TABLE sp_pcb_parts_price (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    parts_id              BIGINT        NOT NULL,
    distributor           VARCHAR(255)  DEFAULT NULL,
    sku                   VARCHAR(255)  DEFAULT NULL,
    stock                 INT           NOT NULL DEFAULT 0,
    moq                   INT           NOT NULL DEFAULT 0,
    pkg                   VARCHAR(100)  DEFAULT NULL,
    updated_date          DATETIME      DEFAULT NULL,
    PRIMARY KEY (id),
    INDEX idx_sp_pcb_parts_price_parts_id (parts_id),
    INDEX idx_sp_pcb_parts_price_distributor (distributor),
    CONSTRAINT fk_sp_pcb_parts_price_parts FOREIGN KEY (parts_id) REFERENCES sp_pcb_parts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- pcb_parts_price_step: 수량별 단가 단계
-- ============================================
CREATE TABLE sp_pcb_parts_price_step (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    price_id              BIGINT        NOT NULL,
    break_quantity        INT           NOT NULL DEFAULT 0,
    unit_price            INT           NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_sp_pcb_parts_price_step_price_id (price_id),
    CONSTRAINT fk_sp_pcb_parts_price_step_price FOREIGN KEY (price_id) REFERENCES sp_pcb_parts_price (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- pcb_parts_image: 부품 이미지
-- ============================================
CREATE TABLE sp_pcb_parts_image (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    parts_id              BIGINT        NOT NULL,
    upload_file_name      VARCHAR(255)  DEFAULT NULL,
    origin_file_name      VARCHAR(255)  DEFAULT NULL,
    path_token            VARCHAR(255)  DEFAULT NULL,
    size                  VARCHAR(50)   DEFAULT NULL,
    PRIMARY KEY (id),
    INDEX idx_sp_pcb_parts_image_parts_id (parts_id),
    CONSTRAINT fk_sp_pcb_parts_image_parts FOREIGN KEY (parts_id) REFERENCES sp_pcb_parts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================
-- pcb_parts_spec: 부품 스펙 (attribute 플래트닝)
-- ============================================
CREATE TABLE sp_pcb_parts_spec (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    parts_id              BIGINT        NOT NULL,
    display_value         VARCHAR(255)  DEFAULT NULL,
    attr_group            VARCHAR(255)  DEFAULT NULL,
    attr_name             VARCHAR(255)  DEFAULT NULL,
    attr_shortname        VARCHAR(255)  DEFAULT NULL,
    PRIMARY KEY (id),
    INDEX idx_sp_pcb_parts_spec_parts_id (parts_id),
    INDEX idx_sp_pcb_parts_spec_attr_name (attr_name),
    CONSTRAINT fk_sp_pcb_parts_spec_parts FOREIGN KEY (parts_id) REFERENCES sp_pcb_parts (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
