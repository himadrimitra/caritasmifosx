CREATE TABLE IF NOT EXISTS `f_authentication` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL DEFAULT '0',
	`description` VARCHAR(100) NOT NULL DEFAULT '0',
	`auth_service_class_name` VARCHAR(100) NOT NULL DEFAULT '0',
	`is_active` SMALLINT(1) NOT NULL DEFAULT '0',
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `name` (`name`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS  `f_transaction_authentication` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`portfolio_type` SMALLINT(3) NOT NULL,
	`transaction_type_enum` SMALLINT(3) NOT NULL,
	`payment_type_id` BIGINT(20) NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL DEFAULT '0.000000',
	`second_app_user_role_id` BIGINT(20) NULL DEFAULT NULL,
	`lastmodified_date` DATE NULL DEFAULT NULL,
	`is_second_app_user_enabled` SMALLINT(1) NOT NULL DEFAULT '0',
	`authentication_id` BIGINT(20) NOT NULL DEFAULT '0',
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `portfolio_transaction_type_enum_payment_type_id_amount` (`portfolio_type`, `transaction_type_enum`, `payment_type_id`, `amount`),
	INDEX `FK_f_transaction_authentication_f_authentication` (`authentication_id`),
	CONSTRAINT `FK_f_transaction_authentication_f_authentication` FOREIGN KEY (`authentication_id`) REFERENCES `f_authentication` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


INSERT IGNORE INTO `f_authentication` ( `name`, `description`, `auth_service_class_name`, `is_active`, `lastmodified_date`, `lastmodifiedby_id`) VALUES
	('Aadhaar OTP', 'Aadhar OTP service', 'SecondLevelAuthenticationServiceUsingAadhaarOtp', 1, Null, Null),
	('Aadhaar fingerprint', 'Aadhaar fingerprint serveice', 'SecondLevelAuthenticationServiceUsingAadhaarFingerprint', 1, NULL, NULL);


INSERT IGNORE INTO `c_external_service` (`id`, `name`) VALUES
	(3, 'Aadhaar_Service');
	
INSERT IGNORE INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) 
SELECT * FROM (SELECT 'host', '54.225.128.92', 3) AS tmp
WHERE NOT EXISTS (
    SELECT name FROM c_external_service_properties WHERE name = 'host' and value ='54.225.128.92'
) LIMIT 1;

INSERT IGNORE INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) 
SELECT * FROM (SELECT 'port', '8980',3) AS tmp
WHERE NOT EXISTS (
    SELECT name FROM c_external_service_properties WHERE name = 'port' and value ='8980'
) LIMIT 1;

INSERT IGNORE INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) 
SELECT * FROM (SELECT 'certificate_type', 'preprod',3) AS tmp
WHERE NOT EXISTS (
    SELECT name FROM c_external_service_properties WHERE name = 'certificate_type' and value ='preprod'
) LIMIT 1;

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('infrastructure', 'READ_AUTHENTICATIONSERVICE', 'AUTHENTICATIONSERVICE', 'READ', 0),('infrastructure', 'UPDATE_AUTHENTICATIONSERVICE', 'AUTHENTICATIONSERVICE', 'UPDATE', 0),('organisation', 'CREATE_TRANSACTIONAUTHENTICATIONSERVICE', 'TRANSACTIONAUTHENTICATIONSERVICE', 'CREATE', 0),( 'organisation', 'UPDATE_TRANSACTIONAUTHENTICATIONSERVICE', 'TRANSACTIONAUTHENTICATIONSERVICE', 'UPDATE', 0),('organisation', 'DELETE_TRANSACTIONAUTHENTICATIONSERVICE', 'TRANSACTIONAUTHENTICATIONSERVICE', 'DELETE', 0),('organisation', 'READ_TRANSACTIONAUTHENTICATIONSERVICE', 'TRANSACTIONAUTHENTICATIONSERVICE', 'READ', 0),('infrastructure', 'GENERATE_OTP', 'OTP', 'GENERATE', 0),('infrastructure', 'READ_KYC_DETAILS', 'KYC', 'READ', 0);


