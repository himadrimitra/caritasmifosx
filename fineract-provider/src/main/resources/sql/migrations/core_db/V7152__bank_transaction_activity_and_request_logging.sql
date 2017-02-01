INSERT INTO `f_risk_field` (`name`, `uname`, `value_type`, `options`, `code_name`, `is_active`)
VALUES
  ('Active MFI Lenders', 'activeMfiLenderCount', 0, NULL, NULL, 1);
INSERT INTO `f_risk_field` (`name`, `uname`, `value_type`, `options`, `code_name`, `is_active`)
VALUES
  ('Active Other MFI Lenders', 'activeOtherMfiLenderCount', 0, NULL, NULL, 1);

INSERT IGNORE INTO f_task_activity(name,identifier,config_values,supported_actions,type)
VALUES
("Bank Transaction","banktransaction",null,null,3);

DROP TABLE IF EXISTS f_thirdparty_request_reponse_log;
CREATE TABLE `f_thirdparty_request_reponse_log` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NOT NULL,
	`url` VARCHAR(512) NOT NULL,
	`request_method` VARCHAR(16) NOT NULL,
	`request` MEDIUMTEXT NULL,
	`response` MEDIUMTEXT NULL,
	`response_time_ms` BIGINT(20) NULL,
	`http_status_code` SMALLINT(4) NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `INX_f_thirdparty_request_reponse_log_entity_type_entity_id` (`entity_type`,`entity_id`),
	CONSTRAINT `FK_f_thirdparty_request_reponse_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_thirdparty_request_reponse_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)COLLATE = 'utf8_general_ci' ENGINE = InnoDB;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
	('banktransaction', 'RETRY_BANK_TRANSACTION', 'BANK_TRANSACTION', 'RETRY', 0);