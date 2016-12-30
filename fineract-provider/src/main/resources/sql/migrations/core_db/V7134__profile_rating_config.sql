CREATE TABLE `f_profile_rating_config` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`type` SMALLINT(3) NOT NULL,
	`criteria_id` BIGINT(20) NOT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	UNIQUE INDEX `f_profile_rating_config_UNIQUE` (`type`, `criteria_id`),
	CONSTRAINT `FK_f_profile_rating_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_profile_rating_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_profile_rating_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	PRIMARY KEY (`id`)
);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('riskmanagement', 'CREATE_PROFILERATINGCONFIG', 'PROFILERATINGCONFIG', 'CREATE', 0),
('riskmanagement', 'CREATE_PROFILERATINGCONFIG_CHECKER', 'PROFILERATINGCONFIG', 'CREATE_CHECKER', 0),
('riskmanagement', 'UPDATE_PROFILERATINGCONFIG', 'PROFILERATINGCONFIG', 'UPDATE', 0),
('riskmanagement', 'UPDATE_PROFILERATINGCONFIG_CHECKER', 'PROFILERATINGCONFIG', 'UPDATE_CHECKER', 0),
('riskmanagement', 'ACTIVATE_PROFILERATINGCONFIG', 'PROFILERATINGCONFIG', 'ACTIVATE', 0),
('riskmanagement', 'ACTIVATE_PROFILERATINGCONFIG_CHECKER', 'PROFILERATINGCONFIG', 'ACTIVATE_CHECKER', 0),
('riskmanagement', 'INACTIVATE_PROFILERATINGCONFIG', 'PROFILERATINGCONFIG', 'INACTIVATE', 0),
('riskmanagement', 'INACTIVATE_PROFILERATINGCONFIG_CHECKER', 'PROFILERATINGCONFIG', 'INACTIVATE_CHECKER', 0),
('riskmanagement', 'DELETE_PROFILERATINGCONFIG', 'PROFILERATINGCONFIG', 'DELETE', 0),
('riskmanagement', 'DELETE_PROFILERATINGCONFIG_CHECKER', 'PROFILERATINGCONFIG', 'DELETE_CHECKER', 0),
('riskmanagement', 'READ_PROFILERATINGCONFIG', 'PROFILERATINGCONFIG', 'READ', 0);