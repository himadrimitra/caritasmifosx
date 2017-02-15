ALTER TABLE `m_loan_glim`
	ADD COLUMN `overpaid_amount` DECIMAL(19,6) NULL DEFAULT NULL;
ALTER TABLE `m_loan_glim_transaction`
	ADD COLUMN `overpaid_amount`  DECIMAL(19,6) NULL DEFAULT NULL;