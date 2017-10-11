ALTER TABLE `c_external_service_properties`
	ADD UNIQUE INDEX `UQ_c_external_service_properties` (`name`, `external_service_id`);

INSERT IGNORE INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) 
VALUES 
('DOCUMENT_TYPE_ID01_PASSPORT', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('DOCUMENT_TYPE_ID02_VOTER_ID', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('DOCUMENT_TYPE_ID03_UID', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('DOCUMENT_TYPE_ID04_OTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('DOCUMENT_TYPE_ID05_RATION_CARD', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('DOCUMENT_TYPE_ID06_DRIVING_CARD', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('DOCUMENT_TYPE_ID07_PAN', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D01_RESIDENCE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D02_COMPANY', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D03_RESCUMOFF', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D04_PERMANENT', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D05_CURRENT', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D06_FOREIGN', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D07_MILITARY', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('ADDRESS_TYPE_D08_OTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('GENDER_TYPE_MALE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('GENDER_TYPE_FEMALE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_SPOUSE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K01_FATHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K02_HUSBAND', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K03_MOTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K04_SON', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K05_DAUGHTER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K06_WIFE', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K07_BROTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K09_FATHER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K11_SISTER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K12_SON_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK')),
('RELATIONSHIP_TYPE_K15_OTHER', null, (SELECT es.id FROM c_external_service es WHERE es.name = 'HIGHMARK'));

INSERT IGNORE INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`, `is_mandatory`, `parent_id`)
VALUES
((SELECT c.id FROM m_code c WHERE c.code_name = 'Gender'), 'Male', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Gender'), 'Female', NULL, 2, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Customer Identifier'), 'Voter Id', NULL, 2, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Mother', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Husband', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Son', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Daughter', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Wife', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Brother', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Mother in law', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Brother in law', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Daughter in law', NULL, 1, NULL, 1, 0, NULL),
((SELECT c.id FROM m_code c WHERE c.code_name = 'Relationship'), 'Other', NULL, 1, NULL, 1, 0, NULL);

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Residential Address' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'AddressType'))
WHERE esp.name = 'ADDRESS_TYPE_D01_RESIDENCE' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Permanent Address' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'AddressType'))
WHERE esp.name = 'ADDRESS_TYPE_D04_PERMANENT' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Passport' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Customer Identifier'))
WHERE esp.name = 'DOCUMENT_TYPE_ID01_PASSPORT' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Voter Id' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Customer Identifier'))
WHERE esp.name = 'DOCUMENT_TYPE_ID02_VOTER_ID' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

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
WHERE es.name = 'HIGHMARK');

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
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Father' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K01_FATHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Mother' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K03_MOTHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Husband' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K02_HUSBAND' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Son' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K04_SON' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Daughter' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K05_DAUGHTER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Wife' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K06_WIFE' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Brother' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K07_BROTHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Mother in law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K08_MOTHER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'father-in-law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K09_FATHER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Daughter in law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K10_DAUGHTER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Brother in law' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K13_BROTHER_IN_LAW' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

UPDATE c_external_service_properties esp SET esp.value = (
SELECT cv.id
FROM m_code_value cv
WHERE cv.code_value = 'Other' AND cv.code_id = (
SELECT c.id
FROM m_code c
WHERE c.code_name = 'Relationship'))
WHERE esp.name = 'RELATIONSHIP_TYPE_K15_OTHER' AND esp.external_service_id = (
SELECT es.id
FROM c_external_service es
WHERE es.name = 'HIGHMARK');

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
WHERE es.name = 'HIGHMARK');
