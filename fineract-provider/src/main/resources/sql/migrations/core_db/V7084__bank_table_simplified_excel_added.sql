ALTER TABLE `f_bank` 
ADD COLUMN `support_simplified_statement` TINYINT(1) NULL DEFAULT '0';
ALTER TABLE `f_bank_statement_details` 
ADD COLUMN `receipt_number` VARCHAR(50) NULL DEFAULT NULL,
ADD COLUMN `is_error` TINYINT(1) NULL DEFAULT '0';;
ALTER TABLE `f_bank_statement` 
ADD COLUMN `payment_type` VARCHAR(50) NULL DEFAULT NULL;