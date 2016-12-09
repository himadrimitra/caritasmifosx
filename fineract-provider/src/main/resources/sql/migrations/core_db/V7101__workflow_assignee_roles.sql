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
DROP TABLE IF EXISTS `f_task_action_log`;
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
	`task_type` SMALLINT(3) NOT NULL,
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
  `task_activity_id` BIGINT(20) NULL DEFAULT NULL,
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

DROP TABLE IF EXISTS `f_task_action_log`;
CREATE TABLE `f_task_action_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`task_id` BIGINT(20) NOT NULL,
	`action` SMALLINT(3) NOT NULL,
	`action_by` BIGINT(20) NOT NULL,
	`action_on` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_task_action_log_task_id` (`task_id`),
	CONSTRAINT `FK_f_task_action_log_task_id` FOREIGN KEY (`task_id`) REFERENCES `f_task` (`id`),
	CONSTRAINT `FK_f_task_action_log_action_user` FOREIGN KEY (`action_by`) REFERENCES `m_appuser` (`id`)
);

INSERT IGNORE INTO `m_code` (`code_name`, `is_system_defined`, `parent_id`)
VALUES
	('CGTStatus', 1, NULL),
	('HouseVisitStatus', 1, NULL),
	('GRTStatus', 1, NULL);


INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`, `is_mandatory`, `parent_id`)
VALUES
	((SELECT c.id FROM m_code c WHERE c.code_name = 'CGTStatus'), 'Completed', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'CGTStatus'), 'Scheduled', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'CGTStatus'), 'Cancelled', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'HouseVisitStatus'), 'Completed', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'HouseVisitStatus'), 'Scheduled', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'HouseVisitStatus'), 'Cancelled', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'GRTStatus'), 'Pass', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'GRTStatus'), 'Fail', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'GRTStatus'), 'Rescheduled', NULL, 1, NULL, 1, 0, NULL),
	((SELECT c.id FROM m_code c WHERE c.code_name = 'GRTStatus'), 'Cancelled', NULL, 1, NULL, 1, 0, NULL);


INSERT IGNORE INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`, `scoping_criteria_enum`)
VALUES
	('CGT1', 'm_client', 100, NULL),
	('CGT2', 'm_client', 100, NULL),
	('CGT3', 'm_client', 100, NULL),
	('GRT', 'm_client', 100, NULL),
	('House Visit', 'm_client', 100, NULL);


