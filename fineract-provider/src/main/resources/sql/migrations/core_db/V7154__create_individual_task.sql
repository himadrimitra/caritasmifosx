DROP TABLE IF EXISTS f_task_config_template ;
 
CREATE TABLE `f_task_config_template` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) UNIQUE NOT NULL,
	`short_name` VARCHAR(10) UNIQUE NOT NULL,
	`entity_type` SMALLINT(2) NULL DEFAULT NULL,
	`task_config_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_task_config_id` (`task_config_id`),
	CONSTRAINT `FK_task_config_id` FOREIGN KEY (`task_config_id`) REFERENCES `f_task_config` (`id`)
);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('taskconfig', 'CREATE_TASKCONFIGTEMPLATE', 'TASKCONFIGTEMPLATE', 'CREATE', '0');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('taskconfig', 'UPDATE_TASKCONFIGTEMPLATE', 'TASKCONFIGTEMPLATE', 'UPDATE', '0');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('taskconfig', 'CREATE_TEMPLATETASK', 'TEMPLATETASK', 'CREATE', '0');