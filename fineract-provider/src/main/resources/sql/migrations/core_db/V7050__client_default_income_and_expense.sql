ALTER TABLE `f_client_income_expense`
	ADD COLUMN `default_income` DECIMAL(19,6) NULL DEFAULT NULL AFTER `quintity`,
	ADD COLUMN `default_expense` DECIMAL(19,6) NULL DEFAULT NULL AFTER `default_income`;