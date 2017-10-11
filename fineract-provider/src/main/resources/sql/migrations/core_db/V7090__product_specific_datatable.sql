CREATE TABLE `f_registered_table_scoping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`registered_table_id` BIGINT(20) NOT NULL,
	`loan_product_id` BIGINT(20) NULL DEFAULT NULL,
	`savings_product_id` BIGINT(20) NULL DEFAULT NULL,
	`code_value_id` INT(11) NULL DEFAULT NULL,
	`legal_form_enum` INT(5) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK__x_registered_table` FOREIGN KEY (`registered_table_id`) REFERENCES `x_registered_table` (`id`),
	CONSTRAINT `FK__m_product_loan` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`),
	CONSTRAINT `FK__m_savings_product` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`),
	CONSTRAINT `FK__m_code_value1` FOREIGN KEY (`code_value_id`) REFERENCES `m_code_value` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

ALTER TABLE `x_registered_table`
	CHANGE COLUMN `id` `id` BIGINT(20) NOT NULL AUTO_INCREMENT FIRST,
	ADD COLUMN `scoping_criteria_enum` SMALLINT(5) NULL DEFAULT NULL AFTER `category`;