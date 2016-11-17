package com.finflux.workflow.configuration.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "f_workflow_step_action_role")
public class WorkflowStepActionRole extends AbstractPersistable<Long> {

    @Column(name = "workflow_step_action_id", nullable = false)
    private Long workflowStepActionId;

    @Column(name = "role_id", nullable = true)
    private Long roleId;

    protected WorkflowStepActionRole() {}

    public Long getWorkflowStepActionId() {
        return workflowStepActionId;
    }

    public void setWorkflowStepActionId(Long workflowStepActionId) {
        this.workflowStepActionId = workflowStepActionId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}