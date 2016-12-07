








SELECT ln.loan_type_enum
,ounder.id branch,cl.external_id
,0 as cid
,0 as gid
,'NA' groupName
,'NA' centerName
,cl.display_name clientName
,ln.account_no accNo
,IFNULL(lt.principal_portion_derived,0) 'Principal Amount'
,IFNULL(lt.interest_portion_derived,0) 'Intrest Amount'
,IFNULL(lt.fee_charges_portion_derived,0) 'Fees'
,IFNULL(lt.penalty_charges_portion_derived,0) 'Penalty'
,IFNULL(lt.overpayment_portion_derived,0) + IFNULL(lt.unrecognized_income_portion,0) 'Others'
,lt.amount 'Total Receipt Amount'
,CONCAT(au.firstname, ' ' , au.lastname) 'Mifos User'
,lt.transaction_date 'Action Date' 
,lt.created_date 'Created Date' 
,ifnull(mpd.receipt_number,'') Receipt
,ounder.name branchName
,case ln.loan_type_enum
	when 1 then 'Individual'
	when 2 then 'Group'
	when 3 then 'JLG'
	end as loanType
,ifnull(cl.mobile_no,'NA') mobileNo,ifnull(ms.display_name ,'NA') staff
FROM
m_office o
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,'%')
and ounder.hierarchy like concat(${userhierarchy},'%')
INNER JOIN m_client cl ON cl.office_id = ounder.id
INNER JOIN m_loan ln ON ln.client_id = cl.id and ln.loan_type_enum=1
INNER JOIN m_loan_transaction lt ON lt.loan_id = ln.id
LEFT JOIN m_appuser au ON au.id = lt.createdby_id
left join m_payment_detail mpd on mpd.id=lt.payment_detail_id
left join m_staff ms on ms.id=ln.loan_officer_id
WHERE o.id=${branch}
and  ln.loan_status_id IN (300,600,700,601)
AND  DATE(lt.created_date) between ${fromDate} and ${toDate}
and lt.is_reversed=0 
and lt.transaction_type_enum in (2,8)
and lt.amount<>0

union

SELECT ln.loan_type_enum
,ounder.id branch,mc.external_id
,0 as cid
,0 as gid
,gr.display_name groupName
,ifnull(cn.display_name,'NA') centerName
,'NA' clientName
,ln.account_no accNo
,IFNULL(lt.principal_portion_derived,0) 'Principal Amount'
,IFNULL(lt.interest_portion_derived,0) 'Intrest Amount'
,IFNULL(lt.fee_charges_portion_derived,0) 'Fees'
,IFNULL(lt.penalty_charges_portion_derived,0) 'Penalty'
,IFNULL(lt.overpayment_portion_derived,0) + IFNULL(lt.unrecognized_income_portion,0) 'Others'
,lt.amount 'Total Receipt Amount'
,CONCAT(au.firstname, ' ' , au.lastname) 'Mifos User'
,lt.transaction_date 'Action Date' 
,lt.created_date 'Created Date' 
,ifnull(mpd.receipt_number,'') Receipt
,ounder.name branchName
,case ln.loan_type_enum
	when 1 then 'Individual'
	when 2 then 'Group'
	when 3 then 'JLG'
	end as loanType
,'NA' mobileNo,ifnull(ms.display_name ,'NA') staff
FROM
m_office o
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,'%')
and ounder.hierarchy like concat(${userhierarchy},'%')
left join m_client mc on mc.office_id=ounder.id
INNER JOIN m_group gr ON gr.office_id = ounder.id
left join m_group cn on cn.id = gr.parent_id
INNER JOIN m_loan ln ON ln.group_id = gr.id and ln.loan_type_enum=2
INNER JOIN m_loan_transaction lt ON lt.loan_id = ln.id
LEFT JOIN m_appuser au ON au.id = lt.createdby_id
left join m_payment_detail mpd on mpd.id=lt.payment_detail_id
left join m_staff ms on ms.id=ln.loan_officer_id
WHERE o.id=${branch}
and  ln.loan_status_id IN (300,600,601,700)
AND  DATE(lt.created_date) between ${fromDate} and ${toDate}
and lt.is_reversed=0 
and lt.transaction_type_enum in (2,8)
and lt.amount<>0

union

SELECT ln.loan_type_enum
,ounder.id branch,c.external_id
,0 as cid
,0 as gid
,gr.display_name groupName
,ifnull(cn.display_name,'NA') centerName
,c.display_name clientName
,ln.account_no accNo
,IFNULL(lt.principal_portion_derived,0) 'Principal Amount'
,IFNULL(lt.interest_portion_derived,0) 'Intrest Amount'
,IFNULL(lt.fee_charges_portion_derived,0) 'Fees'
,IFNULL(lt.penalty_charges_portion_derived,0) 'Penalty'
,IFNULL(lt.overpayment_portion_derived,0) + IFNULL(lt.unrecognized_income_portion,0) 'Others'
,lt.amount 'Total Receipt Amount'
,CONCAT(au.firstname, ' ' , au.lastname) 'Mifos User'
,lt.transaction_date 'Action Date' 
,lt.created_date 'Created Date' 
,ifnull(mpd.receipt_number,'') Receipt
,ounder.name branchName
,case ln.loan_type_enum
	when 1 then 'Individual'
	when 2 then 'Group'
	when 3 then 'JLG'
	end as loanType
,ifnull(c.mobile_no,'NA') mobileNo,ifnull(ms.display_name ,'NA') staff
FROM
m_office o
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,'%')
and ounder.hierarchy like concat(${userhierarchy},'%')
inner join m_client c on c.office_id=ounder.id
inner join m_group_client mgc on mgc.client_id=c.id
INNER JOIN m_group gr ON gr.id = mgc.group_id
left join m_group cn on cn.id = gr.parent_id
INNER JOIN m_loan ln ON ln.client_id=c.id and ln.group_id = gr.id and ln.loan_type_enum=3
INNER JOIN m_loan_transaction lt ON lt.loan_id = ln.id
LEFT JOIN m_appuser au ON au.id = lt.createdby_id
left join m_payment_detail mpd on mpd.id=lt.payment_detail_id
left join m_staff ms on ms.id=ln.loan_officer_id
WHERE o.id=${branch}
and  ln.loan_status_id IN (300,600,700,601)
AND  DATE(lt.created_date) between ${fromDate} and ${toDate}
and lt.is_reversed=0 
and lt.transaction_type_enum in (2,8)
and lt.amount<>0

order by 1,2,3,4                                                                                                            