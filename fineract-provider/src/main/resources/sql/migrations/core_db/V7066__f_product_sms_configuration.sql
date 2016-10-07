CREATE TABLE `f_product_sms_configuration` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`product_type` SMALLINT(3) NOT NULL,
	`product_id` BIGINT(20) NOT NULL,
	`is_enabled` TINYINT(1) NOT NULL,
	`is_active` TINYINT(1) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_prod_sms_config_product_type_product_id` (`product_type`, `product_id`),
	INDEX `FK_f_prod_sms_config_createdby_id` (`createdby_id`),
	INDEX `FK_f_prod_sms_config_lmb_id` (`lastmodifiedby_id`),
	CONSTRAINT `FK_f_prod_sms_config_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_prod_sms_config_lmb_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

CREATE TABLE `f_product_sms_transaction_type_config` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`product_sms_config_id` BIGINT(20) NOT NULL,
	`type` SMALLINT(3) NOT NULL,
	`priority_type` SMALLINT(3) NOT NULL,
	`message_template` VARCHAR(250) NULL DEFAULT NULL,
	`is_active` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_prod_sms_tran_type` (`product_sms_config_id`, `type`),
	INDEX `FK_f_prod_sms_tran_type_priority` (`priority_type`),
	CONSTRAINT `FK_f_prod_sms_tran_type_config_id` FOREIGN KEY (`product_sms_config_id`) REFERENCES `f_product_sms_configuration` (`id`)
);

UPDATE sms_configuration sc SET sc.value = 'http://localhost:8999/sms-brdge/api/v1/sms'
WHERE sc.name = 'API_BASE_URL';

ALTER TABLE `sms_messages_outbound`
	ADD COLUMN `entity_type_enum` SMALLINT(3) NULL DEFAULT NULL AFTER `id`,
	ADD COLUMN `entity_id` BIGINT(20) NULL DEFAULT NULL AFTER `entity_type_enum`;