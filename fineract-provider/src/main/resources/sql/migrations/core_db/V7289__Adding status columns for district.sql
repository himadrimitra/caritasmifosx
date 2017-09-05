ALTER TABLE `f_district`
	CHANGE COLUMN `iso_district_code` `iso_district_code` VARCHAR(5) NULL DEFAULT NULL AFTER `state_id`,
	ADD COLUMN `status_enum` INT(5) NOT NULL DEFAULT '300' AFTER `district_name`,
	ADD COLUMN `activation_date` DATE NULL DEFAULT NULL AFTER `status_enum`,
	ADD COLUMN `actvivatedby_userid` BIGINT(20) NULL DEFAULT NULL AFTER `activation_date`,
	ADD COLUMN `rejectedon_date` DATE NULL DEFAULT NULL AFTER `actvivatedby_userid`,
	ADD COLUMN `rejectedby_userid` BIGINT(20) NULL DEFAULT NULL AFTER `rejectedon_date`,
	ADD COLUMN `createdby_id` BIGINT NULL DEFAULT NULL AFTER `rejectedby_userid`,
	ADD COLUMN `created_date` DATETIME NULL DEFAULT NULL AFTER `createdby_id`,
	ADD COLUMN `lastmodifiedby_id` BIGINT NULL DEFAULT NULL AFTER `created_date`,
	ADD COLUMN `lastmodified_date` DATETIME NULL DEFAULT NULL AFTER `lastmodifiedby_id`;

UPDATE f_district d SET d.createdby_id = 1, d.created_date = NOW(), d.lastmodifiedby_id = 1, d.lastmodified_date= NOW()
WHERE d.createdby_id IS NULL;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
 ('gis', 'READ_DISTRICT', 'DISTRICT', 'READ', 0),
 ('gis', 'CREATE_DISTRICT', 'DISTRICT', 'CREATE', 0),
 ('gis', 'CREATE_DISTRICT_CHECKER', 'DISTRICT', 'CREATE_CHECKER', 0),
 ('gis', 'UPDATE_DISTRICT', 'DISTRICT', 'UPDATE', 0),
 ('gis', 'UPDATE_DISTRICT_CHECKER', 'DISTRICT', 'UPDATE_CHECKER', 0),
 ('gis', 'DELETE_DISTRICT', 'DISTRICT', 'DELETE', 0),
 ('gis', 'DELETE_DISTRICT_CHECKER', 'DISTRICT', 'DELETE_CHECKER', 0),
 ('gis', 'ACTIVATE_DISTRICT', 'DISTRICT', 'ACTIVATE', 1),
 ('gis', 'ACTIVATE_DISTRICT_CHECKER', 'DISTRICT', 'ACTIVATE_CHECKER', 0),
 ('gis', 'REJECT_DISTRICT', 'DISTRICT', 'REJECT', 1),
 ('gis', 'REJECT_DISTRICT_CHECKER', 'DISTRICT', 'REJECT_CHECKER', 0);
