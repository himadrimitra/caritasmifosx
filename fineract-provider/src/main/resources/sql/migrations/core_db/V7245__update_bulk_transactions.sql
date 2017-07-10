ALTER TABLE `f_bank_statement_details`
	ADD COLUMN `transaction_id_for_update` BIGINT(20) NULL DEFAULT NULL,
	CHANGE COLUMN `amount` `amount` DECIMAL(19,6) NULL DEFAULT NULL;
	