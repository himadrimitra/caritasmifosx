ALTER TABLE `f_task`
	CHANGE COLUMN `due_date` `due_date` DATE NULL DEFAULT NULL AFTER `priority`,
	ADD COLUMN `due_time` TIME NULL DEFAULT NULL AFTER `lastmodified_date`;