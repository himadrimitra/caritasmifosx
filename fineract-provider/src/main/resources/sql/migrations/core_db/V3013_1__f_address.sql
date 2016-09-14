CREATE TABLE `f_address` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,	
	`house_no` VARCHAR(20) NOT NULL,
	`street_no` VARCHAR(20) NULL DEFAULT NULL,	
	`address_line_one` VARCHAR(200) NOT NULL,
	`address_line_two` VARCHAR(200) NULL DEFAULT NULL,	
	`landmark` VARCHAR(100) NULL DEFAULT NULL,
	`village_town` VARCHAR(100) NULL DEFAULT NULL,
	`taluka` VARCHAR(100) NULL DEFAULT NULL,
	`district_id` BIGINT(20) NULL DEFAULT NULL,
	`state_id` BIGINT(20) NULL DEFAULT NULL,
	`country_id` BIGINT(20) NULL DEFAULT NULL,
	`postal_code` VARCHAR(10) NOT NULL,	
	`latitude` DECIMAL(19,6) NULL DEFAULT NULL,
	`longitude` DECIMAL(19,6) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_address_district_id` FOREIGN KEY (`district_id`) REFERENCES `f_district` (`id`),
	CONSTRAINT `FK_f_address_state_id` FOREIGN KEY (`state_id`) REFERENCES `f_state` (`id`),
	CONSTRAINT `FK_f_address_country_id` FOREIGN KEY (`country_id`) REFERENCES `f_country` (`id`),
	CONSTRAINT `FK_f_address_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_address_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_address_entity` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`address_id` BIGINT(20) NOT NULL,
	`address_type` INT(11) NOT NULL,
	`entity_id` BIGINT(20) NOT NULL,
	`entity_type_enum` SMALLINT(3) NOT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`parent_address_type` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),	
	UNIQUE INDEX `f_entity_address_UNIQUE` (`address_type`,`entity_id`, `entity_type_enum`),
	INDEX `FK_f_entity_address_address_id` (`address_id`),
	INDEX `FK_f_entity_address_type_m_cv_id` (`address_type`),
	CONSTRAINT `FK_f_entity_address_address_id` FOREIGN KEY (`address_id`) REFERENCES `f_address` (`id`),
	CONSTRAINT `FK_f_entity_address_type_m_cv_id` FOREIGN KEY (`address_type`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_entity_p_address_type_m_cv_id` FOREIGN KEY (`parent_address_type`) REFERENCES `m_code_value` (`id`)
)AUTO_INCREMENT=1;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('kyc', 'CREATE_ADDRESSES', 'ADDRESSES', 'CREATE', 0),
('kyc', 'CREATE_ADDRESSES_CHECKER', 'ADDRESSES', 'CREATE_CHECKER', 0),
('kyc', 'UPDATE_ADDRESSES', 'ADDRESSES', 'UPDATE', 0),
('kyc', 'UPDATE_ADDRESSES_CHECKER', 'ADDRESSES', 'UPDATE_CHECKER', 0),
('kyc', 'DELETE_ADDRESSES', 'ADDRESSES', 'DELETE', 0),
('kyc', 'DELETE_ADDRESSES_CHECKER', 'ADDRESSES', 'DELETE_CHECKER', 0),
('kyc', 'READ_ADDRESSES', 'ADDRESSES', 'READ', 0);


INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES 
('AddressType', 1);

INSERT INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`) VALUES 
((SELECT c.id from m_code c where c.code_name = 'AddressType'), 'Permanent Address', NULL, 0, NULL, 1),
((SELECT c.id from m_code c where c.code_name = 'AddressType'), 'Residential Address', NULL, 0, NULL, 1),
((SELECT c.id from m_code c where c.code_name = 'AddressType'), 'Business Address', NULL, 0, NULL, 1),
((SELECT c.id from m_code c where c.code_name = 'AddressType'), 'Office Address', NULL, 0, NULL, 1);