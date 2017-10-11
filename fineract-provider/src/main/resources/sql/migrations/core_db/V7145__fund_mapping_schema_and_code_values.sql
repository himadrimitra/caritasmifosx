INSERT INTO m_code (code_name , is_system_defined) VALUES
 ("FundSource",0),
 ("FacilityType",1),
 ("Category",0),
 ("FundRepaymentFrequency",0);
 
 INSERT INTO m_code_value (code_id, code_value, is_active) VALUES 
 ((select id from m_code where code_name = 'FacilityType'),"Own",1),
 ((select id from m_code where code_name = 'FacilityType'),"Term",1),
 ((select id from m_code where code_name = 'FacilityType'),"Buyout",1),
 ((select id from m_code where code_name = 'FacilityType'),"Secuterization",1),
 ((select id from m_code where code_name = 'FacilityType'),"Assignment",1);
 
 
ALTER TABLE `m_fund` 
  ADD COLUMN  `fund_source` INT NULL DEFAULT NULL,
  ADD COLUMN `fund_category` INT NULL DEFAULT NULL,
  ADD COLUMN  `facility_type` INT NULL DEFAULT NULL,
  ADD COLUMN  `assignment_start_date` DATE NULL DEFAULT NULL,
  ADD COLUMN  `assignment_end_date` DATE NULL DEFAULT NULL,
  ADD COLUMN  `sanctioned_date` DATE NULL DEFAULT NULL,
  ADD COLUMN  `sanctioned_amount` DECIMAL(19,6) NULL DEFAULT NULL,
  ADD COLUMN  `disbursed_date` DATE NULL DEFAULT NULL,
  ADD COLUMN  `disbursed_amount` DECIMAL(19,6) NULL DEFAULT NULL,
  ADD COLUMN  `maturity_date` DATE NULL DEFAULT NULL,
  ADD COLUMN  `interest_rate` DECIMAL(19,6) NULL DEFAULT NULL,
  ADD COLUMN  `fund_repayment_frequency` INT NULL DEFAULT NULL,
  ADD COLUMN  `tenure_frequency` INT NULL DEFAULT NULL,
  ADD COLUMN  `tenure` INT NULL DEFAULT NULL,
  ADD COLUMN  `morotorium_frequency` INT NULL DEFAULT NULL,
  ADD COLUMN  `morotorium` INT  NULL DEFAULT NULL,
  ADD COLUMN  `loan_portfolio_fee` DECIMAL(19,6)  NULL DEFAULT NULL,
  ADD COLUMN  `book_debt_hypothecation` DECIMAL(19,6)  NULL DEFAULT NULL,
  ADD COLUMN  `cash_collateral` DECIMAL(19,6)  NULL DEFAULT NULL,
  ADD COLUMN  `personal_gurantee` TEXT NULL DEFAULT NULL,
  ADD COLUMN  `is_active` TINYINT(1) NOT NULL DEFAULT '1',
  ADD COLUMN  `is_loan_assigned` TINYINT(1) NOT NULL DEFAULT '0',
  ADD COLUMN  `createdby_id` BIGINT(20) NULL DEFAULT NULL,
  ADD COLUMN  `created_date` DATE NULL DEFAULT NULL,
  ADD COLUMN  `lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
  ADD COLUMN  `lastmodified_date` DATE NULL DEFAULT NULL,
  ADD FOREIGN KEY (`fund_source`) REFERENCES `m_code_value` (`id`),
  ADD FOREIGN KEY (`facility_type`) REFERENCES `m_code_value` (`id`),
  ADD FOREIGN KEY (`fund_repayment_frequency`) REFERENCES `m_code_value` (`id`),
  ADD FOREIGN KEY (`fund_category`) REFERENCES `m_code_value` (`id`),
  ADD FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  ADD FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`);
  
  UPDATE m_fund SET facility_type = 
  (select id from m_code_value where code_value = 'Own' and 
  code_id = ((select id from m_code where code_name = 'FacilityType'))) where facility_type IS NULL;
  
 
 CREATE TABLE `f_fund_loan_purpose` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`fund_id` BIGINT(20) NULL DEFAULT NULL,
	`loan_purpose_id` INT(11) NOT NULL,
	`loan_purpose_amount` DECIMAL(19,6) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_fund_loan_purpose_m_fund` (`fund_id`),
	CONSTRAINT `FK_f_fund_loan_purpose_m_fund` FOREIGN KEY (`fund_id`) REFERENCES `m_fund` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('organisation', 'ASSIGN_FUND', 'FUND', 'ASSIGN', 0),
('organisation', 'ACTIVATE_FUND', 'FUND', 'ACTIVATE', 0),
('organisation', 'DEACTIVATE_FUND', 'FUND', 'DEACTIVATE', 0);

CREATE TABLE `f_fund_mapping_history` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`fund_id` BIGINT(20) NOT NULL,
	`loan_id` BIGINT(20) NOT NULL,
	`assignment_date` DATE NOT NULL,
	`assignment_end_date` DATE NULL DEFAULT NULL,
	`created_by` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_fund_mapping_history_m_fund` (`fund_id`),
	INDEX `FK_f_fund_mapping_history_m_loan` (`loan_id`),
	INDEX `FK3_f_fund_mapping_history_m_loan` (`created_by`),
	CONSTRAINT `FK3_f_fund_mapping_history_m_loan` FOREIGN KEY (`created_by`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_fund_mapping_history_m_fund` FOREIGN KEY (`fund_id`) REFERENCES `m_fund` (`id`),
	CONSTRAINT `FK_f_fund_mapping_history_m_loan` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
