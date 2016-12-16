package com.finflux.task.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "f_task_activity", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "UQ_f_task_activity_name") })
public class TaskActivity extends AbstractPersistable<Long> {

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "identifier", length = 200, nullable = false)
    private String identifier;

    @Column(name = "config_values")
    private String configValues;

    @Column(name = "supported_actions", length = 500)
    private String supportedActions;

    @Column(name = "type", length = 3)
    private Integer type;

    protected TaskActivity() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getConfigValues() {
        return configValues;
    }

    public void setConfigValues(String configValues) {
        this.configValues = configValues;
    }

    public String getSupportedActions() {
        return supportedActions;
    }

    public void setSupportedActions(String supportedActions) {
        this.supportedActions = supportedActions;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
