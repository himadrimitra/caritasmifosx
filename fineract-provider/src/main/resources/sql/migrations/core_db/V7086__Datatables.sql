ALTER TABLE `m_code`
  ADD `parent_id` INT(11) NULL DEFAULT NULL;
  
ALTER TABLE `m_code_value`
  ADD `parent_id` INT(11) NULL DEFAULT NULL;

ALTER TABLE `m_code`
	ADD CONSTRAINT `FK_m_code_m_code` FOREIGN KEY (`parent_id`) REFERENCES `m_code` (`id`);
	
ALTER TABLE `m_code_value`
	ADD CONSTRAINT `FK_m_code_value_m_code_value` FOREIGN KEY (`parent_id`) REFERENCES `m_code_value` (`id`);
	
ALTER TABLE `x_registered_table`
	ADD COLUMN `id` BIGINT(20) NOT NULL AUTO_INCREMENT AFTER `category`,
	ADD INDEX `KEY` (`id`);

CREATE TABLE `x_registered_table_metadata` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`registered_table_id` BIGINT(20) NOT NULL,
	`column_name` VARCHAR(100) NOT NULL,
	`associate_with` BIGINT(20) NULL DEFAULT NULL,
	`display_name` VARCHAR(500) NULL DEFAULT NULL,
	`order_position` INT(11) NULL DEFAULT NULL,
	`visible` TINYINT(1) NULL DEFAULT '1',
	`mandatory_if_visible` TINYINT(1) NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	INDEX `FK_x_registered_table_metadata_x_registered_table` (`registered_table_id`),
	INDEX `FK_x_registered_table_metadata_x_registered_table_metadata` (`associate_with`),
	CONSTRAINT `FK_x_registered_table_metadata_x_registered_table` FOREIGN KEY (`registered_table_id`) REFERENCES `x_registered_table` (`id`),
	CONSTRAINT `FK_x_registered_table_metadata_x_registered_table_metadata` FOREIGN KEY (`associate_with`) REFERENCES `x_registered_table_metadata` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `x_registered_table_display_rules` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`registered_table_metadata_id` BIGINT(20) NOT NULL DEFAULT '0',
	`watch_column` BIGINT(20) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	INDEX `FK__x_registered_table_metadata` (`registered_table_metadata_id`),
	INDEX `FK__x_registered_table_metadata_2` (`watch_column`),
	CONSTRAINT `FK__x_registered_table_metadata` FOREIGN KEY (`registered_table_metadata_id`) REFERENCES `x_registered_table_metadata` (`id`),
	CONSTRAINT `FK__x_registered_table_metadata_2` FOREIGN KEY (`watch_column`) REFERENCES `x_registered_table_metadata` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;


CREATE TABLE `x_registered_table_display_rules_value` (
	`code_value_id` INT(11) NULL DEFAULT NULL,
	`registered_table_display_rules_id` BIGINT(20) NULL DEFAULT NULL,
	INDEX `FK__code_value_2` (`code_value_id`),
	INDEX `FK__x_registered_table_display_rules` (`registered_table_display_rules_id`),
	CONSTRAINT `FK__code_value_2` FOREIGN KEY (`code_value_id`) REFERENCES `m_code_value` (`id`),
	CONSTRAINT `FK__x_registered_table_display_rules` FOREIGN KEY (`registered_table_display_rules_id`) REFERENCES `x_registered_table_display_rules` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;