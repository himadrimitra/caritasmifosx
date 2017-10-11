package com.finflux.risk.creditbureau.configuration.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CreditBureauConfigurationWritePlatformService {

    CommandProcessingResult updateConfiguration(String implementationKey, JsonCommand command);
}
