ALTER TABLE `f_loan_utilization_check`
	ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT '1' AFTER `audit_done_on`,

