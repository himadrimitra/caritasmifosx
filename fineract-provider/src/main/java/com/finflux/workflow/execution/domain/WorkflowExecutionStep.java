package com.finflux.workflow.execution.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_workflow_execution_step")
public class WorkflowExecutionStep extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "workflow_execution_id", nullable = false)
    private Long workflowExecutionId;

    @Column(name = "workflow_step_id", nullable = false)
    private Long workflowStepId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "assigned_to", nullable = true)
    private Long assignedTo;

    @Column(name = "criteria_action", nullable = true)
    private Integer criteriaAction;

    @Column(name = "criteria_result", nullable = true)
    private String criteriaResult;

    protected WorkflowExecutionStep() {}

    private WorkflowExecutionStep(final Long workflowExecutionId, final Long workflowStepId, final Integer status) {
        this.workflowExecutionId = workflowExecutionId;
        this.workflowStepId = workflowStepId;
        this.status = status;
    }

    public static WorkflowExecutionStep create(final Long workflowExecutionId, final Long workflowStepId, final Integer status) {
        return new WorkflowExecutionStep(workflowExecutionId, workflowStepId, status);
    }

    public Long getWorkflowExecutionId() {
        return workflowExecutionId;
    }

    public void setWorkflowExecutionId(Long workflowExecutionId) {
        this.workflowExecutionId = workflowExecutionId;
    }

    public Long getWorkflowStepId() {
        return workflowStepId;
    }

    public void setWorkflowStepId(Long workflowStepId) {
        this.workflowStepId = workflowStepId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCriteriaAction() {
        return criteriaAction;
    }

    public void setCriteriaAction(Integer criteriaAction) {
        this.criteriaAction = criteriaAction;
    }

    public String getCriteriaResult() {
        return criteriaResult;
    }

    public void setCriteriaResult(String criteriaResult) {
        this.criteriaResult = criteriaResult;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }
}