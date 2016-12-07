package com.finflux.task.execution.data;

import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 15/10/16.
 */
public class TaskActivityData {

    private Long id;
    private String name;
    private String identifier;
    private Map<String, String> configValues;
    private EnumOptionData type;

    public TaskActivityData(Long id, String name, String identifier, EnumOptionData type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.identifier = identifier;
    }

    public static TaskActivityData instance(Long id, String name, String identifier, EnumOptionData type) {
        return new TaskActivityData(id, name, identifier, type);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumOptionData getType() {
        return type;
    }

    public void setType(EnumOptionData type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
