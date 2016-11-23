














select cl.display_name,lt.transaction_type_enum
,ifnull(cl.mobile_no,'-') Phone_No,
ln.account_no,
lt.id 'Transaction ID',
lt.transaction_date 'Action Date' ,
lt.created_date 'Created Date',
ifnull(mpd.receipt_number,'-') Receipt_No,
ifnull(gp.display_name,'-') 'Group name',
ifnull(gp1.id,'-') centerID,cl.external_id,
CONCAT(mapp.firstname, " , " , mapp.lastname) 'Mifos User',
lt.amount,
ounder.name Branch,mpt.value,CONCAT(mapp.firstname, " , " , mapp.lastname) 'User',
case ifnull(lt.transaction_type_enum,0) 
when 1 then 'Disbursement'
when 2 then 'Repayment'
when 3 then 'Contra'
when 4 then 'Waive Interest'
when 5 then 'Repayment At Disbursement'
when 6 then 'Write-Off'
when 7 then 'Marked for Rescheduling'
when 8 then 'Recovery Repayment'
when 9 then 'Waive Charges'
when 10 then 'Apply Charges'
when 11 then 'Apply Interest'
when 12 then 'Adjusted'
end as 'type',


case lt.transaction_type_enum
when 2 then ifnull(lt.amount,0)
when 4 then ifnull(lt.amount,0)
when 5 then ifnull(lt.amount,0)
when 6 then ifnull(lt.amount,0)
when 7 then ifnull(lt.amount,0)
when 8 then ifnull(lt.amount,0)
when 9 then ifnull(lt.amount,0)
end as 'receipts',
case lt.transaction_type_enum
when 1 then ifnull(lt.amount,0)
when 3 then ifnull(lt.amount,0)
when 10 then ifnull(lt.amount,0)
when 11 then ifnull(lt.amount,0)
end as 'Disbursements'

/*if(lt.transaction_type_enum = 1, lt.amount,0) as 'Disbursement',
if(lt.transaction_type_enum = 2, lt.amount,0) as 'Repayment',
if(lt.transaction_type_enum = 3, lt.amount,0) as 'Contra',
if(lt.transaction_type_enum = 4, lt.amount,0) as 'Waive Interest',
if(lt.transaction_type_enum = 5, lt.amount,0) as 'Repayment At Disbursement',
if(lt.transaction_type_enum = 6, lt.amount,0) as 'Write-Off',
if(lt.transaction_type_enum = 7, lt.amount,0) as 'Marked for Rescheduling',
if(lt.transaction_type_enum = 8, lt.amount,0) as 'Recovery Repayment',
if(lt.transaction_type_enum = 9, lt.amount,0) as 'Waive Charges',
if(lt.transaction_type_enum = 10, lt.amount,0) as 'Apply Charges',
if(lt.transaction_type_enum = 11, lt.amount,0) as 'Apply Interest',
if(lt.transaction_type_enum = 12, lt.amount,0) as 'Adjusted' */
FROM  
m_office o
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,'%')
and ounder.hierarchy like concat(${userhierarchy},'%')
INNER JOIN m_client cl ON cl.office_id = ounder.id
INNER JOIN m_loan ln ON ln.client_id = cl.id
INNER JOIN m_loan_transaction lt ON lt.loan_id = ln.id
LEFT JOIN m_group_client gc ON gc.client_id = cl.id
LEFT JOIN m_group gp ON gp.id=ln.group_id AND gp.level_id = 2
left join m_group gp1 on gp1.id=ln.group_id and gp1.level_id=1
left join m_appuser mapp on mapp.id= lt.createdby_id  
left join m_payment_detail mpd on mpd.id=lt.payment_detail_id
left join m_payment_type mpt on mpd.payment_type_id=mpt.id

WHERE (DATE(lt.transaction_date) between ${ondate} and ${todate})
AND  o.id=${Branch} and lt.is_reversed=0 and ln.loan_status_id IN ('300','600') 
and (lt.transaction_type_enum=${transaction} or ${transaction}=-1) 
and ( mapp.id=${user} or ${user}=-1)
and ( mpt.id=${payment} or ${payment}=-1)
group by ln.id,lt.transaction_date                                                                                                       