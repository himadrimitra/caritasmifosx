package com.finflux.task.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanCoApplicant;
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;

@Component
public class LoanCoApplicantWorkflow implements WorkflowCreator
{
	private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
	private final TaskPlatformWriteService taskPlatformWriteService;
	@Autowired
	public LoanCoApplicantWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
			final TaskPlatformWriteService taskPlatformWriteService) 
	{
		this.taskConfigEntityTypeMappingRepository=taskConfigEntityTypeMappingRepository;
		this.taskPlatformWriteService=taskPlatformWriteService;
	}
	@Override
	public Boolean createWorkFlow(WorkflowDTO workflowDTO)
	{
		LoanProduct loanProduct=workflowDTO.getLoanProduct();
		LoanApplicationReference loanApplicationReference=workflowDTO.getLoanApplicationReference();
		LoanCoApplicant coApplicant=workflowDTO.getLoanCoApplicant();
		Client client=workflowDTO.getClient();
		Long loanProductId = loanProduct.getId();
        final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.LOANPRODUCT_COAPPLICANT.getValue(), loanProductId);
        if (taskConfigEntityTypeMapping != null) {
            final Long loanApplicationId = loanApplicationReference.getId();
            final Long clientId = client.getId();
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.LOANAPPLICATION_COAPPLICANT_ID, String.valueOf(coApplicant.getId()));
            map.put(TaskConfigKey.CLIENT_ID, String.valueOf(clientId));
            map.put(TaskConfigKey.LOANAPPLICATION_ID, String.valueOf(loanApplicationId));
            AppUser assignedTo = null;
            Date dueDate = null;
            Date dueTime = null;
            String description = loanProduct.getProductName() + " Coapplicant: " + WordUtils.capitalizeFully(client.getDisplayName()) + "("
                    + client.getId() + ") for loan application #" + loanApplicationReference.getLoanApplicationReferenceNo() + " in  "
                    + WordUtils.capitalizeFully(client.getOfficeName()) + "| Amount: " + loanApplicationReference.getLoanAmountRequested();
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
                    TaskEntityType.LOAN_APPLICATION_COAPPLICANT, coApplicant.getId(), client, assignedTo, dueDate, client.getOffice(), map,
                    description, dueTime);
            return true;
        }
        return false;
    }
}
