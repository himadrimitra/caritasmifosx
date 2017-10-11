ALTER TABLE `m_survey_scorecards`
	DROP FOREIGN KEY `m_survey_scorecards_ibfk_4`,
	DROP FOREIGN KEY `m_survey_scorecards_ibfk_5`;
	
ALTER TABLE `m_survey_scorecards`
	DROP COLUMN `user_id`,
	DROP COLUMN `client_id`,
	DROP COLUMN `created_on`;

ALTER TABLE `m_survey_scorecards`
	ADD COLUMN `survey_taken_id` BIGINT(20) NOT NULL AFTER `id`;
	
CREATE TABLE `f_survey_taken` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` BIGINT(20) NOT NULL,
	`survey_id` BIGINT(20) NOT NULL,
	`surveyed_by` BIGINT(20) NOT NULL,
	`surveyed_on` DATE NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `survey_id` (`survey_id`),
	CONSTRAINT `FK_f_survey_taken_survey_id` FOREIGN KEY (`survey_id`) REFERENCES `m_surveys` (`id`),
	CONSTRAINT `FK_f_survey_taken_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_survey_taken_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

ALTER TABLE `m_survey_scorecards`
	ADD CONSTRAINT `m_survey_scorecards_ibfk_4` FOREIGN KEY (`survey_taken_id`) REFERENCES `f_survey_taken` (`id`);