UPDATE camdo.stretchy_report st

 SET st.report_sql = " SELECT 
 c.display_name as 'Client',
 c.id as 'Client ID',
 p.name as 'Product',
 l.account_no as 'Loan Account Number',
 l.approved_principal as 'Principal',
 acct.name as 'Account',
 SUM(CASE gl.type_enum WHEN 1 THEN gl.amount WHEN 2 THEN gl.amount *-1 END) as 'Amount'

FROM f_journal_entry je
INNER JOIN f_journal_entry_detail gl ON gl.journal_entry_id = je.id
LEFT JOIN m_loan_transaction ltx ON ltx.id = je.entity_transaction_id AND je.entity_type_enum = 1
LEFT JOIN m_loan l ON l.id = ltx.loan_id
LEFT JOIN m_client c ON c.id = l.client_id
LEFT JOIN m_product_loan p ON p.id=l.product_id
LEFT JOIN acc_gl_account acct ON acct.id = gl.account_id 
WHERE je.entry_date BETWEEN '${startDate}' AND '${endDate}'

GROUP BY l.id, gl.account_id"

WHERE st.report_name = 'Sum Accounting Entries by Loan'