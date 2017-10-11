INSERT IGNORE INTO `m_code` (`code_name`, `is_system_defined`) VALUES ('LoanPurposeGroupType', 1);

INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`) VALUES 
((SELECT id FROM m_code WHERE code_name = 'LoanPurposeGroupType'), 'Grouping', NULL, 0, NULL, 1),
((SELECT id FROM m_code WHERE code_name = 'LoanPurposeGroupType'), 'Consumption', NULL, 0, NULL, 1);

ALTER TABLE `f_loan_purpose_group`
	ALTER `type_enum_id` DROP DEFAULT;
ALTER TABLE `f_loan_purpose_group`
	CHANGE COLUMN `type_enum_id` `type_cv_id` INT(11) NOT NULL AFTER `description`;
	
ALTER TABLE `f_loan_purpose_group`
	ADD CONSTRAINT `FK_f_loan_purpose_group_type_cv_id` FOREIGN KEY (`type_cv_id`) REFERENCES `m_code_value` (`id`);