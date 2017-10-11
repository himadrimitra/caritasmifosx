package com.finflux.task.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.finflux.task.domain.TaskConfig;

@Entity
@Table(name = "f_task_config_template")
public class TaskConfigTemplate extends AbstractPersistable<Long>
{
    @Column(name="name",nullable=false)
    private String name;
    
    @Column(name="short_name",nullable=false)
    private String shortName;
    
    @Column(name="entity_type")
    private int entity;
    
    @OneToOne
    @JoinColumn(name="task_config_id")
    private TaskConfig taskConfig;

    
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

    
    public int getEntity() {
        return this.entity;
    }

    
    public void setEntity(int entity) {
        this.entity = entity;
    }

    
    public TaskConfig getTaskConfig() {
        return this.taskConfig;
    }

    
    public void setTaskConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
    
    
}
