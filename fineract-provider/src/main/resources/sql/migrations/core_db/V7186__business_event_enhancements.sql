CREATE TABLE `f_risk_rule_sql` (
	`id` BIGINT(5) NOT NULL AUTO_INCREMENT,
	`rule_id` BIGINT(20) NOT NULL,
	`sql_query` TEXT NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_risk_rule_sql_rule_id` FOREIGN KEY (`rule_id`) REFERENCES `f_risk_rule` (`id`)
);

CREATE TABLE `f_business_event_rule_mapping` (
	`id` BIGINT(10) NOT NULL AUTO_INCREMENT,
	`business_event` VARCHAR(50) NOT NULL,
	`rule_id` BIGINT(20) NOT NULL,
	`min_output_value_for_validation` SMALLINT(3) NOT NULL,
	`validation_exception_code` VARCHAR(200) NOT NULL,
	`input_param_detail` TEXT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK_business_event_rule_mapping_rule_id` FOREIGN KEY (`rule_id`) REFERENCES `f_risk_rule` (`id`)
);

CREATE TABLE `f_business_event_listners` (
	`id` BIGINT(10) NOT NULL AUTO_INCREMENT,
	`business_event_name` VARCHAR(50) NOT NULL,
	`pre_listeners` VARCHAR(500) NULL,
	`post_listners` VARCHAR(500) NULL,
	PRIMARY KEY (`id`)
);

