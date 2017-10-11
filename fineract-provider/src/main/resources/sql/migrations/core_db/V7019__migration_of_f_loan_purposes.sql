ALTER TABLE `m_loan`
	ADD COLUMN `loan_purpose_id` BIGINT(20) NULL DEFAULT NULL AFTER `loanpurpose_cv_id`;
	
ALTER TABLE `m_loan`
	ADD CONSTRAINT `FK_loan_purpose_id_m_loan` FOREIGN KEY (`loan_purpose_id`) REFERENCES `f_loan_purpose` (`id`);
	
INSERT INTO `f_loan_purpose` (`name`,`short_name`,`description`, `is_active`, `createdby_id`, `created_date`, `lastmodifiedby_id`, `lastmodified_date`)
SELECT v.code_value,CONCAT(SUBSTR(v.code_value, 1, 4),'_',v.id),v.code_description,v.is_active,'1', CURDATE(),'1', CURDATE()
FROM m_code_value v
INNER JOIN m_code c ON c.id = v.code_id
WHERE c.code_name = 'LoanPurpose';

UPDATE m_loan l
JOIN m_code_value cv ON cv.id = l.loanpurpose_cv_id
JOIN f_loan_purpose lp ON lp.name = cv.code_value SET l.loan_purpose_id = lp.id;

ALTER TABLE `m_loan`
	DROP FOREIGN KEY `FK_m_loanpurpose_codevalue`;
	
-- ALTER TABLE `m_loan` DROP COLUMN `loanpurpose_cv_id`;
	
ALTER TABLE `f_loan_application_reference`
	DROP FOREIGN KEY `FK_m_code_value_id_to_f_loan_application_reference_product_id`;
ALTER TABLE `f_loan_application_reference`
	CHANGE COLUMN `loan_purpose_cv_id` `loan_purpose_id` BIGINT NULL DEFAULT NULL AFTER `loan_product_id`,
	ADD CONSTRAINT `FK_f_loan_purpose_id_to_f_loan_application_reference_purpose_id` FOREIGN KEY (`loan_purpose_id`) REFERENCES `f_loan_purpose` (`id`);