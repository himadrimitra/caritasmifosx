ALTER TABLE `m_guarantor`
ADD COLUMN `national_id` VARCHAR(20) NULL DEFAULT NULL AFTER `is_active`;