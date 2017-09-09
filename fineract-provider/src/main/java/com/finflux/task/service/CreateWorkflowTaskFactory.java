package com.finflux.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.task.data.TaskConfigEntityType;
import com.finflux.task.domain.ClientBankAccountWorkflow;
import com.finflux.task.domain.ClientOnboardingWorkflow;
import com.finflux.task.domain.DistrictOnboardingWorkflow;
import com.finflux.task.domain.LoanApplicantWorkflow;
import com.finflux.task.domain.LoanApplicationWorkflow;
import com.finflux.task.domain.LoanCoApplicantWorkflow;
import com.finflux.task.domain.OfficeOnboardingWorkflow;
import com.finflux.task.domain.VillageOnboardingWorkflow;
import com.finflux.task.domain.WorkflowCreator;

@Component
public class CreateWorkflowTaskFactory {

    private final ClientOnboardingWorkflow clientOnboardingWorkflow;
    private final LoanApplicationWorkflow loanApplicationWorkflow;
    private final LoanApplicantWorkflow loanApplicantWorkflow;
    private final LoanCoApplicantWorkflow loanCoApplicantWorkflow;
    private final ClientBankAccountWorkflow clientBankAccountWorkflow;
    private final VillageOnboardingWorkflow villageOnboradingWorkflow;
    private final DistrictOnboardingWorkflow districtOnboardingWorkflow;
    private final OfficeOnboardingWorkflow officeOnboardingWorkflow;

    @Autowired
    public CreateWorkflowTaskFactory(final ClientOnboardingWorkflow clientOnboardingWorkflow,
            final LoanApplicationWorkflow loanApplicationWorkflow, final LoanApplicantWorkflow loanApplicantWorkflow,
            final LoanCoApplicantWorkflow loanCoApplicantWorkflow, ClientBankAccountWorkflow clientBankAccountWorkflow,
            final VillageOnboardingWorkflow villageOnboradingWorkflow, final DistrictOnboardingWorkflow districtOnboardingWorkflow,
            final OfficeOnboardingWorkflow officeOnboardingWorkflow) {
        this.clientOnboardingWorkflow = clientOnboardingWorkflow;
        this.loanApplicationWorkflow = loanApplicationWorkflow;
        this.loanApplicantWorkflow = loanApplicantWorkflow;
        this.loanCoApplicantWorkflow = loanCoApplicantWorkflow;
        this.clientBankAccountWorkflow = clientBankAccountWorkflow;
        this.villageOnboradingWorkflow = villageOnboradingWorkflow;
        this.districtOnboardingWorkflow = districtOnboardingWorkflow;
        this.officeOnboardingWorkflow = officeOnboardingWorkflow;
    }

    public WorkflowCreator create(TaskConfigEntityType taskConfigEntityType) {
        WorkflowCreator workflowCreator = null;
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.CLIENTONBOARDING.getValue())) {
            workflowCreator = this.clientOnboardingWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOANPRODUCT.getValue())) {
            workflowCreator = this.loanApplicationWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.LOAN_APPLICANTION.getValue())) {
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
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.DISTRICTONBOARDING.getValue())) {
            workflowCreator = this.districtOnboardingWorkflow;
        }
        if (taskConfigEntityType.getValue().equals(TaskConfigEntityType.OFFICEONBOARDING.getValue())) {
            workflowCreator = this.officeOnboardingWorkflow;
        }
        return workflowCreator;
    }
}
