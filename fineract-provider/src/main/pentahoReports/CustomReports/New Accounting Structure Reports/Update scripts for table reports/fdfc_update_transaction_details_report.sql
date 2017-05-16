UPDATE fdfclivestretchy_report st
 SET st.report_sql = " Select * 
from f_journal_entry je
INNER JOIN f_journal_entry_detail jed on jed.journal_entry_id = je.id 
where transaction_id = '${transactionId}' "

WHERE st.report_name = 'Transaction Details'