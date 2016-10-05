package com.finflux.portfolio.loanproduct.creditbureau.service;

import java.util.Collection;

import com.finflux.portfolio.loanproduct.creditbureau.data.CreditBureauLoanProductMappingData;

public interface CreditBureauLoanProductMappingReadPlatformService {

    Collection<CreditBureauLoanProductMappingData> retrieveAllCreditbureauLoanproductMappingData();

    CreditBureauLoanProductMappingData retrieveCreditbureauLoanproductMappingData(final Long productId);
}