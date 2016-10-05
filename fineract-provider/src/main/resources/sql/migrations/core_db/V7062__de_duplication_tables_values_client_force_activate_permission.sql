INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'FORCE_ACTIVATE_CLIENT', 'CLIENT', 'FORCE_ACTIVATE', 0);

INSERT INTO `c_configuration` (`name`, `description`) VALUES ('customer-deduplication', 'Avoiding client  duplication ');

CREATE TABLE `de_duplication_table` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`table_name` VARCHAR(100) NOT NULL,
	`error_message` VARCHAR(100) NOT NULL,
	`join_tables` VARCHAR(100),
	PRIMARY KEY (`id`)
);

CREATE TABLE `de_duplication_criteria` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`de_duplication_columns` VARCHAR(200) NOT NULL,
	`criteria` BIGINT NOT NULL,
	`active` INT NOT NULL DEFAULT '1',
	PRIMARY KEY (`id`),
	CONSTRAINT `FK__de_duplication_table` FOREIGN KEY (`criteria`) REFERENCES `de_duplication_table` (`id`)
);

INSERT INTO `de_duplication_table` (`table_name`, `error_message`, `join_tables`) VALUES ('m_client', 'error.msg.duplicate.client.entry','m_office,office_id,id,name');

INSERT INTO `de_duplication_criteria` (`de_duplication_columns`, `criteria`) VALUES ('firstname,middlename,lastname,date_of_birth', 1);

INSERT INTO `de_duplication_criteria` (`de_duplication_columns`, `criteria`) VALUES ('mobile_no', 1);