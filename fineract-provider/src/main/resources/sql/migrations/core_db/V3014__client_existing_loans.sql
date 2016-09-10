CREATE TABLE `f_existing_loan` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`loan_application_id` BIGINT(20) NULL DEFAULT NULL,
	`loan_id` BIGINT(20) NULL DEFAULT NULL,
	`source_cv_id` INT(11) NULL DEFAULT NULL,
	`bureau_cv_id` INT(11) NULL DEFAULT NULL,
	`bureau_enq_ref_id` BIGINT(20) NULL DEFAULT NULL,
	`lender_cv_id` INT(11) NULL DEFAULT NULL,
	`lender_not_listed` VARCHAR(500) NULL DEFAULT NULL,
	`loantype_cv_id` INT(11) NULL DEFAULT NULL,
	`amount_borrowed` DECIMAL(19,6) NOT NULL,
	`current_outstanding` DECIMAL(19,6) NULL DEFAULT NULL,
	`amt_overdue` DECIMAL(19,6) NULL DEFAULT '0.000000',
	`written_off_amount` DECIMAL(19,6) NULL DEFAULT '0.000000',
	`loan_tenure` SMALLINT(5) NULL DEFAULT NULL,
	`loan_tenure_period_type` SMALLINT(5) NULL DEFAULT NULL,
	`repayment_frequency` SMALLINT(5) NULL DEFAULT NULL,
	`repayment_frequency_multiple_of` SMALLINT(5) NULL DEFAULT NULL,
	`installment_amount` DECIMAL(19,6) UNSIGNED NOT NULL,
	`external_loan_purpose_cv_id` INT(11) NULL DEFAULT NULL,
	`loan_status_id` SMALLINT(5) NULL DEFAULT NULL,
	`disbursed_date` DATE NULL DEFAULT NULL,
	`maturity_date` DATE NULL DEFAULT NULL,
	`gt_0_dpd_3_mths` SMALLINT(5) NULL DEFAULT '0',
	`30_dpd_12_mths` SMALLINT(5) NULL DEFAULT '0',
	`30_dpd_24_mths` SMALLINT(5) NULL DEFAULT '0',
	`60_dpd_24_mths` SMALLINT(5) NULL DEFAULT '0',
	`remark` VARCHAR(500) NULL DEFAULT NULL,
	`archive` TINYINT(4) NOT NULL DEFAULT '0',
	`createdby_id` BIGINT(20) NOT NULL,	
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,	
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_ext_loan_m_client` (`client_id`),
	INDEX `FK_f_ext_loan_source_cv_id` (`source_cv_id`),
	INDEX `FK_f_ext_loan_bureau_cv_id` (`bureau_cv_id`),
	INDEX `FK_f_ext_loan_lender_cv_id` (`lender_cv_id`),
	INDEX `FK_f_ext_loan_loantype_cv_id` (`loantype_cv_id`),
	INDEX `FK_f_ext_loan_createdby_id` (`createdby_id`),
	INDEX `FK_f_ext_loan_lastmodifiedby_id` (`lastmodifiedby_id`),
	INDEX `FK_f_ext_loan_external_loan_purpose_cv_id` (`external_loan_purpose_cv_id`),
	CONSTRAINT `FK_f_ext_loan_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_ext_loan_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_ext_loan_external_loan_purpose_cv_id` FOREIGN KEY (`external_loan_purpose_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_ext_loan_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_f_ext_loan_source_cv_id` FOREIGN KEY (`source_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_ext_loan_bureau_cv_id` FOREIGN KEY (`bureau_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_ext_loan_lender_cv_id` FOREIGN KEY (`lender_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_ext_loan_loantype_cv_id` FOREIGN KEY (`loantype_cv_id`) REFERENCES `m_code_value` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'SAVE_EXISTINGLOAN', 'EXISTINGLOAN', 'SAVE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'UPDATE_EXISTINGLOAN', 'EXISTINGLOAN', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'DELETE_EXISTINGLOAN', 'EXISTINGLOAN', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'READ_EXISTINGLOAN', 'EXISTINGLOAN', 'READ', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'SAVE_EXISTINGLOAN_CHECKER', 'EXISTINGLOAN', 'SAVE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'UPDATE_EXISTINGLOAN_CHECKER', 'EXISTINGLOAN', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'DELETE_EXISTINGLOAN_CHECKER', 'EXISTINGLOAN', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('existingLoan', 'READ_EXISTINGLOAN_CHECKER', 'EXISTINGLOAN', 'READ', 0);

INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('ExistingLoanSource', 1);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('BureauOption', 1);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('LenderOption', 1);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('LoanType', 1);
INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('externalLoanPurpose', 1);


