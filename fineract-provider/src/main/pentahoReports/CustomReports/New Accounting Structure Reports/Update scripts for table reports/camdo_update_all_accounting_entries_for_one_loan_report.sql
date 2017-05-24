UPDATE camdo.stretchy_report st

 SET st.report_sql = " SELECT 
 je.id 'GL ID',
 je.entry_date 'Transaction Date',
 je.entity_transaction_id 'Loan Transaction ID',
 acct.name as 'Account',
 CASE gl.type_enum WHEN 1 THEN gl.amount WHEN 2 THEN 0 END as 'Debit',
 CASE gl.type_enum WHEN 2 THEN gl.amount WHEN 1 THEN 0 END as 'Credit',
 user.username as 'Created by'

FROM
f_journal_entry je
INNER JOIN f_journal_entry_detail gl ON  gl.journal_entry_id = je.id
LEFT JOIN m_loan_transaction ltx ON ltx.id = je.entity_transaction_id AND je.entity_type_enum = 1
LEFT JOIN m_loan l ON l.id = ltx.loan_id
LEFT JOIN m_client c ON c.id = l.client_id
LEFT JOIN m_product_loan p ON p.id=l.product_id
LEFT JOIN acc_gl_account acct ON acct.id = gl.account_id 
LEFT JOIN  m_appuser user ON user.id = je.createdby_id

WHERE l.account_no = '${accountNo}' AND je.reversed =0
ORDER BY je.entry_date, gl.id ASC "

WHERE st.report_name = 'All Accounting Entries for ONE Loan'