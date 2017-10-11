ALTER TABLE `f_task_action_group`
	ADD COLUMN `identifier` VARCHAR(50) NULL DEFAULT NULL AFTER `id`;
	
INSERT INTO `f_task_action_group` (`identifier`) VALUES ('adhoctaskgroup');

INSERT INTO f_task_action (action,action_group_id) VALUES (1,
(
SELECT f.id
FROM f_task_action_group f
WHERE f.identifier='adhoctaskgroup'));
	

