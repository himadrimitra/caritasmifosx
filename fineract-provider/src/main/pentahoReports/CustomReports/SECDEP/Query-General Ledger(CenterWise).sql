


SELECT aa.cid centerId,
IFNULL(aa.cname,if(aa.offname is null, null , 'Manual Entry')) centerName,
aa.offname,
aga.gl_code code,
aga.name name, 
IFNULL(aa.debit,0) debit, 
IFNULL(aa.credit,0) credit, 
CONVERT((IFNULL(opb.opening,0)), DECIMAL(50,2)) opening_balance,
IF((aga.classification_enum = 1 OR aga.classification_enum = 3), TRUE, FALSE) AS closingvalue, 
IFNULL(opb.opening,0) + IFNULL(aa.debit,0) - IFNULL(aa.credit,0) closing_balance,
entry_date,
opdentrydate
FROM acc_gl_account aga
LEFT JOIN (
SELECT je.id jeidopb,
je.account_id geidopb, 
MAX(je.entry_date) AS opdentrydate, 
IF(aga1.classification_enum IN (1,5), (SUM(IF(je.type_enum=2, IFNULL(je.amount,0),0))- SUM(IF(je.type_enum=1, IFNULL(je.amount,0),0))), (SUM(IF(je.type_enum=1, IFNULL(je.amount,0),0))- SUM(IF(je.type_enum=2, IFNULL(je.amount,0),0)))) opening,
ounder.id opboffice,
ounder.name
FROM m_office o
LEFT JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy,'%') AND ounder.hierarchy LIKE CONCAT(${userhierarchy},'%')
LEFT JOIN acc_gl_journal_entry je ON je.office_id = ounder.id
LEFT JOIN acc_gl_account aga1 ON aga1.id=je.account_id
WHERE je.entry_date <= DATE_SUB(${fromDate}, INTERVAL 1 DAY) AND je.office_running_balance IS NOT NULL AND (o.id=${office})
GROUP BY je.account_id
) opb ON opb.geidopb=aga.id
LEFT JOIN ( 
SELECT gl_code AS code, 
a.c_name cname, a.cd cid,
offname,
gid AS gid, CONVERT(debit, DECIMAL(50,2)) debit, 
CONVERT(credit, DECIMAL(50,2)) credit,
entry_date,
loan_id
FROM(
SELECT co.gl_code, 
o.name AS offname, 
center.display_name c_name, 
center.id cd,
co.name,
co.id gid, 
SUM(CASE je.type_enum WHEN 1 THEN je.amount WHEN 2 THEN 0 END) AS credit, 
SUM(CASE je.type_enum WHEN 2 THEN je.amount WHEN 1 THEN 0 END) AS debit,
je.entry_date,
je.entity_type_enum,
je.entity_id AS loan_id
FROM m_office o
LEFT JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy,'%') AND ounder.hierarchy LIKE CONCAT(${userhierarchy},'%')
LEFT JOIN acc_gl_journal_entry je ON je.office_id=ounder.id
LEFT JOIN acc_gl_account co ON co.id=je.account_id
LEFT JOIN m_loan ml ON ml.id = je.entity_id AND je.entity_type_enum = 1 AND je.loan_transaction_id is not null
LEFT JOIN m_savings_account msa ON msa.id = je.entity_id AND je.entity_type_enum = 2 AND je.savings_transaction_id is not null
left join m_client mct on mct.id = je.entity_id AND je.entity_type_enum = 2 AND je.client_transaction_id is not null
Left join m_client mcl on mcl.id=ml.client_id
Left join m_client mcs on mcs.id=msa.client_id

left JOIN m_group_client mgc ON mgc.client_id=mcl.id or mgc.client_id = mct.id or mgc.client_id = mcs.id 
left JOIN m_group gp ON gp.id=mgc.group_id
left JOIN m_group center ON gp.parent_id=center.id and center.level_id=1
WHERE (o.id=${office}) 
AND je.entry_date BETWEEN ${fromDate} AND ${toDate}
GROUP BY co.gl_code,je.entry_date,center.id
   ) a
)aa ON aa.gid=aga.id
-- GROUP BY aga.gl_code,entry_date,cid
Order by aga.gl_code,entry_date, centerName            