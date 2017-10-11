DROP TABLE IF EXISTS `m_loan_glim`;

CREATE TABLE `m_loan_glim` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NULL DEFAULT NULL,
	`loanpurpose_cv_id` INT(20) NULL DEFAULT NULL,
	`proposed_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`approved_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`disbursed_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`is_client_selected` tinyint(1) NOT NULL DEFAULT '1',
	PRIMARY KEY (`id`),
	INDEX `FK_m_loan_glim_m_client` (`client_id`),
	INDEX `FK_m_loan_glim_m_loan` (`loan_id`),
	INDEX `FK_m_loan_glim_m_code_value` (`loanpurpose_cv_id`),
	CONSTRAINT `FK_m_loan_glim_m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_m_loan_glim_m_code_value` FOREIGN KEY (`loanpurpose_cv_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK_m_loan_glim_m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;