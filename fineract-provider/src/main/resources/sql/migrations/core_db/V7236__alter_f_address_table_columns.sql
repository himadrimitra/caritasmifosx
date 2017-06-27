ALTER TABLE `f_address`
CHANGE COLUMN `house_no` `house_no` VARCHAR(200) NULL DEFAULT NULL AFTER `id`,
CHANGE COLUMN `street_no` `street_no` VARCHAR(200) NULL DEFAULT NULL AFTER `house_no`;