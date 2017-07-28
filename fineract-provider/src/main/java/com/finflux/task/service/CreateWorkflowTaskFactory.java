package com.finflux.task.service;

import com.finflux.task.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;

@Component
public class CreateWorkflowTaskFactory {

    private final ClientOnboardingWorkflow clientOnboardingWorkflow;
    private final LoanApplicationWorkflow loanApplicationWorkflow;
    private final LoanApplicantWorkflow loanApplicantWorkflow;
    private final LoanCoApplicantWorkflow loanCoApplicantWorkflow;
    private final ClientBankAccountWorkflow clientBankAccountWorkflow;
    private final VillageOnboardingWorkflow villageOnboradingWorkflow;

    @Autowired
    public CreateWorkflowTaskFactory(final ClientOnboardingWorkflow clientOnboardingWorkflow,
            final LoanApplicationWorkflow loanApplicationWorkflow, final LoanApplicantWorkflow loanApplicantWorkflow,
            final LoanCoApplicantWorkflow loanCoApplicantWorkflow, ClientBankAccountWorkflow clientBankAccountWorkflow,
            final VillageOnboardingWorkflow villageOnboradingWorkflow) {
        this.clientOnboardingWorkflow = clientOnboardingWorkflow;
        this.loanApplicationWorkflow = loanApplicationWorkflow;
        this.loanApplicantWorkflow = loanApplicantWorkflow;
        this.loanCoApplicantWorkflow = loanCoApplicantWorkflow;
        this.clientBankAccountWorkflow = clientBankAccountWorkflow;
        this.villageOnboradingWorkflow = villageOnboradingWorkflow ;
    }

    public WorkflowCreator create(TaskConfigEntityType taskConfigEntityType) {
        WorkflowCreator workflowCreator = null;
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.CLIENTONBOARDING.getValue())) {
            workflowCreator = this.clientOnboardingWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT.getValue())) {
            workflowCreator = this.loanApplicationWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT_APPLICANT.getValue())) {
            workflowCreator = this.loanApplicantWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT_COAPPLICANT.getValue())) {
            workflowCreator = this.loanCoApplicantWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.CLIENTBANKACCOUNT.getValue())) {
            workflowCreator = this.clientBankAccountWorkflow;
        }
        if(taskConfigEntityType.getValue().equals(TaskConfigEntityType.VILLAGEONBOARDING.getValue())) {
            workflowCreator = this.villageOnboradingWorkflow ;
        }
        return workflowCreator;
    }
}
