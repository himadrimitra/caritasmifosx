CREATE TABLE `f_registered_table_section` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`registered_table_id` BIGINT(20) NOT NULL,
	`display_name` VARCHAR(50) NOT NULL,
	`display_position` INT(11) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `Index 2` (`registered_table_id`, `display_name`),
	CONSTRAINT `FK_f_registered_table_section_x_registered_table` FOREIGN KEY (`registered_table_id`) REFERENCES `x_registered_table` (`id`) ON DELETE CASCADE
);

ALTER TABLE `x_registered_table_metadata`
	ADD COLUMN `section_id` BIGINT(20) NULL DEFAULT NULL AFTER `mandatory_if_visible`,
	ADD CONSTRAINT `FK_x_registered_table_metadata_f_registered_table_section` FOREIGN KEY (`section_id`) REFERENCES `f_registered_table_section` (`id`) ON DELETE SET NULL;

