UPDATE stretchy_report s
SET s.report_sql = "select v.arrPeriod ArrearsBy ,v.totalDisbAmt,v.OutstandingAmt,v.arrearsAmt, b.tot `count` from (select a.arrPeriod arrPeriod,sum(a.totalDisbAmt) totalDisbAmt,sum(a.OutstandingAmt) OutstandingAmt, sum(a.arrearsAmt)  arrearsAmt
from (SELECT ml.id loanId, 
sum(IFNULL(ml.principal_disbursed_derived,0)) as TotalDisbAmt,
sum(ml.total_outstanding_derived) as OutstandingAmt,
sum(laa.total_overdue_derived) ArrearsAmt,
IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate,
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_loan ml on ml.loan_officer_id = s.id
inner join m_loan_arrears_aging laa ON laa.loan_id = ml.id
WHERE ml.loan_status_id=300
and a.id = ${currentUserId}
group by 6)a
group by 1)v
left join 
(select sum(a.total) tot,a.arrPeriod from ((select count(*) total,IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_loan ml on ml.loan_officer_id = s.id and ml.loan_type_enum = 4
inner join m_loan_arrears_aging aa on aa.loan_id = ml.id
inner join m_loan_glim lg on lg.loan_id = ml.id
where a.id = ${currentUserId}

group by 2)
union
(select count(*),IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_loan ml on ml.loan_officer_id = s.id and ml.loan_type_enum in (1,2,3)
inner join m_loan_arrears_aging aa on aa.loan_id = ml.id
where a.id = ${currentUserId}
group by 2)
)a
group by 2)b on b.arrPeriod =v.arrPeriod
order by v.arrearsAmt desc
"
WHERE s.report_name = 'Arrears Summary isloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "select a.loanId
,a.TotalDisbAmt
,a.OutstandingAmt
,a.ArrearsAmt
,a.days
,a.loantype
from  (SELECT ml.id loanId, if((ml.loan_type_enum = 4),'glim', 'non-glim') loantype,
IFNULL(ml.principal_disbursed_derived,0) as TotalDisbAmt,
ml.total_outstanding_derived as OutstandingAmt,
laa.total_overdue_derived ArrearsAmt,
IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate,
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
,DATEDIFF(CURDATE(), laa.overdue_since_date_derived) days
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_loan ml on ml.loan_officer_id = s.id
inner join m_loan_arrears_aging laa ON laa.loan_id = ml.id
left join m_loan_glim lg on lg.loan_id = ml.id
WHERE ml.loan_status_id=300
and a.id = ${currentUserId}
having 
case when  '${accountNo}' = '1-30' then arrPeriod = '1 - 30'
     when  '${accountNo}' = '31-60' then arrPeriod = '31 - 60'
	  when  '${accountNo}' = '61-90' then arrPeriod = '61 - 90' 
	  when  '${accountNo}' = '91' then arrPeriod = '91+' end)a
	  order by a.ArrearsAmt desc
