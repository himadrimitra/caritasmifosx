ALTER TABLE `m_holiday`
	CHANGE COLUMN `repayments_rescheduled_to` `repayments_rescheduled_to` DATETIME NULL DEFAULT NULL,
	ADD COLUMN `extend_repayment_schedule` TINYINT(1) NOT NULL DEFAULT '0';