package com.finflux.task.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;

@Component
public class ClientOnboardingWorkflow implements WorkflowCreator
{
	private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
	private final TaskPlatformWriteService taskPlatformWriteService;
	@Autowired
	public ClientOnboardingWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
			final TaskPlatformWriteService taskPlatformWriteService) 
	{
		this.taskConfigEntityTypeMappingRepository=taskConfigEntityTypeMappingRepository;
		this.taskPlatformWriteService=taskPlatformWriteService;
	}
	@Override
	public Boolean createWorkFlow(WorkflowDTO workflowDTO) 
	{
		final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.CLIENTONBOARDING.getValue(),-1L);
        Client newClient=workflowDTO.getClient();
		if (taskConfigEntityTypeMapping != null) 
		{
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.CLIENT_ID, String.valueOf(newClient.getId()));
            AppUser assignedTo=null;
            Date dueDate=null;
            String description = newClient.getId()+"- New Client "+ WordUtils.capitalizeFully(newClient.getFirstname()+" "+newClient.getLastname())+" for office - "+ WordUtils.capitalizeFully(newClient.getOfficeName());
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
                    TaskEntityType.CLIENT_ONBOARDING, newClient.getId(),newClient,assignedTo,dueDate,
                    newClient.getOffice(), map, description);
            return true;
		}
		return false;// TODO Auto-generated method stub
	}

}
