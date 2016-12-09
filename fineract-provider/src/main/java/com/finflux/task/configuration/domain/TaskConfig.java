package com.finflux.task.configuration.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.ruleengine.configuration.domain.RuleModel;
import com.finflux.task.execution.domain.Task;

@Entity
@Table(name = "f_task_config")
public class TaskConfig extends AbstractPersistable<Long> {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "short_name", length = 20, nullable = false)
    private String shortName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TaskConfig parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_activity_id")
    private TaskActivity taskActivity;

    @Column(name = "task_config_order", length = 3, nullable = true)
    private Integer taskConfigOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id")
    private RuleModel criteria;

    @Column(name = "approval_logic", length = 256)
    private String approvalLogic;

    @Column(name = "rejection_logic", length = 256)
    private String rejectionLogic;

    @Column(name = "config_values")
    private String configValues;

    @Column(name = "action_group_id")
    private Long actionGroupId;

    @Column(name = "task_type")
    private Integer taskType;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taskConfig", orphanRemoval = true)
    private Set<TaskConfigEntityTypeMapping> taskConfigEntityTypeMappings = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "taskConfig", orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    protected TaskConfig() {}

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

    public TaskConfig getParent() {
        return this.parent;
    }

    public void setParent(TaskConfig parent) {
        this.parent = parent;
    }

    public TaskActivity getTaskActivity() {
        return this.taskActivity;
    }

    public void setTaskActivity(TaskActivity taskActivity) {
        this.taskActivity = taskActivity;
    }

    public Integer getTaskConfigOrder() {
        return this.taskConfigOrder;
    }

    public void setTaskConfigOrder(Integer taskConfigOrder) {
        this.taskConfigOrder = taskConfigOrder;
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

    public Long getActionGroupId() {
        return this.actionGroupId;
    }

    public void setActionGroupId(Long actionGroupId) {
        this.actionGroupId = actionGroupId;
    }

    public Set<TaskConfigEntityTypeMapping> getTaskConfigEntityTypeMappings() {
        return this.taskConfigEntityTypeMappings;
    }

    public void setTaskConfigEntityTypeMappings(Set<TaskConfigEntityTypeMapping> taskConfigEntityTypeMappings) {
        this.taskConfigEntityTypeMappings = taskConfigEntityTypeMappings;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}