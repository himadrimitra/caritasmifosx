UPDATE fdfclive.stretchy_report st

SET st.report_sql = " SELECT 
 c.display_name as 'Client',
 p.name as 'Product',
 l.account_no as 'Account Number',
 l.approved_principal as 'Principal',
 sum(CASE when gl.type_enum = 1 and gl.account_id = 7 THEN gl.amount WHEN gl.type_enum = 2 and gl.account_id = 7 THEN -gl.amount END ) as 'Interest Income',
 sum(CASE when gl.type_enum = 1 and gl.account_id = 8 THEN gl.amount WHEN gl.type_enum = 2 and gl.account_id = 8 THEN -gl.amount END ) as 'Fee Income'

FROM
f_journal_entry je 
JOIN f_journal_entry_detail gl ON gl.journal_entry_id = je.id 
LEFT JOIN m_loan_transaction ltx ON ltx.id = je.entity_transaction_id and je.entity_type_enum = 1
LEFT JOIN m_loan l ON l.id = ltx.loan_id
LEFT JOIN m_client c ON c.id = l.client_id
LEFT JOIN m_product_loan p ON p.id=l.product_id

WHERE (gl.account_id = 7 or gl.account_id = 8)
AND je.entry_date BETWEEN '${startDate}' AND '${endDate}'

GROUP BY ltx.loan_id "

WHERE st.report_name = 'Interest Income'