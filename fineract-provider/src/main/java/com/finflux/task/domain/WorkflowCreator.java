package com.finflux.task.domain;

import com.finflux.task.data.WorkflowDTO;

public interface WorkflowCreator 
{
	Boolean createWorkFlow(WorkflowDTO workflowDTO);
}
