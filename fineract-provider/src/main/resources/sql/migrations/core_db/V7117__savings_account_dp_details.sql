INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`) VALUES ('Reduce Dp Limit For Savings', 'Reduce Dp Limit For Savings', '0 0 12 1/1 * ? *', '2015-04-08 09:24:15', '2015-10-09 07:57:28', '2016-12-03 00:00:00', 'Reduce Dp Limit For Savings1 _ DEFAULT', NULL);

CREATE TABLE `f_savings_account_dp_details` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`savings_id` BIGINT(20) NOT NULL,
	`frequency` SMALLINT(5) NOT NULL,
	`dp_reduction_every` SMALLINT(5) NOT NULL,
	`duration` SMALLINT(5) NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`dp_amount` DECIMAL(19,6) NOT NULL,
	`calculation_type` SMALLINT(5) NOT NULL,
	`amount_or_percentage` DECIMAL(19,6) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK__m_savings_account` (`savings_id`),
	CONSTRAINT `FK__m_savings_account` FOREIGN KEY (`savings_id`) REFERENCES `m_savings_account` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1
;
