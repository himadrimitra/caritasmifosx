package com.finflux.portfolio.investmenttracker.service;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.service.SearchParameters;

import com.finflux.portfolio.investmenttracker.data.InvestmentAccountChargeData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountSavingsLinkagesData;


public interface InvestmentAccountReadService {

    InvestmentAccountData retrieveInvestmentAccountTemplate(InvestmentAccountData investmentAccountData,final boolean staffInSelectedOfficeOnly,
            final Long OfficeId);
    
    Collection<InvestmentAccountData> retrieveAll(final SearchParameters searchParameters);
    
    InvestmentAccountData retrieveInvestmentAccount(Long id);
    
    Collection<InvestmentAccountSavingsLinkagesData> retrieveInvestmentAccountSavingLinkages(final Long investmentAccountId);
    
    Collection<InvestmentAccountChargeData> retrieveInvestmentAccountCharges(final Long investmentAccountId);
    
    InvestmentAccountSavingsLinkagesData retrieveInvestmentSavingsLinkageAccountData(final Long investmentAccountId, final Long savingsLinkageAccountId);
}
