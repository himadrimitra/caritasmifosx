ALTER TABLE `stretchy_report` ADD COLUMN `track_usage` TINYINT(1) NOT NULL DEFAULT 0;

CREATE TABLE `f_stretchy_report_logs` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`user_id` BIGINT(20) NOT NULL ,
	`report_id` INT(11) NOT NULL,
	`execution_start_date` DATETIME NOT NULL,
	`execution_end_date` DATETIME NOT NULL,
	`param_as_json` TEXT NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK1_m_appuser_f_stretchy_report_logs` (`user_id`),
	CONSTRAINT `FK1_m_appuser_f_stretchy_report_logs` FOREIGN KEY (`user_id`) REFERENCES `m_appuser` (`id`),
	INDEX `FK2_stretchy_report_f_stretchy_report_logs` (`report_id`),
	CONSTRAINT `FK2_stretchy_report_f_stretchy_report_logs` FOREIGN KEY (`report_id`) REFERENCES `stretchy_report` (`id`)
);