"
WHERE s.report_name = 'Arrears bucket isloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "	  select v.arrPeriod band,v.totalDisbAmt,v.OutstandingAmt,v.arrearsAmt, b.tot `count` from (select a.arrPeriod arrPeriod,sum(a.totalDisbAmt) totalDisbAmt,sum(a.OutstandingAmt) OutstandingAmt, sum(a.arrearsAmt)  arrearsAmt
from (SELECT ml.id loanId, 
sum(IFNULL(ml.principal_disbursed_derived,0)) as totalDisbAmt,
sum(ml.total_outstanding_derived) as OutstandingAmt,
sum(laa.total_overdue_derived) arrearsAmt,
IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate,
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_client c on c.office_id = ounder.id
inner join m_loan ml on ml.client_id = c.id
inner join m_loan_arrears_aging laa ON laa.loan_id = ml.id


WHERE ml.loan_status_id=300
AND a.id = ${currentUserId}
group by 6
union
SELECT ml.id loanId, 
sum(IFNULL(ml.principal_disbursed_derived,0)) as totalDisbAmt,
sum(ml.total_outstanding_derived) as OutstandingAmt,
sum(laa.total_overdue_derived) arrearsAmt,
IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate,
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_group c on c.office_id = ounder.id
inner join m_loan ml on ml.group_id = c.id
inner join m_loan_arrears_aging laa ON laa.loan_id = ml.id
#left join m_loan_glim lg on lg.loan_id = ml.id
WHERE ml.loan_status_id=300
AND a.id = ${currentUserId}
group by 6)a
group by 1)v
left join 
(select sum(a.total) tot,a.arrPeriod from ((select count(*) total,IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_group c on c.office_id = ounder.id
inner join m_loan m on m.group_id = c.id
inner join m_loan_arrears_aging aa on aa.loan_id = m.id
inner join m_loan_glim lg on lg.loan_id = m.id
where a.id = ${currentUserId}
group by 2)
union
(select count(*),IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_group c on c.office_id = ounder.id
inner join m_loan m on m.group_id = c.id
inner join m_loan_arrears_aging aa on aa.loan_id = m.id and m.loan_type_enum = 2
where a.id = ${currentUserId}
group by 2)
union
(select count(*),IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), aa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_client c on c.office_id = ounder.id
inner join m_loan m on m.client_id = c.id
inner join m_loan_arrears_aging aa on aa.loan_id = m.id and m.loan_type_enum in (1,3)
where a.id = ${currentUserId}
group by 2))a
group by 2)b on b.arrPeriod =v.arrPeriod
order by v.arrearsAmt desc
"
WHERE s.report_name = 'Arrears Summary notaloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "select a.loanId
,a.TotalDisbAmt
,a.OutstandingAmt
,a.ArrearsAmt
,a.days
,a.loantype
from (SELECT ml.id loanId, if((ml.loan_type_enum = 4),'glim', 'non-glim') loantype,
IFNULL(lg.disbursed_amount,ml.principal_disbursed_derived) as totalDisbAmt,
ifnull((lg.total_payble_amount-lg.paid_amount),ml.total_outstanding_derived) as OutstandingAmt,
laa.total_overdue_derived arrearsAmt,
IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate,
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod,
DATEDIFF(CURDATE(), laa.overdue_since_date_derived) days
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_group c on c.office_id = ounder.id
inner join m_loan ml on ml.group_id = c.id
inner join m_loan_arrears_aging laa ON laa.loan_id = ml.id
left join m_loan_glim lg on lg.loan_id = ml.id
WHERE ml.loan_status_id=300
AND a.id = ${currentUserId}
having 
case when '${accountNo}' = '1-30' then arrPeriod = '1 - 30'
     when '${accountNo}' = '31-60' then arrPeriod = '31 - 60'
	  when '${accountNo}' = '61-90' then arrPeriod = '61 - 90' 
	  when '${accountNo}' = '91' then arrPeriod = '91+' end
union
SELECT ml.id loanId, if((ml.loan_type_enum = 4),'glim', 'non-glim') loantype,
IFNULL(ml.principal_disbursed_derived,0) as totalDisbAmt,
ml.total_outstanding_derived as OutstandingAmt,
laa.total_overdue_derived arrearsAmt,
IFNULL(laa.overdue_since_date_derived, curdate()) as minOverdueDate,
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 1 AND 30, '1 - 30',  
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 31 AND 60, '31 - 60', 
IF(DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN 61 AND 90, '61 - 90', '91+'))) AS arrPeriod
,DATEDIFF(CURDATE(), laa.overdue_since_date_derived) days
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_client c on c.office_id = ounder.id
inner join m_loan ml on ml.client_id = c.id and ml.loan_type_enum = 1
inner join m_loan_arrears_aging laa ON laa.loan_id = ml.id
WHERE ml.loan_status_id=300
AND a.id = ${currentUserId}
having 
case when '${accountNo}' = '1-30' then arrPeriod = '1 - 30'
     when '${accountNo}' = '31-60' then arrPeriod = '31 - 60'
	  when '${accountNo}' = '61-90' then arrPeriod = '61 - 90' 
	  when '${accountNo}' = '91' then arrPeriod = '91+' end)a
order by a.ArrearsAmt desc,loanId
"
WHERE s.report_name = 'Arrears bucket notaloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "
select a.loan_product_id,
a.loan_product_name,
sum(a.loan_count) as loan_count,
sum(a.outstanding_amount) as outstanding_amount

 from (SELECT 
mpl.id as 'loan_product_id',
mpl.name as 'loan_product_name',
COUNT(ml.id) as 'loan_count',
SUM(ml.total_outstanding_derived) as 'outstanding_amount' 

FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_client c on c.staff_id = s.id
inner join m_loan ml on ml.client_id = c.id and ml.loan_type_enum = 1
JOIN  m_product_loan mpl on ml.product_id = mpl.id
where a.id = ${currentUserId}
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))

