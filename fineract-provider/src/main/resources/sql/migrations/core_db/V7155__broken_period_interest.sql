ALTER TABLE `m_product_loan`
	ADD COLUMN `broken_period_method_enum` SMALLINT(5) NULL DEFAULT NULL;

	
ALTER TABLE `m_loan`
	ADD COLUMN `broken_period_method_enum` SMALLINT(5) NULL DEFAULT NULL;
	