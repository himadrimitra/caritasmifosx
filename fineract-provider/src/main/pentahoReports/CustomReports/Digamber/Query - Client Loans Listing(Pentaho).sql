a







select 
concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.name) 
	as "Office/Branch",ounder.name, ounder.id, c.account_no as "Client Account No.", 
c.display_name as "Name",
r.enum_message_property as "Client Status",
ifnull(lo.display_name,"-") as "Loan Officer", ifnull(l.account_no,"-") as "Loan Account No.",ifnull(l.external_id,'-') as
 "External Id", p.name as Loan, st.enum_message_property as "Status",  
ifnull(f.name,'-') as Fund, ifnull(purp.name,'-') as "Loan Purpose",
ifnull(cur.display_symbol, l.currency_code) as Currency,  
l.principal_amount, ifnull(l.arrearstolerance_amount,0) as "Arrears Tolerance Amount",
ifnull(l.number_of_repayments,0) as "Expected No. Repayments", 
ifnull(l.annual_nominal_interest_rate,0) as "Annual_Nominal_Interest_Rate", 
ifnull(l.nominal_interest_rate_per_period,0) as "Nominal Interest Rate Per Period",
ifnull(ipf.enum_message_property,'-') as "Interest Rate Frequency",
ifnull(im.enum_message_property,'-') as "Interest Method",
ifnull(icp.enum_message_property,'-') as "Interest Calculated in Period",
ifnull(l.term_frequency,0) as "Term Frequency",
ifnull(tf.enum_message_property,'-') as "Term Frequency Period",
ifnull(l.repay_every,'-') as "Repayment Frequency",
ifnull(rf.enum_message_property,'-') as "Repayment Frequency Period",
ifnull(am.enum_message_property,'-') as "Amortization",
ifnull(l.total_charges_due_at_disbursement_derived,0) as "Total Charges Due At Disbursement",
date(l.submittedon_date) as Submitted, 
date(l.approvedon_date) Approved, 
l.expected_disbursedon_date As "Expected Disbursal",
date(l.expected_firstrepaymenton_date) as "Expected First Repayment", 
date(l.interest_calculated_from_date) as "Interest Calculated From" ,
date(l.disbursedon_date) as Disbursed, 
date(l.expected_maturedon_date) "Expected Maturity",
date(l.maturedon_date) as "Matured On", date(l.closedon_date) as Closed,
date(l.rejectedon_date) as Rejected, date(l.rescheduledon_date) as Rescheduled, 
date(l.withdrawnon_date) as Withdrawn, date(l.writtenoffon_date) "Written Off"
from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')
and ounder.hierarchy like concat(${userhierarchy}, '%')
join m_client c on c.office_id = ounder.id
left join r_enum_value r on r.enum_name = 'status_enum' 
 and r.enum_id = c.status_enum
left join m_loan l on l.client_id = c.id
left join m_staff lo on lo.id = l.loan_officer_id
left join m_product_loan p on p.id = l.product_id
left join m_fund f on f.id = l.fund_id
left join r_enum_value st on st.enum_name = "loan_status_id" and st.enum_id = l.loan_status_id
left join r_enum_value ipf on ipf.enum_name = "interest_period_frequency_enum" 
 and ipf.enum_id = l.interest_period_frequency_enum
left join r_enum_value im on im.enum_name = "interest_method_enum" 
 and im.enum_id = l.interest_method_enum
left join r_enum_value tf on tf.enum_name = "term_period_frequency_enum" 
 and tf.enum_id = l.term_period_frequency_enum
left join r_enum_value icp on icp.enum_name = "interest_calculated_in_period_enum" 
 and icp.enum_id = l.interest_calculated_in_period_enum
left join r_enum_value rf on rf.enum_name = "repayment_period_frequency_enum" 
 and rf.enum_id = l.repayment_period_frequency_enum
left join r_enum_value am on am.enum_id = l.amortization_method_enum and am.enum_name = "amortization_method_enum"  
left join f_loan_purpose purp on purp.id = l.loan_purpose_id
left join m_currency cur on cur.code = l.currency_code
where o.id = ${Branch}
and (ifnull(l.loan_officer_id, -10) = ${Loan Officer} or "-1" = ${Loan Officer})
and (l.currency_code = ${CurrencyId} or "-1" = ${CurrencyId})
and (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})
and (l.product_id = ${loanProductId} or "-1" = ${loanProductId})
and (ifnull(l.loan_purpose_id, -10) = ${loanPurposeId} or "-1" = ${loanPurposeId})


order by ounder.hierarchy, 2 , l.id, lo.id                                                                                                      