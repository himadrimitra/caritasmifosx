
alter table f_workflow_step_action drop column roles;

DROP TABLE IF EXISTS f_workflow_step_action_role;
CREATE TABLE `f_workflow_step_action_role` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_step_action_id` BIGINT(20) NOT NULL,
	`role_id`  BIGINT(20) DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_f_workflow_step_action_role` (`workflow_step_action_id`,`role_id`),
	CONSTRAINT `FK_f_workflow_step_action_role_actionid` FOREIGN KEY (workflow_step_action_id) REFERENCES `f_workflow_step_action` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_role_roleid` FOREIGN KEY (role_id) REFERENCES `m_role` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_role_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_role_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

alter table f_workflow_execution_step add column assigned_to BIGINT(20) DEFAULT NULL;
alter table f_workflow_execution_step add CONSTRAINT `FK_f_workflow_execution_step_assigned_to` FOREIGN KEY (`assigned_to`) REFERENCES `m_appuser` (`id`);

# DROP TABLE IF EXISTS f_workflow_execution_step_action_log;
# CREATE TABLE `f_workflow_execution_step_action_log` (
# 	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
# 	`workflow_execution_step_id` BIGINT(20) NOT NULL,
# 	`role_id`  BIGINT(20) DEFAULT NULL,
# 	`createdby_id` BIGINT(20) NOT NULL,
# 	`created_date` DATETIME NOT NULL,
# 	`lastmodifiedby_id` BIGINT(20) NOT NULL,
# 	`lastmodified_date` DATETIME NOT NULL,
# 	PRIMARY KEY (`id`),
# 	UNIQUE KEY `uk_f_workflow_step_action_role` (`workflow_step_action_id`,`role_id`),
# 	CONSTRAINT `FK_f_workflow_step_action_role_actionid` FOREIGN KEY (workflow_step_action_id) REFERENCES `f_workflow_step_action` (`id`),
# 	CONSTRAINT `FK_f_workflow_step_action_role_roleid` FOREIGN KEY (role_id) REFERENCES `m_role` (`id`),
# 	CONSTRAINT `FK_f_workflow_step_action_role_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
# 	CONSTRAINT `FK_f_workflow_step_action_role_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
# )COLLATE = 'utf8_general_ci' ENGINE = InnoDB;


alter table f_workflow_execution add column `config_values`	VARCHAR(512) NULL DEFAULT NULL;

ALTER TABLE `f_workflow_step`
	ADD COLUMN `short_name` VARCHAR(10) NULL AFTER `name`,
	ADD INDEX `INX_short_name` (`short_name`);
	
UPDATE f_workflow_step ws SET ws.short_name = SUBSTR(ws.name, 1, 5) WHERE ws.short_name IS NULL;

ALTER TABLE `f_workflow_step`
	ALTER `short_name` DROP DEFAULT;
ALTER TABLE `f_workflow_step`
	CHANGE COLUMN `short_name` `short_name` VARCHAR(10) NOT NULL AFTER `name`;
	
ALTER TABLE `f_workflow_execution_step`
	ADD COLUMN `current_action` SMALLINT(3) NULL AFTER `status`;