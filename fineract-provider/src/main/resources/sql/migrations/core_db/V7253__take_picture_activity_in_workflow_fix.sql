ALTER TABLE `m_image`
	CHANGE COLUMN `created_by` `createdby_id` BIGINT NULL DEFAULT NULL AFTER `entity_id`,
	CHANGE COLUMN `created_on` `created_date` DATETIME NULL DEFAULT NULL AFTER `createdby_id`;

ALTER TABLE `m_image`
	ADD COLUMN `lastmodifiedby_id` BIGINT NULL DEFAULT NULL ,
	ADD COLUMN `lastmodified_date` DATETIME NULL DEFAULT NULL ;
	
ALTER TABLE `m_image`
	ADD CONSTRAINT `FK_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	ADD CONSTRAINT `FK_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`);
