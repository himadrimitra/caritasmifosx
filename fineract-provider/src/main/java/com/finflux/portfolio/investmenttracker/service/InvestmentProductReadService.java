package com.finflux.portfolio.investmenttracker.service;

import java.util.Collection;

import com.finflux.portfolio.investmenttracker.data.InvestmentProductData;

public interface InvestmentProductReadService {

    InvestmentProductData retrieveInvestmentProductTemplate(InvestmentProductData investmentProductData);

    Collection<InvestmentProductData> retrieveAll();

    InvestmentProductData retrieveOne(final Long investmentProductId);
}
