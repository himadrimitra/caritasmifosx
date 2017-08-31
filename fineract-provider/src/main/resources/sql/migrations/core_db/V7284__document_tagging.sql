INSERT INTO `m_code` (`code_name`, `is_system_defined`) VALUES 
('Client Document Tags', '1'),
('Loan Document Tags', '1');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES('portfolio', 'GENERATE_DOCUMENT', 'DOCUMENT', 'GENERATE', '0');

CREATE TABLE `f_entity_tags` (
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`entity_type` VARCHAR(50) NOT NULL,
`tag_id` INT(11) NOT NULL,
`product_id` BIGINT(20) NULL DEFAULT NULL,
PRIMARY KEY (`id`),
CONSTRAINT `FK1_f_f_entity_tags_product_id` FOREIGN KEY (`product_id`) REFERENCES `m_product_loan` (`id`),
CONSTRAINT `FK1_f_f_entity_tags_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `m_code_value` (`id`)
);

CREATE TABLE `f_entity_tag_report_mapping` (
`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
`report_id` INT(11) NOT NULL,
`entity_tag_id` BIGINT(20) NOT NULL,
`output_type` VARCHAR(10) NOT NULL,
`display_name` VARCHAR(200) NOT NULL,
`description` VARCHAR(500) NULL DEFAULT NULL,
PRIMARY KEY (`id`),
CONSTRAINT `FK1_report_id_f_entity_tags` FOREIGN KEY (`report_id`) REFERENCES `stretchy_report` (`id`),
CONSTRAINT `FK2_entity_tag_id_f_entity_tags` FOREIGN KEY (`entity_tag_id`) REFERENCES `f_entity_tags` (`id`)
);

ALTER TABLE `m_document`
ADD COLUMN `tag_id` INT(11) NULL DEFAULT NULL AFTER `location`,
ADD COLUMN `report_mapping_id` BIGINT(20) NULL DEFAULT NULL AFTER `tag_id`,
ADD CONSTRAINT `fk_m_document_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `m_code_value`(`id`),
ADD CONSTRAINT `fk_m_document_report_mapping_id` FOREIGN KEY (`report_mapping_id`) REFERENCES `f_entity_tag_report_mapping`(`id`);