CREATE TABLE `f_family_details` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`salutation_cv_id` INT(11) NULL DEFAULT NULL,
	`firstname` VARCHAR(50) NOT NULL,
	`middlename` VARCHAR(50) NULL DEFAULT NULL,
	`lastname` VARCHAR(50) NULL DEFAULT NULL,
	`relationship_cv_id` INT(11) NULL DEFAULT NULL,
	`gender_cv_id` INT(11) NULL DEFAULT NULL,
	`dateOfBirth` DATE NULL DEFAULT NULL,
	`age` SMALLINT(3) NULL DEFAULT NULL,
	`occupation_details_cv_id` INT(11) NULL DEFAULT NULL,
	`education_cv_id` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `client_id` (`client_id`),
	INDEX `FK_f_family_details_m_code_value` (`salutation_cv_id`),
	INDEX `FK_f_family_details_m_code_value_2` (`relationship_cv_id`),
	INDEX `FK_f_family_details_m_code_value_3` (`gender_cv_id`),
	INDEX `FK_f_family_details_m_code_value_4` (`occupation_details_cv_id`),
	INDEX `FK_f_family_details_m_code_value_5` (`education_cv_id`),
	CONSTRAINT `FK_f_family_details_m_code_value` FOREIGN KEY (`salutation_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_family_details_m_code_value_2` FOREIGN KEY (`relationship_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_family_details_m_code_value_3` FOREIGN KEY (`gender_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_family_details_m_code_value_4` FOREIGN KEY (`occupation_details_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_family_details_m_code_value_5` FOREIGN KEY (`education_cv_id`) REFERENCES `m_code_value` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=22
;



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_FAMILYDETAIL', 'FAMILYDETAIL', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_FAMILYDETAIL', 'FAMILYDETAIL', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_FAMILYDETAIL', 'FAMILYDETAIL', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'CREATE_FAMILYDETAIL_CHECKER', 'FAMILYDETAIL', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'UPDATE_FAMILYDETAIL_CHECKER', 'FAMILYDETAIL', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portflio', 'DELETE_FAMILYDETAIL_CHECKER', 'FAMILYDETAIL', 'DELETE', 0);


INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('Salutation', 0);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('Relationship', 1);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('Education', 0);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('Occupation', 0);
