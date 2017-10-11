CREATE TABLE `f_client_kyc_details` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`requested_date` DATE NULL DEFAULT NULL,
	`kyc_type` INT NOT NULL,
	`kyc_source` INT NOT NULL,
	`kyc_mode` INT NOT NULL,
	`response_data` TEXT NULL,
	`identifier_id` VARCHAR(50) NOT NULL, PRIMARY KEY (`id`)
);
ALTER TABLE `f_address` ADD COLUMN `is_verified` TINYINT(2) NULL DEFAULT NULL, ADD COLUMN `document_id` BIGINT(20) NULL DEFAULT NULL;
ALTER TABLE `m_code_value` CHANGE COLUMN `system_identifier` `system_identifier` VARCHAR(5) NULL DEFAULT NULL;
INSERT INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`,`is_mandatory`, `parent_id`, `system_identifier`) VALUES 
((
SELECT mc.id
FROM m_code mc
WHERE mc.code_name = 'AddressType'), 'kycAddress', NULL, 0, NULL, '1', '0', NULL, 'eKyc');

