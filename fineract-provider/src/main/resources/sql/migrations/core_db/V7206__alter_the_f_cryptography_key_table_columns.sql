DELETE FROM f_cryptography_key;

ALTER TABLE `f_cryptography_key`
	ALTER `entity_type` DROP DEFAULT,
	ALTER `key_type` DROP DEFAULT;
ALTER TABLE `f_cryptography_key`
	CHANGE COLUMN `entity_type` `entity_type` SMALLINT(3) NOT NULL AFTER `id`,
	CHANGE COLUMN `key_type` `key_type` SMALLINT(3) NOT NULL AFTER `entity_type`;
	
ALTER TABLE `f_cryptography_key`
	ADD COLUMN `user_id` BIGINT(20) NULL AFTER `key_value`,
	DROP INDEX `UQ_f_cryptography_entity_type_key_type`,
	ADD UNIQUE INDEX `UQ_f_cryptography_entity_type_key_type` (`entity_type`, `key_type`, `user_id`);

INSERT INTO `c_configuration` (`name`, `value`, `date_value`, `enabled`, `is_trap_door`, `description`) VALUES ('encrypt-login-password-for-authentication', '0', NULL, 1, 0, 'Enable encrypt login password for authentication');
INSERT INTO `c_configuration` (`name`, `value`, `date_value`, `enabled`, `is_trap_door`, `description`) VALUES ('every-user-login-generate-new-cryptographic-key-pair', '0', NULL, 1, 0, 'Enable every user login generate new cryptographic key pair');