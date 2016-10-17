CREATE TABLE `f_loan_utilization_check` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`to_be_audited_by` BIGINT(20) NULL DEFAULT NULL,
	`audite_scheduled_on` DATE NULL DEFAULT NULL,
	`audit_done_by` BIGINT(20) NULL DEFAULT NULL,
	`audit_done_on` DATE NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_loan_utilization_check_to_be_audited_by` FOREIGN KEY (`to_be_audited_by`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_loan_utilization_check_audit_done_by` FOREIGN KEY (`audit_done_by`) REFERENCES `m_staff` (`id`)
)AUTO_INCREMENT=1;

CREATE TABLE `f_loan_utilization_check_detail` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_utilization_check_id` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NOT NULL,
	`loan_purpose_id` BIGINT(20) NULL DEFAULT NULL,
	`is_same_as_oroginal_purpose` TINYINT(1) NOT NULL,
	`amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`comment` VARCHAR(1000) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_loan_utilization_check_detail_loan_check_id` FOREIGN KEY (`loan_utilization_check_id`) REFERENCES `f_loan_utilization_check` (`id`),
	CONSTRAINT `FK_f_loan_utilization_check_detail_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)AUTO_INCREMENT=1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('riskmanagement', 'CREATE_LOANUTILIZATIONCHECK', 'LOANUTILIZATIONCHECK', 'CREATE', 0),
('riskmanagement', 'CREATE_LOANUTILIZATIONCHECK_CHECKER', 'LOANUTILIZATIONCHECK', 'CREATE_CHECKER', 0),
('riskmanagement', 'UPDATE_LOANUTILIZATIONCHECK', 'LOANUTILIZATIONCHECK', 'UPDATE', 0),
('riskmanagement', 'UPDATE_LOANUTILIZATIONCHECK_CHECKER', 'LOANUTILIZATIONCHECK', 'UPDATE_CHECKER', 0),
('riskmanagement', 'DELETE_LOANUTILIZATIONCHECK', 'LOANUTILIZATIONCHECK', 'DELETE', 0),
('riskmanagement', 'DELETE_LOANUTILIZATIONCHECK_CHECKER', 'LOANUTILIZATIONCHECK', 'DELETE_CHECKER', 0),
('riskmanagement', 'READ_LOANUTILIZATIONCHECK', 'LOANUTILIZATIONCHECK', 'READ', 0);