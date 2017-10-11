package com.finflux.task.template.data;




public class TaskConfigTemplateObject 
{
    private Long id;
    private String taskName;
    private String shortName;
    private TaskConfigTemplateEntityType entity;
    private Long taskConfigId;
    
    public TaskConfigTemplateObject(Long id,
    String taskName,
    String shortName,
    TaskConfigTemplateEntityType entity2,
    Long taskConfigId)
    {
       this.id=id;
       this.taskName=taskName;
       this.shortName=shortName;
       this.entity=entity2;
       this.taskConfigId=taskConfigId;
    }
    
    public Long getId() {
        return this.id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

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
    
    public TaskConfigTemplateEntityType getEntity() {
        return this.entity;
    }
    
    public void setEntity(TaskConfigTemplateEntityType entity) {
        this.entity = entity;
    }

    
    public Long getTaskConfigId() {
        return this.taskConfigId;
    }

    
    public void setTaskConfigId(Long taskConfigId) {
        this.taskConfigId = taskConfigId;
    }
    
    
}
