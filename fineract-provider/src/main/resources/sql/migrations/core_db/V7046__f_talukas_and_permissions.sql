CREATE TABLE `f_taluka` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`district_id` BIGINT(20) NOT NULL,
	`iso_taluka_code` CHAR(3) NULL DEFAULT NULL,
	`taluka_name` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_sid_iso_taluka_code_name` (`district_id`, `iso_taluka_code`, `taluka_name`),
	INDEX `FK_district_id` (`district_id`),
	INDEX `INX_iso_taluka_code` (`iso_taluka_code`),
	INDEX `INX_taluka_name` (`taluka_name`),
	CONSTRAINT `FK_district_id` FOREIGN KEY (`district_id`) REFERENCES `f_district` (`id`)
)AUTO_INCREMENT=1;

ALTER TABLE `f_address`
	CHANGE COLUMN `taluka` `taluka_id` BIGINT(20) NULL DEFAULT NULL AFTER `village_town`,
	ADD CONSTRAINT `FK_f_address_taluka_id` FOREIGN KEY (`taluka_id`) REFERENCES `f_taluka` (`id`);
	
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('gis', 'CREATE_TALUKA', 'TALUKA', 'CREATE', 0),
('gis', 'CREATE_TALUKA_CHECKER', 'TALUKA', 'CREATE_CHECKER', 0),
('gis', 'UPDATE_TALUKA', 'TALUKA', 'UPDATE', 0),
('gis', 'UPDATE_TALUKA_CHECKER', 'TALUKA', 'UPDATE_CHECKER', 0),
('gis', 'DELETE_TALUKA', 'TALUKA', 'DELETE', 0),
('gis', 'DELETE_TALUKA_CHECKER', 'TALUKA', 'DELETE_CHECKER', 0),
('gis', 'READ_TALUKA', 'TALUKA', 'READ', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('fingerprint', 'CREATE_FINGERPRINT', 'FINGERPRINT', 'CREATE', 0),
('fingerprint', 'CREATE_FINGERPRINT_CHECKER', 'FINGERPRINT', 'CREATE_CHECKER', 0),
('fingerprint', 'READ_FINGERPRINT', 'FINGERPRINT', 'READ', 0);

CREATE TABLE `f_client_fingerprint` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`client_id` BIGINT(20) NOT NULL,
	`finger_id` SMALLINT(3) NOT NULL,
	`finger_print` VARCHAR(5000) NOT NULL,
	`createdby_id` BIGINT(20) NOT NULL,
	`created_date` DATETIME NOT NULL,
	`lastmodifiedby_id` BIGINT(20) NOT NULL,
	`lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `f_client_fingerprint_UNIQUE` (`client_id`, `finger_id`),
	INDEX `FK_f_fingerprint_client_id` (`client_id`),
	INDEX `FK_f_fingerprint_createdby_id` (`createdby_id`),
	INDEX `FK_f_fingerprint_lastmodifiedby_id` (`lastmodifiedby_id`),
	CONSTRAINT `FK_f_fingerprint_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
	CONSTRAINT `FK_f_fingerprint_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
	CONSTRAINT `FK_f_fingerprint_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

INSERT INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`, `enum_type`) 
VALUES ('right_hand_fingers', 1, 'Right_hand_thumb', 'RIGHT_THUMB', 0),
('right_hand_fingers', 2, 'Right_hand_index', 'RIGHT_INDEX', 0),
('right_hand_fingers', 3, 'Right_hand_middle', 'RIGHT_MIDDLE', 0),
('right_hand_fingers', 4, 'Right_hand_ring', 'RIGHT_RING', 0),
('right_hand_fingers', 5, 'Right_hand_pinky', 'RIGHT_PINKY', 0),
('left_hand_fingers', 6, 'Left_hand_thumb', 'LEFT_THUMB', 0),
('left_hand_fingers', 7, 'Left_hand_index', 'LEFT_INDEX', 0),
('left_hand_fingers', 8, 'Left_hand_middle', 'LEFT_MIDDLE', 0),
('left_hand_fingers', 9, 'Left_hand_ring', 'LEFT_RING', 0),
('left_hand_fingers', 10, 'Left_hand_pinky', 'LEFT_PINKY', 0);