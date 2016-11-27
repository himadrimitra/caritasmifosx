package com.finflux.workflow.configuration.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.useradministration.domain.AppUser;

@Entity
@Table(name = "f_workflow_step")
public class WorkflowStep extends AbstractAuditableCustom<AppUser, Long> {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name", nullable = true)
    private String shortName;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "workflow_id", nullable = false)
    private Long workflowId;

    @Column(name = "step_order", length = 3, nullable = true)
    private Integer stepOrder;

    @Column(name = "criteria_id", nullable = true)
    private Long criteriaId;

    @Column(name = "approval_logic", length = 512)
    private String approvalLogic;

    @Column(name = "rejection_logic", length = 512)
    private String rejectionLogic;

    @Column(name = "config_values", nullable = true)
    private String configValues;

    @Column(name = "action_group_id", nullable = true)
    private Long actionGroupId;

    protected WorkflowStep() {}

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public Long getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getConfigValues() {
        return configValues;
    }

    public void setConfigValues(String configValues) {
        this.configValues = configValues;
    }

    public String getApprovalLogic() {
        return approvalLogic;
    }

    public void setApprovalLogic(String approvalLogic) {
        this.approvalLogic = approvalLogic;
    }

    public String getRejectionLogic() {
        return rejectionLogic;
    }

    public void setRejectionLogic(String rejectionLogic) {
        this.rejectionLogic = rejectionLogic;
    }

    public Long getActionGroupId() {
        return actionGroupId;
    }

    public void setActionGroupId(Long actionGroupId) {
        this.actionGroupId = actionGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}