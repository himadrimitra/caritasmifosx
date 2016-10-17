CREATE TABLE `f_client_income_expense` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`family_details_id` BIGINT(20) NULL DEFAULT NULL,
	`income_expense_id` BIGINT(20) NOT NULL,
	`quintity` DECIMAL(10,2) NULL DEFAULT NULL,
	`total_income` DECIMAL(19,6) NULL DEFAULT NULL,
	`total_expense` DECIMAL(19,6) NULL DEFAULT NULL,
	`is_month_wise_income` TINYINT(1) NULL DEFAULT NULL,
	`is_primary_income` TINYINT(1) NULL DEFAULT NULL,
	`is_active` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_client_income_expense_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_f_client_income_expense_family_details_id` FOREIGN KEY (`family_details_id`) REFERENCES `f_family_details` (`id`),
	CONSTRAINT `FK_f_client_income_expense_income_expense_id` FOREIGN KEY (`income_expense_id`) REFERENCES `f_income_expense` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_client_month_wise_income_expense` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_income_expense_id` BIGINT(20) NOT NULL,
	`month` TINYINT(2) NOT NULL,
	`year` SMALLINT(4) NOT NULL,
	`income_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`expense_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`is_active` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_client_month_wise_income_expense_client_income_expense_id` FOREIGN KEY (`client_income_expense_id`) REFERENCES `f_client_income_expense` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('portfolio', 'CREATE_CLIENTINCOMEEXPENSE', 'CLIENTINCOMEEXPENSE', 'CREATE', 0),
('portfolio', 'CREATE_CLIENTINCOMEEXPENSE_CHECKER', 'CLIENTINCOMEEXPENSE', 'CREATE_CHECKER', 0),
('portfolio', 'UPDATE_CLIENTINCOMEEXPENSE', 'CLIENTINCOMEEXPENSE', 'UPDATE', 0),
('portfolio', 'UPDATE_CLIENTINCOMEEXPENSE_CHECKER', 'CLIENTINCOMEEXPENSE', 'UPDATE_CHECKER', 0),
('portfolio', 'DELETE_CLIENTINCOMEEXPENSE', 'CLIENTINCOMEEXPENSE', 'DELETE', 0),
('portfolio', 'DELETE_CLIENTINCOMEEXPENSE_CHECKER', 'CLIENTINCOMEEXPENSE', 'DELETE_CHECKER', 0),
('portfolio', 'READ_CLIENTINCOMEEXPENSE', 'CLIENTINCOMEEXPENSE', 'READ', 0);