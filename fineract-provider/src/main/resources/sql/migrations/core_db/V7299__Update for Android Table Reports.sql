/****** UPDATE FOR Client Status report isloanofficer****/

UPDATE stretchy_report s 
SET s.report_sql = "SELECT a.center_id 'Center ID',
a.center_name 'Center Name',
a.group_id 'Group ID',
a.group_name 'Group Name',
a.client_account_no 'Client Acc No.',
a.client_name 'Client Name',
a.`Status` 'Status',
IF(a.`Status` = 'Active','',MAX(a.close_date))  'Last Loan Closed On Date'
FROM 
(
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Active' AS 'Status'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_staff staff on usr.staff_id = staff.id
JOIN m_client cl on cl.staff_id = staff.id
JOIN m_loan loan on cl.id = loan.client_id and loan.loan_status_id = 300
join ( select max(m.id) latestloan from m_loan m where m.client_id IS NOT NULL
group by client_id)la on la.latestloan = loan.id
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
join ( select max(m.id) latestloan from m_loan m where m.loan_type_enum = 4
group by group_id)la on la.latestloan = loan.id
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
join ( select max(m.id) latestloan from m_loan m where m.client_id IS NOT NULL
group by client_id)la on la.latestloan = loan.id
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
join ( select max(m.id) latestloan from m_loan m where m.loan_type_enum = 4
group by group_id)la on la.latestloan = loan.id
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id

where usr.id = ${currentUserId}
and loan.last_repayment_date between  date_sub(curdate(), interval  1  year) and curdate()
GROUP BY cl.id
) a
GROUP BY a.client_account_no
ORDER BY 7,8 DESC"
WHERE s.report_name = 'Client Status report isloanofficer';


/****** UPDATE FOR Client Status report isnotloanofficer****/
UPDATE stretchy_report s 
SET s.report_sql = " SELECT a.center_id 'Center ID',
a.center_name 'Center Name',
a.group_id 'Group ID',
a.group_name 'Group Name',
a.client_account_no 'Client Acc No.',
a.client_name 'Client Name',
a.`Status` 'Status',
IF(a.`Status` = 'Active','',MAX(a.close_date))  'Last Loan Closed On Date'
FROM 
(
SELECT 
center.id as 'center_id',
center.display_name as 'center_name', 
groups.id as 'group_id',
groups.display_name as 'group_name',
cl.id as 'client_id',
cl.display_name as 'client_name',
cl.account_no as 'client_account_no'
,'Active' AS 'Status'
,'' as 'close_date'
FROM m_appuser usr 
JOIN m_client cl on cl.office_id = usr.office_id
JOIN m_loan loan on cl.id = loan.client_id and loan.loan_status_id = 300
join ( select max(m.id) latestloan from m_loan m where m.client_id IS NOT NULL
group by client_id)la on la.latestloan = loan.id
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
join ( select max(m.id) latestloan from m_loan m where m.loan_type_enum = 4
group by group_id)la on la.latestloan = loan.id
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
join ( select max(m.id) latestloan from m_loan m where m.client_id IS NOT NULL
group by client_id)la on la.latestloan = loan.id
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
join ( select max(m.id) latestloan from m_loan m where m.loan_type_enum = 4
group by group_id)la on la.latestloan = loan.id
left JOIN m_group_client mapping on mapping.client_id = cl.id
left JOIN m_group groups on groups.id = mapping.group_id
left JOIN m_group center on center.id = groups.parent_id


where usr.id = ${currentUserId}
and loan.last_repayment_date between  date_sub(curdate(), interval  1  year) and curdate()
GROUP BY cl.id
) a
GROUP BY a.client_account_no
ORDER BY 7,8 DESC"
WHERE s.report_name = 'Client Status report isnotloanofficer'