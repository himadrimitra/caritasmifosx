UPDATE camdo.stretchy_report st
 SET st.report_sql = "SELECT 
 
 c.display_name as 'Client',
 p.name as 'Product',
 l.account_no as 'Loan Account Number',
 l.approved_principal as 'Principal',
 CASE glcredit.type_enum WHEN 1 THEN 'CREDIT' WHEN 2 THEN 'DEBIT' END AS 'Booking Type',
 acct.name as 'Account',
 je.*,
 glcredit.account_id,
glcredit.type_enum,
glcredit.amount

FROM f_journal_entry je
INNER JOIN f_journal_entry_detail glcredit on glcredit.journal_entry_id = je.id 
LEFT JOIN m_loan_transaction ltx ON ltx.id = je.entity_transaction_id AND je.entity_type_enum = 1
LEFT JOIN m_loan l ON l.id = ltx.loan_id
LEFT JOIN m_client c ON c.id = l.client_id
LEFT JOIN m_product_loan p ON p.id=l.product_id
LEFT JOIN acc_gl_account acct ON acct.id = glcredit.account_id 

WHERE je.entry_date BETWEEN '${startDate}' AND '${endDate}' "

WHERE st.report_name = 'All Accounting Entries'