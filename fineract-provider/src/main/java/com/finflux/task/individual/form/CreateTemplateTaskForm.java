package com.finflux.task.individual.form;


public class CreateTemplateTaskForm 
{

    private long templateId;
    private long entityId;
    private long userId;
    private String duedate;
    private String duetime;
    private String description;
    
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTemplateId() {
        return this.templateId;
    }
  
    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }
    
    public long getEntity_id() {
        return this.entityId;
    }
    
    public void setEntity_id(long entity_id) {
        this.entityId = entity_id;
    }
    
    public long getUserId() {
        return this.userId;
    }
    
    public void setUserId(long userId) {
        this.userId = userId;
    }
    
    public String getDuedate() {
        return this.duedate;
    }
    
    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }
    
    public String getDuetime() {
        return this.duetime;
    }
    
    public void setDuetime(String duetime) {
        this.duetime = duetime;
    }
}
