CREATE TABLE `f_profile_rating_config` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`type` SMALLINT(3) NOT NULL,
	`criteria_id` BIGINT(20) NOT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `f_profile_rating_config_UNIQUE` ( `type`),
	CONSTRAINT `FK_f_profile_rating_config_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_profile_rating_config_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_profile_rating_config_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

CREATE TABLE `f_profile_rating_run` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`scope_entity_type` SMALLINT(3) NOT NULL,
	`scope_entity_id`  BIGINT(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`criteria_id` BIGINT(20) NOT NULL,
	`start_time` DATETIME NULL DEFAULT NULL,
	`end_time` DATETIME NULL DEFAULT NULL,
	`status` SMALLINT(3) NOT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_f_profile_rating_run_criteria_id` FOREIGN KEY (`criteria_id`) REFERENCES `f_risk_rule` (`id`),
	CONSTRAINT `FK_f_profile_rating_run_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_profile_rating_run_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

CREATE TABLE `f_profile_rating_score` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`profile_rating_run_id` BIGINT(20) NULL DEFAULT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NOT NULL,
	`computed_score` SMALLINT(3) NULL DEFAULT NULL,
	`overridden_score` SMALLINT(3) NULL DEFAULT NULL,
	`final_score` SMALLINT(3) NOT NULL,
	`criteria_result` TEXT NULL,
	`updated_time` DATETIME NOT NULL,
	`overridden_by_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `f_profile_rating_score_UNIQUE` (`entity_type`,`entity_id`),
	CONSTRAINT `FK_f_profile_rating_score_run_id` FOREIGN KEY (`profile_rating_run_id`) REFERENCES `f_profile_rating_run` (`id`)
);

CREATE TABLE `f_profile_rating_score_history` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`profile_rating_run_id` BIGINT(20) NULL DEFAULT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NOT NULL,
	`computed_score` SMALLINT(3) NULL DEFAULT NULL,
	`overridden_score` SMALLINT(3) NULL DEFAULT NULL,
	`final_score` SMALLINT(3) NOT NULL,
	`criteria_result` TEXT NULL,
	`updated_time` DATETIME NOT NULL,
	`overridden_by_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('riskmanagement', 'CREATE_PROFILE_RATING_CONFIG', 'PROFILE_RATING_CONFIG', 'CREATE', 0),
('riskmanagement', 'CREATE_PROFILE_RATING_CONFIG_CHECKER', 'PROFILE_RATING_CONFIG', 'CREATE_CHECKER', 0),
('riskmanagement', 'UPDATE_PROFILE_RATING_CONFIG', 'PROFILE_RATING_CONFIG', 'UPDATE', 0),
('riskmanagement', 'UPDATE_PROFILE_RATING_CONFIG_CHECKER', 'PROFILE_RATING_CONFIG', 'UPDATE_CHECKER', 0),
('riskmanagement', 'ACTIVATE_PROFILE_RATING_CONFIG', 'PROFILE_RATING_CONFIG', 'ACTIVATE', 0),
('riskmanagement', 'ACTIVATE_PROFILE_RATING_CONFIG_CHECKER', 'PROFILE_RATING_CONFIG', 'ACTIVATE_CHECKER', 0),
('riskmanagement', 'INACTIVATE_PROFILE_RATING_CONFIG', 'PROFILE_RATING_CONFIG', 'INACTIVATE', 0),
('riskmanagement', 'INACTIVATE_PROFILE_RATING_CONFIG_CHECKER', 'PROFILE_RATING_CONFIG', 'INACTIVATE_CHECKER', 0),
('riskmanagement', 'DELETE_PROFILE_RATING_CONFIG', 'PROFILE_RATING_CONFIG', 'DELETE', 0),
('riskmanagement', 'DELETE_PROFILE_RATING_CONFIG_CHECKER', 'PROFILE_RATING_CONFIG', 'DELETE_CHECKER', 0),
('riskmanagement', 'READ_PROFILE_RATING_CONFIG', 'PROFILE_RATING_CONFIG', 'READ', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('riskmanagement', 'COMPUTE_PROFILE_RATING', 'PROFILE_RATING', 'COMPUTE', 0),
('riskmanagement', 'COMPUTE_PROFILE_RATING_CHECKER', 'PROFILE_RATING', 'COMPUTE_CHECKER', 0);