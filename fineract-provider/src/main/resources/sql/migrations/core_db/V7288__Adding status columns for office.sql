INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('organisation', 'REJECT_OFFICE', 'OFFICE', 'REJECT', 0), ('organisation', 'REJECT_OFFICE_CHECKER', 'OFFICE', 'REJECT_CHECKER', 0),
('organisation', 'ACTIVATE_OFFICE', 'OFFICE', 'ACTIVATE', 0), ('organisation', 'ACTIVATE_OFFICE_CHECKER', 'OFFICE', 'ACTIVATE_CHECKER', 0);

INSERT IGNORE INTO `f_authentication` (`name`, `description`, `auth_service_class_name`, `is_active`) VALUES 
('Aadhaar Iris', 'Aadhaar iris services', 'SecondLevelAuthenticationServiceUsingAadhaarIris', 1);

ALTER TABLE `m_office`
	ADD COLUMN `status_enum` INT NOT NULL DEFAULT '300' AFTER `office_code`,
	ADD COLUMN `activation_date` DATE NULL DEFAULT NULL AFTER `status_enum`,
	ADD COLUMN `actvivatedby_userid` BIGINT NULL DEFAULT NULL AFTER `activation_date`,
	ADD COLUMN `rejectedon_date` DATE NULL DEFAULT NULL AFTER `actvivatedby_userid`,
	ADD COLUMN `rejectedby_userid` BIGINT NULL DEFAULT NULL AFTER `rejectedon_date`,
	ADD COLUMN `createdby_id` BIGINT NULL DEFAULT NULL AFTER `rejectedby_userid`,
	ADD COLUMN `created_date` DATETIME NULL DEFAULT NULL AFTER `createdby_id`,
	ADD COLUMN `lastmodifiedby_id` BIGINT NULL DEFAULT NULL AFTER `created_date`,
	ADD COLUMN `lastmodified_date` DATETIME NULL DEFAULT NULL AFTER `lastmodifiedby_id`;

UPDATE m_office o SET o.createdby_id = 1, o.created_date = NOW(), o.lastmodifiedby_id = 1, o.lastmodified_date= NOW()
WHERE o.createdby_id IS NULL;
