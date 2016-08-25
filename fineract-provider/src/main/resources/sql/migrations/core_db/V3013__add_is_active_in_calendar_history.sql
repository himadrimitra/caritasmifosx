ALTER TABLE `m_calendar_history`
	ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT '1' AFTER `second_reminder`;