ALTER TABLE `f_loan_mandates`
	CHANGE COLUMN `ifsc` `ifsc` VARCHAR(15) NULL DEFAULT NULL AFTER `micr`;