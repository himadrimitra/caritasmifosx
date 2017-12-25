UPDATE `stretchy_report` SET `report_type`='SMS', `report_subtype`='NonTriggered', `report_category`='Loan'
WHERE `report_name` IN ('Loan Repayment Reminders', 'Loan First Overdue Repayment Reminder', 'Loan Second Overdue Repayment Reminder', 'Loan Third Overdue Repayment Reminder', 'Loan Fourth Overdue Repayment Reminder', 'DefaultWarning -  guarantors', 'DefaultWarning - Clients');

UPDATE `stretchy_report` SET `report_type`='SMS', `report_subtype`='NonTriggered', `report_category`='Savings'
WHERE `report_name` IN ('DormancyWarning - Clients');