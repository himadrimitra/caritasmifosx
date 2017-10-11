INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Disbursement Reminder Sms', 'report', NULL, 'sms', 
'select c.id as id ,c.mobile_no as mobileNo, 
c.display_name as clientName, l.principal_amount as disbursementAmount
,l.expected_disbursedon_date as disbursementDate
 from m_client c 
left join m_loan l on l.client_id=c.id 
where l.expected_disbursedon_date = DATE_ADD(CURDATE(),INTERVAL 1 DAY)'
, NULL, 1, 1);

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Disbursement Success Sms', 'report', NULL, 'sms', 
'select c.id as id ,c.mobile_no as mobileNo, 
c.display_name as clientName, mlt.amount as disbursementAmount
,mlt.transaction_date as disbursementDate
 from m_client c 
left join m_loan l on l.client_id=c.id 
left join m_loan_transaction mlt on mlt.loan_id=l.id
where mlt.transaction_type_enum=1 and (mlt.created_date > DATE_SUB(NOW(), INTERVAL 5 MINUTE))'
, NULL, 1, 1);

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Repayment Reminder Sms', 'report', NULL, 'sms', 
'select c.id as id ,c.mobile_no as mobileNo, c.display_name as clientName, lrs.duedate as repaymentDate
from m_client c 
left join m_loan l on l.client_id=c.id 
left join m_loan_repayment_schedule lrs on lrs.loan_id = l.id
where lrs.duedate = DATE_ADD(CURDATE(),INTERVAL 1 DAY)'
,NULL, 1, 1);

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Repayment Success Sms', 'report', NULL, 'sms',
'select c.id as id 
,c.mobile_no as mobileNo
, c.display_name as clientName
,mlt.transaction_date as repaymentDate
,mlt.amount AS repaymentAmount
from m_client c 
left join m_loan l on l.client_id=c.id 
left join m_loan_transaction mlt on mlt.loan_id=l.id
where mlt.transaction_type_enum=2 
and 
(mlt.created_date > DATE_SUB(NOW(), INTERVAL 5 MINUTE))'
, NULL, 1, 1);
