package com.finflux.portfolio.investmenttracker.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface InvestmentAccountWritePlatformService {

    CommandProcessingResult createInvestmentAccount(final JsonCommand command);
    
    CommandProcessingResult approveInvestmentAccount(final Long investmentAccountId, final JsonCommand command);
    
    CommandProcessingResult activataeInvestmentAccount(final Long investmentAccountId, final JsonCommand command);
}
