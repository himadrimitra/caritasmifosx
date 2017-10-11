ALTER TABLE `m_report_mailing_job`
	ALTER `created_on_date` DROP DEFAULT,
	ALTER `created_by_userid` DROP DEFAULT;
ALTER TABLE `m_report_mailing_job`
	CHANGE COLUMN `created_on_date` `created_date` DATE NOT NULL AFTER `recurrence`,
	CHANGE COLUMN `created_by_userid` `createdby_id` BIGINT(20) NOT NULL AFTER `created_date`,
	ADD COLUMN `lastmodifiedby_id` BIGINT(20) NOT NULL AFTER `createdby_id`,
	ADD COLUMN `lastmodified_date` DATE NOT NULL AFTER `lastmodifiedby_id`,
	DROP FOREIGN KEY `m_report_mailing_job_ibfk_1`,
	ADD CONSTRAINT `FK_m_report_mailing_job_created_by` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	ADD CONSTRAINT `FK_m_report_mailing_job_updated_by` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`);
