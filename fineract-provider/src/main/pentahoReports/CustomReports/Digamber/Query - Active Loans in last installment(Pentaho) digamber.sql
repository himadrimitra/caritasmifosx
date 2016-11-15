







select concat(repeat("..",   
   ((LENGTH(lastInstallment.`hierarchy`) - LENGTH(REPLACE(lastInstallment.`hierarchy`, '.', '')) - 1))), lastInstallment.branch) as "Office/Branch",lastInstallment.Bname as name ,
lastInstallment.Currency,
lastInstallment.`Loan Officer`, 
lastInstallment.`Client Account No`, lastInstallment.`Client`, 
lastInstallment.`Loan Account No`, lastInstallment.`Product`, 
lastInstallment.`Fund`,  lastInstallment.`Loan Amount`, 
lastInstallment.`Annual Nominal Interest Rate`, 
lastInstallment.`Disbursed`, lastInstallment.`Expected Matured On` ,

l.principal_repaid_derived as "Principal Repaid",
l.principal_outstanding_derived as "Principal Outstanding",
laa.principal_overdue_derived as "Principal Overdue",

l.interest_repaid_derived as "Interest Repaid",
l.interest_outstanding_derived as "Interest Outstanding",
laa.interest_overdue_derived as "Interest Overdue",

l.fee_charges_repaid_derived as "Fees Repaid",
l.fee_charges_outstanding_derived  as "Fees Outstanding",
laa.fee_charges_overdue_derived as "Fees Overdue",

l.penalty_charges_repaid_derived as "Penalties Repaid",
l.penalty_charges_outstanding_derived as "Penalties Outstanding",
laa.penalty_charges_overdue_derived as "Penalties Overdue"

from 
(select l.id as loanId, l.number_of_repayments, min(r.installment), ounder.name AS Bname,
ounder.id, ounder.hierarchy, ounder.`name` as branch, 
ifnull(cur.display_symbol, l.currency_code) as Currency,
lo.display_name as "Loan Officer", c.account_no as "Client Account No",
c.display_name as "Client", l.account_no as "Loan Account No", pl.`name` as "Product", 
f.`name` as Fund,  l.principal_amount as "Loan Amount", 
l.annual_nominal_interest_rate as "Annual Nominal Interest Rate", 
date(l.disbursedon_date) as "Disbursed", date(l.expected_maturedon_date) as "Expected Matured On"
from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')
and ounder.hierarchy like concat(${userhierarchy}, '%')
join m_client c on c.office_id = ounder.id
join m_loan l on l.client_id = c.id
join m_product_loan pl on pl.id = l.product_id
left join m_staff lo on lo.id = l.loan_officer_id
left join m_currency cur on cur.code = l.currency_code
left join m_fund f on f.id = l.fund_id
left join m_loan_repayment_schedule r on r.loan_id = l.id

where  o.id = ${Branch} 
and (ifnull(l.loan_officer_id, -10) = ${Loan Officer} or "-1" = ${Loan Officer})
and (l.currency_code = ${CurrencyId} or "-1" = ${CurrencyId})
and (l.product_id = ${loanProductId} or "-1" = ${loanProductId})
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})
and (ifnull(l.loan_purpose_id, -10) = ${loanPurposeId} or "-1" = ${loanPurposeId})

and l.loan_status_id = 300
and r.completed_derived is false
and r.duedate >= curdate()
group by l.id
having l.number_of_repayments = min(r.installment)) lastInstallment
join m_loan l on l.id = lastInstallment.loanId
left join m_loan_arrears_aging laa on laa.loan_id = l.id
order by lastInstallment.hierarchy, lastInstallment.Currency, lastInstallment.`Client Account No`, lastInstallment.`Loan Account No`                                                