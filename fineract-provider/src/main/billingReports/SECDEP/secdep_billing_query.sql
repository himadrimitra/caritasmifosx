SELECT office_id, office_name, client_id, client_name, COUNT(IF(account_type = 'loan',1,NULL)) Loans, COUNT(IF(account_type = 'deposit',1,NULL)) Deposits, COUNT(IF(account_type = 'charges',1,NULL)) Charges

FROM
-- SELECT * FROM
(
-- 1) Active Loans disbursed before end-date

SELECT o.id office_id, o.name office_name, c.id client_id, c.display_name client_name, c.sub_status sub_status , 
	'loan' account_type, l.loan_status_id status, l.id account_id, p.name product, 
	MAX(lt.transaction_date) max_txn_date
FROM m_client c
JOIN m_office o ON c.office_id = o.id
JOIN m_loan l ON c.id = l.client_id
JOIN m_product_loan p ON p.id = l.product_id
JOIN m_loan_transaction lt ON l.id = lt.loan_id
WHERE l.loan_status_id = 300 and c.status_enum=300
AND l.disbursedon_date <= '2017-02-28' 
GROUP BY c.id, l.id


UNION ALL
-- 2) Closed and Overpaid loans that were disbursed before end-date and with last transaction between start-date and end-date

SELECT o.id office_id, o.name office_name, c.id client_id, c.display_name client_name, c.sub_status sub_status , 
	'loan' account_type, l.loan_status_id status, l.id account_id, p.name product, 
	MAX(lt.transaction_date) max_txn_date
FROM m_client c
JOIN m_office o ON c.office_id = o.id
JOIN m_loan l ON c.id = l.client_id
JOIN m_product_loan p ON p.id = l.product_id
JOIN m_loan_transaction lt ON l.id = lt.loan_id
WHERE l.loan_status_id IN (600,601,602,700)
AND l.disbursedon_date <= '2017-02-28' 
AND lt.transaction_date >= '2017-02-01' and lt.is_reversed =0 and lt.transaction_type_enum in (1,2,5)
GROUP BY c.id, l.id




UNION ALL

-- 3) Savings accounts that were activated prior to end-date

SELECT o.id office_id, o.name office_name,sc.id client_id, sc.display_name client_name, sc.sub_status sub_status, 
   'deposit' account_type, s.status_enum status, s.id account_id, sp.name product,
   s.activatedon_date max_txn_date
FROM m_client sc
JOIN m_office o ON sc.office_id = o.id
JOIN m_savings_account s ON sc.id = s.client_id
JOIN m_savings_product sp ON sp.id = s.product_id
WHERE s.status_enum = 300
AND s.activatedon_date <= '2017-02-28' and s.account_balance_derived > 0
GROUP BY sc.id, s.id

UNION ALL

-- 5) Closed savings that had at least one transaction between start-date and end-date

SELECT o.id office_id, o.name office_name, sc.id client_id, sc.display_name client_name, sc.sub_status sub_status, 
   'deposit' account_type, s.status_enum status, s.id account_id, sp.name product,
   MAX(st.transaction_date) max_txn_date
FROM m_client sc
JOIN m_office o ON sc.office_id = o.id
JOIN m_savings_account s ON sc.id = s.client_id
JOIN m_savings_product sp ON sp.id = s.product_id
JOIN m_savings_account_transaction st ON s.id = st.savings_account_id
WHERE s.status_enum = 600 AND s.activatedon_date <= '2017-02-28' 
and st.transaction_date >= '2017-02-01' and st.is_reversed = 0 and st.transaction_type_enum in (1,2)
GROUP BY sc.id, s.id

UNION ALL

-- 6) Client Charges between start-date and end-date

select o.id office_id, o.name office_name, mc.id client_id, mc.display_name client_name, mc.sub_status sub_status , 
	'charges' account_type, mcrc.is_active status, mcrc.id account_id, mcrc.charge_id product, 
	MAX(mcc.charge_due_date) max_txn_date 
from m_client mc
join m_client_recurring_charge mcrc on mcrc.client_id = mc.id and mcrc.is_active = 1
join m_client_charge mcc on mcc.client_id = mc.id
JOIN m_office o ON mc.office_id = o.id
where mcc.charge_due_date between '2017-02-01' and '2017-02-28' and mc.status_enum = 300
GROUP BY mc.id, mcrc.id

) q
GROUP BY office_id, client_id
ORDER BY q.client_id