package com.finflux.task.template.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.task.domain.TaskActivity;


public class TaskConfigTemplateFormData 
{
    private final Collection<EnumOptionData> entityOptions;
    private final Collection<TaskActivity> activityType;
    

    public TaskConfigTemplateFormData(Collection<TaskActivity> activityType2,Collection<EnumOptionData> entityOptions2)
    {
        this.entityOptions=entityOptions2;
        this.activityType=activityType2;
    }
    public Collection<EnumOptionData> getEntityOptions() {
        return this.entityOptions;
    }
    public Collection<TaskActivity> getActivityType() {
        return this.activityType;
    }

    public static TaskConfigTemplateFormData retrieveTaskTemplate(Collection<TaskActivity> activityType,Collection<EnumOptionData> entityOptions)
    {
        return new TaskConfigTemplateFormData(activityType,entityOptions);
    }
}
