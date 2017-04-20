ALTER TABLE `f_task_config`
	ADD COLUMN `complete_on_action` SMALLINT NULL AFTER `config_values`;
	
	ALTER TABLE `f_task`
	ADD COLUMN `complete_on_action` SMALLINT NULL DEFAULT NULL AFTER `task_activity_id`;
	