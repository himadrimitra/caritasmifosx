INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('portfolio', 'CREATE_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'CREATE', 0),
('portfolio', 'CREATE_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'CREATE_CHECKER', 0),
('portfolio', 'UPDATE_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'UPDATE', 0),
('portfolio', 'UPDATE_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'UPDATE_CHECKER', 0),
('portfolio', 'DELETE_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'DELETE', 0),
('portfolio', 'DELETE_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'DELETE_CHECKER', 0),
('portfolio', 'REQUESTFORAPPROVAL_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'REQUESTFORAPPROVAL', 0),
('portfolio', 'REQUESTFORAPPROVAL_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'REQUESTFORAPPROVAL_CHECKER', 0),
('portfolio', 'REJECT_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'REJECT', 0),
('portfolio', 'REJECT_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'REJECT', 0),
('portfolio', 'APPROVE_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'APPROVE', 0),
('portfolio', 'APPROVE_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'APPROVE', 0),
('portfolio', 'UNDOAPPROVE_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'UNDOAPPROVE', 0),
('portfolio', 'UNDOAPPROVE_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'UNDOAPPROVE', 0),
('portfolio', 'DISBURSE_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'DISBURSE', 0),
('portfolio', 'DISBURSE_LOANAPPLICATIONREFERENCE_CHECKER', 'LOANAPPLICATIONREFERENCE', 'DISBURSE', 0),
('portfolio', 'READ_LOANAPPLICATIONREFERENCE', 'LOANAPPLICATIONREFERENCE', 'READ', 0);

CREATE TABLE `f_loan_application_reference` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_application_reference_no` VARCHAR(50) NOT NULL,
	`external_id_one` VARCHAR(100) NULL DEFAULT NULL,
	`external_id_two` VARCHAR(100) NULL DEFAULT NULL,
	`loan_id` BIGINT(20) NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`loan_officer_id` BIGINT(20) NULL DEFAULT NULL,
	`group_id` BIGINT(20) NULL DEFAULT NULL,
	`status_enum` SMALLINT(5) NOT NULL,
	`account_type_enum` SMALLINT(5) NULL DEFAULT NULL,
	`loan_product_id` BIGINT(20) NOT NULL,
	`loan_purpose_cv_id` INT(11) NULL DEFAULT NULL,
	`loan_amount_requested` DECIMAL(19,6) NOT NULL,
	`number_of_repayments` SMALLINT(5) NOT NULL,
	`repayment_period_frequency_enum` SMALLINT(5) NULL DEFAULT NULL,
	`repay_every` SMALLINT(5) NULL DEFAULT NULL,
	`term_period_frequency_enum` SMALLINT(5) NULL DEFAULT NULL,
	`term_frequency` SMALLINT(5) NULL DEFAULT NULL,
	`fixed_emi_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`no_of_tranche` SMALLINT(5) NULL DEFAULT NULL,
	`submittedon_date` DATE NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
 	`created_date` DATETIME NOT NULL,
 	`lastmodifiedby_id` BIGINT(20) NOT NULL,
 	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `IX_f_loan_application_reference_loan_application_reference_no` (`loan_application_reference_no`),
	UNIQUE INDEX `UQ_loan_application_reference_no` (`loan_application_reference_no`),
	UNIQUE INDEX `UQ_loan_app_ref_external_id_one` (`external_id_one`),
	UNIQUE INDEX `UQ_loan_app_ref_external_id_two` (`external_id_two`),
	UNIQUE INDEX `UQ_loan_app_ref_loan_id` (`loan_id`),
	INDEX `IX_m_client_id_to_f_loan_application_reference_client_id` (`client_id`),
	INDEX `IX_m_staff_id_to_f_loan_application_reference_loan_officer_id` (`loan_officer_id`),
	INDEX `IX_m_group_id_to_f_loan_application_reference_group_id` (`group_id`),
	INDEX `IX_m_product_loan_id_to_f_loan_application_reference_product_id` (`loan_product_id`),
	INDEX `IX_m_code_value_id_to_f_loan_application_reference_product_id` (`loan_purpose_cv_id`),
	CONSTRAINT `FK_m_loan_id_to_f_loan_application_reference_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
	CONSTRAINT `FK_m_client_id_to_f_loan_application_reference_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_m_staff_id_to_f_loan_application_reference_loan_officer_id` FOREIGN KEY (`loan_officer_id`) REFERENCES `m_staff` (`id`),
	CONSTRAINT `FK_m_group_id_to_f_loan_application_reference_group_id` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
	CONSTRAINT `FK_m_product_loan_id_to_f_loan_application_reference_product_id` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`),
	CONSTRAINT `FK_m_code_value_id_to_f_loan_application_reference_product_id` FOREIGN KEY (`loan_purpose_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_f_loan_application_reference_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
 	CONSTRAINT `FK_f_loan_application_reference_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_loan_application_charge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_app_ref_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	`due_for_collection_as_of_date` DATE NULL DEFAULT NULL,
	`charge_amount_or_percentage` DECIMAL(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `loan_application_charge_id` (`charge_id`),
	INDEX `f_loan_application_charge_ibfk_2` (`loan_app_ref_id`),
	CONSTRAINT `f_loan_application_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
	CONSTRAINT `f_loan_application_charge_ibfk_2` FOREIGN KEY (`loan_app_ref_id`) REFERENCES `f_loan_application_reference` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_loan_application_sanction` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_app_ref_id` BIGINT(20) NOT NULL,
	`loan_amount_approved` DECIMAL(19,6) NOT NULL,
	`approvedon_date` DATE NULL DEFAULT NULL,
	`expected_disbursement_date` DATE NULL DEFAULT NULL,
	`repayments_starting_from_date` DATE NULL DEFAULT NULL,
	`number_of_repayments` SMALLINT(5) NOT NULL,
	`repayment_period_frequency_enum` SMALLINT(5) NULL DEFAULT NULL,
	`repay_every` SMALLINT(5) NULL DEFAULT NULL,
	`term_period_frequency_enum` SMALLINT(5) NULL DEFAULT NULL,
	`term_frequency` SMALLINT(5) NULL DEFAULT NULL,
	`fixed_emi_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`max_outstanding_loan_balance` DECIMAL(19,6) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
 	`created_date` DATETIME NOT NULL,
 	`lastmodifiedby_id` BIGINT(20) NOT NULL,
 	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `IX_f_loan_app_ref_id_to_f_loan_app_sanction` (`loan_app_ref_id`),
	CONSTRAINT `FX_f_loan_app_ref_id_to_f_loan_app_sanction` FOREIGN KEY (`loan_app_ref_id`) REFERENCES `f_loan_application_reference` (`id`),
	CONSTRAINT `FK_f_loan_application_sanction_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
 	CONSTRAINT `FK_f_loan_application_sanction_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_loan_app_sanction_tranche` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_app_sanction_id` BIGINT(20) NOT NULL,
	`tranche_amount` DECIMAL(19,6) NOT NULL,
	`fixed_emi_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`expected_tranche_disbursement_date` DATE NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `IX_f_loan_app_sanction_tranche_loan_app_sanction_id` (`loan_app_sanction_id`),
	CONSTRAINT `FK_f_loan_app_sanction_tranche_loan_app_sanction_id` FOREIGN KEY (`loan_app_sanction_id`) REFERENCES `f_loan_application_sanction` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `sequences` (
	`sequence_name` VARCHAR(50) NOT NULL,
	`increment_by` INT(12) UNSIGNED NOT NULL DEFAULT '1',
	`min_value` INT(12) UNSIGNED NOT NULL DEFAULT '1',
	`max_value` BIGINT(20) UNSIGNED NOT NULL DEFAULT '999999999999999',
	`cur_value` BIGINT(20) UNSIGNED NULL DEFAULT '1',
	`is_reset_daily` TINYINT(1) NOT NULL DEFAULT '0',
	`sequence_date` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`sequence_name`)
);

INSERT INTO `sequences` (`sequence_name`, `increment_by`, `min_value`, `max_value`, `cur_value`, `is_reset_daily`, `sequence_date`) VALUES ('loan_application_reference_no', 1, 1, 999999999999999, 0, 0, CURRENT_DATE());


DELIMITER //

CREATE FUNCTION `generate_next_sequence_value` (`seq_name` VARCHAR(50))
RETURNS BIGINT(20) NOT DETERMINISTIC
BEGIN

	DECLARE overflow_flag TINYINT(1);
	DECLARE cur_val BIGINT(20);
	DECLARE step_val INT(12);
	DECLARE min_val BIGINT(12);
	DECLARE new_val BIGINT(20);
	
	SELECT
		cur_value, increment_by, min_value,
		IF (((is_reset_daily = 1) AND (CURRENT_DATE() <> sequence_date))
				OR (IF (((cur_value+increment_by) > max_value),1,0) = 1), 1, 0)
	INTO 
		cur_val, step_val, min_val, overflow_flag
	FROM
		sequences
	WHERE
		sequence_name = seq_name
	FOR UPDATE;
	
	IF (overflow_flag = 1) THEN
		SET new_val = min_val;
	ELSE
		SET new_val = cur_val + step_val;
	END IF;

	UPDATE sequences SET cur_value = new_val, sequence_date = CURRENT_DATE() WHERE sequence_name = seq_name;
	
	RETURN new_val;
	
END;
//