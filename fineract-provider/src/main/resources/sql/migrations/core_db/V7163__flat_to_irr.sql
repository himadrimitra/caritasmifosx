ALTER TABLE `m_product_loan`
	ADD COLUMN `is_flat_interest_rate` TINYINT NOT NULL DEFAULT '0';

ALTER TABLE `m_loan`
	ADD COLUMN `flat_interest_rate` DECIMAL(19,6) NULL DEFAULT NULL;
