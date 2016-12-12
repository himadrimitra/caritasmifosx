
CREATE TABLE `m_entity_field_type` (
	`entity_type` INT(11) NOT NULL,
	`preriquisities_type` VARCHAR(50) NULL DEFAULT NULL,
	`field_name` VARCHAR(50) NULL DEFAULT NULL,
	`regex` VARCHAR(50) NOT NULL,
	`error_msg` VARCHAR(50) NOT NULL
);