GROUP BY mpl.id
union
SELECT 
mpl.id as 'loan_product_id',
mpl.name as 'loan_product_name',
COUNT(ml.id) as 'loan_count',
sum(ifnull((lg.total_payble_amount-lg.paid_amount),ml.total_outstanding_derived)) as 'outstanding_amount'
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_group c on c.staff_id = s.id
inner join m_loan ml on ml.group_id = c.id
JOIN  m_product_loan mpl on ml.product_id = mpl.id
left join m_loan_glim lg on lg.loan_id = ml.id
where a.id = ${currentUserId}
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))
GROUP BY mpl.id)a
group by 1
order by outstanding_amount desc
"
WHERE s.report_name = 'Matured loan report isloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "SELECT 
ml.id as 'loan_id',
ifnull(ml.principal_disbursed_derived,0) 'disbursed_amount',
ifnull(ml.total_outstanding_derived,0) as 'outstanding_amount',
ml.expected_maturedon_date
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_loan ml on ml.loan_officer_id = s.id
JOIN  m_product_loan mpl on ml.product_id = mpl.id
where a.id = ${currentUserId}
and mpl.id = '${loanProductIdonly}'
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))
union
SELECT 
ml.id as 'loan_id',
ifnull(ml.principal_disbursed_derived,0) 'disbursed_amount',
ifnull(ml.total_outstanding_derived,0) as 'outstanding_amount',
ml.expected_maturedon_date
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_staff s on s.id = a.staff_id
inner join m_loan ml on ml.loan_officer_id = s.id
left join m_loan_glim lg on lg.loan_id = ml.id
JOIN  m_product_loan mpl on ml.product_id = mpl.id
where a.id = ${currentUserId}
and mpl.id = '${loanProductIdonly}'
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))
order by outstanding_amount desc
"
WHERE s.report_name = 'Matured loan product level isloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "select a.loan_product_id,
a.loan_product_name,
sum(a.loan_count) as loan_count,
sum(a.outstanding_amount) as outstanding_amount

 from (SELECT 
mpl.id as 'loan_product_id',
mpl.name as 'loan_product_name',
COUNT(ml.id) as 'loan_count',
SUM(ml.total_outstanding_derived) as 'outstanding_amount' 

FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_client c on c.office_id = ounder.id
inner join m_loan ml on ml.client_id = c.id and ml.loan_type_enum = 1
JOIN  m_product_loan mpl on ml.product_id = mpl.id
where a.id = ${currentUserId}
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')  
and year(ml.expected_maturedon_date) = year('${startDate}'))

GROUP BY mpl.id
union
SELECT 
mpl.id as 'loan_product_id',
mpl.name as 'loan_product_name',
COUNT(ml.id) as 'loan_count',
sum(ifnull((lg.total_payble_amount-lg.paid_amount),ml.total_outstanding_derived)) as 'outstanding_amount'
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_group c on c.office_id = ounder.id
inner join m_loan ml on ml.group_id = c.id
JOIN  m_product_loan mpl on ml.product_id = mpl.id
left join m_loan_glim lg on lg.loan_id = ml.id
where a.id = ${currentUserId}
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))
GROUP BY mpl.id)a
group by 1
order by outstanding_amount desc
"
WHERE s.report_name = 'Matured loan report notaloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "SELECT 
ml.id as 'loan_id',
ifnull(ml.principal_disbursed_derived,0) 'disbursed_amount',
ifnull(ml.total_outstanding_derived,0) as 'outstanding_amount',
ml.expected_maturedon_date
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_client c on c.office_id = ounder.id
inner join m_loan ml on ml.client_id = c.id and ml.loan_type_enum = 1
JOIN  m_product_loan mpl on ml.product_id = mpl.id
#WHERE ml.loan_status_id=300
where a.id = ${currentUserId}
and mpl.id = '${loanProductIdonly}'
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))
union
SELECT 
ml.id as 'loan_id',
ifnull(lg.disbursed_amount,ml.principal_disbursed_derived) 'disbursed_amount',
ifnull((lg.total_payble_amount-lg.paid_amount),ml.total_outstanding_derived) as 'outstanding_amount',
ml.expected_maturedon_date
FROM m_appuser a

