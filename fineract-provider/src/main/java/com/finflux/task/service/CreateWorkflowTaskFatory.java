package com.finflux.task.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;

@Component
public class CreateWorkflowTaskFatory 
{
	@Autowired
	public CreateWorkflowTaskFatory()
	{
		
	}
	
	public void createWorkflow(TaskConfigEntityType taskConfigEntityType,Map<String,Object> objectMap)
	{
		if(taskConfigEntityType.getValue().equals(TaskConfigEntityType.CLIENTONBOARDING.getValue()))
		{
			
		}
	}
}
