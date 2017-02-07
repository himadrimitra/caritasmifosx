INSERT INTO `c_configuration` (`name`, `value`, `enabled`) VALUES ('client_dedup_threshold_person', 50, 0);
INSERT INTO `c_configuration` (`name`, `value`, `enabled`) VALUES ('client_dedup_threshold_entity', 50, 0);

CREATE TABLE `f_client_dedup_weightage` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`legal_form` TINYINT UNSIGNED NOT NULL DEFAULT 0,
	`firstname_exact` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`firstname_like` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`middlename_exact` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`middlename_like` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`lastname_exact` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`lastname_like` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`fullname_exact` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`fullname_like` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`mobile_no` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`date_of_birth` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`gender_cv_id` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`incorp_no` SMALLINT UNSIGNED NULL DEFAULT NULL,
	`client_identifier` SMALLINT UNSIGNED NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO `f_client_dedup_weightage` 
(`legal_form`, `firstname_exact`, `firstname_like`, `middlename_exact`, `middlename_like`, 
`lastname_exact`, `lastname_like`, `mobile_no`, `date_of_birth`, `gender_cv_id`, `client_identifier`) 
VALUES ('1', '20', '0', '10', '0', '10', '0', '50', '20', '0', '0');

INSERT INTO `f_client_dedup_weightage` 
(`legal_form`, `fullname_exact`, `fullname_like`, `mobile_no`, `incorp_no`, `client_identifier`) 
VALUES ('2', '50', '0', '50', '0', '0');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) 
VALUES ('configuration', 'READ_CLIENTDEDUPWEIGHTAGES', 'CLIENTDEDUPWEIGHTAGES', 'READ', 0),
	('configuration', 'UPDATE_CLIENTDEDUPWEIGHTAGES', 'CLIENTDEDUPWEIGHTAGES', 'UPDATE', 1),
	('configuration', 'UPDATE_CLIENTDEDUPWEIGHTAGES_CHECKER', 'CLIENTDEDUPWEIGHTAGES', 'UPDATE_CHECKER', 0);

ALTER TABLE `m_client`
	ADD COLUMN `is_force_activated` TINYINT(4) NOT NULL AFTER `reopened_by_userid`,
	ADD INDEX `m_client_dedup_index_firstname` (`firstname`),
	ADD INDEX `m_client_dedup_index_middlename` (`middlename`),
	ADD INDEX `m_client_dedup_index_lastname` (`lastname`),
	ADD INDEX `m_client_dedup_index_fullname` (`fullname`),
	ADD INDEX `m_client_dedup_index_mobile_no` (`mobile_no`),
	ADD INDEX `m_client_dedup_index_gender_cv_id` (`gender_cv_id`),
	ADD INDEX `m_client_dedup_index_date_of_birth` (`date_of_birth`),
	ADD INDEX `m_client_dedup_index_legal_form_enum` (`legal_form_enum`);

ALTER TABLE `m_client_non_person`
	ADD INDEX `m_client_non_person_dedup_index` (`incorp_no`);

DROP PROCEDURE IF EXISTS `clientdedupmatches`;
DELIMITER $$
CREATE PROCEDURE `clientdedupmatches`(
	IN `clientid` BIGINT(20)
)
BEGIN

DECLARE firstname VARCHAR(50) DEFAULT NULL;
DECLARE middlename VARCHAR(50) DEFAULT NULL;
DECLARE lastname VARCHAR(50) DEFAULT NULL;
DECLARE fullname VARCHAR(100) DEFAULT NULL;
DECLARE mobile_no VARCHAR(50) DEFAULT NULL;
DECLARE gender_cv_id INT(11) DEFAULT NULL;
DECLARE date_of_birth DATE DEFAULT NULL;
DECLARE legal_form_enum INT(5) DEFAULT NULL;
DECLARE incorp_no VARCHAR(50) DEFAULT NULL;
DECLARE document_keys VARCHAR(500) DEFAULT NULL;
	
DECLARE firstname_exact_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE firstname_like_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE middlename_exact_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE middlename_like_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE lastname_exact_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE lastname_like_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE fullname_exact_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE fullname_like_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE mobile_no_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE date_of_birth_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE gender_cv_id_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE incorp_no_wgt SMALLINT UNSIGNED DEFAULT NULL;
DECLARE client_identifier_wgt SMALLINT UNSIGNED DEFAULT NULL;

DECLARE dedup_threshold SMALLINT UNSIGNED DEFAULT 0;
	
SELECT cl.firstname, cl.middlename, cl.lastname, cl.fullname, cl.mobile_no, cl.gender_cv_id, 
cl.date_of_birth, cl.legal_form_enum, clnp.incorp_no, GROUP_CONCAT(clid.document_key)
INTO
firstname, middlename, lastname, fullname, mobile_no, gender_cv_id, 
date_of_birth, legal_form_enum, incorp_no, document_keys
FROM m_client as cl
LEFT JOIN m_client_non_person as clnp on (cl.id = clnp.client_id and cl.legal_form_enum = 2)
LEFT JOIN m_client_identifier as clid on (cl.id = clid.client_id and clid.status = 300)
where cl.id = clientid;

IF legal_form_enum IS NULL THEN
	SET legal_form_enum = 1;
END IF;

IF legal_form_enum = 1 THEN
	SELECT CONVERT(cfg.value, UNSIGNED) INTO dedup_threshold 
	FROM c_configuration as cfg WHERE cfg.name = 'client_dedup_threshold_person';
ELSE
	SELECT CONVERT(cfg.value, UNSIGNED) INTO dedup_threshold 
	FROM c_configuration as cfg WHERE cfg.name = 'client_dedup_threshold_entity';
END IF;

