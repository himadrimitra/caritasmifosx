package com.finflux.portfolio.investmenttracker.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface InvestmentProductWriteService {

    CommandProcessingResult createInvestmentProduct(final JsonCommand command);

    CommandProcessingResult updateInvestmentProduct(final Long investmentProductId, final JsonCommand command);
}
