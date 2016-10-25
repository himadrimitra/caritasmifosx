package com.finflux.workflow.configuration.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_loan_product_workflow")
public class LoanProductWorkflow extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "loan_product_id", nullable = false)
    private Long loanProductId;

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;


    protected LoanProductWorkflow() {}

    private LoanProductWorkflow(final Long loanProductId, final Long workflowId) {
        this.loanProductId = loanProductId;
        this.workflowId = workflowId;
    }

    public static LoanProductWorkflow create(final Long loanProductId, final Long workflowId) {
        return new LoanProductWorkflow(loanProductId,workflowId);
    }

    public Long getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Long loanProductId) {
        this.loanProductId = loanProductId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }
}