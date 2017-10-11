ALTER TABLE `f_bank_account_details`
	ADD COLUMN `last_transaction_date` DATE NULL DEFAULT NULL AFTER `account_type_enum`;