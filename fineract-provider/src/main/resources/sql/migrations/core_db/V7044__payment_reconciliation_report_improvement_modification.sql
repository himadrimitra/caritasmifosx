ALTER TABLE `f_bank_statement_details`
	ADD COLUMN `bank_statement_detail_type` SMALLINT(5) NOT NULL DEFAULT 0,
	DROP COLUMN `is_journal_entry`;

update stretchy_report set report_sql= 'select tr.id AS LOAN_TRANSACTION_NO,
tr.amount AS AMOUNT,
l.id AS LOAN_ID, 
l.account_no AS LOAN_ACCOUNT_NO,
g.external_id AS GROUP_EXTERNAL_ID,
office.name AS BRANCH,
tr.transaction_date AS TRANSACTION_DATE,
IF(tr.transaction_type_enum = 1,"Disbursement","Client Payment") as TRANSACTION_TYPE
FROM m_office office 
JOIN m_office ounder on ounder.hierarchy like concat(office.hierarchy,"%") 
and ounder.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
JOIN m_loan_transaction tr ON  tr.office_id = office.id and (tr.transaction_type_enum in (1,2,8) and tr.is_reversed = 0 
and tr.is_reconciled = 0)
JOIN m_loan l ON (tr.loan_id = l.id)
JOIN m_group g ON g.id=l.group_id
where ${searchCriteria}
group by tr.id order by tr.id desc'
where report_name = "LoanTransactionsForPaymentReconciliation";