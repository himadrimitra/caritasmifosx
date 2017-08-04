ALTER TABLE `m_loan_repayment_schedule`
	ADD COLUMN `capitalized_charge_amount` DECIMAL(19,6) NULL DEFAULT NULL AFTER `advance_payment_amount`;
