DROP TABLE IF EXISTS `f_loan_application_workflow_execution`;
DROP TABLE IF EXISTS `f_workflow_execution_step`;
DROP TABLE IF EXISTS `f_workflow_execution`;
DROP TABLE IF EXISTS `f_loan_product_workflow`;
DROP TABLE IF EXISTS `f_workflow_step_action`;
DROP TABLE IF EXISTS `f_workflow_step`;
DROP TABLE IF EXISTS `f_workflow`;
DROP TABLE IF EXISTS `f_task`;

CREATE TABLE `f_task` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL,
	`identifier` VARCHAR(200) NOT NULL,
	`config` MEDIUMTEXT NULL DEFAULT NULL,
	`supported_actions` VARCHAR(500) NULL DEFAULT NULL,
	`type` SMALLINT(3) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `INX_f_task_name` (`name`),
	CONSTRAINT `FK_f_task_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_task_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_workflow` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `INX_f_workflow_name` (`name`),
	CONSTRAINT `FK_f_workflow_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_workflow_step` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL,
	`task_id` BIGINT(20) NOT NULL,
	`workflow_id` BIGINT(20) NOT NULL,
	`step_order` SMALLINT(3) NULL DEFAULT NULL,
	`criteria_id` BIGINT(20) NULL DEFAULT NULL,
	`approval_logic`         VARCHAR(256)   DEFAULT NULL,
	`rejection_logic`         VARCHAR(256)   DEFAULT NULL,
	`config_values`	VARCHAR(100) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_workflow_task_id` FOREIGN KEY (`task_id`) REFERENCES `f_task` (`id`),
	CONSTRAINT `FK_f_workflow_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `f_workflow` (`id`),
	CONSTRAINT `FK_f_workflow_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_workflow_step_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_workflow_execution` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_id` BIGINT(20) NOT NULL,
	`execution_status` SMALLINT(3)  NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_workflow_execution_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `f_workflow` (`id`),
	CONSTRAINT `FK_f_workflow_execution_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_execution_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_workflow_execution_step` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_execution_id` BIGINT(20) NOT NULL,
	`workflow_step_id` BIGINT(20) NOT NULL,
	`status` SMALLINT(3) NOT NULL,
	`criteria_result` text DEFAULT NULL,
	`criteria_action` SMALLINT(3) DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_workflow_execution_id` FOREIGN KEY (workflow_execution_id) REFERENCES `f_workflow_execution` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_workflow_step_id` FOREIGN KEY (workflow_step_id) REFERENCES `f_workflow_step` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_execution_step_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;


CREATE TABLE `f_loan_product_workflow` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_product_id`  BIGINT(20)   NOT NULL,
	`workflow_id` BIGINT(20) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_f_loan_product_workflow_lp` (`loan_product_id`),
	CONSTRAINT `FK_f_lp_workflow_workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `f_workflow` (`id`),
	CONSTRAINT `FK_f_lp_workflow_loan_product_id` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`),
	CONSTRAINT `FK_f_lp_workflow_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_lp_workflow_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

CREATE TABLE `f_loan_application_workflow_execution` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_application_id` BIGINT(20)  NOT  NULL,
	`workflow_execution_id` BIGINT(20) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_f_loan_application_workflow_execution_loan` (`loan_application_id`),
	UNIQUE KEY `uk_f_loan_application_workflow_execution_workflow` (`workflow_execution_id`),
	CONSTRAINT `FK_f_la_workflow_execution_workflow_execution_id` FOREIGN KEY (`workflow_execution_id`) REFERENCES `f_workflow_execution` (`id`),
	CONSTRAINT `FK_f_la_workflow_execution_loan_application_id` FOREIGN KEY (`loan_application_id`) REFERENCES `f_loan_application_reference` (`id`),
	CONSTRAINT `FK_f_la_workflow_execution_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_la_workflow_execution_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;


CREATE TABLE `f_workflow_step_action` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`workflow_step_id` BIGINT(20) NOT NULL,
	`action` SMALLINT(3) NOT NULL,
	`roles`  VARCHAR(100) DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `uk_f_workflow_step_action` (`workflow_step_id`,`action`),
	CONSTRAINT `FK_f_workflow_step_action_workflow_step_id` FOREIGN KEY (workflow_step_id) REFERENCES `f_workflow_step` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_workflow_step_action_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;
