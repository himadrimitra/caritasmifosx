ALTER TABLE `f_loan_utilization_check`
	ADD COLUMN `loan_id` BIGINT(20) NULL DEFAULT NULL AFTER `id`;

ALTER TABLE `f_loan_utilization_check`
	ADD CONSTRAINT `FK_f_loan_utilization_check_loan_id` FOREIGN KEY (`loan_id`) REFERENCES `m_loan` (`id`);
	
UPDATE f_loan_utilization_check lu 
INNER JOIN f_loan_utilization_check_detail lud ON
lu.id = lud.loan_utilization_check_id
SET lu.loan_id = lud.loan_id;

ALTER TABLE `f_loan_utilization_check`
	ALTER `loan_id` DROP DEFAULT;
ALTER TABLE `f_loan_utilization_check`
	CHANGE COLUMN `loan_id` `loan_id` BIGINT(20) NOT NULL AFTER `id`;
	
ALTER TABLE `f_loan_utilization_check_detail`
	DROP FOREIGN KEY `FK_f_loan_utilization_check_detail_loan_id`;

COMMIT;
	
ALTER TABLE `f_loan_utilization_check_detail`
	DROP COLUMN `loan_id`;
	
ALTER TABLE `f_loan_purpose`
	ALTER `short_name` DROP DEFAULT;
ALTER TABLE `f_loan_purpose`
	CHANGE COLUMN `short_name` `system_code` VARCHAR(50) NOT NULL AFTER `name`;
	
ALTER TABLE `f_loan_purpose_group`
	ALTER `short_name` DROP DEFAULT;
ALTER TABLE `f_loan_purpose_group`
	CHANGE COLUMN `short_name` `system_code` VARCHAR(50) NOT NULL AFTER `name`;
	
ALTER TABLE `f_loan_purpose_group`
	DROP INDEX `UQ_f_loan_purpose_group_short_name`,
	ADD UNIQUE INDEX `UQ_f_loan_purpose_group_system_code` (`system_code`);
	
ALTER TABLE `f_loan_purpose`
	DROP INDEX `UQ_f_loan_purpose_short_name`,
	ADD UNIQUE INDEX `UQ_f_loan_purpose_system_code` (`system_code`);
	
ALTER TABLE `f_loan_purpose_group`
	ADD COLUMN `is_system_defined` TINYINT(1) NOT NULL DEFAULT '0' AFTER `is_active`;

INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`) VALUES 
((SELECT id FROM m_code WHERE code_name = 'LoanPurposeGroupType'), 'Consumption Wise', NULL, 0, NULL, 1),
((SELECT id FROM m_code WHERE code_name = 'LoanPurposeGroupType'), 'Sector Wise', NULL, 0, NULL, 1),
((SELECT id FROM m_code WHERE code_name = 'LoanPurposeGroupType'), 'PSL', NULL, 0, NULL, 1);

INSERT IGNORE INTO `f_loan_purpose_group` (`name`, `system_code`, `description`, `type_cv_id`, `is_active`, `is_system_defined`) 
VALUES 
('Income generation-Working capital- intangibles', 'income.generating.working.capital.intangible', 'Income generation-Working capital- intangibles', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Income generation-Working capital- tangibles', 'income.generating.working.capital.tangible', 'Income generation-Working capital- tangibles', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Income generation-Movable assets', 'income.generating.movable.assets', 'Income generation-Movable assets', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Income generation-Immovable assets', 'income.generating.immovable.assets', 'Income generation-Immovable assets', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Non-income generating-Consumption', 'non.income.generating.consumption', 'Non-income generating-Consumption', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Non-income generating-Asset creation', 'non.income.generating.asset.creation', 'Non-income generating-Asset creation', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Non-income generating-Emergency', 'non.income.generating.emergency', 'Non-income generating-Emergency', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1),
('Non-income generating-Education', 'non.income.generating.education', 'Non-income generating-Emergency', (
SELECT cv.id
FROM m_code_value cv
JOIN m_code c ON c.id = cv.code_id AND c.code_name = 'LoanPurposeGroupType'
WHERE cv.code_value = 'Consumption Wise'), 1, 1);
	