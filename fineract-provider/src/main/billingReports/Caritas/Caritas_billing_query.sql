SELECT office_id, office_name, client_id, client_name, COUNT(IF(account_type = 'loan',1,NULL)) Loans, COUNT(IF(account_type = 'deposit',1,NULL)) Deposits FROM
-- SELECT * FROM
(
-- Active Loans disbursed before end-date
SELECT o.id office_id, o.name office_name, c.id client_id, c.display_name client_name, c.sub_status sub_status , 
	'loan' account_type, l.loan_status_id status, l.id account_id, p.name product, 
	MAX(lt.transaction_date) max_txn_date
FROM m_client c
JOIN m_office o ON c.office_id = o.id
JOIN m_loan l ON c.id = l.client_id
JOIN m_product_loan p ON p.id = l.product_id
JOIN m_loan_transaction lt ON l.id = lt.loan_id
WHERE l.loan_status_id = 300
AND l.disbursedon_date <= '${endDate}' 
GROUP BY c.id, l.id


UNION ALL
-- Closed and Overpaid loans that were disbursed before end-date and with last transaction between start-date and end-date
SELECT o.id office_id, o.name office_name, c.id client_id, c.display_name client_name, c.sub_status sub_status , 
	'loan' account_type, l.loan_status_id status, l.id account_id, p.name product, 
	MAX(lt.transaction_date) max_txn_date
FROM m_client c
JOIN m_office o ON c.office_id = o.id
JOIN m_loan l ON c.id = l.client_id
JOIN m_product_loan p ON p.id = l.product_id
JOIN m_loan_transaction lt ON l.id = lt.loan_id
WHERE l.loan_status_id IN (600,601,602,700)
AND l.disbursedon_date <= '${endDate}' 
GROUP BY c.id, l.id
HAVING MAX(lt.transaction_date) BETWEEN '${startDate}' AND '${endDate}'

UNION ALL
-- Savings accounts that were activated in the last 4 months prior to end-date
SELECT o.id office_id, o.name office_name,sc.id client_id, sc.display_name client_name, sc.sub_status sub_status, 
   'deposit' account_type, s.status_enum status, s.id account_id, sp.name product,
   s.activatedon_date max_txn_date
FROM m_client sc
JOIN m_office o ON sc.office_id = o.id
JOIN m_savings_account s ON sc.id = s.client_id
JOIN m_savings_product sp ON sp.id = s.product_id
WHERE s.status_enum = 300
AND s.activatedon_date BETWEEN DATE_ADD(DATE_SUB('${endDate}', INTERVAL 4 MONTH),INTERVAL 1 DAY) AND '${endDate}'
GROUP BY sc.id, s.id

UNION ALL
-- Active savings that had at least one transaction in the last 4 months prior to end-date
SELECT o.id office_id, o.name office_name, sc.id client_id, sc.display_name client_name, sc.sub_status sub_status, 
   'deposit' account_type, s.status_enum status, s.id account_id, sp.name product,
   MAX(st.transaction_date) max_txn_date
FROM m_client sc
JOIN m_office o ON sc.office_id = o.id
JOIN m_savings_account s ON sc.id = s.client_id
JOIN m_savings_product sp ON sp.id = s.product_id
JOIN m_savings_account_transaction st ON s.id = st.savings_account_id
WHERE s.status_enum = 300
AND st.transaction_date <= '${endDate}'
AND st.created_date <= '${endDate}'
GROUP BY sc.id, s.id
HAVING MAX(st.transaction_date) BETWEEN DATE_ADD(DATE_SUB('${endDate}', INTERVAL 4 MONTH),INTERVAL 1 DAY) AND '${endDate}'

UNION ALL
-- Closed savings that had at least one transaction between start-date and end-date
SELECT o.id office_id, o.name office_name, sc.id client_id, sc.display_name client_name, sc.sub_status sub_status, 
   'deposit' account_type, s.status_enum status, s.id account_id, sp.name product,
   MAX(st.transaction_date) max_txn_date
FROM m_client sc
JOIN m_office o ON sc.office_id = o.id
JOIN m_savings_account s ON sc.id = s.client_id
JOIN m_savings_product sp ON sp.id = s.product_id
JOIN m_savings_account_transaction st ON s.id = st.savings_account_id
WHERE s.status_enum = 600
AND st.transaction_date <= '${endDate}'
AND st.created_date <= '${endDate}'
GROUP BY sc.id, s.id
HAVING MAX(st.transaction_date) BETWEEN '${startDate}' AND '${endDate}'

) q
GROUP BY office_id, office_name, client_id, client_name
ORDER BY q.client_id, q.account_id