CREATE TABLE IF NOT EXISTS `cgt1` (
	`client_id` BIGINT(20) NOT NULL,
	`Planned Date` DATE NULL DEFAULT NULL,
	`Planned Time` VARCHAR(7) NULL DEFAULT NULL,
	`Location` VARCHAR(50) NULL DEFAULT NULL,
	`Notes` TEXT NULL,
	`CGTStatus_cd_Status` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`client_id`),
	CONSTRAINT `fk_cgt1_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `cgt2` (
	`client_id` BIGINT(20) NOT NULL,
	`Planned Date` DATE NULL DEFAULT NULL,
	`Planned Time` VARCHAR(7) NULL DEFAULT NULL,
	`Location` VARCHAR(50) NULL DEFAULT NULL,
	`Notes` TEXT NULL,
	`CGTStatus_cd_Status` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`client_id`),
	CONSTRAINT `fk_cgt2_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `cgt3` (
	`client_id` BIGINT(20) NOT NULL,
	`Planned Date` DATE NULL DEFAULT NULL,
	`Planned Time` VARCHAR(7) NULL DEFAULT NULL,
	`Location` VARCHAR(50) NULL DEFAULT NULL,
	`Notes` TEXT NULL,
	`CGTStatus_cd_Status` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`client_id`),
	CONSTRAINT `fk_cgt3_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `house visit` (
	`client_id` BIGINT(20) NOT NULL,
	`Planned Date` DATE NULL DEFAULT NULL,
	`Planned Time` VARCHAR(7) NULL DEFAULT NULL,
	`Notes` TEXT NULL,
	`Recommmended Loan Value` DECIMAL(19,6) NULL DEFAULT NULL,
	`HouseVisitStatus_cd_Status` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`client_id`),
	CONSTRAINT `fk_house_visit_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `grt` (
	`client_id` BIGINT(20) NOT NULL,
	`Planned Date` DATE NULL DEFAULT NULL,
	`Planned Time` VARCHAR(7) NULL DEFAULT NULL,
	`Location` VARCHAR(50) NULL DEFAULT NULL,
	`Notes` TEXT NULL,
	`GRTStatus_cd_Status` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`client_id`),
	CONSTRAINT `fk_grt_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES
	('datatable', 'CREATE_CGT1', 'CGT1', 'CREATE', 1),
	('datatable', 'CREATE_CGT1_CHECKER', 'CGT1', 'CREATE', 0),
	('datatable', 'READ_CGT1', 'CGT1', 'READ', 0),
	('datatable', 'UPDATE_CGT1', 'CGT1', 'UPDATE', 1),
	('datatable', 'UPDATE_CGT1_CHECKER', 'CGT1', 'UPDATE', 0),
	('datatable', 'DELETE_CGT1', 'CGT1', 'DELETE', 1),
	('datatable', 'DELETE_CGT1_CHECKER', 'CGT1', 'DELETE', 0),
	('datatable', 'CREATE_CGT2', 'CGT2', 'CREATE', 1),
	('datatable', 'CREATE_CGT2_CHECKER', 'CGT2', 'CREATE', 0),
	('datatable', 'READ_CGT2', 'CGT2', 'READ', 0),
	('datatable', 'UPDATE_CGT2', 'CGT2', 'UPDATE', 1),
	('datatable', 'UPDATE_CGT2_CHECKER', 'CGT2', 'UPDATE', 0),
	('datatable', 'DELETE_CGT2', 'CGT2', 'DELETE', 1),
	('datatable', 'DELETE_CGT2_CHECKER', 'CGT2', 'DELETE', 0),
	('datatable', 'CREATE_CGT3', 'CGT3', 'CREATE', 1),
	('datatable', 'CREATE_CGT3_CHECKER', 'CGT3', 'CREATE', 0),
	('datatable', 'READ_CGT3', 'CGT3', 'READ', 0),
	('datatable', 'UPDATE_CGT3', 'CGT3', 'UPDATE', 1),
	('datatable', 'UPDATE_CGT3_CHECKER', 'CGT3', 'UPDATE', 0),
	('datatable', 'DELETE_CGT3', 'CGT3', 'DELETE', 1),
	('datatable', 'DELETE_CGT3_CHECKER', 'CGT3', 'DELETE', 0),
	('datatable', 'CREATE_GRT', 'GRT', 'CREATE', 1),
	('datatable', 'CREATE_GRT_CHECKER', 'GRT', 'CREATE', 0),
	('datatable', 'READ_GRT', 'GRT', 'READ', 0),
	('datatable', 'UPDATE_GRT', 'GRT', 'UPDATE', 1),
	('datatable', 'UPDATE_GRT_CHECKER', 'GRT', 'UPDATE', 0),
	('datatable', 'DELETE_GRT', 'GRT', 'DELETE', 1),
	('datatable', 'DELETE_GRT_CHECKER', 'GRT', 'DELETE', 0),
	('datatable', 'CREATE_House Visit', 'House Visit', 'CREATE', 1),
	('datatable', 'CREATE_House Visit_CHECKER', 'House Visit', 'CREATE', 0),
	('datatable', 'READ_House Visit', 'House Visit', 'READ', 0),
	('datatable', 'UPDATE_House Visit', 'House Visit', 'UPDATE', 1),
	('datatable', 'UPDATE_House Visit_CHECKER', 'House Visit', 'UPDATE', 0),
	('datatable', 'DELETE_House Visit', 'House Visit', 'DELETE', 1),
	('datatable', 'DELETE_House Visit_CHECKER', 'House Visit', 'DELETE', 0);


CREATE TABLE IF NOT EXISTS `Banks` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Banks_cd_Name` INT(11) NULL DEFAULT NULL,
	`Account Type_cd_Account Type` INT(11) NULL DEFAULT NULL,
	`Account Number` VARCHAR(50) NULL DEFAULT NULL,
	`Control Date` DATE NULL DEFAULT NULL,
	`Actual Balance` DECIMAL(10,0) NULL DEFAULT NULL,
	`Good Checcking Confirmation` VARCHAR(100) NULL DEFAULT NULL,
	`Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_client_id` (`client_id`),
	CONSTRAINT `fk_banks_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Utility Bills` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Sectors_cd_Sector` INT(11) NULL DEFAULT NULL,
	`Provider_cd_Provider` INT(11) NULL DEFAULT NULL,
	`Month_cd_Month` INT(11) NULL DEFAULT NULL,
	`Amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`Total Delay Days` INT(11) NULL DEFAULT NULL,
	`Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_client_id` (`client_id`),
	CONSTRAINT `fk_utility_bills_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Mortgage` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Assessment Value` DECIMAL(19,6) NULL DEFAULT NULL,
	`Mortgage Value` DECIMAL(19,6) NULL DEFAULT NULL,
	`Appraiser Name` VARCHAR(100) NULL DEFAULT NULL,
	`Location` VARCHAR(100) NULL DEFAULT NULL,
	`Type of Mortgage_cd_Type of Mortgage` INT(11) NULL DEFAULT NULL,
	`Asset Type_cd_Asset Type` INT(11) NULL DEFAULT NULL,
	`AO Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_mortgage_client_id` (`client_id`),
	CONSTRAINT `fk_mortgage_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Customers` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Name` VARCHAR(100) NULL DEFAULT NULL,
	`Products_cd_Products` INT(11) NULL DEFAULT NULL,
	`Annual Volume in Pesos` DECIMAL(10,0) NULL DEFAULT NULL,
	`Quality Issues` INT(11) NULL DEFAULT NULL,
	`Average` DECIMAL(10,0) NULL DEFAULT NULL,
	`Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_customers_client_id` (`client_id`),
	CONSTRAINT `fk_customers_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Guaranties` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Guarantor Name` VARCHAR(100) NULL DEFAULT NULL,
	`Address` VARCHAR(200) NULL DEFAULT NULL,
	`Total Amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`Notary Date` DATE NULL DEFAULT NULL,
	`Expiry Date` DATE NULL DEFAULT NULL,
	`Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_client_id` (`client_id`),
	CONSTRAINT `fk_guaranties_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Post Dated Checks` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Total Amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`Number of Checks` INT(11) NULL DEFAULT NULL,
	`Banks_cd_Bank` INT(11) NULL DEFAULT NULL,
	`Issuer of Checks` VARCHAR(100) NULL DEFAULT NULL,
	`Check Date` DATE NULL DEFAULT NULL,
	`Encashment Date` DATE NULL DEFAULT NULL,
	`Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_post_dated_checks_client_id` (`client_id`),
	CONSTRAINT `fk_post_dated_checks_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Recievables` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Client Name` VARCHAR(100) NULL DEFAULT NULL,
	`Total Amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`Due Date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_client_id` (`client_id`),
	CONSTRAINT `fk_recievables_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


CREATE TABLE IF NOT EXISTS `Suppliers` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`Name` VARCHAR(100) NULL DEFAULT NULL,
	`Products_cd_Products` INT(11) NULL DEFAULT NULL,
	`Annual Volume in Pesos` DECIMAL(10,0) NULL DEFAULT NULL,
	`Total Purchase Volume` DECIMAL(10,0) NULL DEFAULT NULL,
	`Required payment Days` INT(11) NULL DEFAULT NULL,
	`Payment Delays in past 2 years` INT(11) NULL DEFAULT NULL,
	`Open Balance In Pesos` DECIMAL(10,0) NULL DEFAULT NULL,
	`Balance Date` DATE NULL DEFAULT NULL,
	`Comments` VARCHAR(200) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_suppliers_client_id` (`client_id`),
	CONSTRAINT `fk_suppliers_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
);


INSERT IGNORE INTO `x_registered_table` (`registered_table_name`, `application_table_name`, `category`)
VALUES
	('Banks', 'm_client', 100),
	('Customers', 'm_client', 100),
	('Guaranties', 'm_client', 100),
	('Mortgage', 'm_client', 100),
	('Post Dated Checks', 'm_client', 100),
	('Recievables', 'm_client', 100),
	('Suppliers', 'm_client', 100),
	('Utility Bills', 'm_client', 100);


INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES
	('datatable', 'CREATE_Suppliers', 'Suppliers', 'CREATE', 1),
	('datatable', 'CREATE_Suppliers_CHECKER', 'Suppliers', 'CREATE', 0),
	('datatable', 'READ_Suppliers', 'Suppliers', 'READ', 0),
	('datatable', 'UPDATE_Suppliers', 'Suppliers', 'UPDATE', 1),
	('datatable', 'UPDATE_Suppliers_CHECKER', 'Suppliers', 'UPDATE', 0),
	('datatable', 'DELETE_Suppliers', 'Suppliers', 'DELETE', 1),
	('datatable', 'DELETE_Suppliers_CHECKER', 'Suppliers', 'DELETE', 0),
	('datatable', 'CREATE_Customers', 'Customers', 'CREATE', 1),
	('datatable', 'CREATE_Customers_CHECKER', 'Customers', 'CREATE', 0),
	('datatable', 'READ_Customers', 'Customers', 'READ', 0),
	('datatable', 'UPDATE_Customers', 'Customers', 'UPDATE', 1),
	('datatable', 'UPDATE_Customers_CHECKER', 'Customers', 'UPDATE', 0),
	('datatable', 'DELETE_Customers', 'Customers', 'DELETE', 1),
	('datatable', 'DELETE_Customers_CHECKER', 'Customers', 'DELETE', 0),
	('datatable', 'CREATE_Banks', 'Banks', 'CREATE', 1),
	('datatable', 'CREATE_Banks_CHECKER', 'Banks', 'CREATE', 0),
	('datatable', 'READ_Banks', 'Banks', 'READ', 0),
	('datatable', 'UPDATE_Banks', 'Banks', 'UPDATE', 1),
	('datatable', 'UPDATE_Banks_CHECKER', 'Banks', 'UPDATE', 0),
	('datatable', 'DELETE_Banks', 'Banks', 'DELETE', 1),
	('datatable', 'DELETE_Banks_CHECKER', 'Banks', 'DELETE', 0),
	('datatable', 'CREATE_Utility Bills', 'Utility Bills', 'CREATE', 1),
	('datatable', 'CREATE_Utility Bills_CHECKER', 'Utility Bills', 'CREATE', 0),
	('datatable', 'READ_Utility Bills', 'Utility Bills', 'READ', 0),
	('datatable', 'UPDATE_Utility Bills', 'Utility Bills', 'UPDATE', 1),
	('datatable', 'UPDATE_Utility Bills_CHECKER', 'Utility Bills', 'UPDATE', 0),
	('datatable', 'DELETE_Utility Bills', 'Utility Bills', 'DELETE', 1),
	('datatable', 'DELETE_Utility Bills_CHECKER', 'Utility Bills', 'DELETE', 0),
	('datatable', 'CREATE_Mortgage', 'Mortgage', 'CREATE', 1),
	('datatable', 'CREATE_Mortgage_CHECKER', 'Mortgage', 'CREATE', 0),
	('datatable', 'READ_Mortgage', 'Mortgage', 'READ', 0),
	('datatable', 'UPDATE_Mortgage', 'Mortgage', 'UPDATE', 1),
	('datatable', 'UPDATE_Mortgage_CHECKER', 'Mortgage', 'UPDATE', 0),
	('datatable', 'DELETE_Mortgage', 'Mortgage', 'DELETE', 1),
	('datatable', 'DELETE_Mortgage_CHECKER', 'Mortgage', 'DELETE', 0),
	('datatable', 'CREATE_Post Dated Checks', 'Post Dated Checks', 'CREATE', 1),
	('datatable', 'CREATE_Post Dated Checks_CHECKER', 'Post Dated Checks', 'CREATE', 0),
	('datatable', 'READ_Post Dated Checks', 'Post Dated Checks', 'READ', 0),
	('datatable', 'UPDATE_Post Dated Checks', 'Post Dated Checks', 'UPDATE', 1),
	('datatable', 'UPDATE_Post Dated Checks_CHECKER', 'Post Dated Checks', 'UPDATE', 0),
	('datatable', 'DELETE_Post Dated Checks', 'Post Dated Checks', 'DELETE', 1),
	('datatable', 'DELETE_Post Dated Checks_CHECKER', 'Post Dated Checks', 'DELETE', 0),
	('datatable', 'CREATE_Recievables', 'Recievables', 'CREATE', 1),
	('datatable', 'CREATE_Recievables_CHECKER', 'Recievables', 'CREATE', 0),
	('datatable', 'READ_Recievables', 'Recievables', 'READ', 0),
	('datatable', 'UPDATE_Recievables', 'Recievables', 'UPDATE', 1),
	('datatable', 'UPDATE_Recievables_CHECKER', 'Recievables', 'UPDATE', 0),
	('datatable', 'DELETE_Recievables', 'Recievables', 'DELETE', 1),
	('datatable', 'DELETE_Recievables_CHECKER', 'Recievables', 'DELETE', 0),
	('datatable', 'CREATE_Guaranties', 'Guaranties', 'CREATE', 1),
	('datatable', 'CREATE_Guaranties_CHECKER', 'Guaranties', 'CREATE', 0),
	('datatable', 'READ_Guaranties', 'Guaranties', 'READ', 0),
	('datatable', 'UPDATE_Guaranties', 'Guaranties', 'UPDATE', 1),
	('datatable', 'UPDATE_Guaranties_CHECKER', 'Guaranties', 'UPDATE', 0),
	('datatable', 'DELETE_Guaranties', 'Guaranties', 'DELETE', 1),
	('datatable', 'DELETE_Guaranties_CHECKER', 'Guaranties', 'DELETE', 0);




-- ----------------------------------
INSERT IGNORE INTO f_task_activity(name,identifier,config_values,supported_actions,type)
VALUES
("Credit Bureau","creditbureau",null,null,3),
("Datatable Task","datatable",null,null,2),
("Existing Loans","existingloans",null,null,3),
("Upload Document","clientdocument",null,null,3),
("Survey Task","survey",null,null,1),
("Loan Application Approval","loanapplicationapproval",null,null,3),
("Loan Disbursal","loanapplicationdisbursal",null,null,3);


-- f_task_config parent
INSERT IGNORE INTO `f_task_config` (`name`, `short_name`,`task_type`)
VALUES
	('JLG Workflow','JLG',1);


--	f_task_config child
INSERT IGNORE INTO f_task_config(name,short_name,parent_id,task_activity_id,task_config_order,config_values,`task_type`)
VALUES
	("Customer Details","Customers",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),1,'{"datatablename":"Customers"}',2),
	("Family Details","Family",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),2,'{"datatablename":"Utility Bills"}',2),
	("Credit Bureau","Credit",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Credit Bureau'),3,null,2),
	("CGT1","CGT1",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),4,'{"datatablename":"cgt1"}',2),
	("CGT2","CGT2",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),5,'{"datatablename":"cgt2"}',2),
	("CGT3","CGT3",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),6,'{"datatablename":"cgt3"}',2),
	("House Visit","HouseVisit",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),7,'{"datatablename":"house visit"}',2),
	("GRT","GRT",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),8,'{"datatablename":"grt"}',2),
	("Bank Details","Banks",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Datatable Task'),9,'{"datatablename":"Banks"}',2),
	("Loan Application Approval","Sanction",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Loan Application Approval'),10,null,2),
	("Loan Disbursal","Disbursal",(SELECT tc.id FROM f_task_config tc WHERE tc.name = 'JLG Workflow'),(SELECT ta.id FROM f_task_activity ta WHERE ta.name = 'Loan Disbursal'),11,null,2);


insert into f_task_action_group(id)
		VALUES
			(1),
			(2);


insert into f_task_action(action, action_group_id)
VALUES
	(3,1),
	(4,1),
	(4,2);