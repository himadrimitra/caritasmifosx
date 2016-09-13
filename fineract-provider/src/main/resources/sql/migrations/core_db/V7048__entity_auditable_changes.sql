ALTER TABLE `m_loan_term_variations`
	ADD COLUMN `created_date` DATETIME NULL DEFAULT NULL,
	ADD COLUMN `createdby_id` BIGINT(20) NULL DEFAULT NULL AFTER `created_date`,
	ADD COLUMN `lastmodified_date` DATETIME NULL DEFAULT NULL AFTER `createdby_id`,
	ADD COLUMN `lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL AFTER `lastmodified_date`;
	
ALTER TABLE `m_loan_repayment_schedule`
	DROP COLUMN `created_date`,
	DROP COLUMN `createdby_id`,
	DROP COLUMN `lastmodified_date`,
	DROP COLUMN `lastmodifiedby_id`;
	
ALTER TABLE `m_loan_repayment_schedule_history`
	DROP COLUMN `created_date`,
	DROP COLUMN `createdby_id`,
	DROP COLUMN `lastmodified_date`,
	DROP COLUMN `lastmodifiedby_id`;