SELECT wgt.firstname_exact, wgt.firstname_like, wgt.middlename_exact, wgt.middlename_like,
wgt.lastname_exact, wgt.lastname_like, wgt.fullname_exact, wgt.fullname_like,
wgt.mobile_no, wgt.date_of_birth, wgt.gender_cv_id, wgt.incorp_no, wgt.client_identifier
INTO firstname_exact_wgt, firstname_like_wgt, middlename_exact_wgt, middlename_like_wgt,
lastname_exact_wgt, lastname_like_wgt, fullname_exact_wgt, fullname_like_wgt,
mobile_no_wgt, date_of_birth_wgt, gender_cv_id_wgt, incorp_no_wgt, client_identifier_wgt
FROM f_client_dedup_weightage as wgt
WHERE wgt.legal_form = legal_form_enum;


SELECT allmatch.id, allmatch.firstname, allmatch.middlename, allmatch.lastname, allmatch.fullname, allmatch.display_name, allmatch.mobile_no, allmatch.gender_cv_id, cv.code_value as gender_value, 
			allmatch.date_of_birth, allmatch.legal_form_enum, allmatch.incorp_no, SUM(allmatch.weight) as weight
FROM
(
SELECT g1.id, g1.firstname, g1.middlename, g1.lastname, g1.fullname, g1.display_name, g1.mobile_no, g1.gender_cv_id, 
			g1.date_of_birth, g1.legal_form_enum, g1.incorp_no, SUM(g1.weight) as weight
FROM
	(
		SELECT c.id, c.firstname, c.middlename, c.lastname, c.fullname, c.display_name, c.mobile_no, c.gender_cv_id, 
			c.date_of_birth, c.legal_form_enum, cnp.incorp_no,
		(IF(c.firstname = firstname, firstname_exact_wgt, IF(c.firstname LIKE CONCAT('%',firstname,'%'), firstname_like_wgt,0))
		+IF(c.middlename = middlename, middlename_exact_wgt, IF(c.middlename LIKE CONCAT('%',middlename,'%'), middlename_like_wgt,0))
		+IF(c.lastname = lastname, lastname_exact_wgt, IF(c.lastname LIKE CONCAT('%',lastname,'%'), lastname_like_wgt,0))
		+IF(c.fullname = fullname, fullname_exact_wgt, IF(c.fullname LIKE CONCAT('%',fullname,'%'), fullname_like_wgt,0))
		+IF(c.mobile_no LIKE CONCAT('%',mobile_no,'%'), mobile_no_wgt,0)
		+IF(c.gender_cv_id = gender_cv_id, IFNULL(gender_cv_id_wgt,0), 0)
		+IF(c.date_of_birth = date_of_birth, IFNULL(date_of_birth_wgt,0), 0)
		+IF(cnp.incorp_no = incorp_no, IFNULL(incorp_no_wgt,0), 0)
		) as weight
		FROM m_client as c
		LEFT JOIN m_client_non_person as cnp ON c.id = cnp.client_id
		WHERE IF(legal_form_enum=1,c.legal_form_enum IS NULL OR c.legal_form_enum = 1, c.legal_form_enum = 2)
		AND c.id <> clientid
		AND c.status_enum IN (300,303,304)
		AND (
			(c.firstname LIKE CONCAT('%',firstname,'%'))
			OR (c.middlename LIKE CONCAT('%',middlename,'%'))
			OR (c.lastname LIKE CONCAT('%',lastname,'%'))
			OR (c.fullname LIKE CONCAT('%',fullname,'%'))
			OR (c.mobile_no LIKE CONCAT('%',mobile_no,'%'))
			OR (c.date_of_birth = date_of_birth)
			OR (c.gender_cv_id = gender_cv_id)
			OR (cnp.incorp_no = incorp_no)
		)
	) as g1
	GROUP BY g1.id, g1.firstname, g1.middlename, g1.lastname, g1.fullname, g1.display_name, g1.mobile_no, g1.gender_cv_id, 
			g1.date_of_birth, g1.legal_form_enum, g1.incorp_no	
	HAVING sum(g1.weight) >= dedup_threshold

UNION ALL

	SELECT cl.id, cl.firstname, cl.middlename, cl.lastname, cl.fullname, cl.display_name, cl.mobile_no, cl.gender_cv_id, 
			cl.date_of_birth, cl.legal_form_enum, cnp.incorp_no, sum(client_identifier_wgt) as weight
	FROM m_client_identifier as id
	INNER JOIN m_client as cl ON cl.id = id.client_id
	LEFT JOIN m_client_non_person as cnp ON cl.id = cnp.client_id 
	WHERE IF(legal_form_enum=1,cl.legal_form_enum IS NULL OR cl.legal_form_enum = 1, cl.legal_form_enum = 2)
	AND cl.id <> clientid
	AND cl.status_enum IN (300,303,304)
	AND FIND_IN_SET(id.document_key, document_keys) > 0
	GROUP BY cl.id, cl.firstname, cl.middlename, cl.lastname, cl.fullname, cl.display_name, cl.mobile_no, cl.gender_cv_id, 
			cl.date_of_birth, cl.legal_form_enum, cnp.incorp_no

) as allmatch
LEFT JOIN m_code_value cv on cv.id = allmatch.gender_cv_id 
GROUP BY allmatch.id, allmatch.firstname, allmatch.middlename, allmatch.lastname, allmatch.fullname, allmatch.display_name, allmatch.mobile_no, allmatch.gender_cv_id,  cv.code_value , 
			allmatch.date_of_birth, allmatch.legal_form_enum, allmatch.incorp_no
ORDER BY sum(allmatch.weight) desc;

END$$
DELIMITER ;
