package com.finflux.workflow.execution.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_workflow_execution")
public class WorkflowExecution extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Column(name = "execution_status", nullable = false)
    private Integer executionStatus;

    protected WorkflowExecution() {}

    private WorkflowExecution(final Long workflowId) {
        this.workflowId = workflowId;
    }

    public static WorkflowExecution create(final Long workflowId) {
        return new WorkflowExecution(workflowId);
    }
}