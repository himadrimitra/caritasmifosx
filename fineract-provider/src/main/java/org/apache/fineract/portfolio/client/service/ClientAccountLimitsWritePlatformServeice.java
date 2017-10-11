package org.apache.fineract.portfolio.client.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ClientAccountLimitsWritePlatformServeice {

    CommandProcessingResult createAccountLimits(Long clientId, JsonCommand command);

    CommandProcessingResult updateAccountLimits(Long limitId, JsonCommand command);

}
