ALTER TABLE `f_family_details`
	CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT FIRST,
	ADD COLUMN `is_dependent` TINYINT(1) NULL DEFAULT NULL AFTER `education_cv_id`,
	ADD COLUMN `is_serious_illness` TINYINT(1) NULL DEFAULT NULL AFTER `is_dependent`,
	ADD COLUMN `is_deceased` TINYINT(1) NULL DEFAULT NULL AFTER `is_serious_illness`,
	ADD COLUMN `createdby_id` BIGINT(20) NOT NULL AFTER `is_deceased`,
	ADD COLUMN `created_date` DATETIME NOT NULL AFTER `createdby_id`,
	ADD COLUMN `lastmodifiedby_id` BIGINT(20) NOT NULL AFTER `created_date`,
	ADD COLUMN `lastmodified_date` DATETIME NOT NULL AFTER `lastmodifiedby_id`,
	ADD CONSTRAINT `FK_f_family_details_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	ADD CONSTRAINT `FK_f_family_details_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`);
	
ALTER TABLE `f_family_details`
	DROP INDEX `client_id`,
	ADD INDEX `FK_f_family_details_m_client_id` (`client_id`),
	ADD CONSTRAINT `FK_f_family_details_m_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`);
	
ALTER TABLE `f_family_details`
	DROP FOREIGN KEY `FK_f_family_details_m_code_value_4`;
	
ALTER TABLE `f_family_details`
	CHANGE COLUMN `occupation_details_cv_id` `occupation_details_id` BIGINT(20) NULL DEFAULT NULL AFTER `age`;
	
ALTER TABLE `f_family_details`
	ADD CONSTRAINT `FK_f_family_details_occupation_details_id` FOREIGN KEY (`occupation_details_id`) REFERENCES `f_income_expense` (`id`);
	
CREATE TABLE `f_family_details_summary` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`no_of_family_members` TINYINT(2) NOT NULL,
	`no_of_dependent_minors` TINYINT(2) NULL DEFAULT NULL,
	`no_of_dependent_adults` TINYINT(2) NULL DEFAULT NULL,
	`no_of_dependent_seniors` TINYINT(2) NULL DEFAULT NULL,
	`no_of_dependents_with_serious_illness` TINYINT(2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_family_details_summary_client_id` (`client_id`),
	CONSTRAINT `FK_f_family_details_summary_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('portfolio', 'CREATE_FAMILYDETAILSSUMMARY', 'FAMILYDETAILSSUMMARY', 'CREATE', 0),
('portfolio', 'CREATE_FAMILYDETAILSSUMMARY_CHECKER', 'FAMILYDETAILSSUMMARY', 'CREATE_CHECKER', 0),
('portfolio', 'UPDATE_FAMILYDETAILSSUMMARY', 'FAMILYDETAILSSUMMARY', 'UPDATE', 0),
('portfolio', 'UPDATE_FAMILYDETAILSSUMMARY_CHECKER', 'FAMILYDETAILSSUMMARY', 'UPDATE_CHECKER', 0),
('portfolio', 'DELETE_FAMILYDETAILSSUMMARY', 'FAMILYDETAILSSUMMARY', 'DELETE', 0),
('portfolio', 'DELETE_FAMILYDETAILSSUMMARY_CHECKER', 'FAMILYDETAILSSUMMARY', 'DELETE_CHECKER', 0),
('portfolio', 'READ_FAMILYDETAILSSUMMARY', 'FAMILYDETAILSSUMMARY', 'READ', 0);