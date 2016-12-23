
select concat(repeat("..",   
   ((LENGTH(mounder.`hierarchy`) - LENGTH(REPLACE(mounder.`hierarchy`, '.', '')) - 1))), mounder.`name`) as "Office/Branch",
	mounder.name, mounder.id

,mc.id as client_id

,ml.account_no as Loan_No
,mc.display_name as Customer_Name 
,mcvv.code_value as Activity
, ml.principal_amount  as Sanc_Amount
,ml.submittedon_date as Sanc_Date
,ml.approved_principal as Disb_Amount
,ml.approvedon_date as Disb_Date
,mlrs.duedate as Sch_Date
,ml.fixed_emi_amount as Schedule_Amt
,mcv.value as Pay_Mode
,a.code as FA_Code
,ms.display_name loanofficer
,mpd.check_number as Cheque_no
,mo.id as office_id
,ms.id as loan_officer
,mpl.id as product_id
,mpl.name as prodname
,ms.display_name as loanofficer_name



,case 

					when ml.repay_every=1 and ml.repayment_period_frequency_enum = 0 then 'Dialy'
					when ml.repay_every=1 and ml.repayment_period_frequency_enum = 1 then 'Weekly'
					when ml.repay_every=1 and ml.repayment_period_frequency_enum = 2 then 'Monthly'
					when ml.repay_every=2 and ml.repayment_period_frequency_enum = 1 then 'Bi-Weekly'
					when ml.repay_every=4 and ml.repayment_period_frequency_enum = 1 then '4th-Weekly'

				else "Yearly"
end as Repay_Freq



from m_office mo 
join m_office mounder on mounder.hierarchy like concat(mo.hierarchy, '%')
and mounder.hierarchy like concat(${userhierarchy}, '%')
inner join m_client mc on mc.office_id=mounder.id
inner join m_loan ml on mc.id=ml.client_id


left join m_loan_transaction mlt on mlt.loan_id=ml.id and mlt.transaction_type_enum=1 and mlt.is_reversed=0
left join m_payment_detail mpd on mpd.id=mlt.payment_detail_id
left join m_payment_type mcv on mcv.id=mpd.payment_type_id

left join m_staff ms on ms.id=ml.loan_officer_id
inner join m_product_loan mpl on mpl.id=ml.product_id
left join m_code_value mcvv on mcvv.id=ml.loan_purpose_id and  mcvv.code_id=3
left join m_loan_repayment_schedule mlrs on mlrs.loan_id=ml.id and mlrs.installment=1

left join (select mlt.loan_id id, agc.gl_code code from acc_gl_journal_entry aj

inner join m_loan_transaction mlt on mlt.id=aj.loan_transaction_id and mlt.transaction_type_enum=1 and mlt.is_reversed=0
inner join acc_gl_account agc on agc.id=aj.account_id

where aj.loan_transaction_id is not null 
and aj.type_enum=1)a on a.id=ml.id


where mo.id=${selectOffice}
and (ifnull(ml.loan_officer_id, -10) = ${loanOfficer} or -1 = ${loanOfficer}) 
and (ml.product_id = ${loanProductId} or -1 = ${loanProductId}) 
and ml.disbursedon_date between  ${fromDate} AND ${toDate}

order by  mounder.id,mpl.id,ms.id                                                                  