ALTER TABLE `f_bank_statement_details`
	ADD COLUMN `payment_type` VARCHAR(50) NULL ,
	ADD COLUMN `payment_detail_account_number` VARCHAR(50) NULL ,
	ADD COLUMN `payment_detail_cheque_number` VARCHAR(50) NULL ,
	ADD COLUMN `routing_code` VARCHAR(50) NULL ,
	ADD COLUMN `payment_detail_bank_number` VARCHAR(50) NULL ,
	ADD COLUMN `note` VARCHAR(50) NULL,
	ADD COLUMN `savings_account_number` VARCHAR(50) NULL DEFAULT NULL,
	ADD COLUMN `error_msg` TEXT NULL DEFAULT NULL ;
	
ALTER TABLE `f_bank_statement`
	ADD COLUMN `statement_type` TINYINT(2) NOT NULL DEFAULT '1';
	
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('bulk_transaction', 'CREATE_BULKPORTFOLIOTRANSACTIONS', 'BULKPORTFOLIOTRANSACTIONS', 'CREATE', 0);