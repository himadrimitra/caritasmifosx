



select m.id 'Loan ID',c.external_id 'Customer Internal Id',ounder.name 'Branch',cn.display_name 'Centre',
concat(c.display_name,' ',pii.`Spouse Name`) 'Full Name of Customer',
concat(pii.`Permanent Address 1`,' ',pii.`Permanent Address 2`,' ',pii.`Pin Code`,' ',pii.District,' ',pii.State_cd_State) full_Address,
mcv.name 'Purpose of Loan',
m.disbursedon_date 'Date of Disbursement',
m.principal_disbursed_derived 'Disbursed Amount',
m.number_of_repayments 'No of Installemnts',
a.emi 'Inst.AmtRs.',
concat ('every ', m.repay_every,' ',(
case when m.repayment_period_frequency_enum =1 then 'weeks'
when m.repayment_period_frequency_enum =2 then 'month' end)) 'Collection Frequency',
b.repaymentspaid 'Remaining Installments',
a.fisrtrp 'Date of First Installment',
a.lastrp 'Date of Last Installment',
ifnull(m.principal_disbursed_derived,0)-ifnull(c1.summpaid,0) 'Principal Outstanding as on Date',
ifnull(aa.total_overdue_derived,0) 'DPD as on Date',
f.name 'Fund'
from m_office o
left join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%') 
and ounder.hierarchy like concat(${userhierarchy}, '%')
inner join m_client c on c.office_id = ounder.id
inner join m_loan m on m.client_id = c.id and m.disbursedon_date <= ${fromDate} and m.loan_status_id = 300
left join (select r.loan_id loan,
count(*) repayments,
(r.principal_amount+r.interest_amount) emi,
min(r.duedate) fisrtrp,
max(r.duedate) lastrp
from m_loan_repayment_schedule r
group by r.loan_id) a on a.loan = m.id
left join (select r.loan_id loann,
count(*) repaymentspaid,
min(r.duedate) minduedate
from m_loan_repayment_schedule r
 where r.completed_derived = 0
group by r.loan_id)b on b.loann = m.id
left join (select r.loan_id loannn,
sum(r.principal_completed_derived) summpaid
from m_loan_repayment_schedule r
where r.duedate <= ${fromDate}
group by r.loan_id)c1 on c1.loannn = m.id
left join m_loan_arrears_aging aa on aa.loan_id = m.id
left join m_group g on g.id = m.group_id
left join m_fund f on f.id = m.fund_id
left join m_group cn on cn.id = g.parent_id
left join f_loan_purpose mcv on mcv.id = m.loan_purpose_id
left join `personal information` pii on pii.client_id = c.id
where o.id = ${branch}                       