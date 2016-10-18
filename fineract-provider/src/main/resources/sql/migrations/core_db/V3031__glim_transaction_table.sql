CREATE TABLE `m_loan_glim_transaction` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`glim_id` INT(11) NULL DEFAULT NULL,
	`loan_transaction_id` BIGINT(20) NULL DEFAULT NULL,
	`principal_portion` DECIMAL(19,6) NULL DEFAULT NULL,
	`interest_portion` DECIMAL(19,6) NULL DEFAULT NULL,
	`fee_portion` DECIMAL(19,6) NULL DEFAULT NULL,
	`penalty_portion` DECIMAL(19,6) NULL DEFAULT NULL,
	`total_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_m_loan_glim_transaction_m_loan_glim` (`glim_id`),
	INDEX `FK_m_loan_glim_transaction_m_loan_transaction` (`loan_transaction_id`),
	CONSTRAINT `FK_m_loan_glim_transaction_m_loan_glim` FOREIGN KEY (`glim_id`) REFERENCES `m_loan_glim` (`id`),
	CONSTRAINT `FK_m_loan_glim_transaction_m_loan_transaction` FOREIGN KEY (`loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`)
);