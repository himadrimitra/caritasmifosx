


select concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.`name`) as "Office/Branch",ounder.name, ounder.id,
ifnull(cur.display_symbol, l.currency_code) as Currency,
ifnull(lo.`display_name`,'-') as "Loan Officer", 
c.display_name as "Client", 
l.account_no as "Loan Account No.",
ifnull(pl.`name`,'-') as "Product", 
ifnull(f.`name`,'-') as Fund,  
ifnull(l.`principal_amount`,0) as "Loan Amount", 
ifnull(l.`annual_nominal_interest_rate`,0)as "Annual Nominal Interest Rate", 
date(l.disbursedon_date) as "Disbursed Date", 
date(l.expected_maturedon_date) as "Expected Matured On",

sum(ifnull(mlrs.principal_completed_derived,0)) as "Principal Repaid",
(l.principal_disbursed_derived-sum(ifnull(mlrs.principal_completed_derived,0))) as "Principal Outstanding",
(sum(mlrs.principal_amount)-sum(ifnull(mlrs.principal_completed_derived,0))) as "Principal Overdue",

sum(ifnull(mlrs.interest_completed_derived,0)) as "Interest Repaid",
(l.interest_charged_derived-sum(ifnull(mlrs.interest_completed_derived,0))) as "Interest Outstanding",
(sum(mlrs.interest_amount)-sum(ifnull(mlrs.interest_completed_derived,0))) as "Interest Overdue",

sum(ifnull(mlrs.fee_charges_completed_derived,0)) as "Fees Repaid",
(l.fee_charges_charged_derived-sum(ifnull(mlrs.fee_charges_completed_derived,0)))  as "Fees Outstanding",
(sum(mlrs.fee_charges_amount)-sum(ifnull(mlrs.fee_charges_completed_derived,0))) as "Fees Overdue",

sum(ifnull(mlrs.penalty_charges_completed_derived,0)) as "Penalties Repaid",
(l.penalty_charges_charged_derived-sum(ifnull(mlrs.penalty_charges_completed_derived,0))) as "Penalties Outstanding",
(sum(mlrs.penalty_charges_amount)-sum(ifnull(mlrs.penalty_charges_completed_derived,0))) as "Penalties Overdue" 

from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%') 
and ounder.hierarchy like concat(${userhierarchy}, '%') 
join m_client c on c.office_id = ounder.id 
join m_loan l on l.client_id = c.id 
join m_product_loan pl on pl.id = l.product_id 
join m_loan_repayment_schedule mlrs on mlrs.loan_id =l.id and mlrs.duedate <= ${ondate}
left join m_staff lo on lo.id = l.loan_officer_id 
left join m_currency cur on cur.code = l.currency_code 
left join m_fund f on f.id = l.fund_id 

where o.id = ${branch} 
and (ifnull(l.loan_officer_id, -10) = ${loanOfficer} or "-1" = ${loanOfficer}) 
and (l.currency_code = ${currencyId} or "-1" = ${currencyId}) 
and (l.product_id = ${loanProductId} or "-1" = ${loanProductId}) 
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId}) 
and (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or "-1" = ${loanPurposeId}) 
and l.disbursedon_date < ${ondate} 
and if(l.closedon_date IS NOT NULL,l.closedon_date > ${ondate},true)
and if(l.writtenoffon_date IS NOT NULL,l.withdrawnon_date > ${ondate} ,true) 
and l.loan_status_id in (300,600,601,700) 

group by l.id                                                                                                