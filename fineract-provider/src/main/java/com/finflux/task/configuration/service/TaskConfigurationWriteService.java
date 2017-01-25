package com.finflux.task.configuration.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface TaskConfigurationWriteService {

    CommandProcessingResult createTaskConfig(final Long entityId, final JsonCommand command);
}