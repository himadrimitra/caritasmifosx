ALTER TABLE `m_surveys`
	ADD COLUMN `is_co_officer_required` TINYINT(1) NOT NULL DEFAULT '0' AFTER `is_active`;
	
ALTER TABLE `f_survey_taken`
	ADD COLUMN `co_surveyed_by` BIGINT(20) NULL DEFAULT NULL AFTER `surveyed_by`;