CREATE TABLE `f_pdc_cheque_detail` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`bank_name` VARCHAR(100) NOT NULL,
	`branch_name` VARCHAR(100) NOT NULL,
	`account_number` VARCHAR(50) NULL DEFAULT NULL,
	`ifsc_code` VARCHAR(50) NOT NULL,
	`cheque_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`cheque_type` SMALLINT(3) NOT NULL,
	`cheque_number` VARCHAR(30) NOT NULL,
	`cheque_date` DATE NULL DEFAULT NULL,
	`present_status` SMALLINT(3) NOT NULL,
	`previous_status` SMALLINT(3) NULL DEFAULT NULL,
	`presented_date` DATE NULL DEFAULT NULL,
	`presented_description` VARCHAR(500) NULL DEFAULT NULL,
	`bounced_date` DATE NULL DEFAULT NULL,
	`bounced_description` VARCHAR(500) NULL DEFAULT NULL,
	`cleared_date` DATE NULL DEFAULT NULL,
	`cleared_description` VARCHAR(500) NULL DEFAULT NULL,
	`cancelled_date` DATE NULL DEFAULT NULL,
	`cancelled_description` VARCHAR(500) NULL DEFAULT NULL,
	`returned_date` DATE NULL DEFAULT NULL,
	`returned_description` VARCHAR(500) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `f_pdc_cheque_detail_mapping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`pdc_cheque_detail_id` BIGINT(20) NOT NULL,
	`payment_type` INT(11) NULL DEFAULT NULL,
	`entity_type` SMALLINT(3) NULL DEFAULT NULL,
	`entity_id` BIGINT(20) NULL DEFAULT NULL,
	`due_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`due_date` DATE NULL DEFAULT NULL,
	`paid_status` TINYINT(1) NOT NULL DEFAULT '0',
	`transaction_id` BIGINT(20) NULL DEFAULT NULL,
	`is_deleted` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `f_pdc_cheque_detail_mapping_UNIQUE` (`pdc_cheque_detail_id`, `entity_type`, `entity_id`, `due_date`, `is_deleted`),
	CONSTRAINT `FK1_f_pdc_cheque_detail_mapping_id` FOREIGN KEY (`pdc_cheque_detail_id`) REFERENCES `f_pdc_cheque_detail` (`id`)
);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('cheque', 'CREATE_PDC', 'PDC', 'CREATE', 0),
('cheque', 'CREATE_PDC_CHECKER', 'PDC', 'CREATE_CHECKER', 0),
('cheque', 'UPDATE_PDC', 'PDC', 'UPDATE', 0),
('cheque', 'UPDATE_PDC_CHECKER', 'PDC', 'UPDATE_CHECKER', 0),
('cheque', 'DELETE_PDC', 'PDC', 'DELETE', 0),
('cheque', 'DELETE_PDC_CHECKER', 'PDC', 'DELETE_CHECKER', 0),
('cheque', 'PRESENT_PDC', 'PDC', 'PRESENT', 0),
('cheque', 'PRESENT_PDC_CHECKER', 'PDC', 'PRESENT_CHECKER', 0),
('cheque', 'BOUNCED_PDC', 'PDC', 'BOUNCED', 0),
('cheque', 'BOUNCED_PDC_CHECKER', 'PDC', 'BOUNCED_CHECKER', 0),
('cheque', 'CLEAR_PDC', 'PDC', 'CLEAR', 0),
('cheque', 'CLEAR_PDC_CHECKER', 'PDC', 'CLEAR_CHECKER', 0),
('cheque', 'CANCEL_PDC', 'PDC', 'CANCEL', 0),
('cheque', 'CANCEL_PDC_CHECKER', 'PDC', 'CANCEL_CHECKER', 0),
('cheque', 'RETURN_PDC', 'PDC', 'RETURN', 0),
('cheque', 'RETURN_PDC_CHECKER', 'PDC', 'RETURN_CHECKER', 0),
('cheque', 'UNDO_PDC', 'PDC', 'UNDO', 0),
('cheque', 'UNDO_PDC_CHECKER', 'PDC', 'UNDO_CHECKER', 0),
('cheque', 'READ_PDC', 'PDC', 'READ', 0);
