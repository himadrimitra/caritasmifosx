
CREATE TABLE `f_business_events_email_config` (
	`buisness_event` VARCHAR(50) NOT NULL,
	`center_display_name` VARCHAR(50) NOT NULL,
	`attachment_type` VARCHAR(50) NOT NULL,
	`report_name` VARCHAR(50) NOT NULL
);	

ALTER TABLE `m_email_messages_outbound`
	ADD COLUMN `loan_amount` DECIMAL(19,6) NULL DEFAULT NULL AFTER `sent_date`;
	