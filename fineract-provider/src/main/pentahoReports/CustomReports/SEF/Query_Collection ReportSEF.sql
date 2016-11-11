















SELECT
ifnull(gp.display_name,'-') 'Group name',
cl.display_name clients,
IFNULL(lt.principal_portion_derived,0) 'Principal Amount',
IFNULL(lt.interest_portion_derived,0) 'Intrest Amount',
IFNULL(lt.principal_portion_derived,0) + IFNULL(lt.interest_portion_derived,0) 'Total Receipt Amount',
CONCAT(au.firstname, " , " , au.lastname) 'Mifos User',
lt.transaction_date 'Action Date' ,
lt.created_date 'Created Date' ,
ifnull(mpd.receipt_number,'') Receipt,
ounder.name
FROM
m_office o
JOIN m_office ounder on ounder.hierarchy like concat(o.hierarchy,'%')
and ounder.hierarchy like concat(${userhierarchy},'%')
INNER JOIN m_client cl ON cl.office_id = ounder.id
INNER JOIN m_loan ln ON ln.client_id = cl.id
INNER JOIN m_loan_transaction lt ON lt.loan_id = ln.id
LEFT JOIN m_group_client gc ON gc.client_id = cl.id
LEFT JOIN m_group gp ON gp.id=ln.group_id
INNER JOIN m_appuser au ON au.id = lt.appuser_id
left join m_payment_detail mpd on mpd.id=lt.payment_detail_id
WHERE (DATE(lt.created_date) between ${ondate} and ${todate}) and  ln.loan_status_id IN ('300','600')
AND  ounder.id=${Branch}   and lt.is_reversed=0 and lt.transaction_type_enum=2
group by lt.id                                                                                                                             