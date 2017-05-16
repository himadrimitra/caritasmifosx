UPDATE  fdfclive.stretchy_report st 

SET st.report_sql = " SELECT 

 mo.name as 'Office',
 c.display_name as 'Client',
 p.name as 'Product',
 l.account_no as 'Loan Account Number',
 CASE jed.type_enum WHEN 1 THEN 'CREDIT' WHEN 2 THEN 'DEBIT' END AS 'Booking Type',
 acct.name as 'Account',
 IFNULL(c_ent.code_value, CASE je.manual_entry WHEN 0 THEN 'FDFC' WHEN 1 THEN '' END ) 'Legal Entity',
 c_tax.code_value 'Tax Category',
 c_sup.code_value 'Supplier',
 jed.amount AS 'Amount',
 je.reversed 'Reversed',
 je.entry_date 'Entry Date',
 date(je.created_date) 'Created Date',
 date(je.lastmodified_date) 'Last Modified Date'

FROM
 f_journal_entry je
 INNER JOIN f_journal_entry_detail jed on jed.journal_entry_id = je.id

LEFT JOIN m_loan_transaction ltx ON ltx.id = je.entity_transaction_id and je.entity_type_enum = 1
LEFT JOIN m_loan l ON l.id = ltx.loan_id
LEFT JOIN m_client c ON c.id = l.client_id
LEFT JOIN m_product_loan p ON p.id=l.product_id
LEFT JOIN acc_gl_account acct ON acct.id = jed.account_id 
LEFT JOIN journalTags jt ON jt.gl_journal_entry_id = je.transaction_identifier 
LEFT JOIN m_code_value c_ent ON c_ent.id = jt.journalLegalEntity_cd_legalEntity 
LEFT JOIN m_code_value c_tax ON c_tax.id = jt.journalTaxCategory_cd_taxCategory
LEFT JOIN m_code_value c_sup ON c_sup.id = jt.journalSupplier_cd_Supplier
LEFT JOIN m_office mo ON mo.id = je.office_id

WHERE je.entry_date BETWEEN '${startDate}' AND '${endDate}'
AND (je.office_id = '${officeId}' OR '${officeId}' = '1' OR '${officeId}' = '-1') "

WHERE st.report_name  = 'All Accounting Entries'