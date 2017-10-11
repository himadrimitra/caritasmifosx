set @as:='2017-06-30';
select count( distinct cb.ac) 'Active Clients' from (
/* Query to handle migrated loans */
select ad.client_id ac

from m_loan l

/* to get client loan mapping */
join `additional details` ad on ad.`Loan No`=l.id
/* considers all currently active loans disbursed before the AsonDate */
where ((l.disbursedon_date <= @as and l.loan_status_id =300)
/Loans that are currently closed but was active As on a date/
or (l.loan_status_id in (600,601,700) and l.disbursedon_date <= @as and ifnull(l.closedon_date,l.writtenoffon_date) > @as))
/* id hardcoded to consider migrated loans only */
and l.id <=23822

union all

/* Query to handle GLIM loans (Post Migration) */
select c.id

from m_loan l
join m_loan_glim lg on lg.loan_id=l.id
join m_client c on c.id=lg.client_id and lg.is_client_selected=1
/* considers all active currently loans disbursed before the AsonDate */
where ((l.disbursedon_date <= @as and l.loan_status_id =300)
/Loans that are currently closed but was active As on a date/
or (l.loan_status_id in (600,601,700) and l.disbursedon_date <= @as and ifnull(l.closedon_date,l.writtenoffon_date) > @as)) ) cb


/* ################################################################################################ */

Detailed query

set @ad:='2017-06-30';
select distinct cb.branch Branch
,cb.client Client
,cb.client_id 'Client ID'
,group_concat(cb.loanNo) Loans
from (
select o.name branch
,c.display_name client
,c.external_id client_id
#,g.display_name groupName
#,g.external_id pfcode
#,l.id noofloans
,c.id noOfClients
,l.id loanNo
from m_loan l
join m_group g on g.id=l.group_id

join `additional details` ad on ad.`Loan No`=l.id
join m_client c on c.id=ad.client_id
join m_office o on o.id=c.office_id

where ((l.disbursedon_date <= @ad and l.loan_status_id =300)
or (l.loan_status_id in (600,601,700) and l.disbursedon_date <= @ad and ifnull(l.closedon_date,l.writtenoffon_date) > @ad))
and l.id <=23822
#group by c.id

union all

select o.name branch
,c.display_name client
,c.external_id client_id
#,g.display_name groupName
#,g.external_id pfcode
#,l.id noofloans
,c.id
,l.id
from m_loan l
join m_loan_glim lg on lg.loan_id=l.id
join m_group g on g.id=l.group_id
join m_client c on c.id=lg.client_id and lg.is_client_selected=1
join m_office o on o.id=c.office_id
where ((l.disbursedon_date <= @ad and l.loan_status_id =300)
or (l.loan_status_id in (600,601,700) and l.disbursedon_date <= @ad and ifnull(l.closedon_date,l.writtenoffon_date) > @ad))
#group by c.id
) cb
group by cb.noOfClients
