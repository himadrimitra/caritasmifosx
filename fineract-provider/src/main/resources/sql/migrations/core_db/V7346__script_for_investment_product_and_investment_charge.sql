CREATE TABLE `f_investment_product` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`short_name` VARCHAR(4) NOT NULL,
	`description` VARCHAR(500) NOT NULL,
	`currency_code` VARCHAR(3) NOT NULL,
	`currency_digits` SMALLINT(5) NOT NULL,
	`currency_multiplesof` SMALLINT(5) NULL DEFAULT NULL,
	`min_nominal_interest_rate` DECIMAL(19,6)  NULL DEFAULT NULL,
	`default_nominal_interest_rate` DECIMAL(19,6) NOT NULL,
	`max_nominal_interest_rate` DECIMAL(19,6)  NULL DEFAULT NULL,
	`nominal_interest_rate_type` SMALLINT(5) NOT NULL,
	`interest_compounding_period_type` SMALLINT(5) NOT NULL,
	`min_investment_term_period` SMALLINT(5) NULL DEFAULT NULL,
	`default_investment_term_period` SMALLINT(5) NULL NOT NULL,
	`max_investment_term_period` SMALLINT(5) NULL DEFAULT NULL,
	`investment_term_type` SMALLINT(5) NOT NULL,
	`override_terms_in_investment_accounts` TINYINT(1) NOT NULL DEFAULT 0,
	`nominal_interset_rate` TINYINT(1) NOT NULL DEFAULT 0,
	`interest_compounding_period` TINYINT(1) NOT NULL DEFAULT 0,
	`investment_term` TINYINT(1) NOT NULL DEFAULT 0,
	`accounting_type` SMALLINT(5) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unq_name` (`name`),
	UNIQUE INDEX `unq_short_name` (`short_name`)
);

CREATE TABLE `f_investment_product_charge` (
	`investment_product_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`investment_product_id`, `charge_id`),
	INDEX `idx_charge_id` (`charge_id`),
	INDEX `idx_investment_product_id` (`investment_product_id`),
	CONSTRAINT `f_investment_product_charge_fk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
	CONSTRAINT `f_investment_product_charge_fk_2` FOREIGN KEY (`investment_product_id`) REFERENCES `f_investment_product` (`id`)
);

INSERT IGNORE INTO m_permission (`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) 
 VALUES ('portfolio','CREATE_INVESTMENT_PRODUCT','INVESTMENT_PRODUCT','CREATE',0),
 ('portfolio','CREATE_INVESTMENT_PRODUCT_CHECKER','INVESTMENT_PRODUCT','CREATE_CHECKER',0),
 ('portfolio','UPDATE_INVESTMENT_PRODUCT','INVESTMENT_PRODUCT','UPDATE',0),
 ('portfolio','UPDATE_INVESTMENT_PRODUCT_CHECKER','INVESTMENT_PRODUCT','UPDATE_CHECKER',0),
 ('portfolio','DELETE_INVESTMENT_PRODUCT','INVESTMENT_PRODUCT','DELETE',0),
 ('portfolio','DELETE_INVESTMENT_PRODUCT_CHECKER','INVESTMENT_PRODUCT','DELETE_CHECKER',0),
 ('portfolio','READ_INVESTMENT_PRODUCT','INVESTMENT_PRODUCT','READ',0);
 
 CREATE TABLE `f_charge_investment_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`charge_id` BIGINT(20) NOT NULL,
	`apply_to_linked_savings_account` TINYINT(1) NOT NULL DEFAULT '0',
	`not_apply_to_investment_account` TINYINT(1) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	INDEX `idx_investment_details_charge_id` (`charge_id`),
	CONSTRAINT `FK_investment_details_charge_id` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`)
);