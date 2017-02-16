package com.finflux.task.template.form;

public class TaskConfigTemplateForm 
{
    private String taskName;
    private String shortName;
    private Integer entity_id;
    private Long activity_id;
    
    public String getTaskName() {
        return this.taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String getShortName() {
        return this.shortName;
    }
    
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    public Integer getEntity_id() {
        return this.entity_id;
    }
    
    public void setEntity_id(Integer entity_id) {
        this.entity_id = entity_id;
    }
    
    public Long getActivity_id() {
        return this.activity_id;
    }
    
    public void setActivity_id(Long activity_id) {
        this.activity_id = activity_id;
    }
    
    
}
