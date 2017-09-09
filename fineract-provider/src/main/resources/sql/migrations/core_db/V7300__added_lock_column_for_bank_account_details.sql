ALTER TABLE `f_bank_account_details`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `checker_info`;