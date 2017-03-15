SELECT office_id, office_name, client_id, client_name, COUNT(IF(account_type = 'loan',1,NULL)) Loans, COUNT(IF(account_type = 'deposit',1,NULL)) Deposits, COUNT(IF(account_type = 'charges',1,NULL)) Charges

FROM
-- SELECT * FROM
(

-- 1) Active Loans disbursed before end-date

SELECT o.id office_id, o.name office_name, mc.id client_id, mc.display_name client_name, 
	'loan' account_type, ml.loan_status_id status, ml.id account_id, p.name product, 
	MAX(lt.transaction_date) max_txn_date
FROM m_loan ml
JOIN m_group mg ON ml.group_id = mg.id
JOIN m_group_client mgc ON mgc.group_id = mg.id
JOIN m_client mc ON mc.id = mgc.client_id
JOIN m_office o ON mg.office_id = o.id
JOIN m_product_loan p ON p.id = ml.product_id
JOIN m_loan_transaction lt ON ml.id = lt.loan_id
WHERE ml.loan_status_id = 300 and mc.status_enum=300
AND ml.disbursedon_date <= '2017-02-28' 
GROUP BY mc.id, ml.id


UNION ALL
-- 2) Closed and Overpaid loans that were disbursed before end-date and with last transaction between start-date and end-date

SELECT o.id office_id, o.name office_name, mc.id client_id, mc.display_name client_name, 
	'loan' account_type, ml.loan_status_id status, ml.id account_id, p.name product, 
	MAX(lt.transaction_date) max_txn_date
FROM m_loan ml
JOIN m_group mg ON ml.group_id = mg.id
JOIN m_group_client mgc ON mgc.group_id = mg.id
JOIN m_client mc ON mc.id = mgc.client_id
JOIN m_office o ON mg.office_id = o.id
JOIN m_product_loan p ON p.id = ml.product_id
JOIN m_loan_transaction lt ON ml.id = lt.loan_id
WHERE ml.loan_status_id IN (600,601,602,700)
AND ml.disbursedon_date <= '2017-02-28' 
AND lt.transaction_date >= '2017-02-01' and lt.is_reversed =0 and lt.transaction_type_enum not in (10)
GROUP BY mc.id, ml.id

) q
GROUP BY office_id, client_id
ORDER BY q.client_id