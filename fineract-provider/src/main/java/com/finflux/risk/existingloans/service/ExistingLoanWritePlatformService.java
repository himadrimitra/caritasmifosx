package com.finflux.risk.existingloans.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ExistingLoanWritePlatformService {

    CommandProcessingResult saveExistingLoan(Long clientId, JsonCommand command);

    CommandProcessingResult deleteExistingLoan(Long clientId, Long existingLoanId);

    CommandProcessingResult updateExistingLoan(Long clientId, Long existingLoanId, JsonCommand command);

    void createOrUpdateExistingLoans(final Long clientId, final JsonCommand command);
}