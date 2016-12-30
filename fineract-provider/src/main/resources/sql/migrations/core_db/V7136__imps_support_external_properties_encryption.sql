DROP TABLE IF EXISTS `f_external_service_properties`;

CREATE TABLE `f_external_service_properties` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`external_service_id` BIGINT(20) NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	`value` VARCHAR(512) NOT NULL,
	`is_encrypted` TINYINT(1) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY UK_f_external_service_properties (`external_service_id`,`name`),
	CONSTRAINT `FK_f_external_service_properties_service_id` FOREIGN KEY (`external_service_id`) REFERENCES `f_external_service_details` (`id`)
)
ENGINE=InnoDB;

ALTER TABLE `f_bank_account_details`
		ADD COLUMN bank_name VARCHAR(20)  NULL,
	  ADD COLUMN bank_city VARCHAR(20)  NULL;