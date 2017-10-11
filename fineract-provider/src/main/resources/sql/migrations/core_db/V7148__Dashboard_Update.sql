/*ClientTrendsByMonth*/
UPDATE stretchy_report
SET report_sql =
'SELECT  COUNT(cl.id) AS count,   
MONTHNAME(cl.activation_date) AS Months
FROM m_office of  
LEFT JOIN m_client cl on of.id = cl.office_id 
WHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" )  
AND (cl.activation_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 11 MONTH) AND DATE(NOW()))GROUP BY Months'
WHERE report_name LIKE '%ClientTrendsByMonth%' ;

/*LoanTrendsByMonth*/
UPDATE stretchy_report
SET report_sql =
'SELECT 	COUNT(ln.id) AS lcount, 
		MONTHNAME(ln.disbursedon_date) AS Months
FROM m_office of 
	LEFT JOIN m_client cl on of.id = cl.office_id
	LEFT JOIN m_loan ln on cl.id = ln.client_id
WHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
	AND (ln.disbursedon_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 11 MONTH) AND DATE(NOW()))
GROUP BY Months

UNION

SELECT 	COUNT(ln.id) AS lcount, 
		MONTHNAME(ln.disbursedon_date) AS Months
FROM m_office of 
	LEFT JOIN m_group cl on of.id = cl.office_id 
	LEFT JOIN m_loan ln on cl.id = ln.group_id and ln.client_id IS NULL
WHERE of.hierarchy like concat((select ino.hierarchy from m_office ino where ino.id = ${officeId}),"%" ) 
	AND (ln.disbursedon_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 11 MONTH) AND DATE(NOW()))
GROUP BY Months'

WHERE report_name LIKE '%LoanTrendsByMonth%'