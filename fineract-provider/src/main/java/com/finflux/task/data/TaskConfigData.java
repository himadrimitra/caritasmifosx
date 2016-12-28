package com.finflux.task.data;

import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class TaskConfigData {

    private Long id;
    private Long parentId;
    private String name;
    private String shortName;
    private EnumOptionData type;
    private TaskActivityData taskActivity;
    private Integer order;
    private Long criteriaId;
    private String approvalLogic;
    private String rejectionLogic;
    private Map<String, String> configValues;
    private Long actionGroupId;

    private TaskConfigData(final Long id, final Long parentId, final String name, final String shortName, final EnumOptionData taskType,
            final TaskActivityData taskActivity, final Integer order, final Long criteriaId, final String approvalLogic,
            final String rejectionLogic, final Map<String, String> configValues, final Long actionGroupId) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.shortName = shortName;
        this.type = taskType;
        this.taskActivity = taskActivity;
        this.order = order;
        this.criteriaId = criteriaId;
        this.approvalLogic = approvalLogic;
        this.rejectionLogic = rejectionLogic;
        this.configValues = configValues;
        this.actionGroupId = actionGroupId;
    }

    public static TaskConfigData instance(final Long id, final Long parentId, final String name, final String shortName,
            final EnumOptionData type, final TaskActivityData taskActivity, final Integer order, final Long criteriaId,
            final String approvalLogic, final String rejectionLogic, final Map<String, String> configValues, final Long actionGroupId) {
        return new TaskConfigData(id, parentId, name, shortName, type, taskActivity, order, criteriaId, approvalLogic, rejectionLogic,
                configValues, actionGroupId);
    }

    public static TaskConfigData lookup(final Long id, final Long parentId, final String name, final EnumOptionData type) {
        final String shortName = null;
        final TaskActivityData taskActivity = null;
        final Integer order = null;
        final Long criteriaId = null;
        final String approvalLogic = null;
        final String rejectionLogic = null;
        final Map<String, String> configValues = null;
        final Long actionGroupId = null;
        return new TaskConfigData(id, parentId, name, shortName, type, taskActivity, order, criteriaId, approvalLogic, rejectionLogic,
                configValues, actionGroupId);
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

    public EnumOptionData getType() {
        return this.type;
    }

    public void setType(EnumOptionData type) {
        this.type = type;
    }

    public TaskActivityData getTaskActivity() {
        return this.taskActivity;
    }

    public void setTaskActivity(TaskActivityData taskActivity) {
        this.taskActivity = taskActivity;
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

    public Long getActionGroupId() {
        return this.actionGroupId;
    }

    public void setActionGroupId(Long actionGroupId) {
        this.actionGroupId = actionGroupId;
    }

}