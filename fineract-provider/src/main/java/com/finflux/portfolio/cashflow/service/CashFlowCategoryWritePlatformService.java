package com.finflux.portfolio.cashflow.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface CashFlowCategoryWritePlatformService {

    CommandProcessingResult create(final JsonCommand command);

    CommandProcessingResult update(final Long cashFlowId, final JsonCommand command);

    CommandProcessingResult activate(final Long cashFlowId, final JsonCommand command);

    CommandProcessingResult inActivate(final Long cashFlowId, final JsonCommand command);
}