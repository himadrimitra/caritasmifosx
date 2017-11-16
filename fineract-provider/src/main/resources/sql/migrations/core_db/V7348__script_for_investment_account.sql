CREATE TABLE `f_investment_account`(
 `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
 `account_no` VARCHAR(30) NOT NULL,
 `partner_id` INT(11) NOT NULL,
 `office_id` BIGINT(20) NOT NULL,
 `investment_product_id` BIGINT(20) NOT NULL,
 `external_id` VARCHAR(100) NULL DEFAULT NULL,
 `status_enum` SMALLINT(5) NOT NULL,
 `currency_code` VARCHAR(3) NOT NULL,
 `currency_digits` SMALLINT(5) NOT NULL,
 `currency_multiplesof` SMALLINT(5) NULL DEFAULT NULL,
 `submittedon_date` DATE NOT NULL,
 `submittedon_userid` BIGINT(20) NULL DEFAULT NULL,
 `approvedon_date` DATE NULL DEFAULT NULL,
 `approvedon_userid` BIGINT(20) NULL DEFAULT NULL,
 `activatedon_date` DATE NULL DEFAULT NULL,
 `activatedon_userid` BIGINT(20) NULL DEFAULT NULL,
 `investmenton_date` DATE NOT NULL,
 `investmenton_userid` BIGINT(20) NULL DEFAULT NULL,
 `investment_amount` DECIMAL(19,6) NOT NULL DEFAULT '0.000000',
 `interest_rate` DECIMAL(19,6) NOT NULL,
 `interest_rate_type` SMALLINT(5) NOT NULL,
 `investment_term` SMALLINT(5) NOT NULL,
 `investment_term_type` SMALLINT(5) NOT NULL,
 `maturityon_date` DATE NOT NULL,
 `maturityon_userid` BIGINT(20) NULL DEFAULT NULL,
 `maturity_amount` DECIMAL(19,6) NOT NULL DEFAULT '0.000000',
 `reinvest_after_maturity` TINYINT(1) NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `ia_account_no_UNIQUE` (`account_no`),
	UNIQUE INDEX `ia_externalid_UNIQUE` (`external_id`),
	INDEX `idx_investment_product_id` (`investment_product_id`),
	INDEX `idx_status_enum` (`status_enum`),
	CONSTRAINT `fk1_investment_product_id` FOREIGN KEY (`investment_product_id`) REFERENCES `f_investment_product` (`id`),
	CONSTRAINT `fk2_partner_id` FOREIGN KEY (`partner_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `fk3_office_id` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
);

CREATE TABLE `f_investment_account_savings_linkages`(
 `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
 `investment_account_id` BIGINT(20) NOT NULL,
 `savings_account_id` BIGINT(20) NOT NULL, 
 `investment_amount` DECIMAL(19,6) NOT NULL,
 PRIMARY KEY(`id`),
 INDEX `idx_investment_account_id` (`investment_account_id`),
 INDEX `idx_savings_account_id` (`savings_account_id`),
 CONSTRAINT `fk1_investment_account_id` FOREIGN KEY (`investment_account_id`) REFERENCES `f_investment_account` (`id`),
 CONSTRAINT `fk2_savings_account_id` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
);

CREATE TABLE `f_investment_account_charge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`investment_account_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	`is_penalty` TINYINT(1) NOT NULL DEFAULT '0',
	`is_active` TINYINT(1) NOT NULL DEFAULT '1',
	`inactivated_on_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `idx_charge_id` (`charge_id`),
	INDEX `idx_investment_account_id` (`investment_account_id`),
	CONSTRAINT `fk1_charge_id` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
	CONSTRAINT `fk2_investment_account_id` FOREIGN KEY (`investment_account_id`) REFERENCES `f_investment_account` (`id`)
);

INSERT IGNORE INTO m_permission (`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`) 
 VALUES ('portfolio','CREATE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','CREATE',0),
 ('portfolio','CREATE_INVESTMENT_ACCOUNT_CHECKER','INVESTMENT_ACCOUNT','CREATE_CHECKER',0),
 ('portfolio','UPDATE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','UPDATE',0),
 ('portfolio','UPDATE_INVESTMENT_ACCOUNT_CHECKER','INVESTMENT_ACCOUNT','UPDATE_CHECKER',0),
 ('portfolio','DELETE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','DELETE',0),
 ('portfolio','DELETE_INVESTMENT_ACCOUNT_CHECKER','INVESTMENT_ACCOUNT','DELETE_CHECKER',0),
 ('portfolio','READ_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','READ',0),
 ('portfolio','APPROVE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','APPROVE',0),
 ('portfolio','ACTIVE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','ACTIVE',0),
 ('portfolio','REJECT_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','REJECT',0),
 ('portfolio','ACTIVE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','ACTIVE',0),
 ('portfolio','MATURE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','MATURE',0),
 ('portfolio','CLOSE_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','CLOSE',0),
 ('portfolio','REINVEST_INVESTMENT_ACCOUNT','INVESTMENT_ACCOUNT','REINVEST',0);