ALTER TABLE `chai_villages`
	DROP COLUMN `taluk`,
	DROP COLUMN `district`,
	DROP COLUMN `pincode`,
	DROP COLUMN `state`;
	
	ALTER TABLE `f_address`
	ALTER `house_no` DROP DEFAULT,
	ALTER `address_line_one` DROP DEFAULT,
	ALTER `postal_code` DROP DEFAULT;
ALTER TABLE `f_address`
	CHANGE COLUMN `house_no` `house_no` VARCHAR(20) NULL AFTER `id`,
	CHANGE COLUMN `address_line_one` `address_line_one` VARCHAR(200) NULL AFTER `street_no`,
	CHANGE COLUMN `postal_code` `postal_code` VARCHAR(10) NULL AFTER `country_id`;
	ALTER TABLE `f_address_entity`
	ALTER `address_type` DROP DEFAULT;
ALTER TABLE `f_address_entity`
	CHANGE COLUMN `address_type` `address_type` INT(11) NULL AFTER `address_id`;
	
INSERT INTO `c_configuration` (`id`, `name`,`value`, `enabled`, `description`) 
VALUES (NULL, 'enable-clients-address', '0', '0',"Enable the address while creating client");

INSERT INTO `c_configuration` (`id`, `name`,`value`, `enabled`, `description`) 
VALUES (NULL, 'populate_client_address_from_villages', '0', '0',"Populate client address from villages");
	