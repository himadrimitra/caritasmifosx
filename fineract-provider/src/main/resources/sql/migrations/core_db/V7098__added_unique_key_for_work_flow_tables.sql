ALTER TABLE `f_task`
	ADD UNIQUE INDEX `UQ_f_task` (`name`);
	
ALTER TABLE `f_workflow`
	ADD UNIQUE INDEX `UQ_f_workflow_name` (`name`);
	
ALTER TABLE `f_workflow_step`
	ADD UNIQUE INDEX `UQ_f_workflow_step` (`name`, `task_id`, `workflow_id`);