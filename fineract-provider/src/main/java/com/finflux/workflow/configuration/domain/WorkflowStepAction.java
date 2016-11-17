package com.finflux.workflow.configuration.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_workflow_step_action")
public class WorkflowStepAction extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "workflow_step_id", nullable = false)
    private Long workflowStepId;

    @Column(name = "action", nullable = false)
    private Integer action;

    protected WorkflowStepAction() {}

    public Long getWorkflowStepId() {
        return workflowStepId;
    }

    public void setWorkflowStepId(Long workflowStepId) {
        this.workflowStepId = workflowStepId;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

}