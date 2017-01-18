CREATE TABLE `f_loan_emi_packs` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_product_id` BIGINT(20) NOT NULL,
	`repay_every` SMALLINT(5) NOT NULL,
	`repayment_period_frequency_enum` SMALLINT(5) NOT NULL,
	`number_of_repayments` SMALLINT(5) NOT NULL,
	`sanction_amount` DECIMAL(19,6) NOT NULL,
	`fixed_emi` DECIMAL(19,6) NOT NULL,
	`disbursal_1_amount` DECIMAL(19,6) NULL,
	`disbursal_2_amount` DECIMAL(19,6) NULL,
	`disbursal_3_amount` DECIMAL(19,6) NULL,
	`disbursal_4_amount` DECIMAL(19,6) NULL,
	`disbursal_2_emi` SMALLINT(5) NULL,
	`disbursal_3_emi` SMALLINT(5) NULL,
	`disbursal_4_emi` SMALLINT(5) NULL,
	PRIMARY KEY (`id`),
	INDEX `loan_product_id` (`loan_product_id`),
	CONSTRAINT `FK1_loan_emi_packs_lp` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('portfolio', 'READ_LOANEMIPACKS', 'LOANEMIPACKS', 'READ', 0),
	('portfolio', 'CREATE_LOANEMIPACKS', 'LOANEMIPACKS', 'CREATE', 1),
	('portfolio', 'CREATE_LOANEMIPACKS_CHECKER', 'LOANEMIPACKS', 'CREATE_CHECKER', 0), 
	('portfolio', 'UPDATE_LOANEMIPACKS', 'LOANEMIPACKS', 'UPDATE', 1),
	('portfolio', 'UPDATE_LOANEMIPACKS_CHECKER', 'LOANEMIPACKS', 'UPDATE_CHECKER', 0),
	('portfolio', 'DELETE_LOANEMIPACKS', 'LOANEMIPACKS', 'DELETE', 1),
	('portfolio', 'DELETE_LOANEMIPACKS_CHECKER', 'LOANEMIPACKS', 'DELETE_CHECKER', 0);
	
ALTER TABLE `f_loan_application_reference`
	ADD COLUMN `loan_emi_pack_id` BIGINT(20) NULL DEFAULT NULL AFTER `expected_repayment_payment_type_id`,
	ADD CONSTRAINT `FK_loan_emi_pack_id_to_f_loan_emi_packs_id` FOREIGN KEY (`loan_emi_pack_id`) REFERENCES `f_loan_emi_packs` (`id`);
	
ALTER TABLE `f_loan_application_sanction`
	ADD COLUMN `loan_emi_pack_id` BIGINT(20) NULL DEFAULT NULL AFTER `max_outstanding_loan_balance`,
	ADD CONSTRAINT `FK1_loan_emi_pack_id_to_f_loan_emi_packs_id` FOREIGN KEY (`loan_emi_pack_id`) REFERENCES `f_loan_emi_packs` (`id`);
	

