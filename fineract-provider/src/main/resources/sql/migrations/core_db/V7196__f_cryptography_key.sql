CREATE TABLE `f_cryptography_key` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`entity_type` VARCHAR(100) NOT NULL,
	`key_type` VARCHAR(50) NOT NULL,
	`key_value` LONGBLOB NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_f_cryptography_entity_type_key_type` (`entity_type`, `key_type`)
)
AUTO_INCREMENT=1
;