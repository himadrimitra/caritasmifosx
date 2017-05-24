UPDATE camdo.stretchy_report st

 SET st.report_sql = " SELECT
   SUM(CASE gl.type_enum WHEN 2 THEN gl.amount WHEN 1 THEN gl.amount *-1 END) LoanBalance
FROM f_journal_entry je
INNER JOIN f_journal_entry_detail gl on gl.journal_entry_id = je.id
WHERE gl.account_id = '2' 
AND je.entry_date <= '${endDate}' "

WHERE st.report_name = 'LoanBalance'