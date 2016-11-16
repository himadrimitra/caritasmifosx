ALTER TABLE `m_product_loan`
	ADD COLUMN `adjust_interest_for_rounding` TINYINT(1) NOT NULL DEFAULT '0';