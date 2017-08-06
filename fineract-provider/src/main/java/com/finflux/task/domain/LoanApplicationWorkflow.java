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
public class LoanApplicationWorkflow implements WorkflowCreator
{
	private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
	private final TaskPlatformWriteService taskPlatformWriteService;
	@Autowired
	public LoanApplicationWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
			final TaskPlatformWriteService taskPlatformWriteService) 
	{
		this.taskConfigEntityTypeMappingRepository=taskConfigEntityTypeMappingRepository;
		this.taskPlatformWriteService=taskPlatformWriteService;
	}
	@Override
	public Boolean createWorkFlow(WorkflowDTO workflowDTO)
	{
		Long loanProductId = workflowDTO.getLoanProduct().getId();
        final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
				.findOneByEntityTypeAndEntityId(TaskConfigEntityType.LOANPRODUCT.getValue(), loanProductId);
        if (taskConfigEntityTypeMapping != null) {
			final Long loanApplicationId =workflowDTO.getLoanApplicationReference().getId();
			final Long clientId = workflowDTO.getLoanApplicationReference().getClient().getId();
			Client client = workflowDTO.getLoanApplicationReference().getClient();
			final Map<TaskConfigKey, String> map = new HashMap<>();
			map.put(TaskConfigKey.CLIENT_ID, String.valueOf(clientId));
			map.put(TaskConfigKey.LOANAPPLICATION_ID, String.valueOf(loanApplicationId));
			AppUser assignedTo=null;
			Date dueDate=null;
			Date dueTime = null;
			String description = workflowDTO.getLoanProduct().getProductName() + " application #" + workflowDTO.getLoanApplicationReference().getLoanApplicationReferenceNo() + " for "
	                + WordUtils.capitalizeFully(client.getDisplayName())+"(" + client.getId()+") in  " + WordUtils.capitalizeFully(client.getOfficeName()) + "| Amount: "+
	                workflowDTO.getLoanApplicationReference().getLoanAmountRequested();
			this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
					TaskEntityType.LOAN_APPLICATION, loanApplicationId, workflowDTO.getLoanApplicationReference().getClient(),assignedTo,dueDate,
					workflowDTO.getLoanApplicationReference().getClient().getOffice(), map, description, dueTime);
            return true;
		}
        return false;
	}
	
}
