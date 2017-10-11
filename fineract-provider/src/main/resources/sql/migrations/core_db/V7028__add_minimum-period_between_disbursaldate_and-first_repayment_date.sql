ALTER TABLE `m_product_loan`
	ADD COLUMN `min_periods_between_disbursal_and_first_repayment` INT(3) NULL DEFAULT NULL AFTER `min_days_between_disbursal_and_first_repayment`;
	
	
