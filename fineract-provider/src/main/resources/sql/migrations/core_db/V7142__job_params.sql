CREATE TABLE `job_parameters` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`job_id` BIGINT(20) NOT NULL,
	`param_key` VARCHAR(50) NOT NULL,
	`param_value` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `job_id_param_key_UNIQUE` (`job_id`, `param_key`),
	INDEX `FK_job_to_job_parameters` (`job_id`),
	CONSTRAINT `FK_job_to_job_parameters` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;
