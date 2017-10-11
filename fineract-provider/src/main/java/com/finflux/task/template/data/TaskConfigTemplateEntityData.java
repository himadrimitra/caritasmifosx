package com.finflux.task.template.data;

import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public class TaskConfigTemplateEntityData 
{
 
    private EnumOptionData entity;
    private List<IdAndName> idAndName;
    
    public TaskConfigTemplateEntityData(TaskConfigTemplateEntityType entity,List<IdAndName> idAndNames)
    {
        this.entity=entity.getEnumOptionData();
        this.idAndName=idAndNames;
    }

    
    public EnumOptionData getEntity() {
        return this.entity;
    }

    
    public List<IdAndName> getIdAndNames() {
        return this.idAndName;
    }

}
