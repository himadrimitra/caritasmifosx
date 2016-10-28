ALTER TABLE `de_duplication_criteria`
	ADD COLUMN `de_duplication_for_null_columns` VARCHAR(200) NULL DEFAULT NULL AFTER `active`;
	
UPDATE `de_duplication_criteria` SET `de_duplication_for_null_columns`='middlename' WHERE  `id`=1;