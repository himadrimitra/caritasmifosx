ALTER TABLE `f_bank_account_details` ADD COLUMN `micr_code` VARCHAR(20) NULL AFTER `ifsc_code`;
ALTER TABLE `f_bank_account_details` ADD COLUMN `branch_name` VARCHAR(100) NULL AFTER `bank_name`;