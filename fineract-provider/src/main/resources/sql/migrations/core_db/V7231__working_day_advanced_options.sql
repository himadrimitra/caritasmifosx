CREATE TABLE `m_non_working_day_reschedule_detail` (
	`id` BIGINT(5) NOT NULL AUTO_INCREMENT,
	`from_week_day` VARCHAR(2) NOT NULL,
	`repayment_rescheduling_enum` SMALLINT(2) NOT NULL,
	`to_week_day` VARCHAR(2) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
);
