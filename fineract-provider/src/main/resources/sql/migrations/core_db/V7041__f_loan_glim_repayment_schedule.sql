CREATE TABLE `f_loan_glim_repayment_schedule` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`glim_id` INT(11) NOT NULL,
	`loan_repayment_schedule_id` BIGINT(20) NOT NULL,
	`principal_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`interest_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`fee_charges_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`penalty_charges_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_loan_glim_rc_glim_id` (`glim_id`),
	INDEX `FK_f_loan_glim_rc_lrs_id` (`loan_repayment_schedule_id`),
	CONSTRAINT `FK_f_loan_glim_rc_glim_id` FOREIGN KEY (`glim_id`) REFERENCES `m_loan_glim` (`id`),
	CONSTRAINT `FK_f_loan_glim_rc_lrs_id` FOREIGN KEY (`loan_repayment_schedule_id`) REFERENCES `m_loan_repayment_schedule` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;