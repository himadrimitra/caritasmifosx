ALTER TABLE `f_task`
	ADD COLUMN `short_description` VARCHAR(150) NULL DEFAULT NULL AFTER `description`;