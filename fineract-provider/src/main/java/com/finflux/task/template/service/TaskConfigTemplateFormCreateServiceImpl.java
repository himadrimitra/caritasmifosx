package com.finflux.task.template.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.domain.TaskActivity;
import com.finflux.task.domain.TaskActivityRepository;
import com.finflux.task.template.data.TaskConfigTemplateEntityType;
import com.finflux.task.template.data.TaskConfigTemplateFormData;

@Service
public class TaskConfigTemplateFormCreateServiceImpl implements  TaskConfigTemplateFormCreateService {
    private TaskActivityRepository taskActivityRepository;
    
    
    @Autowired
    public TaskConfigTemplateFormCreateServiceImpl(final TaskActivityRepository taskActivityRepository) {
        this.taskActivityRepository = taskActivityRepository;
        
    }
    
    
    @Override
    public TaskConfigTemplateFormData retrieveTemplate() {
        Collection<EnumOptionData> entityOptions=TaskConfigTemplateEntityType.entityTypeOptions();
        Collection<TaskActivity> activityType=taskActivityRepository.findAll();
        TaskConfigTemplateFormData taskConfigTemplateFormData=new TaskConfigTemplateFormData(activityType, entityOptions);
        return taskConfigTemplateFormData;
    }


}
