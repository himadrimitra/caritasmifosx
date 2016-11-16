CREATE TABLE `f_smartcard` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`entity_type` SMALLINT(3) NOT NULL,
	`entity_id` VARCHAR(100) NOT NULL,
	`card_number` BIGINT(20) NOT NULL,
	`card_status` SMALLINT(3) NOT NULL,
	`note` VARCHAR (500) NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	`deactivatedby_id` BIGINT(20) NULL,
	`deactivated_date` DATETIME NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `f_smartcard_UNIQUE` (`client_id`,`entity_type`,`entity_id`,`card_number`),
	INDEX `FK_f_smartcard_client_id` (`client_id`),
	INDEX `FK_f_smartcard_createdby_id` (`createdby_id`),
	INDEX `FK_f_smartcard_lastmodifiedby_id` (`lastmodifiedby_id`),
	INDEX `FK_f_smartcard_deactivatedby_id`(`deactivatedby_id`),
	CONSTRAINT `FK_f_smartcard_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_smartcard_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_f_smartcard_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_smartcard_deactivatedby_id` FOREIGN KEY (`deactivatedby_id`) REFERENCES `m_appuser` (`id`)
);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('smartcard', 'ACTIVATE_SMARTCARD', 'SMARTCARD', 'ACTIVATE', 0),
('smartcard', 'ACTIVATE_SMARTCARD_CHECKER', 'SMARTCARD', 'ACTIVATE_CHECKER', 0),
('smartcard', 'READ_SMARTCARD', 'SMARTCARD', 'READ', 0),
('smartcard', 'INACTIVATE_SMARTCARD', 'SMARTCARD', 'INACTIVATE', 0),
('smartcard', 'INACTIVATE_SMARTCARD_CHECKER', 'SMARTCARD', 'INACTIVATE_CHECKER', 0);
