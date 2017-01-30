ALTER TABLE `m_loan`
	ADD COLUMN `principal_net_disbursed_derived` DECIMAL(19,6) NOT NULL DEFAULT '0.000000' AFTER `principal_disbursed_derived`;
	
ALTER TABLE `m_loan_disbursement_detail`
	ADD COLUMN `principal_net_disbursed` DECIMAL(19,6) NOT NULL AFTER `principal`;
	
	
update m_loan l set l.principal_net_disbursed_derived = ifnull(l.principal_amount,0) - ifnull(l.total_charges_due_at_disbursement_derived,0);	

UPDATE m_loan_disbursement_detail dd
  SET dd.principal_net_disbursed = dd.principal - IFNULL((select sum( IFNULL(lc.amount,0))  from m_loan_tranche_disbursement_charge tdc 
left JOIN m_loan_charge lc ON lc.id = tdc.loan_charge_id  where tdc.disbursement_detail_id = dd.id) , 0);
