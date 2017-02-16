package com.finflux.task.individual.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface CreateTemplateTaskWriteService 
{

    CommandProcessingResult assignTask(JsonCommand json);
}
