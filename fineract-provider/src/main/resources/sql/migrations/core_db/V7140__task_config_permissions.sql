INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('task', 'READ_TASK_CONFIG', 'TASK_CONFIG', 'READ', '0');

-- Actions
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
('task', 'ACTION_ACTIVITYCOMPLETE_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_ACTIVITYCOMPLETE', '0'),
('task', 'ACTION_CRITERIACHECK_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_CRITERIACHECK', '0'),
('task', 'ACTION_REVIEW_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_REVIEW', '0'),
('task', 'ACTION_APPROVE_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_APPROVE', '0'),
('task', 'ACTION_REJECT_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_REJECT', '0'),
('task', 'ACTION_SKIP_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_SKIP', '0'),
('task', 'ACTION_TASKEDIT_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_TASKEDIT', '0'),
('task', 'ACTION_TASKVIEW_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_TASKVIEW', '0'),
('task', 'ACTION_STARTOVER_TASK_EXECUTION', 'TASK_EXECUTION', 'ACTION_STARTOVER', '0');

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('task', 'READ_TASK_EXECUTION', 'TASK_EXECUTION', 'READ', 0);

INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('task', 'READ_TASK_EXECUTION_ACTIONLOG', 'TASK_EXECUTION_ACTIONLOG', 'READ', 0);

-- Notes
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('task', 'READ_TASK_EXECUTION_NOTE', 'TASK_EXECUTION_NOTE', 'READ', 0),
  ('task', 'CREATE_TASK_EXECUTION_NOTE', 'TASK_EXECUTION_NOTE', 'CREATE', 0);

-- Assign UnAssign
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES
  ('task', 'ASSIGN_TASK', 'TASK', 'ASSIGN', 0),
  ('task', 'UNASSIGN_TASK', 'TASK', 'UNASSIGN', 0);

  UPDATE m_permission set grouping = 'task' where grouping='taskmangement';