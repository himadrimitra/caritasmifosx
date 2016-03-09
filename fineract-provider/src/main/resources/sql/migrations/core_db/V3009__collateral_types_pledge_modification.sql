ALTER TABLE `m_pledge` 
ADD COLUMN `created_by` BIGINT(20) NOT NULL,
ADD COLUMN `created_date` DATE NOT NULL,
ADD COLUMN `updated_by` BIGINT(20) NULL DEFAULT NULL,
ADD COLUMN `updated_date` DATE NULL DEFAULT NULL,
ADD CONSTRAINT `FK1_m_pledge_m_appuser` FOREIGN KEY (`created_by`) REFERENCES `m_appuser` (`id`),
ADD CONSTRAINT `FK2_m_pledge_m_appuser` FOREIGN KEY (`updated_by`) REFERENCES `m_appuser` (`id`);
