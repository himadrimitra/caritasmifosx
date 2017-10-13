ALTER TABLE `m_savings_product`
	ADD COLUMN `is_interest_calculation_from_product_chart` TINYINT(1) NULL DEFAULT '0' AFTER `external_id`;
