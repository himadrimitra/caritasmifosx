ALTER TABLE `m_loan`
	ADD COLUMN `repayment_history_version` INT(3)  DEFAULT null ;

update m_loan ml join (select rh.loan_id as loanId, max(rh.version) as maxversion from m_loan_repayment_schedule_history rh group by rh.loan_id) x on x.loanId = ml.id set ml.repayment_history_version = x.maxversion