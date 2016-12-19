package com.finflux.task.data;

import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.portfolio.client.data.ClientData;

import java.util.Map;

public class TaskInfoData {

    private Long taskId;
    private Long parentTaskId;
    private String taskName;
    private String taskStatus;
    private String currentAction;
    private Long assignedId;
    private String assignedTo;
    private Integer entityTypeId;
    private String entityType;
    private Long entityId;
    private String nextActionUrl;
    private ClientData clientData;
    private OfficeData officeData;
    private Map<String,String> configValues;

    private TaskInfoData(final Long taskId, final Long parentTaskId, final String taskName, final String taskStatus,
            final String currentAction, final Long assignedId, final String assignedTo, final Integer entityTypeId,
            final String entityType, final Long entityId, final String nextActionUrl, final ClientData clientData,
            final OfficeData officeData, final Map<String,String> configValues) {
        this.taskId = taskId;
        this.parentTaskId = parentTaskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.currentAction = currentAction;
        this.assignedId = assignedId;
        this.assignedTo = assignedTo;
        this.entityTypeId = entityTypeId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.nextActionUrl = nextActionUrl;
        this.clientData = clientData;
        this.officeData = officeData;
        this.configValues = configValues;
    }

    public static TaskInfoData instance(final Long taskId, final Long parentTaskId, final String taskName, final String taskStatus,
            final String currentAction, final Long assignedId, final String assignedTo, final Integer entityTypeId,
            final String entityType, final Long entityId, final String nextActionUrl, final ClientData clientData,
            final OfficeData officeData, final Map<String,String> configValues) {
        return new TaskInfoData(taskId, parentTaskId, taskName, taskStatus, currentAction, assignedId, assignedTo, entityTypeId,
                entityType, entityId, nextActionUrl, clientData, officeData, configValues);
    }

    public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getParentTaskId() {
        return this.parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return this.taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
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

    public ClientData getClientData() {
        return this.clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public OfficeData getOfficeData() {
        return this.officeData;
    }

    public void setOfficeData(OfficeData officeData) {
        this.officeData = officeData;
    }

}