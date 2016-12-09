








SELECT 
IFNULL(mo.name,"-") AS "Branch Name",
IFNULL(vi.village_name,"-") AS "Area Name",
IFNULL(center.display_name,"-") AS "Center Name",
IFNULL(gp.display_name,"-") AS "Group Name",
IFNULL(gp.external_id,"-") AS "Group External ID",
IFNULL(cl.display_name,"-") AS "Client Name",
IFNULL(cl.account_no,"-") AS "Client ID",
IFNULL(cl.external_id,"-") AS "ClientExtID",
IFNULL(mpl.name,"-") AS "Loan Product",
IFNULL(ml.account_no,"-") AS "Loan No", 
ROUND(IFNULL(lt.amount,0),2) AS "Written off Amount",
ml.writtenoffon_date AS "Write-Off Date",
IFNULL(mcv.code_value,"-") AS "Write Off Reason "
From m_loan ml
JOIN m_loan_transaction lt on lt.loan_id=ml.id
JOIN m_product_loan mpl ON mpl.id = ml.product_id
JOIN m_code_value mcv ON mcv.id = ml.writeoff_reason_cv_id
JOIN m_client cl on cl.id=ml.client_id
JOIN m_office mo ON mo.id= cl.office_id
LEFT JOIN m_group_client mgc ON mgc.client_id=cl.id
LEFT JOIN m_group gp ON gp.id=mgc.group_id
LEFT JOIN m_group center ON gp.parent_id=center.id
LEFT JOIN chai_village_center vic ON vic.center_id=center.id
LEFT JOIN chai_villages vi on vi.id=vic.village_id
WHERE lt.transaction_type_enum = 6 /*write-off */
AND lt.is_reversed = 0
AND ml.loan_status_id=601
AND (mo.id = ${office} or "-1" = ${office})
AND lt.transaction_date between ${startdate} and ${enddate}
AND (mcv.id = ${writeOffId} or "-1" = ${writeOffId})
Group by ml.id 
Order by 1,2,3,4,6                                                      