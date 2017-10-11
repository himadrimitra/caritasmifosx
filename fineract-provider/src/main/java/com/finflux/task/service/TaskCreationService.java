package com.finflux.task.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

import com.finflux.ruleengine.lib.data.EntityRuleType;

public interface TaskCreationService {

    CommandProcessingResult createAdhocTask(JsonCommand command);
}