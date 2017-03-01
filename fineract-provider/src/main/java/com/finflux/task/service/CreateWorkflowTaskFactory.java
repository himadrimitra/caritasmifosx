package com.finflux.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.domain.ClientOnboardingWorkflow;
import com.finflux.task.domain.LoanApplicantWorkflow;
import com.finflux.task.domain.LoanApplicationWorkflow;
import com.finflux.task.domain.LoanCoApplicantWorkflow;
import com.finflux.task.domain.WorkflowCreator;

@Component
public class CreateWorkflowTaskFactory 
{
	private final ClientOnboardingWorkflow clientOnboardingWorkflow;
	private final LoanApplicationWorkflow loanApplicationWorkflow;
	private final LoanApplicantWorkflow loanApplicantWorkflow;
	private final LoanCoApplicantWorkflow loanCoApplicantWorkflow;
	@Autowired
	public CreateWorkflowTaskFactory(final ClientOnboardingWorkflow clientOnboardingWorkflow,
			final LoanApplicationWorkflow loanApplicationWorkflow,
			final LoanApplicantWorkflow loanApplicantWorkflow,
			final LoanCoApplicantWorkflow loanCoApplicantWorkflow)
	{
		this.clientOnboardingWorkflow=clientOnboardingWorkflow;
		this.loanApplicationWorkflow=loanApplicationWorkflow;
		this.loanApplicantWorkflow=loanApplicantWorkflow;
		this.loanCoApplicantWorkflow=loanCoApplicantWorkflow;
	}
	public WorkflowCreator create(TaskConfigEntityType taskConfigEntityType)
	{
		WorkflowCreator workflowCreator=null;
		if(taskConfigEntityType.getValue().equals(TaskConfigEntityType.CLIENTONBOARDING.getValue()))
		{
			workflowCreator=this.clientOnboardingWorkflow;
		}
		if(taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT.getValue()))
		{
			workflowCreator=this.loanApplicationWorkflow;
		}
		if(taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT_APPLICANT.getValue()))
		{
			workflowCreator=this.loanApplicantWorkflow;
		}
		if(taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT_COAPPLICANT.getValue()))
		{
			workflowCreator=this.loanCoApplicantWorkflow;
		}
		return workflowCreator;
	}
}
