CREATE TABLE `f_bank_account_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`account_number` VARCHAR(50) NOT NULL,
	`ifsc_code` VARCHAR(50) NOT NULL,
	`mobile_number` VARCHAR(50) NULL DEFAULT NULL,
	`email` VARCHAR(50) NULL DEFAULT NULL,
	`status_id` SMALLINT(2) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB;

CREATE TABLE `f_bank_account_detail_associations` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`bank_account_detail_id` BIGINT(20) NOT NULL,
	`entity_type_enum` SMALLINT(2) NOT NULL,
	`entity_id` BIGINT(20) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_bank_account_detail_associations_account_detail_id` (`bank_account_detail_id`),
	CONSTRAINT `FK_bank_account_detail_associations_account_detail_id` FOREIGN KEY (`bank_account_detail_id`) REFERENCES `f_bank_account_details` (`id`)
)
ENGINE=InnoDB;

CREATE TABLE `f_external_service_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`display_code` VARCHAR(50) NOT NULL,
	`type` SMALLINT(2) NOT NULL,
	PRIMARY KEY (`id`)
)
ENGINE=InnoDB;

ALTER TABLE `m_payment_type`
	ADD COLUMN `external_service_id` BIGINT(20) NULL ,
	ADD CONSTRAINT `FK_payment_type_external_service_is` FOREIGN KEY (`external_service_id`) REFERENCES `f_external_service_details` (`id`);
	
ALTER TABLE `m_portfolio_command_source`
	ADD COLUMN `entity_type_id` SMALLINT(2) NULL DEFAULT NULL;
	
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`,`can_maker_checker`) VALUES 
('bank', 'CREATE_BANKACCOUNTDETAIL', 'BANKACCOUNTDETAIL', 'CREATE', 0), 
('bank', 'READ_BANKACCOUNTDETAIL', 'BANKACCOUNTDETAIL', 'READ', 0),
('bank', 'UPDATE_BANKACCOUNTDETAIL', 'BANKACCOUNTDETAIL', 'UPDATE', 0),
('bank', 'DELETE_BANKACCOUNTDETAIL', 'BANKACCOUNTDETAIL', 'DELETE', 0), 
('bank', 'CREATE_BANKACCOUNTDETAIL_CHECKER', 'BANKACCOUNTDETAIL', 'CREATE_CHECKER', 0), 
('bank', 'UPDATE_BANKACCOUNTDETAIL_CHECKER', 'BANKACCOUNTDETAIL', 'UPDATE_CHECKER', 0), 
('bank', 'DELETE_BANKACCOUNTDETAIL_CHECKER', 'BANKACCOUNTDETAIL', 'DELETE_CHECKER', 0);





