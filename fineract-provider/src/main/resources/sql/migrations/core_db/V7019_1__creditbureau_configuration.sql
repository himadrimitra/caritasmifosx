
DROP TABLE IF EXISTS `f_existing_loan`;
DROP TABLE IF EXISTS `f_creditbureau_report_summary`;
DROP TABLE IF EXISTS `f_loan_creditbureau_enquiry`;
DROP TABLE IF EXISTS `f_creditbureau_enquiry`;

DROP TABLE IF EXISTS `f_creditbureau_loanproduct_mapping`;
DROP TABLE IF EXISTS `f_creditbureau_configuration`;
DROP TABLE IF EXISTS `f_creditbureau_product`;


CREATE TABLE `f_creditbureau_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `product` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL,
  `implementation_key` varchar(100) NOT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_creditbureau_product_name` (`name`,`product`,`country`),
  UNIQUE KEY `uk_f_creditbureau_product_key` (`implementation_key`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;

INSERT INTO f_creditbureau_product (name, product, country, implementation_key) values ("Highmark","OLP","INDIA","india.highmark.olp");

CREATE TABLE `f_creditbureau_configuration` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creditbureau_implementation_key` varchar(100) NOT NULL,
  `config_key` varchar(50) NOT NULL,
  `value` varchar(512) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `is_configurable` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_creditbureau_configuration` (`creditbureau_implementation_key`,`config_key`),
  CONSTRAINT `f_creditbureau_configuration_implementation_key` FOREIGN KEY (`creditbureau_implementation_key`) REFERENCES `f_creditbureau_product` (`implementation_key`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;

CREATE TABLE `f_creditbureau_loanproduct_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creditbureau_product_id` bigint(20) NOT NULL,
  `loan_product_id` bigint(20) NOT NULL,
  `is_creditcheck_mandatory` tinyint(1) DEFAULT NULL,
  `skip_creditcheck_in_failure` tinyint(1) DEFAULT NULL,
  `stale_period` int(11) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_creditbureau_loanproduct_mapping` (`creditbureau_product_id`,`loan_product_id`),
  CONSTRAINT `fk_f_creditbureau_loanproduct_mapping_product_id` FOREIGN KEY (`creditbureau_product_id`) REFERENCES `f_creditbureau_product` (`id`),
  CONSTRAINT `fk_f_creditbureau_loanproduct_mapping_loan_product_id` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;


DROP TABLE IF EXISTS `f_existing_loan`;
DROP TABLE IF EXISTS `f_creditbureau_report_summary`;
DROP TABLE IF EXISTS `f_loan_creditbureau_enquiry`;
DROP TABLE IF EXISTS `f_creditbureau_enquiry`;

CREATE TABLE IF NOT EXISTS `f_creditbureau_enquiry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creditbureau_product_id`  bigint(20) NOT NULL,
  `type` varchar(20) NOT NULL, -- Individual or bathc
  `request` text,
  `response` mediumtext,
  `acknowledgement_num` varchar(100) DEFAULT NULL,
  `status` tinyint(2) NOT NULL,
  `createdby_id` BIGINT(20) NOT NULL,
  `created_date` DATETIME NOT NULL,
  `lastmodifiedby_id` BIGINT(20) NOT NULL,
  `lastmodified_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_f_creditbureau_enquiry_product_id` FOREIGN KEY (`creditbureau_product_id`) REFERENCES `f_creditbureau_product` (`id`),
  CONSTRAINT `fk_f_creditbureau_enquiry_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `fk_f_creditbureau_enquiry_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;



CREATE TABLE IF NOT EXISTS `f_loan_creditbureau_enquiry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creditbureau_enquiry_id` bigint(20) NOT NULL,
  `reference_num` varchar(100) NOT NULL, -- time in milli secs
  `cb_report_id` varchar(100) NULL,
  `client_id` bigint(20) NOT NULL,
  `loan_id` bigint(20)  NULL,
  `loan_application_id` bigint(20)  NULL,
  `status` tinyint(2) NOT NULL,
  `response` mediumtext,
  `report_generated_time` datetime NULL, -- Report generated date on bureau's server
  `file_name` varchar(100) DEFAULT NULL,
  `file_content` mediumblob DEFAULT NULL,
  `file_type` tinyint(2) DEFAULT NULL,
  `note`  varchar(100) DEFAULT NULL,
  `is_active` TINYINT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_loan_creditbureau_enquiry` (`creditbureau_enquiry_id`,`reference_num`),
  CONSTRAINT `fk_f_loan_creditbureau_enquiry_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `fk_f_loan_creditbureau_enquiry_loan_application_id` FOREIGN KEY (`loan_application_id`) REFERENCES `f_loan_application_reference` (`id`),
  CONSTRAINT `fk_f_loan_creditbureau_enquiry_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `fk_f_loan_creditbureau_enquiry_creditbureau_enquiry_id` FOREIGN KEY (`creditbureau_enquiry_id`) REFERENCES `f_creditbureau_enquiry` (`id`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `f_creditbureau_report_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `loan_creditbureau_enquiry_id` bigint(20) NOT NULL,
  `active_loan_count` smallint(5) NOT NULL,
  `closed_loan_count` smallint(5) NOT NULL,
  `delinquent_loan_count` smallint(5) NOT NULL,
  `writtenoff_loan_count`  smallint(5) NOT NULL,
  `total_outstanding` decimal(19,6) NOT NULL,
  `total_overdues` decimal(19,6) NOT NULL,
  `total_montlhy_due` decimal(19,6) NOT NULL,
  `first_borrowed_date` date DEFAULT NULL,
  `recent_borrwed_date` date DEFAULT NULL,
  `comments` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_f_creditbureau_report_summary` (`id`),
  CONSTRAINT `fk_f_creditbureau_report_summary_loan_creditbureau_enquiry_id` FOREIGN KEY (`loan_creditbureau_enquiry_id`) REFERENCES `f_loan_creditbureau_enquiry` (`id`)
) COLLATE='utf8_general_ci' ENGINE=InnoDB;

INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`) VALUES ((SELECT id FROM `m_code` WHERE code_name = 'ExistingLoanSource'), 'Credit Bureau', NULL, 1, NULL, 1);

CREATE TABLE `f_existing_loan` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `client_id` BIGINT(20) NOT NULL,
  `loan_application_id` BIGINT(20) NULL DEFAULT NULL,
  `loan_id` BIGINT(20) NULL DEFAULT NULL,
  `source_id` INT(11) NULL DEFAULT NULL,   -- From CB or self declared
  `creditbureau_product_id` bigint(20) NULL DEFAULT NULL,
  `loan_creditbureau_enquiry_id` BIGINT(20) NULL DEFAULT NULL,
  `lender_cv_id` INT(11) NULL DEFAULT NULL,
  `lender_name` VARCHAR(500) NULL DEFAULT NULL,
  `loantype_cv_id` INT(11) NULL DEFAULT NULL,
  `amount_borrowed` DECIMAL(19,6) NOT NULL,
  `current_outstanding` DECIMAL(19,6) NULL DEFAULT NULL,
  `amt_overdue` DECIMAL(19,6) NULL DEFAULT '0.000000',
  `written_off_amount` DECIMAL(19,6) NULL DEFAULT '0.000000',
  `loan_tenure` SMALLINT(5) NULL DEFAULT NULL,
  `loan_tenure_period_type` SMALLINT(5) NULL DEFAULT NULL,
  `repayment_frequency` SMALLINT(5) NULL DEFAULT NULL,
  `repayment_frequency_multiple_of` SMALLINT(5) NULL DEFAULT NULL,
  `installment_amount` DECIMAL(19,6) NULL DEFAULT '0.000000',
  `external_loan_purpose_cv_id` INT(11) NULL DEFAULT NULL,
  `loan_status_id` SMALLINT(5) NULL DEFAULT NULL,
  `disbursed_date` DATE NULL DEFAULT NULL,
  `maturity_date` DATE NULL DEFAULT NULL,
  `gt_0_dpd_3_mths` SMALLINT(5) NULL DEFAULT '0',
  `30_dpd_12_mths` SMALLINT(5) NULL DEFAULT '0',
  `30_dpd_24_mths` SMALLINT(5) NULL DEFAULT '0',
  `60_dpd_24_mths` SMALLINT(5) NULL DEFAULT '0',
  `remark` VARCHAR(500) NULL DEFAULT NULL,
  `archive` TINYINT(4) NULL DEFAULT '0',
  `createdby_id` BIGINT(20) NOT NULL,
  `created_date` DATETIME NOT NULL,
  `lastmodifiedby_id` BIGINT(20) NOT NULL,
  `lastmodified_date` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK_f_existing_loan_m_appuser` (`createdby_id`),
  INDEX `FK_f_existing_loan_m_appuser_last_Modified` (`lastmodifiedby_id`),
  CONSTRAINT `FK_f_existing_loan_m_appuser_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_f_existing_loan_m_appuser_last_modified` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_f_existing_loan_m_code_value` FOREIGN KEY (`external_loan_purpose_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK__m_client` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FK__m_code_value` FOREIGN KEY (`source_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK__m_code_value_bureau` FOREIGN KEY (`creditbureau_product_id`) REFERENCES `f_creditbureau_product` (`id`),
  CONSTRAINT `FK__m_code_value_enquiry` FOREIGN KEY (`loan_creditbureau_enquiry_id`) REFERENCES `f_loan_creditbureau_enquiry` (`id`),
  CONSTRAINT `FK__m_code_value_lender` FOREIGN KEY (`lender_cv_id`) REFERENCES `m_code_value` (`id`),
  CONSTRAINT `FK__m_code_value_loantype` FOREIGN KEY (`loantype_cv_id`) REFERENCES `m_code_value` (`id`)
)COLLATE='utf8_general_ci' ENGINE=InnoDB;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('creditbureau', 'CREATE_CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREATE', 0),
('creditbureau', 'CREATE_CREDIT_BUREAU_LOANPRODUCT_MAPPING_CHECKER', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREATE_CHECKER', 0),
('creditbureau', 'UPDATE_CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'UPDATE', 0),
('creditbureau', 'UPDATE_CREDIT_BUREAU_LOANPRODUCT_MAPPING_CHECKER', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'UPDATE_CHECKER', 0),
('creditbureau', 'INACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'INACTIVE', 0),
('creditbureau', 'INACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING_CHECKER', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'INACTIVE_CHECKER', 0), 
('creditbureau', 'ACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'ACTIVE', 0),
('creditbureau', 'ACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING_CHECKER', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'ACTIVE_CHECKER', 0),
('creditbureau', 'INACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'INACTIVE', 0),
('creditbureau', 'INACTIVE_CREDIT_BUREAU_LOANPRODUCT_MAPPING_CHECKER', 'CREDIT_BUREAU_LOANPRODUCT_MAPPING', 'INACTIVE_CHECKER', 0);
	
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('configuration', 'ACTIVATE_CREDITBUREAU', 'CREDITBUREAU', 'ACTIVATE', 0),
  ('configuration', 'ACTIVATE_CREDITBUREAU_CHECKER', 'CREDITBUREAU', 'ACTIVATE_CHECKER', 0),
  ('configuration', 'DEACTIVATE_CREDITBUREAU', 'CREDITBUREAU', 'DEACTIVATE', 0),
  ('configuration', 'DEACTIVATE_CREDITBUREAU_CHECKER', 'CREDITBUREAU', 'DEACTIVATE_CHECKER', 0);

INSERT INTO `c_external_service` (`id`, `name`) VALUES (6, 'HIGHMARK');


INSERT INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) VALUES 
('PRODUCTTYP', 'INDV', 6),
('PRODUCTVER', '1.0', 6),
('REQMBR', 'MFI0000131', 6),
('SUBMBRID', 'DIGAMBER FINANCE', 6),
('REQVOLTYP', 'INDV', 6),
('TESTFLG', 'HMTEST', 6),
('USERID', 'chmuat@digamberfinance.com', 6),
('PWD', '49F68D1BD8413767A160BDF8CFAFBFCE6DE83114', 6),
('AUTHFLG', 'Y', 6),
('AUTHTITLE', 'USER', 6),
('RESFRMT', 'XML/HTML', 6),
('RESFRMTEMBD', 'Y', 6),
('MEMBERPREOVERRIDE', 'N', 6),
('LOSNAME', 'MIFOS', 6),
('URL', 'http://test.highmark.in/Inquiry/doGet.service/requestResponse', 6),
('CREDTRPTID', 'CRDRQINQR', 6),
('CREDTREQTYP', 'INDV', 6),
('CREDTINQPURPSTYP', 'ACCT-ORIG', 6),
('CREDTINQPURPSTYPDESC', 'PERSONAL LOAN', 6),
('CREDITINQUIRYSTAGE', 'PRE-DISB', 6),
('CREDTRPTTRNDTTM', '01-11-2015', 6);


-- Sample Data
-- INSERT INTO f_creditbureau_product (name, product, country, implementation_key) values ("Highmark","OLP","INDIA","india.highmark.olp");
-- INSERT INTO f_creditbureau_loanproduct_mapping (creditbureau_product_id, loan_product_id) values (1,1);
select creditbure0_.id as id1_22_, creditbure0_.creditbureau_product_id as creditbu6_22_, creditbure0_.is_active as isActive2_22_, creditbure0_.is_creditcheck_mandatory as is_credi3_22_, creditbure0_.loan_product_id as loan_pro7_22_, creditbure0_.skip_creditcheck_in_failure as skip_cre4_22_, creditbure0_.stale_period as stale_pe5_22_
from f_creditbureau_loanproduct_mapping creditbure0_ where creditbure0_.loan_product_id=1;

select * from f_creditbureau_loanproduct_mapping;