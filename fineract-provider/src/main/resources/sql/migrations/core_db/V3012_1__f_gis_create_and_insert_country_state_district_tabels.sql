CREATE TABLE `f_country` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,	
	`iso_country_code` CHAR(2) NOT NULL,
	`country_name` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_iso_country_code` (`iso_country_code`),
	UNIQUE INDEX `UQ_country_name` (`country_name`),
	INDEX `INX_iso_country_code` (`iso_country_code`),
	INDEX `INX_country_name` (`country_name`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_state` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`country_id` BIGINT(20) NOT NULL,	
	`iso_state_code` CHAR(3) NULL DEFAULT NULL,
	`state_name` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_iso_state_code_name_cid` (`iso_state_code`,`state_name`,`country_id`),
	INDEX `FK_country_id` (`country_id`),
	INDEX `INX_iso_state_code` (`iso_state_code`),
	INDEX `INX_state_name` (`state_name`),
	CONSTRAINT `FK_country_id` FOREIGN KEY (`country_id`) REFERENCES `f_country` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_district` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,	
	`state_id` BIGINT(20) NOT NULL,
	`iso_district_code` CHAR(3) NULL DEFAULT NULL,
	`district_name` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_sid_iso_district_code_name` (`state_id`,`iso_district_code`,`district_name`),
	INDEX `FK_state_id` (`state_id`),
	INDEX `INX_iso_district_code` (`iso_district_code`),
	INDEX `INX_district_name` (`district_name`),
	CONSTRAINT `FK_state_id` FOREIGN KEY (`state_id`) REFERENCES `f_state` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('gis', 'CREATE_COUNTRY', 'COUNTRY', 'CREATE', 0),
('gis', 'CREATE_COUNTRY_CHECKER', 'COUNTRY', 'CREATE_CHECKER', 0),
('gis', 'UPDATE_COUNTRY', 'COUNTRY', 'UPDATE', 0),
('gis', 'UPDATE_COUNTRY_CHECKER', 'COUNTRY', 'UPDATE_CHECKER', 0),
('gis', 'DELETE_COUNTRY', 'COUNTRY', 'DELETE', 0),
('gis', 'DELETE_COUNTRY_CHECKER', 'COUNTRY', 'DELETE_CHECKER', 0),
('gis', 'READ_COUNTRY', 'COUNTRY', 'READ', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('gis', 'CREATE_STATE', 'STATE', 'CREATE', 0),
('gis', 'CREATE_STATE_CHECKER', 'STATE', 'CREATE_CHECKER', 0),
('gis', 'UPDATE_STATE', 'STATE', 'UPDATE', 0),
('gis', 'UPDATE_STATE_CHECKER', 'STATE', 'UPDATE_CHECKER', 0),
('gis', 'DELETE_STATE', 'STATE', 'DELETE', 0),
('gis', 'DELETE_STATE_CHECKER', 'STATE', 'DELETE_CHECKER', 0),
('gis', 'READ_STATE', 'STATE', 'READ', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('gis', 'CREATE_DISTRICT', 'DISTRICT', 'CREATE', 0),
('gis', 'CREATE_DISTRICT_CHECKER', 'DISTRICT', 'CREATE_CHECKER', 0),
('gis', 'UPDATE_DISTRICT', 'DISTRICT', 'UPDATE', 0),
('gis', 'UPDATE_DISTRICT_CHECKER', 'DISTRICT', 'UPDATE_CHECKER', 0),
('gis', 'DELETE_DISTRICT', 'DISTRICT', 'DELETE', 0),
('gis', 'DELETE_DISTRICT_CHECKER', 'DISTRICT', 'DELETE_CHECKER', 0),
('gis', 'READ_DISTRICT', 'DISTRICT', 'READ', 0);

