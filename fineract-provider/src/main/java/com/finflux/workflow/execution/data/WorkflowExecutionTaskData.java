package com.finflux.workflow.execution.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 15/10/16.
 */
public class WorkflowExecutionTaskData {

    private Long id;
    private String name;
    private EnumOptionData type;
    private String identifier;

    public WorkflowExecutionTaskData(Long id, String name, EnumOptionData type, String identifier) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.identifier = identifier;
    }

    public static WorkflowExecutionTaskData instance(Long id, String name, EnumOptionData type,String identifier) {
        return new WorkflowExecutionTaskData(id, name, type, identifier);
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
