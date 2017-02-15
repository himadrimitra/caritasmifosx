CREATE TABLE `f_loan_coapplicants_mapping` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`loan_application_reference_id` BIGINT NOT NULL,
	`client_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `loan_application_reference_id_client_id` (`loan_application_reference_id`, `client_id`),
	CONSTRAINT `FK1_coapplicants_loanrefid` FOREIGN KEY (`loan_application_reference_id`) REFERENCES `f_loan_application_reference` (`id`),
	CONSTRAINT `FK2_coapplicants_clientid` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('portfolio', 'READ_COAPPLICANTS', 'COAPPLICANTS', 'READ', 0),
	('portfolio', 'CREATE_COAPPLICANTS', 'COAPPLICANTS', 'CREATE', 0),
	('portfolio', 'DELETE_COAPPLICANTS', 'COAPPLICANTS', 'DELETE', 0);

