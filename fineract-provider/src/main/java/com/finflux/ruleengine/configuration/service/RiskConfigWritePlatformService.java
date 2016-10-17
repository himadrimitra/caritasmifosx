package com.finflux.ruleengine.configuration.service;

import com.finflux.ruleengine.lib.data.EntityRuleType;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface RiskConfigWritePlatformService {

    CommandProcessingResult createRule(EntityRuleType factor, JsonCommand command);

    CommandProcessingResult updateRule(Long ruleId, EntityRuleType factor, JsonCommand command);
}