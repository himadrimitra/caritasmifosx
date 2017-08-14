CREATE TABLE `f_collection_sheet` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`office_id` BIGINT(20) NULL DEFAULT NULL,
	`staff_id` BIGINT(20) NULL DEFAULT NULL,
	`group_id` BIGINT(20) NULL DEFAULT NULL,
	`center_id` BIGINT(20) NULL DEFAULT NULL,
	`meeting_date` DATE NOT NULL,
	PRIMARY KEY (`id`)
);




CREATE TABLE `f_collection_sheet_transaction_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`collection_sheet_id` BIGINT(20) NOT NULL,
	`entity_id` BIGINT(3) NULL DEFAULT NULL,
	`entity_type_enum` SMALLINT(3) NULL DEFAULT NULL,
	`transaction_id` BIGINT(20) NULL DEFAULT NULL,
	`transaction_status` TINYINT(3) NULL DEFAULT NULL,
	`error_message` VARCHAR(500) NULL DEFAULT NULL,
	 PRIMARY KEY (`id`),
	CONSTRAINT `FK_collection_sheet_transaction_f_collection_sheet` FOREIGN KEY (`collection_sheet_id`) REFERENCES `f_collection_sheet` (`id`)
);