inner join  m_office o on o.id = a.office_id
INNER JOIN m_office ounder ON ounder.hierarchy 
LIKE CONCAT(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT('.', '%')
inner join m_group c on c.office_id = ounder.id
inner join m_loan ml on ml.group_id = c.id
JOIN  m_product_loan mpl on ml.product_id = mpl.id
left join m_loan_glim lg on lg.loan_id = ml.id
where a.id = ${currentUserId}
and mpl.id = '${loanProductIdonly}'
and (MONTH(ml.expected_maturedon_date) = MONTH('${startDate}')
and year(ml.expected_maturedon_date) = year('${startDate}'))
order by outstanding_amount desc
"
WHERE s.report_name = 'Matured loan product level notaloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Status'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_staff staff on usr.staff_id = staff.id
JOIN m_client cl on cl.staff_id = staff.id
JOIN m_loan loan on cl.id = loan.client_id and loan.loan_status_id = 300
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}

GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Active'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_staff staff on usr.staff_id = staff.id
JOIN m_client cl on cl.staff_id = staff.id
join m_loan_glim lg on lg.client_id = cl.id
JOIN m_loan loan on loan.id = lg.loan_id and loan.loan_status_id = 300
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}

GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Inactive'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_staff staff on usr.staff_id = staff.id
JOIN m_client cl on cl.staff_id = staff.id
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id
where usr.id = ${currentUserId}
and cl.id not in (select distinct client_id from m_loan where client_id is not null
union
select distinct client_id from m_loan_glim)
GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Dropout'
,loan.last_repayment_date 'close_date'

FROM m_appuser usr 
JOIN m_staff staff on usr.staff_id = staff.id
JOIN m_client cl on cl.staff_id = staff.id
join m_loan loan on loan.client_id = cl.id and loan.loan_status_id in (600,601,700)
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id
where usr.id = ${currentUserId}
and loan.last_repayment_date between  date_sub(curdate(), interval  1  year) and curdate()
GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Dropout'
,loan.last_repayment_date 'close_date'

FROM m_appuser usr 
JOIN m_staff staff on usr.staff_id = staff.id
JOIN m_client cl on cl.staff_id = staff.id
join m_loan_glim lg on lg.client_id = cl.id
JOIN m_loan loan on loan.id = lg.loan_id and loan.loan_status_id in (600,601,700)
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}
and loan.last_repayment_date between  date_sub(curdate(), interval  1  year) and curdate()
GROUP BY cl.id
"
WHERE s.report_name = 'Client Status report isloanofficer';

UPDATE stretchy_report s
SET s.report_sql = "SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Status'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_client cl on cl.office_id = usr.office_id
JOIN m_loan loan on cl.id = loan.client_id and loan.loan_status_id = 300
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}

GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Active'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_client cl on cl.office_id = usr.office_id
join m_loan_glim lg on lg.client_id = cl.id
JOIN m_loan loan on loan.id = lg.loan_id and loan.loan_status_id = 300
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}

GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Inactive'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_client cl on cl.office_id = usr.office_id
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id
where usr.id = ${currentUserId}
and cl.id not in (select distinct client_id from m_loan where client_id is not null
union
select distinct client_id from m_loan_glim)
GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Dropout'
,loan.last_repayment_date 'close_date'

FROM m_appuser usr 
JOIN m_client cl on cl.office_id = usr.office_id
join m_loan loan on loan.client_id = cl.id and loan.loan_status_id in (600,601,700)
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id
where usr.id = ${currentUserId}
and loan.last_repayment_date between  date_sub(curdate(), interval  1  year) and curdate()
GROUP BY cl.id
union
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Dropout'
,loan.last_repayment_date 'close_date'

FROM m_appuser usr 
JOIN m_client cl on cl.office_id = usr.office_id
join m_loan_glim lg on lg.client_id = cl.id
JOIN m_loan loan on loan.id = lg.loan_id and loan.loan_status_id in (600,601,700)
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}
and loan.last_repayment_date between  date_sub(curdate(), interval  1  year) and curdate()
GROUP BY cl.id
"
WHERE s.report_name = 'Client Status report isnotloanofficer';



