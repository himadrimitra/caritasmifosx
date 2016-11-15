







select concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.`name`) as "Office/Branch",ounder.name AS NAME,
ifnull(cur.display_symbol, l.currency_code) as Currency,
c.account_no as "Client Account No", 
c.display_name as "Client",
l.account_no as "Loan Account No", 
ifnull(pl.`name`,'-') as "Product", 
ifnull(f.`name`,'-') as "Fund",  
ifnull(l.principal_amount,0) as "Loan Principal Amount", 
ifnull(l.annual_nominal_interest_rate,0) as "Annual Nominal Interest Rate", 
date(l.disbursedon_date) as "Disbursed Date", 

l.total_expected_repayment_derived as "Total Loan (P+I+F+Pen)",
l.total_repayment_derived as "Total Repaid (P+I+F+Pen)",
ifnull(lo.display_name,'-') as "Loan Officer",
concat('Disbursal Period from',' ',${startDate},' ','To',' ',${endDate}) as period

from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')
and ounder.hierarchy like concat(${userhierarchy}, '%')
join m_client c on c.office_id = ounder.id
join m_loan l on l.client_id = c.id
join m_product_loan pl on pl.id = l.product_id
left join m_staff lo on lo.id = l.loan_officer_id
left join m_currency cur on cur.code = l.currency_code
left join m_fund f on f.id = l.fund_id
left join m_loan_arrears_aging laa on laa.loan_id = l.id
where  l.disbursedon_date between ${startDate} and ${endDate}
and l.loan_status_id = 300
AND o.id = ${Branch} 
and (ifnull(l.loan_officer_id, -10) = ${loanOfficer} or "-1" = ${loanOfficer})
and (l.currency_code = ${CurrencyId} or "-1" = ${CurrencyId})
and (l.product_id = ${loanProductId} or "-1" = ${loanProductId})
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})
and (ifnull(l.loan_purpose_id, -10) = ${loanPurposeId} or "-1" = ${loanPurposeId})
group by l.id
order by ounder.hierarchy, l.currency_code, c.account_no, l.account_no                                                                                          