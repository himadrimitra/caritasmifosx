





SELECT 
concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.`name`) as "Office/Branch",ounder.name, ounder.id,
c.account_no as "Client Account No.", c.display_name as "Client Name", 
ifnull(cur.display_symbol, l.currency_code) as Currency,  pl.`name` as "Product", 
l.account_no as "Loan Account No.", 
l.principal_amount as "Loan Amount", 
l.term_frequency as "Term Frequency",


tf.enum_message_property as "Term Frequency Period",
l.annual_nominal_interest_rate as "Annual Nominal Interest Rate", 
datediff(curdate(), l.submittedon_date) "Days Pending Approval", 
ifnull(purp.name,'-') as "Loan Purpose",
ifnull(lo.display_name,'-') as "Loan Officer"
from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')
and ounder.hierarchy like concat(${userhierarchy}, '%')
join m_client c on c.office_id = ounder.id
join m_loan l on l.client_id = c.id
join m_product_loan pl on pl.id = l.product_id
left join m_staff lo on lo.id = l.loan_officer_id
left join m_currency cur on cur.code = l.currency_code
left join f_loan_purpose purp on purp.id = l.loan_purpose_id
left join r_enum_value tf on tf.enum_name = "term_period_frequency_enum" and tf.enum_id = l.term_period_frequency_enum


where o.id = ${Branch}
and (ifnull(l.loan_officer_id, -10) = ${Loan Officer} or "-1" = ${Loan Officer})
and (l.currency_code = ${CurrencyId} or "-1" = ${CurrencyId})
and (l.product_id = ${loanProductId} or "-10" = ${loanProductId})
and (ifnull(l.loan_purpose_id, -10) = ${loanPurposeId} or "-1" = ${loanPurposeId})
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})
and  l.loan_status_id = 100 /*Submitted and awaiting approval */
order by ounder.hierarchy, l.submittedon_date,  l.account_no 
                                                                       