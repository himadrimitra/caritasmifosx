UPDATE `job` SET `name`='Overdue Calculations For Loans', `display_name`='Overdue Calculations(Interest and Penalties) For Loans' WHERE  `name`='Recalculate Interest For Loans';
UPDATE job_run_history jh JOIN job j1 ON j1.name = 'Overdue Calculations For Loans' JOIN job j2 ON j2.id = jh.job_id AND j2.name = 'Apply penalty to overdue loans' SET jh.job_id=j1.id;
DELETE FROM `job` WHERE  `name`='Apply penalty to overdue loans';
UPDATE `job` SET `depands_on_job_name`='Overdue Calculations For Loans' WHERE `name` = 'Apply Penalty For Broken Periods';
ALTER TABLE `f_charge_overdue_detail`
	ADD COLUMN `stop_charge_on_npa` TINYINT(1) NOT NULL DEFAULT '0';
ALTER TABLE `f_loan_overdue_charge_detail`
	ADD COLUMN `stop_charge_on_npa` TINYINT(1) NOT NULL DEFAULT '0';
ALTER TABLE `m_loan_charge`
	ADD COLUMN `overdue_applied_till` DATE NULL DEFAULT NULL;
	
