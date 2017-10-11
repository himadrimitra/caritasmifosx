UPDATE camdo.stretchy_report st

 SET st.report_sql = " SELECT
   SUM(CASE gl.type_enum WHEN 1 THEN gl.amount WHEN 2 THEN gl.amount *-1 END) Income
FROM f_journal_entry je
INNER JOIN f_journal_entry_detail gl ON gl.journal_entry_id = je.id
LEFT JOIN acc_gl_account a ON a.id = gl.account_id 
WHERE a.classification_enum = '4' 
AND je.entry_date BETWEEN '${startDate}' AND '${endDate}' "

WHERE st.report_name = 'Income'