
DROP TABLE IF EXISTS `f_task_note`;

CREATE TABLE `f_task_note` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`task_id` BIGINT(20) NOT NULL,
	`note` VARCHAR(1024) NOT NULL,
  `createdby_id` BIGINT(20) NOT NULL,
  `created_date` DATETIME NOT NULL,
  `lastmodifiedby_id` BIGINT(20) NOT NULL,
  `lastmodified_date` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
  CONSTRAINT `FK_f_task_note_task_id` FOREIGN KEY (`task_id`) REFERENCES `f_task` (`id`),
  CONSTRAINT `FK_f_task_note_createdby_id` FOREIGN KEY (`createdby_id`) REFERENCES `m_appuser` (`id`),
  CONSTRAINT `FK_f_task_note_lastmodifiedby_id` FOREIGN KEY (`lastmodifiedby_id`) REFERENCES `m_appuser` (`id`)
);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('taskmangement', 'ACTION_TASK', 'TASK', 'ACTION', 0),
  ('taskmangement', 'READ_TASK', 'TASK', 'READ', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('taskmangement', 'CREATE_TASK_NOTE', 'TASK_NOTE', 'CREATE', 0),
  ('taskmangement', 'READ_TASK_NOTE', 'TASK_NOTE', 'READ', 0);