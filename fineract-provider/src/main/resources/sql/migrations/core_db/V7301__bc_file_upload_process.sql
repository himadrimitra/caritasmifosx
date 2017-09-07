CREATE TABLE `f_file_process` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`file_name` VARCHAR(100) NOT NULL,
	`file_type` VARCHAR(50) NOT NULL,
	`file_path` VARCHAR(250) NOT NULL,
	`file_process_type` SMALLINT(3) NOT NULL,
	`total_records` INT(10) NOT NULL DEFAULT '0',
	`total_pending_records` INT(10) NOT NULL DEFAULT '0',
	`total_success_records` INT(10) NOT NULL DEFAULT '0',
	`total_failure_records` INT(10) NOT NULL DEFAULT '0',
	`status` SMALLINT(3) NOT NULL,
	`last_processed_date` DATETIME NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
);

CREATE TABLE `f_file_records` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`file_process_id` BIGINT(20) NOT NULL,
	`content` TEXT NOT NULL,
	`status` SMALLINT(3) NOT NULL,
	`created_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
);