package com.finflux.task.individual.form;


public class CreateTemplateTaskForm 
{

    private long templateId;
    private long entity_id;
    private long userId;
    private String duedate;
    private String duetime;
    
    public long getTemplateId() {
        return this.templateId;
    }
    
    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }
    
    public long getEntity_id() {
        return this.entity_id;
    }
    
    public void setEntity_id(long entity_id) {
        this.entity_id = entity_id;
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
