package com.finflux.task.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.ruleengine.execution.data.EligibilityResult;

import org.apache.fineract.useradministration.data.RoleData;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class TaskExecutionData implements Serializable{

    private Long id;
    private Long parentId;
    private String name;
    private String shortName;
    private EnumOptionData entityType;
    private Long entityId;
    private EnumOptionData status;
    private EnumOptionData priority;
    private LocalDate dueDate;
    private EnumOptionData currentAction;
    private Long assignedToId;
    private String assignedTo;
    private List<RoleData> assignedRoles;
    private Integer order;
    private Long criteriaId;
    private String approvalLogic;
    private String rejectionLogic;
    private Map<String, String> configValues;
    private Long clientId;
    private String clientName;
    private Long officeId;
    private String officeName;
    private Long actionGroupId;
    private EligibilityResult criteriaResult;
    private Integer criteriaActionId;
    private List<EnumOptionData> possibleActions;
    private TaskConfigData taskConfig;
    private TaskActivityData taskActivity;
    private EnumOptionData taskType;
    private LocalDate createdOn;
    private String description;
    private String shortDescription;
    private TaskExecutionData activeTaskData;
    private LocalTime dueTime;

    private TaskExecutionData(final Long id, final Long parentId, final String name, final String shortName,
            final EnumOptionData entityType, final Long entityId, final EnumOptionData status, final EnumOptionData priority,
            final LocalDate dueDate, final EnumOptionData currentAction, final Long assignedToId, final String assignedTo,
            final Integer order, final Long criteriaId, final String approvalLogic, final String rejectionLogic,
            final Map<String, String> configValues, final Long clientId, final String clientName, final Long officeId,
            final String officeName, final Long actionGroupId, final EligibilityResult criteriaResult, final Integer criteriaActionId,
            final List<EnumOptionData> possibleActions, final EnumOptionData taskType, final LocalDate createdOn, final String description,
            final String shortDescription, final LocalTime dueTime) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.shortName = shortName;
        this.entityType = entityType;
        this.entityId = entityId;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.currentAction = currentAction;
        this.assignedToId = assignedToId;
        this.assignedTo = assignedTo;
        this.order = order;
        this.criteriaId = criteriaId;
        this.approvalLogic = approvalLogic;
        this.rejectionLogic = rejectionLogic;
        this.configValues = configValues;
        this.clientId = clientId;
        this.clientName = clientName;
        this.officeId = officeId;
        this.officeName = officeName;
        this.actionGroupId = actionGroupId;
        this.criteriaResult = criteriaResult;
        this.criteriaActionId = criteriaActionId;
        this.possibleActions = possibleActions;
        this.taskType = taskType;
        this.createdOn = createdOn;
        this.description = description;
        this.shortDescription = shortDescription;
        this.dueTime = dueTime;
    }

    public static TaskExecutionData instance(final Long id, final Long parentId, final String name, final String shortName,
            final EnumOptionData entityType, final Long entityId, final EnumOptionData status, final EnumOptionData priority,
            final LocalDate dueDate, final EnumOptionData currentAction, final Long assignedToId, final String assignedTo,
            final Integer order, final Long criteriaId, final String approvalLogic, final String rejectionLogic,
            final Map<String, String> configValues, final Long clientId, final String clientName, final Long officeId,
            final String officeName, final Long actionGroupId, final EligibilityResult criteriaResult, final Integer criteriaActionId,
            final List<EnumOptionData> possibleActions, EnumOptionData taskType, final LocalDate createdOn, String description,
            final String shortDescription, final LocalTime dueTime) {
        return new TaskExecutionData(id, parentId, name, shortName, entityType, entityId, status, priority, dueDate, currentAction,
                assignedToId, assignedTo, order, criteriaId, approvalLogic, rejectionLogic, configValues, clientId, clientName, officeId,
                officeName, actionGroupId, criteriaResult, criteriaActionId, possibleActions, taskType, createdOn, description,
                shortDescription, dueTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public EnumOptionData getEntityType() {
        return this.entityType;
    }

    public void setEntityType(EnumOptionData entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public EnumOptionData getStatus() {
        return this.status;
    }

    public void setStatus(EnumOptionData status) {
        this.status = status;
    }

    public EnumOptionData getPriority() {
        return this.priority;
    }

    public void setPriority(EnumOptionData priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public EnumOptionData getCurrentAction() {
        return this.currentAction;
    }

    public void setCurrentAction(EnumOptionData currentAction) {
        this.currentAction = currentAction;
    }

    public Long getAssignedToId() {
        return this.assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Long getCriteriaId() {
        return this.criteriaId;
    }

    public void setCriteriaId(Long criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getApprovalLogic() {
        return this.approvalLogic;
    }

    public void setApprovalLogic(String approvalLogic) {
        this.approvalLogic = approvalLogic;
    }

    public String getRejectionLogic() {
        return this.rejectionLogic;
    }

    public void setRejectionLogic(String rejectionLogic) {
        this.rejectionLogic = rejectionLogic;
    }

    public Map<String, String> getConfigValues() {
        return this.configValues;
    }

    public void setConfigValues(Map<String, String> configValues) {
        this.configValues = configValues;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Long getActionGroupId() {
        return this.actionGroupId;
    }

    public void setActionGroupId(Long actionGroupId) {
        this.actionGroupId = actionGroupId;
    }

    public EligibilityResult getCriteriaResult() {
        return this.criteriaResult;
    }

    public void setCriteriaResult(EligibilityResult criteriaResult) {
        this.criteriaResult = criteriaResult;
    }

    public Integer getCriteriaActionId() {
        return this.criteriaActionId;
    }

    public void setCriteriaActionId(Integer criteriaActionId) {
        this.criteriaActionId = criteriaActionId;
    }

    public List<EnumOptionData> getPossibleActions() {
        return this.possibleActions;
    }

    public void setPossibleActions(List<EnumOptionData> possibleActions) {
        this.possibleActions = possibleActions;
    }

    public TaskConfigData getTaskConfig() {
        return this.taskConfig;
    }

    public void setTaskConfig(TaskConfigData taskConfig) {
        this.taskConfig = taskConfig;
    }

    public TaskActivityData getTaskActivity() {
        return this.taskActivity;
    }

    public void setTaskActivity(TaskActivityData taskActivity) {
        this.taskActivity = taskActivity;
    }

    public List<RoleData> getAssignedRoles() {
        return assignedRoles;
    }

    public void setAssignedRoles(List<RoleData> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }

    public LocalTime getDueTime() {
        return this.dueTime;
    }

}
