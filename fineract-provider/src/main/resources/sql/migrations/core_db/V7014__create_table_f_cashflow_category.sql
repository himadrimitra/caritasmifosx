CREATE TABLE `f_cashflow_category` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`short_name` VARCHAR(30) NOT NULL,
	`description` VARCHAR(500) NULL DEFAULT NULL,
	`category_enum_id` SMALLINT(3) NOT NULL, --  JAVA ENUM, ex: occupation, asset
	`type_enum_id` SMALLINT(3) NOT NULL, -- JAVA ENUM, income, expense
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_cashflow_category_name` (`name`),
	UNIQUE INDEX `UQ_f_cashflow_category_short_name` (`short_name`),
	CONSTRAINT `FK_f_cashflow_category_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_cashflow_category_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('portfolio', 'CREATE_CASHFLOW', 'CASHFLOW', 'CREATE', 0),
('portfolio', 'CREATE_CASHFLOW_CHECKER', 'CASHFLOW', 'CREATE_CHECKER', 0),
('portfolio', 'UPDATE_CASHFLOW', 'CASHFLOW', 'UPDATE', 0),
('portfolio', 'UPDATE_CASHFLOW_CHECKER', 'CASHFLOW', 'UPDATE_CHECKER', 0),
('portfolio', 'ACTIVATE_CASHFLOW', 'CASHFLOW', 'ACTIVATE', 0),
('portfolio', 'ACTIVATE_CASHFLOW_CHECKER', 'CASHFLOW', 'ACTIVATE_CHECKER', 0),
('portfolio', 'INACTIVATE_CASHFLOW', 'CASHFLOW', 'INACTIVATE', 0),
('portfolio', 'INACTIVATE_CASHFLOW_CHECKER', 'CASHFLOW', 'INACTIVATE_CHECKER', 0),
('portfolio', 'DELETE_CASHFLOW', 'CASHFLOW', 'DELETE', 0),
('portfolio', 'DELETE_CASHFLOW_CHECKER', 'CASHFLOW', 'DELETE_CHECKER', 0),
('portfolio', 'READ_CASHFLOW', 'CASHFLOW', 'READ', 0);

CREATE TABLE `f_income_expense` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`cashflow_category_id` BIGINT(20) NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	`description` VARCHAR(500) NULL DEFAULT NULL,
	`is_quantifier_needed` TINYINT(1) NULL DEFAULT NULL,
	`quantifier_label` VARCHAR(100) NULL DEFAULT NULL,
	`is_capture_month_wise_income` TINYINT(1) NULL DEFAULT NULL,
	`stability_enum_id` SMALLINT(3) NULL DEFAULT NULL, 					-- JAVA ENUM - high,low
	`default_income` DECIMAL(19,6) NULL DEFAULT NULL,
	`default_expense` DECIMAL(19,6) NULL DEFAULT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_income_expense_name` (`cashflow_category_id`,`name`),
	CONSTRAINT `FK_f_income_expense_cashflow_category_id` FOREIGN KEY (`cashflow_category_id`) REFERENCES `f_cashflow_category` (`id`),
	CONSTRAINT `FK_f_income_expense_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_income_expense_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('portfolio', 'CREATE_INCOMEEXPENSE', 'INCOMEEXPENSE', 'CREATE', 0),
('portfolio', 'CREATE_INCOMEEXPENSE_CHECKER', 'INCOMEEXPENSE', 'CREATE_CHECKER', 0),
('portfolio', 'UPDATE_INCOMEEXPENSE', 'INCOMEEXPENSE', 'UPDATE', 0),
('portfolio', 'UPDATE_INCOMEEXPENSE_CHECKER', 'INCOMEEXPENSE', 'UPDATE_CHECKER', 0),
('portfolio', 'ACTIVATE_INCOMEEXPENSE', 'INCOMEEXPENSE', 'ACTIVATE', 0),
('portfolio', 'ACTIVATE_INCOMEEXPENSE_CHECKER', 'INCOMEEXPENSE', 'ACTIVATE_CHECKER', 0),
('portfolio', 'INACTIVATE_INCOMEEXPENSE', 'INCOMEEXPENSE', 'INACTIVATE', 0),
('portfolio', 'INACTIVATE_INCOMEEXPENSE_CHECKER', 'INCOMEEXPENSE', 'INACTIVATE_CHECKER', 0),
('portfolio', 'DELETE_INCOMEEXPENSE', 'INCOMEEXPENSE', 'DELETE', 0),
('portfolio', 'DELETE_INCOMEEXPENSE_CHECKER', 'INCOMEEXPENSE', 'DELETE_CHECKER', 0),
('portfolio', 'READ_INCOMEEXPENSE', 'INCOMEEXPENSE', 'READ', 0);

CREATE TABLE `f_region` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_region_name` (`name`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_income_expense_regionwise` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`income_expense_id` BIGINT(20) NOT NULL,
	`region_id` BIGINT(20) NOT NULL,
	`income_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`expense_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_income_expense_region_income_expense_id` FOREIGN KEY (`income_expense_id`) REFERENCES `f_income_expense` (`id`),
	CONSTRAINT `FK_f_income_expense_region_region_id` FOREIGN KEY (`region_id`) REFERENCES `f_region` (`id`),
	CONSTRAINT `FK_f_income_expense_region_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_income_expense_region_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_office_level_income_expense_lookup` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`region_id` BIGINT(20) NOT NULL,
	`office_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_office_level_income_expense_lookup` (`region_id`,`office_id`),
	CONSTRAINT `FK_f_office_level_income_expense_lookup_region_id` FOREIGN KEY (`region_id`) REFERENCES `f_region` (`id`),
	CONSTRAINT `FK_f_office_level_income_expense_lookup_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)AUTO_INCREMENT=1;