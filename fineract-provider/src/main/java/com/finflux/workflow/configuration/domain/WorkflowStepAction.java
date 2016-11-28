package com.finflux.workflow.configuration.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_workflow_step_action")
public class WorkflowStepAction extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "action_group_id", nullable = false)
    private Long actionGroupId;

    @Column(name = "action", nullable = false)
    private Integer action;

    protected WorkflowStepAction() {}

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Long getActionGroupId() {
        return actionGroupId;
    }

    public void setActionGroupId(Long actionGroupId) {
        this.actionGroupId = actionGroupId;
    }
}