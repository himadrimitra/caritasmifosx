ALTER TABLE `f_existing_loan`
	ADD COLUMN `received_loan_status` VARCHAR(100) NULL DEFAULT NULL AFTER `loan_status_id`;
	
INSERT INTO `c_external_service` (`id`, `name`) VALUES (7, 'EQUIFAX');
INSERT INTO `f_creditbureau_product` (`name`, `product`, `country`, `implementation_key`) values ("Equifax","MCS","INDIA","india.equifax.mcs");

INSERT INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) VALUES 
('CUSTOMER_ID', '21', 7),
('CUSTOMER_REFERENCE_NO', '', 7),
('MEMBER_NUMBER', '027FZ00034', 7),
('PASSWORD', 'A45_K0321', 7),
('PRODUCT_CODE', 'MCS', 7),
('PRODUCT_VERSION', '1.0', 7),
('QNAME', 'http://services.equifax.com/eport/servicedefs/1.0', 7),
('QNAME_VERSION', 'v1.0', 7),
('SECURITY_CODE', 'TT8', 7),
('URL', 'https://eportuat.equifax.co.in/creditreportws/CreditReportWSInquiry/v1.0?wsdl', 7),
('USER_ID', 'STS_CIFC', 7);

INSERT IGNORE INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) 
VALUES 
('DOCUMENT_TYPE_PASSPORT', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('DOCUMENT_TYPE_VOTER_ID', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('DOCUMENT_TYPE_UID', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('DOCUMENT_TYPE_OTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('DOCUMENT_TYPE_RATION_CARD', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('DOCUMENT_TYPE_DRIVING_CARD', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('DOCUMENT_TYPE_PAN', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('GENDER_TYPE_MALE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('GENDER_TYPE_FEMALE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_SPOUSE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_FATHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_HUSBAND', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_MOTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_SON', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_SISTER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_DAUGHTER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_WIFE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_BROTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_MOTHER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_FATHER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_DAUGHTER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_SISTER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_SON_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_BROTHER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX')),
('RELATIONSHIP_TYPE_OTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'EQUIFAX'));

INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`, `is_mandatory`, `parent_id`)
VALUES
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Father', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Father In Law', NULL, 1, NULL, 1, 0, NULL);

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
WHERE es.name = 'EQUIFAX');

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
WHERE es.name = 'EQUIFAX');

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
WHERE es.name = 'EQUIFAX');

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
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Father' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_FATHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Mother' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_MOTHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Husband' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_HUSBAND' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Son' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_SON' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Daughter' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_DAUGHTER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Wife' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_WIFE' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Brother' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_BROTHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Mother in law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_MOTHER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'father-in-law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_FATHER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Daughter in law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_DAUGHTER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Brother in law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_BROTHER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Other' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_OTHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Spouse' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_SPOUSE' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'EQUIFAX');