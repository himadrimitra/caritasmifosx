package com.finflux.workflow.execution.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_loan_application_workflow_execution")
public class LoanApplicationWorkflowExecution extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "loan_application_id", nullable = false)
    private Long loanApplicationId;

    @Column(name = "workflow_execution_id", nullable = false)
    private Long workflowExecutionId;


    protected LoanApplicationWorkflowExecution() {}

    private LoanApplicationWorkflowExecution(final Long loanApplicationId, final Long workflowExecutionId) {
        this.loanApplicationId = loanApplicationId;
        this.workflowExecutionId = workflowExecutionId;
    }

    public static LoanApplicationWorkflowExecution create(final Long loanApplicationId, final Long workflowExecutionId) {
        return new LoanApplicationWorkflowExecution(loanApplicationId, workflowExecutionId);
    }

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Long getWorkflowExecutionId() {
        return workflowExecutionId;
    }

    public void setWorkflowExecutionId(Long workflowExecutionId) {
        this.workflowExecutionId = workflowExecutionId;
    }
}