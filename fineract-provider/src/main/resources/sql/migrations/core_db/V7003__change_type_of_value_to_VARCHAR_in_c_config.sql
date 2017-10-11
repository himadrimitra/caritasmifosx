ALTER TABLE `c_configuration`
	CHANGE COLUMN `value` `value` VARCHAR(50) NULL DEFAULT NULL AFTER `name`;
	
INSERT INTO `c_configuration` (`name`,`enabled`, `description` ) VALUES ('default-organisation-currency', 0, 'setting  default organisation currency ');	