DROP TABLE IF EXISTS f_workflow_entity_type_mapping;
DROP TABLE IF EXISTS f_loan_application_workflow_execution;
DROP TABLE IF EXISTS f_loan_product_workflow;
DROP TABLE IF EXISTS f_workflow_execution_step;
DROP TABLE IF EXISTS f_workflow_execution;
DROP TABLE IF EXISTS f_workflow_step_action_role;
DROP TABLE IF EXISTS f_workflow_step_action;
DROP TABLE IF EXISTS f_workflow_step;
DROP TABLE IF EXISTS f_workflow_step_action_group;

CREATE TABLE `f_workflow_step_action_group` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `f_workflow_step` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL,
	`short_name` VARCHAR(10) NOT NULL,
	`task_id` BIGINT(20) NOT NULL,
	`workflow_id` BIGINT(20) NOT NULL,
	`step_order` SMALLINT(3) NULL DEFAULT NULL,
	`criteria_id` BIGINT(20) NULL DEFAULT NULL,
	`approval_logic` VARCHAR(256) NULL DEFAULT NULL,
	`rejection_logic` VARCHAR(256) NULL DEFAULT NULL,
	`config_values` VARCHAR(100) NULL DEFAULT NULL,
	`action_group_id` BIGINT(20) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_workflow_step` (`name`, `task_id`, `workflow_id`),
	INDEX `FK_f_workflow_task_id` (`task_id`),
	INDEX `FK_f_workflow_workflow_id` (`workflow_id`),
	INDEX `FK_f_workflow_criteria_id` (`criteria_id`),
	INDEX `FK_f_workflow_step_createdby_id` (`createdby_id`),
	INDEX `FK_f_workflow_step_lastmodifiedby_id` (`lastmodifiedby_id`),
	INDEX `INX_short_name` (`short_name`),
	INDEX `FK_f_workflow_step_action_group_id` (`action_group_id`),
	CONSTRAINT `FK_f_workflow_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_group_id` FOREIGN KEY (`action_group_id`) REFERENCES `f_workflow_step_action_group` (`id`),
	CONSTRAINT `FK_f_workflow_step_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_task_id` FOREIGN KEY (`task_id`) REFERENCES `f_task` (`id`),
	CONSTRAINT `FK_f_workflow_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `f_workflow` (`id`)
);

CREATE TABLE `f_workflow_execution` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_id` BIGINT(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL DEFAULT '1',
	`entity_id` BIGINT(20) NULL DEFAULT NULL,
	`execution_status` SMALLINT(3) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	`config_values` VARCHAR(512) NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`office_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_workflow_execution_workflow_id` (`workflow_id`),
	INDEX `FK_f_workflow_execution_createdby_id` (`createdby_id`),
	INDEX `FK_f_workflow_execution_lastmodifiedby_id` (`lastmodifiedby_id`),
	CONSTRAINT `FK_f_workflow_execution_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `f_workflow` (`id`),
	CONSTRAINT `FK_f_workflow_execution_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_execution_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

CREATE TABLE `f_workflow_execution_step` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_execution_id` BIGINT(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL DEFAULT '1',
	`entity_id` BIGINT(20) NULL DEFAULT NULL,
	`workflow_step_id` BIGINT(20) NOT NULL,
	`status` SMALLINT(3) NOT NULL,
	`current_action` SMALLINT(3) NULL DEFAULT NULL,
	`name` VARCHAR(200) NOT NULL,
	`short_name` VARCHAR(10) NOT NULL,
	`assigned_to` BIGINT(20) NULL DEFAULT NULL,
	`task_id` BIGINT(20) NOT NULL,
	`step_order` SMALLINT(3) NULL DEFAULT NULL,
	`criteria_id` BIGINT(20) NULL DEFAULT NULL,
	`approval_logic` VARCHAR(256) NULL DEFAULT NULL,
	`rejection_logic` VARCHAR(256) NULL DEFAULT NULL,
	`config_values` VARCHAR(512) NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`office_id` BIGINT(20) NULL DEFAULT NULL,
	`action_group_id` BIGINT(20) NULL DEFAULT NULL,
	`criteria_result` TEXT NULL,
	`criteria_action` SMALLINT(3) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,	
	PRIMARY KEY (`id`),
	INDEX `FK_f_workflow_execution_step_workflow_execution_id` (`workflow_execution_id`),
	INDEX `FK_f_workflow_execution_step_workflow_step_id` (`workflow_step_id`),
	INDEX `FK_f_workflow_execution_step_createdby_id` (`createdby_id`),
	INDEX `FK_f_workflow_execution_step_lastmodifiedby_id` (`lastmodifiedby_id`),
	INDEX `FK_f_workflow_execution_step_assigned_to` (`assigned_to`),
	INDEX `FK_f_workflow_execution_step_action_group_id` (`action_group_id`),
	INDEX `FK_f_workflow_execution_step_task_id` (`task_id`),
	INDEX `FK_f_workflow_execution_step_criteria_id` (`criteria_id`),
	INDEX `FK_f_workflow_execution_step_client_id` (`client_id`),
	INDEX `FK_f_workflow_execution_step_office_id` (`office_id`),
	CONSTRAINT `FK_f_workflow_execution_step_action_group_id` FOREIGN KEY (`action_group_id`) REFERENCES `f_workflow_step_action_group` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_assigned_to` FOREIGN KEY (`assigned_to`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_task_id` FOREIGN KEY (`task_id`) REFERENCES `f_task` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_workflow_execution_id` FOREIGN KEY (`workflow_execution_id`) REFERENCES `f_workflow_execution` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_workflow_step_id` FOREIGN KEY (`workflow_step_id`) REFERENCES `f_workflow_step` (`id`)
);

CREATE TABLE `f_workflow_step_action` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`action` SMALLINT(3) NOT NULL,
	`action_group_id` BIGINT(20) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_workflow_step_action` (`action_group_id`, `action`),
	INDEX `FK_f_workflow_step_action_createdby_id` (`createdby_id`),
	INDEX `FK_f_workflow_step_action_lastmodifiedby_id` (`lastmodifiedby_id`),
	CONSTRAINT `FK_f_workflow_step_action_action_group_id` FOREIGN KEY (`action_group_id`) REFERENCES `f_workflow_step_action_group` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

CREATE TABLE `f_workflow_step_action_role` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_step_action_id` BIGINT(20) NOT NULL,
	`role_id` BIGINT(20) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `uk_f_workflow_step_action_role` (`workflow_step_action_id`, `role_id`),
	INDEX `FK_f_workflow_step_action_role_roleid` (`role_id`),
	INDEX `FK_f_workflow_step_action_role_createdby_id` (`createdby_id`),
	INDEX `FK_f_workflow_step_action_role_lastmodifiedby_id` (`lastmodifiedby_id`),
	CONSTRAINT `FK_f_workflow_step_action_role_actionid` FOREIGN KEY (`workflow_step_action_id`) REFERENCES `f_workflow_step_action` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_role_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_role_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_role_roleid` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`)
);

CREATE TABLE `f_workflow_entity_type_mapping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_id` BIGINT(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_workflow_entity_type_mapping` (`workflow_id`,`entity_type`,`entity_id`),
	CONSTRAINT `FK_entity_type_mapping_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `f_workflow` (`id`)
);

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
# );