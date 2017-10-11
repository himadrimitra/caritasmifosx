ALTER TABLE `f_client_income_expense`
	ADD COLUMN `is_remmitance_income` TINYINT(1) NULL DEFAULT NULL AFTER `is_primary_income`;
