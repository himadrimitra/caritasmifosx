ALTER TABLE `m_product_loan`
	ADD COLUMN `consider_future_disbursments_in_schedule` TINYINT(1) NOT NULL DEFAULT '0';

ALTER TABLE `m_loan`
	ADD COLUMN `consider_future_disbursments_in_schedule` TINYINT(1) NOT NULL DEFAULT '0'