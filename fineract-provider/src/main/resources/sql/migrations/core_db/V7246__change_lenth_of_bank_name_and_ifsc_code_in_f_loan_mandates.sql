ALTER TABLE `f_loan_mandates`
CHANGE COLUMN `bank_name` `bank_name` VARCHAR(100) NOT NULL AFTER `bank_account_holder_name`,
CHANGE COLUMN `ifsc` `ifsc` VARCHAR(11) NULL DEFAULT NULL AFTER `micr`;