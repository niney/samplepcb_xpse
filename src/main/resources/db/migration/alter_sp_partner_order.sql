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
