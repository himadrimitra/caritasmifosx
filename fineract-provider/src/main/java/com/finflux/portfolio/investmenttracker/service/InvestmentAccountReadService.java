package com.finflux.portfolio.investmenttracker.service;

import java.util.Collection;

import com.finflux.portfolio.investmenttracker.data.InvestmentAccountChargeData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountData;
import com.finflux.portfolio.investmenttracker.data.InvestmentAccountSavingsLinkagesData;


public interface InvestmentAccountReadService {

    InvestmentAccountData retrieveInvestmentAccountTemplate(InvestmentAccountData investmentAccountData);
    
    Collection<InvestmentAccountData> retrieveAll();
    
    InvestmentAccountData retrieveInvestmentAccount(Long id);
    
    Collection<InvestmentAccountSavingsLinkagesData> retrieveInvestmentAccountSavingLinkages(final Long investmentAccountId);
    
    Collection<InvestmentAccountChargeData> retrieveInvestmentAccountCharges(final Long investmentAccountId);
}
