package com.finflux.organisation.transaction.authentication.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface TransactionAuthenticationWritePlatformService {
	CommandProcessingResult createTransactionAuthentication(JsonCommand command);

	CommandProcessingResult updateTransactionAuthentication(Long transactionAuthenticationId, JsonCommand command);

	CommandProcessingResult deleteTransactionAuthentication(Long transactionAuthenticationId);
}
