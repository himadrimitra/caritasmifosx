package com.finflux.task.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.useradministration.domain.AppUser;

import com.finflux.ruleengine.configuration.domain.RuleModel;

@Entity
@Table(name = "f_task")
public class Task extends AbstractAuditableCustom<AppUser, Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Task parent;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "short_name", length = 20, nullable = false)
    private String shortName;

    @Column(name = "entity_type", length = 3)
    private Integer entityType;

    @Column(name = "entity_id", length = 20)
    private Long entityId;

    @Column(name = "task_type", length = 3)
    private Integer taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_config_id")
    private TaskConfig taskConfig;

    @Column(name = "status", length = 3)
    private Integer status;

    @Column(name = "priority", length = 3)
    private Integer priority;

    @Column(name = "due_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDate;

    @Column(name = "current_action", length = 3)
    private Integer currentAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private AppUser assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_activity_id")
    private TaskActivity taskActivity;

    @Column(name = "task_order", length = 3)
    private Integer taskOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id")
    private RuleModel criteria;

    @Column(name = "approval_logic", length = 256)
    private String approvalLogic;

    @Column(name = "rejection_logic", length = 256)
    private String rejectionLogic;

    @Column(name = "config_values")
    private String configValues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;

    @Column(name = "action_group_id", length = 20)
    private Long actionGroupId;

    @Column(name = "criteria_result")
    private String criteriaResult;

    @Column(name = "criteria_action", length = 3)
    private Integer criteriaAction;
    
    @Column(name = "description")
    private String description;

    @Column(name = "complete_on_action")
    private Integer completeOnAction;
    
    @Column(name="due_time",nullable=true)
    @Temporal(TemporalType.TIME)
    private Date dueTime;
    
    protected Task() {}

    private Task(final Task parent, final String name, final String shortName, final Integer entityType, final Long entityId,
            final Integer taskType, final TaskConfig taskConfig, final Integer status, final Integer priority, final Date dueDate,
            final Integer currentAction, final AppUser assignedTo, final Integer taskOrder, final RuleModel criteria,
            final String approvalLogic, final String rejectionLogic, final String configValues, final Client client, final Office office,
            final Long actionGroupId, final String criteriaResult, final Integer criteriaAction, final TaskActivity taskActivity,
            final String description, final Integer completeOnAction, final Date dueTime) {

        this.parent = parent;
        this.name = name;
        this.shortName = shortName;
        this.entityType = entityType;
        this.entityId = entityId;
        this.taskType = taskType;
        this.taskConfig = taskConfig;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.currentAction = currentAction;
        this.assignedTo = assignedTo;

        this.taskOrder = taskOrder;
        this.criteria = criteria;
        this.approvalLogic = approvalLogic;
        this.rejectionLogic = rejectionLogic;
        this.configValues = configValues;
        this.client = client;
        this.office = office;
        this.actionGroupId = actionGroupId;
        this.criteriaResult = criteriaResult;
        this.criteriaAction = criteriaAction;
        this.taskActivity = taskActivity;
        this.description = description;
        this.completeOnAction = completeOnAction;
        this.dueTime = dueTime;
    }

    public static Task create(final Task parent, final String name, final String shortName, final Integer entityType, final Long entityId,
            final Integer taskType, final TaskConfig taskConfig, final Integer status, final Integer priority, final Date dueDate,
            final Integer currentAction, final AppUser assignedTo, final Integer taskOrder, final RuleModel criteria,
            final String approvalLogic, final String rejectionLogic, final String configValues, final Client client, final Office office,
            final Long actionGroupId, final String criteriaResult, final Integer criteriaAction, final TaskActivity taskActivity,
            String description, Integer completeOnAction, Date dueTime) {
        return new Task(parent, name, shortName, entityType, entityId, taskType, taskConfig, status, priority, dueDate, currentAction,
                assignedTo, taskOrder, criteria, approvalLogic, rejectionLogic, configValues, client, office, actionGroupId,
                criteriaResult, criteriaAction, taskActivity, description, completeOnAction, dueTime);
    }

    public Task getParent() {
        return this.parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
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

    public Integer getEntityType() {
        return this.entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Integer getTaskType() {
        return this.taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public TaskConfig getTaskConfig() {
        return this.taskConfig;
    }

    public void setTaskConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getCurrentAction() {
        return this.currentAction;
    }

    public void setCurrentAction(Integer currentAction) {
        this.currentAction = currentAction;
    }

    public AppUser getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(AppUser assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Integer getTaskOrder() {
        return this.taskOrder;
    }

    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }

    public RuleModel getCriteria() {
        return this.criteria;
    }

    public void setCriteria(RuleModel criteria) {
        this.criteria = criteria;
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

    public String getConfigValues() {
        return this.configValues;
    }

    public void setConfigValues(String configValues) {
        this.configValues = configValues;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Office getOffice() {
        return this.office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public Long getActionGroupId() {
        return this.actionGroupId;
    }

    public void setActionGroupId(Long actionGroupId) {
        this.actionGroupId = actionGroupId;
    }

    public String getCriteriaResult() {
        return this.criteriaResult;
    }

    public void setCriteriaResult(String criteriaResult) {
        this.criteriaResult = criteriaResult;
    }

    public Integer getCriteriaAction() {
        return this.criteriaAction;
    }

    public void setCriteriaAction(Integer criteriaAction) {
        this.criteriaAction = criteriaAction;
    }

    public TaskActivity getTaskActivity() {
        return taskActivity;
    }

    public void setTaskActivity(TaskActivity taskActivity) {
        this.taskActivity = taskActivity;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getCompleteOnAction() {
        return this.completeOnAction;
    }

    public void setCompleteOnAction(Integer completeOnAction) {
        this.completeOnAction = completeOnAction;
    }

    public Date getDueTime() {
        return this.dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

}
