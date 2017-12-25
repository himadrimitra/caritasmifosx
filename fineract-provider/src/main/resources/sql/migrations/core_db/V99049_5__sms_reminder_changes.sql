UPDATE `job` SET `is_active` = 0
WHERE name IN ('Loan Repayment Sms Reminder','Loan First Overdue Repayment Reminder','Loan Second Overdue Repayment Reminder','Loan Third Overdue Repayment Reminder','Loan Fourth Overdue Repayment Reminder','Default Wring Sms To Client','Default Warning Sms To gurantor','Dormancy Warning Sms To Clients');

ALTER TABLE `sms_messages_outbound`
	CHANGE COLUMN `external_id` `external_id` VARCHAR(100) NULL DEFAULT NULL AFTER `source_address`;