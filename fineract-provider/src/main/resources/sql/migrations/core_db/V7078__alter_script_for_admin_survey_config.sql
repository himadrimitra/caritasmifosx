ALTER TABLE `m_surveys`
	ADD COLUMN `entity_type` SMALLINT(3) NOT NULL AFTER `id`,
	CHANGE COLUMN `country_code` `country_code` VARCHAR(2) NULL DEFAULT NULL AFTER `description`;
	
ALTER TABLE `m_survey_scorecards`
	CHANGE COLUMN `client_id` `client_id` BIGINT(20) NULL DEFAULT NULL AFTER `user_id`;
	
ALTER TABLE `m_surveys`
	ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT '1' AFTER `valid_to`;
	
ALTER TABLE `m_surveys`
	ADD UNIQUE INDEX `UQ_m_survey_key` (`a_key`);
	
ALTER TABLE `m_survey_components`
	ADD UNIQUE INDEX `UQ_m_survey_components_key` (`a_key`);
	
ALTER TABLE `m_survey_questions`
	ADD UNIQUE INDEX `UQ_m_survey_questions_key` (`a_key`);
