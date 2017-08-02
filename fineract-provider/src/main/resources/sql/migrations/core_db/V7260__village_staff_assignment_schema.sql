INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('portfolio_village', 'ASSIGNSTAFF_VILLAGE', 'VILLAGE', 'ASSIGNSTAFF', 0),
('portfolio_village', 'UNASSIGNSTAFF_VILLAGE', 'VILLAGE', 'UNASSIGNSTAFF', 0),
('portfolio_village', 'ASSIGNSTAFF_VILLAGE_CHECKER', 'VILLAGE', 'ASSIGNSTAFF_CHECKER', 0),
('portfolio_village', 'UNASSIGNSTAFF_VILLAGE_CHECKER', 'VILLAGE', 'UNASSIGNSTAFF_CHECKER', 0);

INSERT INTO `f_task_activity` (`name`, `identifier`, `config_values`, `supported_actions`, `type`) VALUES ('Assign Village to Staff', 'assignvillagestaff', NULL, NULL, 3);

ALTER TABLE `chai_villages` ADD COLUMN `staff_id` BIGINT(20) NULL DEFAULT NULL,
      ADD CONSTRAINT `FK1_chai_village_m_staff` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`);

CREATE TABLE `f_village_staff_assignment_history` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`village_id` BIGINT(20) NOT NULL,
	`staff_id` BIGINT(20) NULL DEFAULT NULL,
	`start_date` DATE NOT NULL,
	`end_date` DATE NULL DEFAULT NULL,
	`createdby_id` BIGINT(20) NULL DEFAULT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`lastmodified_date` DATETIME NULL DEFAULT NULL,
	`lastmodifiedby_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	INDEX `fk_chai_village_f_village_staff_assignment_history` (`village_id`),
	INDEX `fk_m_staff_f_village_staff_assignment_history` (`staff_id`),
	CONSTRAINT `fk_chai_village_f_village_staff_assignment_history` FOREIGN KEY (`village_id`) REFERENCES `chai_villages` (`id`),
	CONSTRAINT `fk_m_staff_f_village_staff_assignment_history` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
