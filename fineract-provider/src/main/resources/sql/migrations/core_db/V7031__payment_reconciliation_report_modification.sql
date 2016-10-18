update stretchy_report set report_sql= 'select mlt.id AS LOAN_TRANSACTION_NO,
mlt.amount AS AMOUNT,
ln.id AS LOAN_ID, 
ln.account_no AS LOAN_ACCOUNT_NO,
grou.id AS GROUP_ID,
grou.external_id AS GROUP_EXTERNAL_ID,
ounder.name AS BRANCH,
mlt.transaction_date AS TRANSACTION_DATE,
STAFF.display_name AS LOAN_OFFICER,
IF(mlt.transaction_type_enum = 1,"Disbursement","Client Payment") as TRANSACTION_TYPE
FROM m_office o 
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,"%") 
and ounder.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
LEFT JOIN m_client cl ON cl.office_id = ounder.id 
LEFT JOIN m_group grou1 ON grou1.office_id = ounder.id 
LEFT JOIN m_loan ln ON (ln.client_id = cl.id or ln.group_id = grou1.id)
LEFT join m_loan_transaction mlt on mlt.loan_id=ln.id 
left join m_staff STAFF on ln.loan_officer_id=STAFF.id 
left join m_group grou on ln.group_id=grou.id 
where mlt.is_reversed=0 and (mlt.transaction_type_enum=2 or mlt.transaction_type_enum=1 or mlt.transaction_type_enum=8)
	  and mlt.is_reconciled=0 ${searchCriteria}
group by mlt.id order by mlt.id desc'
where report_name = "LoanTransactionsForPaymentReconciliation";