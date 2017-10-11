package com.finflux.portfolio.client.cashflow.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ClientIncomeExpenseWritePlatformService {

    CommandProcessingResult create(final Long clientId, final JsonCommand command);

    CommandProcessingResult update(final Long clientIncomeExpenseId, final JsonCommand command);
}