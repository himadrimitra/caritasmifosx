INSERT INTO `c_external_service` (`id`,`name`) VALUES (8,'NACH');

INSERT INTO `c_external_service_properties` (`name`, `external_service_id`) VALUES ('PROCESSOR_QUALIFIER', (select id From c_external_service where name='NACH'));
INSERT INTO `c_external_service_properties` (`name`, `external_service_id`) VALUES ('CORPORATE_UTILITY_CODE', (select id From c_external_service where name='NACH'));
INSERT INTO `c_external_service_properties` (`name`, `external_service_id`) VALUES ('CORPORATE_UTILITY_NAME', (select id From c_external_service where name='NACH'));
INSERT INTO `c_external_service_properties` (`name`, `external_service_id`) VALUES ('SPONSOR_BANK', (select id From c_external_service where name='NACH'));
INSERT INTO `c_external_service_properties` (`name`, `external_service_id`) VALUES ('SPONSOR_BANK_CODE', (select id From c_external_service where name='NACH'));


CREATE TABLE `f_loan_mandates` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`loan_id` BIGINT NOT NULL,
	`mandate_status_enum` SMALLINT(4) NOT NULL,
	`request_date` DATE NOT NULL,
	`umrn` VARCHAR(20) NULL,
	`bank_account_holder_name` VARCHAR(100) NOT NULL,
	`bank_name` VARCHAR(20) NOT NULL,
	`branch_name` VARCHAR(50) NOT NULL,
	`bank_account_number` VARCHAR(20) NOT NULL,
	`micr` VARCHAR(10) NULL,
	`ifsc` VARCHAR(10) NULL,
	`account_type_enum` SMALLINT(4) NOT NULL,
	`period_from_date` DATE NOT NULL,
	`period_to_date` DATE NULL,
	`period_until_cancelled` TINYINT(1) NULL,
	`debit_type_enum` SMALLINT(4) NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`debit_frequency_enum` SMALLINT(4) NOT NULL,
	`scanned_document_id` INT(20) NOT NULL,
	`return_reason` VARCHAR(100) NULL,
	`return_process_date` DATE NULL,
	`return_process_reference_id` VARCHAR(50) NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK1_mandate_loanid` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
	CONSTRAINT `FK2_mandate_documentid` FOREIGN KEY (`scanned_document_id`) REFERENCES `m_document` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `f_mandates_process` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`request_date` DATE NOT NULL,
	`process_type` SMALLINT(4) NOT NULL,
	`process_status` SMALLINT(4) NOT NULL,
	`office_id` BIGINT(20) NOT NULL,
	`include_child_offices` TINYINT(1) NOT NULL DEFAULT '0',
	`include_mandate_scans` TINYINT(1) NULL DEFAULT NULL,
	`payment_due_start_date` DATE NULL DEFAULT NULL,
	`payment_due_end_date` DATE NULL DEFAULT NULL,
	`include_failed_transactions` VARCHAR(1000) NULL DEFAULT NULL,
	`document_id` INT(20) NULL DEFAULT NULL,
	`failed_reason_code` VARCHAR(50) NULL DEFAULT NULL,
	`failed_reason_desc` VARCHAR(200) NULL DEFAULT NULL,
	`total_records` INT NULL DEFAULT NULL,
	`success_records` INT NULL DEFAULT NULL,
	`failed_records` INT NULL DEFAULT NULL,
	`unprocessed_records` INT NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_mandate_process_office` (`office_id`),
	INDEX `FK2_mandate_process_document` (`document_id`),
	CONSTRAINT `FK1_mandate_process_office` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`),
	CONSTRAINT `FK2_mandate_process_document` FOREIGN KEY (`document_id`) REFERENCES `m_document` (`id`)
)
ENGINE=InnoDB
;

CREATE TABLE `f_mandate_transactions` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`mandate_id` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NOT NULL,
	`payment_due_amount` DECIMAL(19,6) NOT NULL,
	`payment_due_date` DATE NOT NULL,
	`request_date` DATE NOT NULL,
	`status` SMALLINT(4) NOT NULL,
	`return_process_date` DATE NULL,
	`return_process_reference_id` VARCHAR(50) NULL,
	`return_reason` VARCHAR(100) NULL,
	`repayment_transaction_id` BIGINT(20) NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK1_mandatetransactions_mandate` FOREIGN KEY (`mandate_id`) REFERENCES `f_loan_mandates` (`id`),
	CONSTRAINT `FK2_mandatetransactions_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('portfolio', 'READ_MANDATE', 'MANDATE', 'READ', 0),
	('portfolio', 'CREATE_MANDATE', 'MANDATE', 'CREATE', 1),
	('portfolio', 'CREATE_MANDATE_CHECKER', 'MANDATE', 'CREATE_CHECKER', 0), 
	('portfolio', 'UPDATE_MANDATE', 'MANDATE', 'UPDATE', 1),
	('portfolio', 'UPDATE_MANDATE_CHECKER', 'MANDATE', 'UPDATE_CHECKER', 0),
	('portfolio', 'CANCEL_MANDATE', 'MANDATE', 'CANCEL', 1),
	('portfolio', 'CANCEL_MANDATE_CHECKER', 'MANDATE', 'CANCEL_CHECKER', 0),
	('portfolio', 'EDIT_MANDATE', 'MANDATE', 'EDIT', 1),
	('portfolio', 'EDIT_MANDATE_CHECKER', 'MANDATE', 'EDIT_CHECKER', 0),
	('portfolio', 'DELETE_MANDATE', 'MANDATE', 'DELETE', 1),
	('portfolio', 'DELETE_MANDATE_CHECKER', 'MANDATE', 'DELETE_CHECKER', 0),
	('mandates', 'READ_MANDATES', 'MANDATES', 'READ', 0),
	('mandates', 'MANDATES_DOWNLOAD_MANDATES', 'MANDATES', 'MANDATES_DOWNLOAD', 0),
	('mandates', 'MANDATES_UPLOAD_MANDATES', 'MANDATES', 'MANDATES_UPLOAD', 0),
	('mandates', 'TRANSACTIONS_DOWNLOAD_MANDATES', 'MANDATES', 'TRANSACTIONS_DOWNLOAD', 0),
	('mandates', 'TRANSACTIONS_UPLOAD_MANDATES', 'MANDATES', 'TRANSACTIONS_UPLOAD', 0);