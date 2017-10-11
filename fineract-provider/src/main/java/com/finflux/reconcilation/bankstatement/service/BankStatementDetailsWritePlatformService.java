package com.finflux.reconcilation.bankstatement.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface BankStatementDetailsWritePlatformService {
	
	public CommandProcessingResult updateBankStatementDetails(final Long bankStatementDetailsId, final JsonCommand command);
}
