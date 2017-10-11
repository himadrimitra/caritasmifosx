ALTER TABLE `m_calendar`
	ADD COLUMN `next_recurring_date` DATE NULL DEFAULT NULL AFTER `meeting_time`;
	
INSERT INTO job (name, display_name, cron_expression, create_time)values ('Update Next Recurring Date', 'Update Next Recurring Date', '0 1 1 * * ?', NOW());