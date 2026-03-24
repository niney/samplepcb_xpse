ALTER TABLE sp_estimate_item
    ADD COLUMN confirmed_price TEXT NULL AFTER selected_partner_estimate_item_id;
