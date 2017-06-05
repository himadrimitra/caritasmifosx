CREATE TABLE `f_loan_product_entity_profile_mapping` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_product_id` BIGINT(20) NOT NULL,
	`profile_type` SMALLINT(3) NOT NULL,
	`value` BIGINT(20) NOT NULL,
	`value_entity_type` SMALLINT(3) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `f_loan_product_entity_profile_mapping_UNIQUE` (`loan_product_id`, `profile_type`, `value`, `value_entity_type`),
	CONSTRAINT `FK1_f_loan_product_id_entity_profile_mapping` FOREIGN KEY (`loan_product_id`) REFERENCES `m_product_loan` (`id`)
);

ALTER TABLE `m_product_loan`
	ADD COLUMN `applicable_for_loan_type` SMALLINT(3) NOT NULL DEFAULT '1' AFTER `percentage_of_disbursement_to_be_transferred`;
	
ALTER TABLE `m_product_loan`
	ADD INDEX `INX_applicable_for_loan_type` (`applicable_for_loan_type`);