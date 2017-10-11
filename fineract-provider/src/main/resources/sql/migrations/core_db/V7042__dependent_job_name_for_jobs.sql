ALTER TABLE `job`
	ADD COLUMN `depands_on_job_name` VARCHAR(100) NULL AFTER `is_misfired`;
	
	UPDATE `job` SET `scheduler_group`='3' WHERE  name = 'Update Loan Arrears Ageing';
	
	update job set depands_on_job_name = 'Update Loan Arrears Ageing' where name = 'Update Non Performing Assets';
	
	update job set depands_on_job_name = 'Update Non Performing Assets' where name = 'Recalculate Interest For Loans';
	
	update job set depands_on_job_name = 'Update Non Performing Assets' where name = 'Apply penalty to overdue loans';
	
	update job set depands_on_job_name = 'Update Non Performing Assets' where name = 'Add Accrual Transactions For Loans With Income Posted As Transactions';
	
	update job set depands_on_job_name = 'Update Non Performing Assets' where name = 'Add Periodic Accrual Transactions';
	
	update job set depands_on_job_name = 'Update Non Performing Assets' where name = 'Add Accrual Transactions';
	