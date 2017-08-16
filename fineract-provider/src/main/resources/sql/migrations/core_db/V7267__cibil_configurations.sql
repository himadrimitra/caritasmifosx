CREATE TABLE `f_loan_creditbureau_score` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`loan_creditbureau_enquiry_id` BIGINT(20) NOT NULL,
	`score_name` VARCHAR(200) NULL DEFAULT NULL,
	`score_card_name` VARCHAR(100) NULL DEFAULT NULL,
	`score_card_version` VARCHAR(50) NULL DEFAULT NULL,
	`score_card_date` DATE NULL DEFAULT NULL,
	`score_value` VARCHAR(10) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK1_f_loan_creditbureau_enquiry_id` FOREIGN KEY (`loan_creditbureau_enquiry_id`) REFERENCES `f_loan_creditbureau_enquiry` (`id`)
);

INSERT INTO `c_external_service` (`id`, `name`) VALUES (9, 'CIBIL');
INSERT INTO `f_creditbureau_product` (`name`, `product`, `country`, `implementation_key`) values ("Cibil","TUEF","INDIA","india.cibil.tuef");

INSERT INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) VALUES 
('USER_NAME', 'NB73391003_UATC2C3', (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('PASSWORD', 'c^5hRznj', (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('CIBIL_HOST', '103.225.112.28', (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('CIBIL_PORT', '1.0', (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL'));

INSERT IGNORE INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) 
VALUES 
('DOCUMENT_TYPE_PASSPORT', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('DOCUMENT_TYPE_VOTER_ID', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('DOCUMENT_TYPE_UID', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('DOCUMENT_TYPE_OTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('DOCUMENT_TYPE_RATION_CARD', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('DOCUMENT_TYPE_DRIVING_CARD', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('DOCUMENT_TYPE_PAN', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('GENDER_TYPE_MALE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('GENDER_TYPE_FEMALE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('ADDRESSTYPE_RESIDENCE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('ADDRESSTYPE_PERMANENT', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL')),
('ADDRESSTYPE_OFFICE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'CIBIL'));

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Passport' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Customer Identifier'))
WHERE esp.name = 'DOCUMENT_TYPE_PASSPORT' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'CIBIL');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Voter Id' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Customer Identifier'))
WHERE esp.name = 'DOCUMENT_TYPE_VOTER_ID' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'CIBIL');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Male' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Gender'))
WHERE esp.name = 'GENDER_TYPE_MALE' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'CIBIL');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Female' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Gender'))
WHERE esp.name = 'GENDER_TYPE_FEMALE' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'CIBIL');