ALTER TABLE `f_existing_loan`
	ADD COLUMN `closed_date` DATE NULL DEFAULT NULL AFTER `maturity_date`;