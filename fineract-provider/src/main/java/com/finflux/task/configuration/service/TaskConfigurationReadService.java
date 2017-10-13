package com.finflux.task.configuration.service;

import java.util.Collection;

import com.finflux.task.configuration.data.LoanProdcutTasksConfigTemplateData;
import com.finflux.task.configuration.data.TaskConfigEntityMappingData;
import com.finflux.task.configuration.data.TaskMappingTemplateData;

public interface TaskConfigurationReadService {

    LoanProdcutTasksConfigTemplateData retrieveLoanProdcutTasksConfigTemplateData();
    
    TaskMappingTemplateData retrieveTaskMappingTemplateData();

    Collection<TaskConfigEntityMappingData> retrieveAllTaskConfigEntityMappings();

    TaskConfigEntityMappingData retrieveTaskConfigEntityMapping(final Long taskConfigId, final Integer taskConfigEntityTypeVaule);
}