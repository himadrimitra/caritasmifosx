package com.finflux.task.individual.data;

import java.util.List;

import org.apache.fineract.useradministration.data.AppUserData;

import com.finflux.task.template.data.TaskConfigTemplateObject;


public class CreateTemplateTaskTemplateData 
{
    private List<TaskConfigTemplateObject> taskConfigTemplateObject;
    private List<AppUserData> user;
    
    public CreateTemplateTaskTemplateData(List<TaskConfigTemplateObject> taskConfigTemplateObject,List<AppUserData> user)
    {
        this.taskConfigTemplateObject=taskConfigTemplateObject;
        this.user=user;
    }
    public List<TaskConfigTemplateObject> getTaskConfigTemplate() {
        return this.taskConfigTemplateObject;
    }
    
    public void setTaskConfigTemplate(List<TaskConfigTemplateObject> taskConfigTemplate) {
        this.taskConfigTemplateObject = taskConfigTemplate;
    }
    
    public List<AppUserData> getUser() {
        return this.user;
    }
    
    public void setUser(List<AppUserData> user) {
        this.user = user;
    }
    
    
}
