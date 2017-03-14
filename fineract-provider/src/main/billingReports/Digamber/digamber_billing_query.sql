SELECT  
o.name 'Office Name',COUNT(DISTINCT(cl.id)) AS 'No. Of Active Client'
FROM m_loan l 
JOIN m_client cl ON l.client_id = cl.id
LEFT JOIN m_office o ON o.id=cl.office_id
WHERE 
 (l.loan_status_id = 300  AND  l.disbursedon_date <= '${endDate}')
 OR (l.loan_status_id in (600, 700) 
 AND l.closedon_date IS NOT NULL 
 AND l.closedon_date BETWEEN '${startDate}' AND '${endDate}' ) 
GROUP BY o.id