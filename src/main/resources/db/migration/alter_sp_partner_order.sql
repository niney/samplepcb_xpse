ALTER TABLE sp_partner_order
  MODIFY status varchar(30) NOT NULL DEFAULT '협력사 견적요청',
  MODIFY price int NULL,
  MODIFY forwarder varchar(30) NULL,
  MODIFY shipping datetime NULL,
  MODIFY tracking varchar(30) NULL,
  MODIFY estimate_file1_subj varchar(50) NULL,
  MODIFY estimate_file1 varchar(150) NULL,
  MODIFY memo text NULL;

ALTER TABLE sp_partner_order
  ADD UNIQUE INDEX uk_sp_partner_order_it_partner (it_id, partner_mb_no);

ALTER TABLE sp_partner_order
  ADD CONSTRAINT fk_sp_partner_order_it_id
    FOREIGN KEY (it_id) REFERENCES g5_shop_item (it_id)
    ON DELETE CASCADE;

ALTER TABLE sp_partner_order
  ADD CONSTRAINT fk_sp_partner_order_mb_no
    FOREIGN KEY (partner_mb_no) REFERENCES g5_member (mb_no)
    ON DELETE CASCADE;
