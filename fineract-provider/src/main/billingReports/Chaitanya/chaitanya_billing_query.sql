select branch, count(distinct client_id) clients
from (
    select mo.name branch, mc.id client_id, mc.display_name client_name,  count(ml.id) loan_count
    from m_office mo 
    join m_client mc on mc.office_id=mo.id
    join m_loan ml on ml.client_id=mc.id
    where     
    ( 
		-- All active loans disbursed between 01-August-2015 (Date of Migration to Finflux) and end of the month
		(ml.loan_status_id = 300 and ml.disbursedon_date between '2015-08-01' and '${endDate}') 
		OR 
		-- All active loans disbursed prior to 01-Aug-2015 (Date of Migration to Finflux) and having an outstanding greater than 500
		(ml.loan_status_id = 300 and ml.disbursedon_date < '2015-08-01' and ml.`principal_outstanding_derived` > 500) 
		OR 
		-- All closed loans which was disbursed before 01-Aug-2015 (Date of Migration to Finflux) and the begining of the month
		-- and was closed after the current month
		(ml.loan_status_id in (600, 700) 
			AND ml.disbursedon_date between '2015-08-01' and date_sub('${endDate}', INTERVAL 1 MONTH)
			AND ml.closedon_date IS NOT NULL 
			AND ml.closedon_date > '${endDate}'
		)
    ) group by mc.id
) clients_list GROUP BY branch