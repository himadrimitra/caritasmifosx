









select concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.`name`) as "Office/Branch",ounder.name, ounder.id,
ifnull(cur.display_symbol, l.currency_code) as Currency,concat("From"," ",${startDate}," ","To"," " ,${endDate})as period,
c.account_no as "Client Account No.", c.display_name as "Client",
l.account_no as "Loan Account No.", pl.`name` as "Product", 
ifnull(f.`name`,'-') as Fund,  
l.principal_amount as "Loan Amount", 
l.total_repayment_derived  as "Total Repaid", 
l.annual_nominal_interest_rate as " Annual Nominal Interest Rate", 
date(l.disbursedon_date) as "Disbursed", 
date(l.closedon_date) as "Closed",

l.principal_repaid_derived as "Principal Repaid",
l.interest_repaid_derived as "Interest Repaid",
ifnull(l.fee_charges_repaid_derived,0) as "Fees Repaid",
ifnull(l.penalty_charges_repaid_derived,0) as "Penalties Repaid",
ifnull(lo.display_name,'-') as "Loan Officer"

from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')
and ounder.hierarchy like concat(${userhierarchy}, '%')
join m_client c on c.office_id = ounder.id
join m_loan l on l.client_id = c.id
join m_product_loan pl on pl.id = l.product_id
left join m_staff lo on lo.id = l.loan_officer_id
left join m_currency cur on cur.code = l.currency_code
left join m_fund f on f.id = l.fund_id


where o.id = ${Branch}
and (ifnull(l.loan_officer_id, -10) = ${Loan Officer} or -1 = ${Loan Officer})
and (l.currency_code = ${CurrencyId} or -1 = ${CurrencyId})
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})
and (l.product_id = ${loanProductId} or -1 = ${loanProductId})
and (ifnull(l.loan_purpose_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})
and (case
	when ${obligDateType} = 1 then
    l.closedon_date between ${startDate} and ${endDate}
	when ${obligDateType} = 2 then
    l.disbursedon_date between ${startDate} and ${endDate}
	else 1 = 1
	end)
and l.loan_status_id = 600
group by l.id
order by ounder.hierarchy, l.currency_code, c.account_no, l.account_no
                                                            