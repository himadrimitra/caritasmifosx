package com.finflux.task.service;

import java.util.Date;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.loanapplicationreference.domain.LoanApplicationReference;
import com.finflux.loanapplicationreference.domain.LoanCoApplicant;
import com.finflux.task.data.TaskConfigKey;
import com.finflux.task.data.TaskEntityType;
import com.finflux.task.domain.TaskActivity;

public interface TaskPlatformWriteService {

	Boolean createClientOnboardingWorkflow(Client client);
	Boolean createLoanApplicationWorkflow(LoanApplicationReference loanApplicationReference, LoanProduct loanProduct);
	Boolean createLoanApplicationApplicantWorkflow(LoanApplicationReference loanApplicationReference, LoanProduct loanProduct);
	Boolean createLoanApplicationCoApplicantWorkflow(Client client,LoanCoApplicant coApplicant, LoanApplicationReference loanApplicationReference, LoanProduct loanProduct);
    Long createTaskFromConfig(final Long taskConfigId, final TaskEntityType entityType, final Long entityId, final Client client,final AppUser assignedTo,final Date dueDate,
							  final Office office, final Map<TaskConfigKey, String> configValues, String description);

    Long createSingleTask(TaskActivity taskActivity, String title, Office office, Map<TaskConfigKey, String> map,
						  Long actionGroupId);

	CommandProcessingResult assignTaskToMe(Long aLong, String json);

	CommandProcessingResult unassignTaskFromMe(Long aLong, String json);
}
