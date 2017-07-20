ALTER TABLE `m_image`
	ADD COLUMN `entity_type` INT NULL AFTER `geo_tag`,
	ADD COLUMN `entity_id` INT NULL AFTER `entity_type`,
	ADD COLUMN `created_by` INT NULL AFTER `entity_id`,
	ADD COLUMN `created_on` INT NULL AFTER `created_by`;
	
INSERT INTO `f_task_activity` (`name`, `identifier`, `config_values`, `supported_actions`, `type`) VALUES ('Take Picture Activity', 'takepicture', NULL, NULL, 3),
('Associate To Group', 'associatetogroup', NULL, NULL, 3);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'DELETE_IMAGE', 'IMAGE', 'DELETE', 1),
(`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'READ_IMAGE', 'IMAGE', 'READ', 1),
(`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ('portfolio', 'SAVE_IMAGE', 'IMAGE', 'SAVE', 1);
