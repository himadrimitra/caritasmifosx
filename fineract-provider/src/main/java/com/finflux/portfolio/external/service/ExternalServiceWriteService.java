package com.finflux.portfolio.external.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ExternalServiceWriteService {

	CommandProcessingResult updateServiceProperties(Long aLong, JsonCommand command);
}
