ALTER TABLE `m_product_loan`
	ADD COLUMN `allow_negative_loan_balances` TINYINT(1) NOT NULL DEFAULT '0',
	ADD COLUMN `consider_future_disbursements_in_schedule` TINYINT(1) NOT NULL DEFAULT '0',
	ADD COLUMN `consider_all_disbursements_in_schedule` TINYINT(1) NOT NULL DEFAULT '0';
	
ALTER TABLE `m_loan`
	ADD COLUMN `calculated_installment_amount` DECIMAL(19,6) NULL DEFAULT NULL;	