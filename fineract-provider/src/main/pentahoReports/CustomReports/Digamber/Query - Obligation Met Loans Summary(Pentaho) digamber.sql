









select concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.`name`) as "Office/Branch",ounder.name, ounder.id,
ifnull(cur.display_symbol, l.currency_code) as Currency,
concat("From"," ",${startDate}," ","To"," " ,${endDate})as "period",
count(distinct(c.id)) as "No. of Clients",
count(distinct(l.id)) as "No. of Loans",
sum(l.principal_amount) as "Total Loan Amount", 
sum(l.principal_repaid_derived) as "Total Principal Repaid",
sum(l.interest_repaid_derived) as "Total Interest Repaid",
sum(l.fee_charges_repaid_derived) as "Total Fees Repaid",
sum(l.penalty_charges_repaid_derived) as "Total Penalties Repaid",
sum(l.interest_waived_derived) as "Total Interest Waived",
sum(l.fee_charges_waived_derived) as "Total Fees Waived",
sum(l.penalty_charges_waived_derived) as "Total Penalties Waived"

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
and (ifnull(l.loan_officer_id, -10) = ${Loan Officer} or "-1" = ${Loan Officer})
and (l.currency_code = ${CurrencyId} or "-1" = ${CurrencyId})
and (l.product_id = ${loanProductId} or "-1" = ${loanProductId})
and (ifnull(l.loan_purpose_id, -10) = ${loanPurposeId} or "-1" = ${loanPurposeId})
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})
and (case
	when ${obligDateType} = 1 then
    l.closedon_date between ${Startdate} and ${Enddate}
	when ${obligDateType} = 2 then
    l.disbursedon_date between ${Startdate} and ${Enddate}
	else 1 = 1
	end)
and l.loan_status_id = 600


group by ounder.hierarchy, l.currency_code
order by ounder.hierarchy, l.currency_code                                                            