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
import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.data.WorkflowDTO;
import com.finflux.task.service.TaskPlatformWriteService;

@Component
public class LoanApplicantWorkflow implements WorkflowCreator 
{
	private final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository;
	private final TaskPlatformWriteService taskPlatformWriteService;
	@Autowired
	public LoanApplicantWorkflow(final TaskConfigEntityTypeMappingRepository taskConfigEntityTypeMappingRepository,
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
		Long loanProductId = loanProduct.getId();
        final TaskConfigEntityTypeMapping taskConfigEntityTypeMapping = this.taskConfigEntityTypeMappingRepository
                .findOneByEntityTypeAndEntityId(TaskConfigEntityType.LOAN_APPLICANTION.getValue(), loanProductId);
        if (taskConfigEntityTypeMapping != null) {
            final Long loanApplicationId = loanApplicationReference.getId();
            final Long clientId = loanApplicationReference.getClient().getId();
            Client client = loanApplicationReference.getClient();
            final Map<TaskConfigKey, String> map = new HashMap<>();
            map.put(TaskConfigKey.CLIENT_ID, String.valueOf(clientId));
            map.put(TaskConfigKey.LOANAPPLICATION_ID, String.valueOf(loanApplicationId));
            AppUser assignedTo=null;
            Date dueDate=null;
            Date dueTime = null;
            String description = loanProduct.getProductName() + " Main Applicant: "+WordUtils.capitalizeFully(client.getDisplayName())+"(" + client.getId()+") for loan application #" +
                    loanApplicationReference.getLoanApplicationReferenceNo() +" in  " + WordUtils.capitalizeFully(client.getOfficeName()) + "| Amount: "+
                    loanApplicationReference.getLoanAmountRequested();
            final StringBuilder shortDescription = new StringBuilder();
            shortDescription.append("On-boarding for: ");
            shortDescription.append(WordUtils.capitalizeFully(client.getDisplayName()));
            shortDescription.append("| Amount: ").append(loanApplicationReference.getLoanAmountRequested());
            this.taskPlatformWriteService.createTaskFromConfig(taskConfigEntityTypeMapping.getTaskConfigId(),
                    TaskEntityType.LOAN_APPLICATION_APPLICANT, loanApplicationId, loanApplicationReference.getClient(), assignedTo, dueDate,
                    loanApplicationReference.getClient().getOffice(), map, description, shortDescription.toString(), dueTime);
            return true;
        }
        return false;
	}
	
}
