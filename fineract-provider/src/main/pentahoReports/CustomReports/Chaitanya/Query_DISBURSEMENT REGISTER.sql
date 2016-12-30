


select mounder.`name` as "Office/Branch",
mgc.display_name as "name",
mgc.id as `center_id`
,mc.id as client_id
,ml.account_no as Loan_No
,mc.display_name as Customer_Name 
,dd.principal as Disb_Amount
,dd.disbursedon_date as Disb_Date
,ml.approved_principal as Sanc_Amount
,ml.approvedon_date as Sanc_Date

,(select min(mlrss.duedate) from m_loan_repayment_schedule mlrss where mlrss.duedate > dd.disbursedon_date and mlrss.loan_id = dd.loan_id) as Sch_Date
,(select max(mlrss.duedate) from m_loan_repayment_schedule mlrss where mlrss.loan_id = dd.loan_id) as Maturity_Date
,(select count(*) from m_loan_repayment_schedule mlrss where mlrss.loan_id = dd.loan_id) as No_of_Installment

-- ,case when dd.disbursedon_date > ml.disbursedon_date then ifnull(tv.decimal_value,ml.fixed_emi_amount)
-- else ml.fixed_emi_amount end as Schedule_Amt
,ifnull(tv.decimal_value,ml.fixed_emi_amount) Schedule_Amt
,centerstaff.display_name loanofficer
,mo.id as office_id
,centerstaff.id as loan_officer
,mpl.id as product_id
,mpl.short_name as prodname
,centerstaff.display_name as loanofficer_name
#,insufee.insurance_type
,GROUP_CONCAT(insufee.insurance_type SEPARATOR ' ') insurance_type
,floor(ifnull(group_concat(insufee.insurance_amount SEPARATOR ' '),0)) as insurance_amount,floor(ifnull(group_concat(lf.lpf SEPARATOR ' '),0)) as  LPF
,floor(ifnull(group_concat(insufee.insurance_amount SEPARATOR ' '),0)+ifnull(group_concat(lf.lpf SEPARATOR ' '),0)) total
,dc.loan_charge_id
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
and mounder.hierarchy -1
inner join m_client mc on mc.office_id=mounder.id
inner join m_loan ml on mc.id=ml.client_id 

inner join m_loan_disbursement_detail dd on dd.loan_id = ml.id
left join m_loan_term_variations tv on tv.loan_id = dd.loan_id
left join m_loan_tranche_disbursement_charge dc on dc.disbursement_detail_id = dd.id
left join (select mlc.id charge_id,mlc.loan_id loan_id, '' as LPF, (case when c.name like '%Single%' then 'Single' when c.name like '%Double%' then 'Double' end) insurance_type, sum(mlc.amount) insurance_amount
 from m_loan_charge mlc
 inner join m_charge c on c.id = mlc.charge_id
where mlc.charge_id not in (1,13,14,15,16,28,31) and mlc.is_active=1
 group by mlc.id)insufee on insufee.loan_id=ml.id and insufee.charge_id=dc.loan_charge_id
 left join (select mlc.id charge_id, mlc.loan_id loan_id,  sum(mlc.amount) LPF 
 from m_loan_charge mlc
 where mlc.charge_id in (1,14,16,28,31) and mlc.is_active=1
 group by mlc.id
 )lf on lf.loan_id=ml.id and lf.charge_id = dc.loan_charge_id
left join m_group mg on ml.group_id=mg.id
left join m_group mgc on mgc.id=mg.parent_id 
left join m_staff centerstaff on centerstaff.id = mgc.staff_id
inner join m_product_loan mpl on mpl.id=ml.product_id
left join m_code_value mcvv on mcvv.id=ml.loan_purpose_id and  mcvv.code_id=3
where mo.id= ${selectOffice}
and dd.disbursedon_date between ${fromDate} AND ${toDate}
group by ml.id
order by  mounder.id,dd.disbursedon_date ,mgc.id                                                                                                                             