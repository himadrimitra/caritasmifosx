CREATE TABLE `f_row_lock_or_unlock_configuration` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`business_event_name` VARCHAR(50) NULL DEFAULT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`condition_json` TEXT NULL,
	PRIMARY KEY (`id`)
);

ALTER TABLE `m_loan`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `amount_for_upfront_collection`;
	
ALTER TABLE `m_client`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `email_id`;
	
ALTER TABLE `m_client_identifier`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `description`;
	
ALTER TABLE `f_address`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `document_id`;
	
ALTER TABLE `m_document`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `storage_type_enum`;
	
ALTER TABLE `f_family_details`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `client_reference`;
	
ALTER TABLE `f_client_income_expense`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `is_active`;	
	
ALTER TABLE `f_existing_loan`
	ADD COLUMN `is_locked` TINYINT(1) NOT NULL DEFAULT '0' AFTER `archive`;
	
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('authorisation', 'LOCK_ENTITY', 'ENTITY', 'LOCK', 0),
('authorisation', 'LOCK_ENTITY_CHECKER', 'ENTITY', 'LOCK_CHECKER', 0),
('authorisation', 'UNLOCK_ENTITY', 'ENTITY', 'UNLOCK', 0),
('authorisation', 'UNLOCK_ENTITY_CHECKER', 'ENTITY', 'UNLOCK_CHECKER', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('authorisation', 'LOCK_CLIENT', 'CLIENT', 'LOCK', 0),
('authorisation', 'LOCK_CLIENT_CHECKER', 'CLIENT', 'LOCK_CHECKER', 0),
('authorisation', 'UNLOCK_CLIENT', 'CLIENT', 'UNLOCK', 0),
('authorisation', 'UNLOCK_CLIENT_CHECKER', 'CLIENT', 'UNLOCK_CHECKER', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('authorisation', 'LOCK_LOAN', 'LOAN', 'LOCK', 0),
('authorisation', 'LOCK_LOAN_CHECKER', 'LOAN', 'LOCK_CHECKER', 0),
('authorisation', 'UNLOCK_LOAN', 'LOAN', 'UNLOCK', 0),
('authorisation', 'UNLOCK_LOAN_CHECKER', 'LOAN', 'UNLOCK_CHECKER', 0);


	
	