package com.finflux.infrastructure.external.authentication.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ExternalAuthenticationServicesWritePlatformService {
	CommandProcessingResult updateTransactionAuthenticationService(Long serviceId, JsonCommand command);
}
