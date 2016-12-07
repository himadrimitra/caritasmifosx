package com.finflux.task.execution.data;

public class WorkFlowStepActionData {

    private Long stepId;
    private String stepName;
    private String stepStatus;
    private String currentAction;
    private Long assignedId;
    private String assignedTo;
    private Integer entityTypeId;
    private String entityType;
    private Long entityId;
    private String nextActionUrl;

    private WorkFlowStepActionData(final Long stepId, final String stepName, final String stepStatus, final String currentAction,
            final Long assignedId, final String assignedTo, final Integer entityTypeId, final String entityType, final Long entityId,
            final String nextActionUrl) {
        this.stepId = stepId;
        this.stepName = stepName;
        this.stepStatus = stepStatus;
        this.currentAction = currentAction;
        this.assignedId = assignedId;
        this.assignedTo = assignedTo;
        this.entityTypeId = entityTypeId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.nextActionUrl = nextActionUrl;
    }

    public static WorkFlowStepActionData instance(final Long stepId, final String stepName, final String stepStatus,
            final String currentAction, final Long assignedId, final String assignedTo, final Integer entityTypeId,
            final String entityType, final Long entityId, final String nextActionUrl) {
        return new WorkFlowStepActionData(stepId, stepName, stepStatus, currentAction, assignedId, assignedTo, entityTypeId, entityType,
                entityId, nextActionUrl);
    }

    public Long getStepId() {
        return this.stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public String getStepName() {
        return this.stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepStatus() {
        return this.stepStatus;
    }

    public void setStepStatus(String stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getCurrentAction() {
        return this.currentAction;
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

    public Long getAssignedId() {
        return this.assignedId;
    }

    public void setAssignedId(Long assignedId) {
        this.assignedId = assignedId;
    }

    public String getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Integer getEntityTypeId() {
        return this.entityTypeId;
    }

    public void setEntityTypeId(Integer entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getNextActionUrl() {
        return this.nextActionUrl;
    }

    public void setNextActionUrl(String nextActionUrl) {
        this.nextActionUrl = nextActionUrl;
    }
}