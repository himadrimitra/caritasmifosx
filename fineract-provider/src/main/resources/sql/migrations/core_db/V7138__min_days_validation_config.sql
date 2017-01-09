ALTER TABLE `m_product_loan`
	ADD COLUMN `min_duration_applicable_for_all_disbursements` TINYINT(1) NOT NULL DEFAULT '0' AFTER `min_periods_between_disbursal_and_first_repayment`;
