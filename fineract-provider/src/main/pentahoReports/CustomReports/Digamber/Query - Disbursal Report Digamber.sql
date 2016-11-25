

select l.loan_type_enum
,ounder.id branch
,0 as cid
,0 as gid
,l.account_no accountNo
,ifnull(mpd.receipt_number,'NA') receiptNo
,ifnull(c.mobile_no,'NA') mobileNo
,c.display_name clientName
,(l.principal_disbursed_derived -(ifnull(pf.amount,0) + ifnull(st.amount,0) + ifnull(asb.amount,0)) ) disbAmount
,mp.name product
,ifnull(pf.amount,0) processingFees
,ifnull(st.amount,0) serviceTax
,ifnull(asb.amount,0) annualSubscriptionFees
,l.disbursedon_date disbDate
,date(t.created_date) createdDate
,case l.loan_type_enum
	when 1 then 'Individual'
	when 2 then 'Group'
	when 3 then 'JLG'
	end as loanType
,ifnull(s.display_name,'NA') loanOfficer
,concat(u.firstname,' ',u.lastname) mifosUser
,ounder.name branchName
,'NA' as groupName
,'NA' as centerName
from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%') 
and ounder.hierarchy like concat(${userhierarchy}, '%')
inner join m_client c on c.office_id=ounder.id
inner join m_loan l on l.client_id=c.id and l.loan_type_enum=1 and l.loan_status_id in (300,600,601,700)
inner join r_enum_value ev on ev.enum_id=l.loan_status_id and ev.enum_name='loan_status_id'
left join m_code_value gn on gn.id=c.gender_cv_id
left join m_staff s on s.id=l.loan_officer_id
left join m_fund f on f.id=l.fund_id
inner join m_product_loan mp on mp.id=l.product_id
left join (select l.id,mc.amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id=2) pf on pf.id=l.id
left join (select l.id,mc.amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id=3) st on st.id=l.id
left join (select l.id,sum(mc.amount) amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id not in (2,3)
group by l.id
order by l.id) asb on asb.id=l.id
inner join m_loan_transaction t on t.loan_id=l.id and t.transaction_type_enum=1 and t.is_reversed=0
inner join m_appuser u on u.id=t.createdby_id
left join m_payment_detail mpd on mpd.id=t.payment_detail_id
where o.id=${branch}
and l.disbursedon_date between ${fromDate} and ${toDate}

union

select l.loan_type_enum
,ounder.id branch
,ifnull(cn.id,0) as cid
,gr.id as gid
,l.account_no accountNo
,ifnull(mpd.receipt_number,'NA') receiptNo
,ifnull(c.mobile_no,'NA') mobileNo
,c.display_name name
,(l.principal_disbursed_derived -(ifnull(pf.amount,0) + ifnull(st.amount,0) + ifnull(asb.amount,0)) ) disbAmount
,mp.name product
,ifnull(pf.amount,0) processingFees
,ifnull(st.amount,0) serviceTax
,ifnull(asb.amount,0) annualSubscriptionFees
,l.disbursedon_date disbDate
,date(t.created_date) createdDate
,case l.loan_type_enum
	when 1 then 'Individual'
	when 2 then 'Group'
	when 3 then 'JLG'
	end as loanType
,ifnull(s.display_name,'NA') loanOfficer
,concat(u.firstname,' ',u.lastname) mifosUser
,ounder.name branchName
,gr.display_name as groupName
,ifnull(cn.display_name,'NA') as centerName
from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%') 
and ounder.hierarchy like concat(${userhierarchy}, '%')
inner join m_client c on c.office_id=ounder.id
inner join m_group_client mgc on mgc.client_id=c.id
inner join m_group gr on gr.id=mgc.group_id
left join m_group cn on cn.id=gr.parent_id
inner join m_loan l on l.client_id=c.id and l.group_id=gr.id and l.loan_type_enum=3 and l.loan_status_id in (300,600,601,700)
inner join r_enum_value ev on ev.enum_id=l.loan_status_id and ev.enum_name='loan_status_id'
left join m_code_value gn on gn.id=c.gender_cv_id
left join m_staff s on s.id=l.loan_officer_id
left join m_fund f on f.id=l.fund_id
inner join m_product_loan mp on mp.id=l.product_id
left join (select l.id,mc.amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id=2) pf on pf.id=l.id
left join (select l.id,mc.amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id=3) st on st.id=l.id
left join (select l.id,sum(mc.amount) amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id not in (2,3)
group by l.id
order by l.id) asb on asb.id=l.id
inner join m_loan_transaction t on t.loan_id=l.id and t.transaction_type_enum=1 and t.is_reversed=0
inner join m_appuser u on u.id=t.createdby_id
left join m_payment_detail mpd on mpd.id=t.payment_detail_id
where o.id=${branch}
and l.disbursedon_date between ${fromDate} and ${toDate}

union

select l.loan_type_enum
,ounder.id branch
,ifnull(cn.id,0) as cid
,gr.id as gid
,l.account_no accountNo
,ifnull(mpd.receipt_number,'NA') receiptNo
,'NA' mobileNo
,'NA' as clientName
,(l.principal_disbursed_derived -(ifnull(pf.amount,0) + ifnull(st.amount,0) + ifnull(asb.amount,0)) ) disbAmount
,mp.name product
,ifnull(pf.amount,0) processingFees
,ifnull(st.amount,0) serviceTax
,ifnull(asb.amount,0) annualSubscriptionFees
,l.disbursedon_date disbDate
,date(t.created_date) createdDate
,case l.loan_type_enum
	when 1 then 'Individual'
	when 2 then 'Group'
	when 3 then 'JLG'
	end as loanType
,ifnull(s.display_name,'NA') loanOfficer
,concat(u.firstname,' ',u.lastname) mifosUser
,ounder.name branchName
,gr.display_name as groupName
,ifnull(cn.display_name,'NA') as centerName
from m_office o 
join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%') 
and ounder.hierarchy like concat(${userhierarchy}, '%')
inner join m_group gr on gr.office_id=ounder.id
left join m_group cn on cn.id=gr.parent_id
inner join m_loan l on l.group_id=gr.id and l.loan_type_enum=2 and l.loan_status_id in (300,600,601,700)
inner join r_enum_value ev on ev.enum_id=l.loan_status_id and ev.enum_name='loan_status_id'
left join m_staff s on s.id=l.loan_officer_id
left join m_fund f on f.id=l.fund_id
inner join m_product_loan mp on mp.id=l.product_id
left join (select l.id,mc.amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id=2) pf on pf.id=l.id
left join (select l.id,mc.amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id=3) st on st.id=l.id
left join (select l.id,sum(mc.amount) amount
from m_loan l
inner join m_loan_charge mc on mc.loan_id=l.id and mc.is_active = 1
inner join m_charge c on c.id=mc.charge_id and c.id not in (2,3)
group by l.id
order by l.id) asb on asb.id=l.id
inner join m_loan_transaction t on t.loan_id=l.id and t.transaction_type_enum=1 and t.is_reversed=0
inner join m_appuser u on u.id=t.createdby_id
left join m_payment_detail mpd on mpd.id=t.payment_detail_id
where o.id=${branch}
and l.disbursedon_date between ${fromDate} and ${toDate}

order by 1,2,3,4                                                