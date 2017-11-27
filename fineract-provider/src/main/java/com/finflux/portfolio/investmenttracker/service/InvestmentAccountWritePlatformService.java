package com.finflux.portfolio.investmenttracker.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;


public interface InvestmentAccountWritePlatformService {

    CommandProcessingResult createInvestmentAccount(final JsonCommand command);
    
    CommandProcessingResult approveInvestmentAccount(final Long investmentAccountId, final JsonCommand command);
    
    CommandProcessingResult activateInvestmentAccount(final Long investmentAccountId, final JsonCommand command);
    
    CommandProcessingResult rejectInvestmentAccount(final Long investmentAccountId, final JsonCommand command);
    
    CommandProcessingResult undoInvestmentAccountApproval(final Long investmentAccountId, final JsonCommand command);
    
    CommandProcessingResult releaseSavingLinkageAccount(final Long investmentAccountId, final Long savingLinkageAccountId, final JsonCommand command);
    
    CommandProcessingResult transferSavingLinkageAccount(final Long investmentAccountId, final Long savingLinkageAccountId, final JsonCommand command);
}
