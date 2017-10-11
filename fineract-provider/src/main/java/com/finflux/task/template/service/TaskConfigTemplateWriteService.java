package com.finflux.task.template.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface TaskConfigTemplateWriteService 
{
    CommandProcessingResult addTemplateAsTask(JsonCommand json);

    CommandProcessingResult updateTaskConfigTemplate(Long templateId,JsonCommand command);

    
}
