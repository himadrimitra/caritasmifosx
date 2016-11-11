






SELECT 
concat(repeat("..",   
   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, '.', '')) - 1))), ounder.`name`) as "Office/Branch",ounder.name AS NAME, ounder.id,
ifnull(cur.display_symbol, ml.currency_code) as Currency,  
c.account_no as "Client Account No.",
c.display_name AS 'Client Name',
ml.account_no AS 'Loan Account No.',
mpl.name AS 'Product Name',
ml.disbursedon_date AS 'Disbursed Date',
lt.transaction_date AS 'Written Off date',
ml.principal_amount as "Loan Amount",
ifnull(lt.principal_portion_derived, 0) AS 'Written-Off Principal',
ifnull(lt.interest_portion_derived, 0) AS 'Written-Off Interest',
ifnull(lt.fee_charges_portion_derived,0) AS 'Written-Off Fees',
ifnull(lt.penalty_charges_portion_derived,0) AS 'Written-Off Penalties',
n.note AS 'Reason For Write-Off',
IFNULL(ms.display_name,'-') AS 'Loan Officer Name'
FROM m_office o
JOIN m_office ounder ON ounder.hierarchy like concat(o.hierarchy, '%')
AND ounder.hierarchy like CONCAT(${userhierarchy}, '%')
JOIN m_client c ON c.office_id = ounder.id
JOIN m_loan ml ON ml.client_id = c.id
JOIN m_product_loan mpl ON mpl.id=ml.product_id
LEFT JOIN m_staff ms ON ms.id=ml.loan_officer_id
JOIN m_loan_transaction lt ON lt.loan_id = ml.id
LEFT JOIN m_note n ON n.loan_transaction_id = lt.id
LEFT JOIN m_currency cur on cur.code = ml.currency_code
WHERE lt.transaction_type_enum = 6 /*write-off */
AND lt.is_reversed = 0
AND ml.loan_status_id=601   
AND o.id=${officeId}
AND (mpl.id=${loanProductId} OR ${loanProductId}=-1)
AND lt.transaction_date BETWEEN ${startDate} AND ${endDate}
AND (ml.currency_code = ${currencyId} or -1 = ${currencyId})
ORDER BY ounder.hierarchy, ifnull(cur.display_symbol, ml.currency_code), ml.account_no                                                                           