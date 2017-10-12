ALTER TABLE `m_product_loan`
	ADD COLUMN `stop_loan_processing_on_npa` TINYINT(1) NULL DEFAULT '0' AFTER `interest_rates_list_per_period`;