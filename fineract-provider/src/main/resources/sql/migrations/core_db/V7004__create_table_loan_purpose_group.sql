CREATE TABLE `f_loan_purpose_group` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL, -- Agri,Service,Production
	`short_name` VARCHAR(30) NOT NULL,
	`description` VARCHAR(500) NULL DEFAULT NULL,
	`type_enum_id` TINYINT(2) NOT NULL, -- Category,Classification
	`is_active` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_loan_purpose_group_short_name` (`short_name`),
	UNIQUE INDEX `UQ_f_loan_purpose_group` (`name`, `type_enum_id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('riskmanagement', 'CREATE_LOANPURPOSEGROUP', 'LOANPURPOSEGROUP', 'CREATE', 0),
('riskmanagement', 'CREATE_LOANPURPOSEGROUP_CHECKER', 'LOANPURPOSEGROUP', 'CREATE_CHECKER', 0),
('riskmanagement', 'UPDATE_LOANPURPOSEGROUP', 'LOANPURPOSEGROUP', 'UPDATE', 0),
('riskmanagement', 'UPDATE_LOANPURPOSEGROUP_CHECKER', 'LOANPURPOSEGROUP', 'UPDATE_CHECKER', 0),
('riskmanagement', 'ACTIVATE_LOANPURPOSEGROUP', 'LOANPURPOSEGROUP', 'ACTIVATE', 0),
('riskmanagement', 'ACTIVATE_LOANPURPOSEGROUP_CHECKER', 'LOANPURPOSEGROUP', 'ACTIVATE_CHECKER', 0),
('riskmanagement', 'INACTIVATE_LOANPURPOSEGROUP', 'LOANPURPOSEGROUP', 'INACTIVATE', 0),
('riskmanagement', 'INACTIVATE_LOANPURPOSEGROUP_CHECKER', 'LOANPURPOSEGROUP', 'INACTIVATE_CHECKER', 0),
('riskmanagement', 'DELETE_LOANPURPOSEGROUP', 'LOANPURPOSEGROUP', 'DELETE', 0),
('riskmanagement', 'DELETE_LOANPURPOSEGROUP_CHECKER', 'LOANPURPOSEGROUP', 'DELETE_CHECKER', 0),
('riskmanagement', 'READ_LOANPURPOSEGROUP', 'LOANPURPOSEGROUP', 'READ', 0);

CREATE TABLE `f_loan_purpose` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL,
	`short_name` VARCHAR(30) NOT NULL,
	`description` VARCHAR(500) NULL DEFAULT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_loan_purpose_short_name` (`short_name`),
	CONSTRAINT `FK_f_loan_purpose_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_loan_purpose_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_loan_purpose_group_mapping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_purpose_group_id` BIGINT(20) NOT NULL,
	`loan_purpose_id` BIGINT(20) NOT NULL,
	`is_active` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	-- UNIQUE INDEX `UQ_f_loan_purpose_group` (`loan_purpose_group_id`, `loan_purpose_id`),
	CONSTRAINT `FK_f_loan_purpose_group_mapping_loan_purpose_group_id` FOREIGN KEY (`loan_purpose_group_id`) REFERENCES `f_loan_purpose_group` (`id`),
	CONSTRAINT `FK_f_loan_purpose_group_mapping_loan_purpose_id` FOREIGN KEY (`loan_purpose_id`) REFERENCES `f_loan_purpose` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('riskmanagement', 'CREATE_LOANPURPOSE', 'LOANPURPOSE', 'CREATE', 0),
('riskmanagement', 'CREATE_LOANPURPOSE_CHECKER', 'LOANPURPOSE', 'CREATE_CHECKER', 0),
('riskmanagement', 'UPDATE_LOANPURPOSE', 'LOANPURPOSE', 'UPDATE', 0),
('riskmanagement', 'UPDATE_LOANPURPOSE_CHECKER', 'LOANPURPOSE', 'UPDATE_CHECKER', 0),
('riskmanagement', 'ACTIVATE_LOANPURPOSE', 'LOANPURPOSE', 'ACTIVATE', 0),
('riskmanagement', 'ACTIVATE_LOANPURPOSE_CHECKER', 'LOANPURPOSE', 'ACTIVATE_CHECKER', 0),
('riskmanagement', 'INACTIVATE_LOANPURPOSE', 'LOANPURPOSE', 'INACTIVATE', 0),
('riskmanagement', 'INACTIVATE_LOANPURPOSE_CHECKER', 'LOANPURPOSE', 'INACTIVATE_CHECKER', 0),
('riskmanagement', 'DELETE_LOANPURPOSE', 'LOANPURPOSE', 'DELETE', 0),
('riskmanagement', 'DELETE_LOANPURPOSE_CHECKER', 'LOANPURPOSE', 'DELETE_CHECKER', 0),
('riskmanagement', 'READ_LOANPURPOSE', 'LOANPURPOSE', 'READ', 0);