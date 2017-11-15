package com.finflux.task.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_task_config_entity_type_mapping")
public class TaskConfigEntityTypeMapping extends AbstractPersistable<Long> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_config_id")
    private TaskConfig taskConfig;

    @Column(name = "entity_type", length = 3)
    private Integer entityType;

    @Column(name = "entity_id", length = 20)
    private Long entityId;

    @Column(name = "is_active")
    private boolean isActive;

    @Autowired
    public TaskConfigEntityTypeMapping(final TaskConfig taskConfig, final Integer entityType, final Long entityId, boolean isActive) {
        this.taskConfig = taskConfig;
        this.entityType = entityType;
        this.entityId = entityId;
        this.isActive = isActive;
    }

    public TaskConfigEntityTypeMapping() {}

    public TaskConfig getTaskConfig() {
        return this.taskConfig;
    }

    public Long getTaskConfigId() {
        return this.taskConfig.getId();
    }

    public void setTaskConfig(final TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    public void setEntityType(final Integer entityType) {
        this.entityType = entityType;
    }

    public void setEntityId(final Long entityId) {
        this.entityId = entityId;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

}