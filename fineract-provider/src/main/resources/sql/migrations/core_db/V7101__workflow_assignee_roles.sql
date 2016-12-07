DROP TABLE IF EXISTS `f_workflow_entity_type_mapping`;
DROP TABLE IF EXISTS `f_loan_application_workflow_execution`;
DROP TABLE IF EXISTS `f_loan_product_workflow`;
DROP TABLE IF EXISTS `f_workflow_execution_step`;
DROP TABLE IF EXISTS `f_workflow_execution`;
DROP TABLE IF EXISTS `f_workflow_step_action_role`;
DROP TABLE IF EXISTS `f_workflow_step_action`;
DROP TABLE IF EXISTS `f_workflow_step`;
DROP TABLE IF EXISTS `f_workflow_step_action_group`;
DROP TABLE IF EXISTS `f_workflow`;
DROP TABLE IF EXISTS `f_task`;
DROP TABLE IF EXISTS `f_task_config_entity_type_mapping`;
DROP TABLE IF EXISTS `f_task_config`;
DROP TABLE IF EXISTS `f_task_action_role`;
DROP TABLE IF EXISTS `f_task_action`;
DROP TABLE IF EXISTS `f_task_action_group`;
DROP TABLE IF EXISTS `f_task_activity`;

CREATE TABLE `f_task_activity` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL,
	`identifier` VARCHAR(200) NOT NULL,
	`config_values` MEDIUMTEXT NULL DEFAULT NULL,
	`supported_actions` VARCHAR(500) NULL DEFAULT NULL,
	`type` SMALLINT(3) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_task_activity_name` (`name`)
);

CREATE TABLE `f_task_action_group` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (`id`)
);

CREATE TABLE `f_task_action` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`action` SMALLINT(3) NOT NULL,
	`action_group_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_task_config_action` (`action_group_id`, `action`),
	CONSTRAINT `FK_f_task_config_action_action_group_id` FOREIGN KEY (`action_group_id`) REFERENCES `f_task_action_group` (`id`)
);

CREATE TABLE `f_task_action_role` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`task_action_id` BIGINT(20) NOT NULL,
	`role_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_task_action_role` (`task_action_id`, `role_id`),
	INDEX `FK_f_task_action_role_roleid` (`role_id`),
	CONSTRAINT `FK_f_task_action_role_action_id` FOREIGN KEY (`task_action_id`) REFERENCES `f_task_action` (`id`),
	CONSTRAINT `FK_f_task_action_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `m_role` (`id`)
);

CREATE TABLE `f_task_config` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`parent_id` BIGINT(20) NULL DEFAULT NULL,
	`name` VARCHAR(200) NOT NULL,
	`short_name` VARCHAR(20) NOT NULL,
	`task_activity_id` BIGINT(20) NULL DEFAULT NULL,
	`task_config_order` SMALLINT(3) NULL DEFAULT NULL,
	`criteria_id` BIGINT(20) NULL DEFAULT NULL,
	`approval_logic` VARCHAR(256) NULL DEFAULT NULL,
	`rejection_logic` VARCHAR(256) NULL DEFAULT NULL,
	`config_values` MEDIUMTEXT NULL DEFAULT NULL,
	`action_group_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `INX_f_task_config_name` (`name`),
	INDEX `INX_f_task_config_short_name` (`short_name`),
	CONSTRAINT `FK_f_task_config_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `f_task_config` (`id`),
	CONSTRAINT `FK_f_task_config_activity_id` FOREIGN KEY (`task_activity_id`) REFERENCES `f_task_activity` (`id`),
	CONSTRAINT `FK_f_task_config_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_task_config_action_group_id` FOREIGN KEY (`action_group_id`) REFERENCES `f_task_action_group` (`id`)
);

CREATE TABLE `f_task_config_entity_type_mapping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`task_config_id` BIGINT(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_task_config_entity_type_mapping` (`task_config_id`, `entity_type`, `entity_id`),
	CONSTRAINT `FK_f_task_config_entity_type_mapping_task_config_id` FOREIGN KEY (`task_config_id`) REFERENCES `f_task_config` (`id`)
);

CREATE TABLE `f_task` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`parent_id` BIGINT(20) NULL DEFAULT NULL,
	`name` VARCHAR(200) NOT NULL,
	`short_name` VARCHAR(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NULL DEFAULT NULL,
	`task_type` SMALLINT(3) NOT NULL,
	`task_config_id` BIGINT(20) NOT NULL,
	`status` SMALLINT(3) NOT NULL,
	`priority` SMALLINT(3) NOT NULL,
	`due_date` DATETIME NULL DEFAULT NULL,
	`current_action` SMALLINT(3) NULL DEFAULT NULL,
	`assigned_to` BIGINT(20) NULL DEFAULT NULL,
	`task_order` SMALLINT(3) NULL DEFAULT NULL,
	`criteria_id` BIGINT(20) NULL DEFAULT NULL,
	`approval_logic` VARCHAR(256) NULL DEFAULT NULL,
	`rejection_logic` VARCHAR(256) NULL DEFAULT NULL,
	`config_values` MEDIUMTEXT NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`office_id` BIGINT(20) NULL DEFAULT NULL,
	`action_group_id` BIGINT(20) NULL DEFAULT NULL,
	`criteria_result` TEXT NULL DEFAULT NULL,
	`criteria_action` SMALLINT(3) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_task_name` (`name`),
	INDEX `FK_f_task_short_name` (`short_name`),
	CONSTRAINT `FK_f_task_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `f_task` (`id`),
	CONSTRAINT `FK_f_task_task_config_id` FOREIGN KEY (`task_config_id`) REFERENCES `f_task_config` (`id`),
	CONSTRAINT `FK_f_task_assigned_to` FOREIGN KEY (`assigned_to`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_task_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_task_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_f_task_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
	CONSTRAINT `FK_f_task_action_group_id` FOREIGN KEY (`action_group_id`) REFERENCES `f_task_action_group` (`id`),
	CONSTRAINT `FK_f_task_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_task_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);