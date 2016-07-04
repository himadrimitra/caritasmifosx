DROP TABLE IF EXISTS `f_bank_statement_details`;
DROP TABLE IF EXISTS `f_bank_statement`;
DROP TABLE IF EXISTS `f_bank`;

CREATE TABLE `f_bank` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(250) NOT NULL,
	`gl_account` BIGINT(20) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_f_bank_acc_gl_account` (`gl_account`),
	CONSTRAINT `FK1_f_bank_acc_gl_account` FOREIGN KEY (`gl_account`) REFERENCES `acc_gl_account` (`id`)
	
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `f_bank_statement` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(250) NOT NULL,
	`description` VARCHAR(250) NULL DEFAULT NULL,
	`cif_key_document_id` INT(20) NULL DEFAULT NULL,
	`org_statement_key_document_id` INT(20) NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATE NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodified_date` DATE NULL DEFAULT NULL,
	`is_reconciled` TINYINT(1) NULL DEFAULT '0',
	`bank` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_f_bank_statement_f_bank` (`bank`),
	INDEX `FK1_f_bank_statement_m_appuser` (`createdby_id`),
	INDEX `FK2_f_bank_statement_m_appuser` (`lastmodifiedby_id`),
	INDEX `FK1_f_bank_statement_m_document` (`cif_key_document_id`),
	INDEX `FK2_f_bank_statement_m_document` (`org_statement_key_document_id`),
	CONSTRAINT `FK1_f_bank_statement_f_bank` FOREIGN KEY (`bank`) REFERENCES `f_bank` (`id`),
	CONSTRAINT `FK1_f_bank_statement_m_appuser` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK1_f_bank_statement_m_document` FOREIGN KEY (`cif_key_document_id`) REFERENCES `m_document` (`id`),
	CONSTRAINT `FK2_f_bank_statement_m_appuser` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK2_f_bank_statement_m_document` FOREIGN KEY (`org_statement_key_document_id`) REFERENCES `m_document` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;
 
CREATE TABLE `f_bank_statement_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`bank_statement_id` BIGINT(20) NOT NULL,
	`transaction_id` VARCHAR(50) NULL DEFAULT NULL,
	`client_account_number` VARCHAR(20) NULL DEFAULT NULL,
	`loan_account_number` VARCHAR(20) NULL DEFAULT NULL,
	`group_external_id` VARCHAR(20) NULL DEFAULT NULL,
	`mobile_number` VARCHAR(20) NULL DEFAULT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`description` VARCHAR(250) NULL DEFAULT NULL,
	`transaction_date` DATE NULL DEFAULT NULL,
	`is_reconciled` TINYINT(1) NULL DEFAULT '0',
	`loan_transaction` BIGINT(20) NULL DEFAULT NULL,
	`branch_external_id` varchar(50) DEFAULT NULL,
	`gl_account` varchar(50) DEFAULT NULL,
	`accounting_type` varchar(50) DEFAULT NULL,
	`is_journal_entry` TINYINT DEFAULT false,
	`transaction_type` varchar(50) DEFAULT NULL,
	`gl_code` VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_f_bank_statement_details_f_bank_statement` (`bank_statement_id`),
	INDEX `FK1_f_bank_statement_details_m_loan_transaction` (`loan_transaction`),
	CONSTRAINT `FK1_f_bank_statement_details_f_bank_statement` FOREIGN KEY (`bank_statement_id`) REFERENCES `f_bank_statement` (`id`) ON DELETE CASCADE,
	CONSTRAINT `FK1_f_bank_statement_details_m_loan_transaction` FOREIGN KEY (`loan_transaction`) REFERENCES `m_loan_transaction` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

ALTER TABLE m_loan_transaction
  ADD COLUMN is_reconciled BOOLEAN DEFAULT FALSE;
 

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('BANK', 'CREATE_BANK', 'BANK', 'CREATE', 0),
('BANK', 'READ_BANK', 'BANK', 'READ', 0),
('BANK', 'DELETE_BANK', 'BANK', 'DELETE', 0),
('BANK', 'UPDATE_BANK', 'BANK', 'UPDATE', 0),
('BANKSTATEMENT', 'RECONCILE_BANKSTATEMENT', 'BANKSTATEMENT', 'RECONCILE', 0),
('BANKSTATEMENTDETAILS', 'RECONCILE_BANKSTATEMENTDETAILS', 'BANKSTATEMENTDETAILS', 'RECONCILE', 0),
('BANKSTATEMENTDETAILS', 'READ_BANKSTATEMENTDETAILS', 'BANKSTATEMENTDETAILS', 'READ', 0),
('BANKSTATEMENTDETAILS', 'UPDATE_BANKSTATEMENTDETAILS', 'BANKSTATEMENTDETAILS', 'UPDATE', 0),
('BANKSTATEMENT', 'UPDATE_BANKSTATEMENT', 'BANKSTATEMENT', 'UPDATE', 0),
('BANKSTATEMENT', 'READ_BANKSTATEMENT', 'BANKSTATEMENT', 'READ', 0),
('BANKSTATEMENT', 'CREATE_BANKSTATEMENT', 'BANKSTATEMENT', 'CREATE', 0),
('BANKSTATEMENT', 'DELETE_BANKSTATEMENT', 'BANKSTATEMENT', 'DELETE', 0);

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('LoanTransactionsForPaymentReconciliation', 'Table', NULL, 'Accounting', NULL, 'payment reconciliation with date', 0, 1);

INSERT INTO stretchy_parameter ( parameter_name, parameter_variable, parameter_label, parameter_displayType, parameter_FormatType, parameter_default) VALUES ( 'searchCriteria', 'searchCriteria', 'searchCriteria', 'text', 'string', 'n/a');

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) VALUES ( (select sr.id from stretchy_report sr where sr.report_name='LoanTransactionsForPaymentReconciliation'), (select sp.id from stretchy_parameter sp where sp.parameter_name='OfficeIdSelectOne'), 'Branch');

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) VALUES ( (select sr.id from stretchy_report sr where sr.report_name='LoanTransactionsForPaymentReconciliation'), (select sp.id from stretchy_parameter sp where sp.parameter_name='searchCriteria'), 'searchCriteria');

INSERT INTO m_permission (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('accounting', 'READ_LoanTransactionsForPaymentReconciliation', 'LoanTransactionsForPaymentReconciliation', 'READ', 0);

update stretchy_report set report_sql= 'select mlt.id AS LOAN_TRANSACTION_NO,
mlt.amount AS AMOUNT,
ln.id AS LOAN_ID, 
ln.account_no AS LOAN_ACCOUNT_NO,
grou.id AS GROUP_ID,
grou.external_id AS GROUP_EXTERNAL_ID,
ounder.name AS BRANCH,
STAFF.display_name AS LOAN_OFFICER,
IF(mlt.transaction_type_enum = 2,"Repayment","Disbursement") as TRANSACTION_TYPE
FROM m_office o 
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,"%") 
and ounder.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
LEFT JOIN m_client cl ON cl.office_id = ounder.id 
LEFT JOIN m_group grou1 ON grou1.office_id = ounder.id 
LEFT JOIN m_loan ln ON (ln.client_id = cl.id or ln.group_id = grou1.id)
LEFT join m_loan_transaction mlt on mlt.loan_id=ln.id 
left join m_staff STAFF on ln.loan_officer_id=STAFF.id 
left join m_group grou on ln.group_id=grou.id 
where mlt.is_reversed=0 and (mlt.transaction_type_enum=2 or mlt.transaction_type_enum=1)
	  and mlt.is_reconciled=0 ${searchCriteria}
group by mlt.id order by mlt.id desc'
where report_name = "LoanTransactionsForPaymentReconciliation";