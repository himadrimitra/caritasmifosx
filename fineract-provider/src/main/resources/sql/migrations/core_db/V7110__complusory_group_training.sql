CREATE TABLE `f_cgt` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_officer_id` BIGINT(20) NULL DEFAULT NULL,
	`unique_id` VARCHAR(50) NOT NULL,
	`expected_start_date` DATE NULL DEFAULT NULL,
	`expected_end_date` DATE NULL DEFAULT NULL,
	`actual_start_date` DATE NULL DEFAULT NULL,
	`actual_end_date` DATE NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	`entity_type` SMALLINT(5) NULL DEFAULT NULL,
	`entity_type_Id` SMALLINT(5) NULL DEFAULT NULL,
	`cgt_status` SMALLINT(5) NOT NULL,
	`location` VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_id` (`unique_id`),
	INDEX `FK_f_cgt_m_staff_loan_officer` (`loan_officer_id`),
	INDEX `FK_f_cgt_m_center` (`entity_type`),
	CONSTRAINT `FK_f_cgt_m_staff_loan_officer` FOREIGN KEY (`loan_officer_id`) REFERENCES `m_staff` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;


CREATE TABLE `f_cgt_client` (
	`cgt_id` BIGINT(20) NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	INDEX `FK_f_cgt_client_f_cgt_id` (`cgt_id`),
	INDEX `FK_f_cgt_client_f_client_id` (`client_id`),
	CONSTRAINT `FK_f_cgt_client_f_cgt_id` FOREIGN KEY (`cgt_id`) REFERENCES `f_cgt` (`id`),
	CONSTRAINT `FK_f_cgt_client_f_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `f_cgt_day` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`cgt_id` BIGINT(20) NULL DEFAULT NULL,
	`scheduled_date` DATE NULL DEFAULT NULL,
	`loan_officer_id` BIGINT(20) NULL DEFAULT NULL,
	`cgt_day_status` SMALLINT(5) NOT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	`completed_date` DATE NULL DEFAULT NULL,
	`location` VARCHAR(50) NULL DEFAULT NULL,
	`cgt_day_name` VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_cgt_sub_f_cgt_id` (`cgt_id`),
	INDEX `FK_f_cgt_sub_m_staff` (`loan_officer_id`),
	CONSTRAINT `FK_f_cgt_sub_f_cgt_id` FOREIGN KEY (`cgt_id`) REFERENCES `f_cgt` (`id`),
	CONSTRAINT `FK_f_cgt_sub_m_staff` FOREIGN KEY (`loan_officer_id`) REFERENCES `m_staff` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `f_cgt_day_client` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`cgt_day_id` BIGINT(20) NULL DEFAULT NULL,
	`client_id` BIGINT(20) NULL DEFAULT NULL,
	`attendance` SMALLINT(5) NULL DEFAULT '1',
	PRIMARY KEY (`id`),
	INDEX `FK_f_cgt_sub_client_f_cgt_sub_id` (`cgt_day_id`),
	INDEX `FK_f_cgt_sub_client_f_client_id` (`client_id`),
	CONSTRAINT `FK_f_cgt_sub_client_f_cgt_day_id` FOREIGN KEY (`cgt_day_id`) REFERENCES `f_cgt_day` (`id`),
	CONSTRAINT `FK_f_cgt_sub_client_m_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE `m_note`
	ADD COLUMN `cgt_id` BIGINT(20) NULL DEFAULT NULL AFTER `lastmodifiedby_id`,
	ADD COLUMN `cgt_day_id` BIGINT(20) NULL DEFAULT NULL AFTER `cgt_id`,
	ADD CONSTRAINT `FK_m_note_f_cgt_id` FOREIGN KEY (`cgt_id`) REFERENCES `f_cgt` (`id`),
	ADD CONSTRAINT `FK_m_note_f_cgt_day_id` FOREIGN KEY (`cgt_day_id`) REFERENCES `f_cgt_day` (`id`);
	
INSERT IGNORE INTO `c_configuration` (`name`,`value`)
	VALUES ('enable-cgt', null),
			 ('min-cgt-days', 3),
			 ('max-cgt-days', 1);
			 
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
	VALUES ('portfolio_center', 'READ_CGT', 'CGT', 'READ', 0),
				('portfolio_center', 'CREATE_CGT', 'CGT', 'CREATE', 0),
				('portfolio_center', 'CREATE_CGT_CHECKER', 'CGT', 'CREATE_CHECKER', 0),
				('portfolio_center', 'UPDATE_CGT', 'CGT', 'UPDATE', 0),
				('portfolio_center', 'UPDATE_CGT_CHECKER', 'CGT', 'UPDATE_CHECKER', 0),
				('portfolio_center', 'DELETE_CGT', 'CGT', 'DELETE', 0),
				('portfolio_center', 'DELETE_CGT_CHECKER', 'CGT', 'DELETE_CHECKER', 0),
				('portfolio_center', 'READ_CGTDAY', 'CGTDAY', 'READ', 0),
				('portfolio_center', 'CREATE_CGTDAY', 'CGTDAY', 'CREATE', 0),
				('portfolio_center', 'CREATE_CGTDAY_CHECKER', 'CGTDAY', 'CREATE_CHECKER', 0),
				('portfolio_center', 'UPDATE_CGTDAY', 'CGTDAY', 'UPDATE', 0),
				('portfolio_center', 'UPDATE_CGTDAY_CHECKER', 'CGTDAY', 'UPDATE_CHECKER', 0),
				('portfolio_center', 'DELETE_CGTDAY', 'CGTDAY', 'DELETE', 0),
				('portfolio_center', 'DELETE_CGTDAY_CHECKER', 'CGTDAY', 'DELETE_CHECKER', 0),
				('portfolio_center', 'COMPLETE_CGT', 'CGT', 'COMPLETE', 0),
				('portfolio_center', 'COMPLETE_CGT_CHECKER', 'CGT', 'COMPLETE_CHECKER', 0),
				('portfolio_center', 'REJECT_CGT', 'CGT', 'REJECT', 0),
				('portfolio_center', 'REJECT_CGT_CHECKER', 'CGT', 'REJECT_CHECKER', 0),
				('portfolio_center', 'COMPLETE_CGTDAY', 'CGTDAY', 'COMPLETE', 0),
				('portfolio_center', 'COMPLETE_CGTDAY_CHECKER', 'CGTDAY', 'COMPLETE_CHECKER', 0);
				
				
				
				
				
				
				
				
				
				