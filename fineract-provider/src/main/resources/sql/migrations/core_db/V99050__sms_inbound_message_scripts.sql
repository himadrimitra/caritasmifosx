CREATE TABLE `sms_inbound_messages` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`submitted_on_date` TIMESTAMP NOT NULL,
	`mobile_number` VARCHAR(50) NOT NULL,
	`ussd_code` VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('organisation', 'CREATE_SMS_INBOUND', 'SMS_INBOUND', 'CREATE', 0);
