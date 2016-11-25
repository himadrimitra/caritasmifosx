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

    @Column(name = "entity_type", nullable = false)
    private Integer entityType;

    @Column(name = "entity_id", nullable = true)
    private Long entityId;

    @Column(name = "execution_status", nullable = false)
    private Integer executionStatus;

    protected WorkflowExecution() {}

    private WorkflowExecution(final Long workflowId, final Integer entityType, final Long entityId) {
        this.workflowId = workflowId;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public static WorkflowExecution create(final Long workflowId, final Integer entityType, final Long entityId) {
        return new WorkflowExecution(workflowId, entityType, entityId);
    }
}