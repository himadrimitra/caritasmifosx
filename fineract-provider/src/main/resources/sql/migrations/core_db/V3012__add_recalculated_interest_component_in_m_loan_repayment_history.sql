ALTER TABLE `m_loan_repayment_schedule_history`
	ADD COLUMN `recalculated_interest_component` TINYINT(1) NULL DEFAULT '0' AFTER `version`;