ALTER TABLE `f_bank_statement_details`
	ADD COLUMN `office_id` BIGINT NULL DEFAULT NULL AFTER `is_manual_reconciled`;