ALTER TABLE `m_loan`
	ADD COLUMN `last_repayment_date` Date DEFAULT null ;
UPDATE m_loan l
join (SELECT MAX(mt.transaction_date) as transactiondate, l.id from m_loan l join m_loan_transaction mt on mt.loan_id=l.id and mt.transaction_type_enum=2 and mt.is_reversed = 0 GROUP BY l.id) x on l.id = x.id
SET `last_repayment_date` = x.transactiondate;
