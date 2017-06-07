CREATE TABLE `f_aadhaar_outbound_request_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`request_id` VARCHAR(20) NOT NULL,
	`aadhaar_number` VARCHAR(16) NOT NULL,
	`status` SMALLINT(3) NOT NULL,
	`purpose` SMALLINT(3) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK_f_aadhaar_outbound_request_details_createdby_id` (`createdby_id`),
	INDEX `INX_request_id` (`request_id`),
	INDEX `INX_aadhaar_number` (`aadhaar_number`),
	INDEX `INX_status`(`status`),
	CONSTRAINT `FK_f_aadhaar_outbound_request_details_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_aadhaar_outbound_request_details` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
)AUTO_INCREMENT=1;

INSERT INTO `c_external_service_properties` (`name`, `value`, `external_service_id`)
VALUES
('saCode', NULL, (select external.id from c_external_service  external where external.name ='Aadhaar_Service')),
('saltKey', NULL, (select external.id from c_external_service  external where external.name ='Aadhaar_Service')),
('otpUrl', 'https://api.aadhaarbridge.com/preprod/_init', (select external.id from c_external_service  external where external.name ='Aadhaar_Service')),
('kycUrl', 'https://api.aadhaarbridge.com/preprod/_kyc', (select external.id from c_external_service  external where external.name ='Aadhaar_Service'));