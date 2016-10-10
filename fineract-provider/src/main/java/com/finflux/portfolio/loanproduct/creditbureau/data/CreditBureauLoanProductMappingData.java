package com.finflux.portfolio.loanproduct.creditbureau.data;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;

public class CreditBureauLoanProductMappingData {

    private final Long id;
    private final CreditBureauData creditBureauData;
    private final Long loanProductId;
    private final String loanProductName;
    private final Boolean isCreditcheckMandatory;
    private final Boolean skipCreditcheckInFailure;
    private final Integer stalePeriod;
    private final Boolean isActive;

    private CreditBureauLoanProductMappingData(final Long id, final CreditBureauData creditBureauData, final Long loanProductId,
            final String loanProductName, final Boolean isCreditcheckMandatory, final Boolean skipCreditcheckInFailure,
            final Integer stalePeriod, final Boolean isActive) {
        this.id = id;
        this.creditBureauData = creditBureauData;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.isCreditcheckMandatory = isCreditcheckMandatory;
        this.skipCreditcheckInFailure = skipCreditcheckInFailure;
        this.stalePeriod = stalePeriod;
        this.isActive = isActive;
    }

    public static CreditBureauLoanProductMappingData instance(final Long id, final CreditBureauData creditBureauData,
            final Long loanProductId, final String loanProductName, final Boolean isCreditcheckMandatory,
            final Boolean skipCreditcheckInFailure, final Integer stalePeriod, final Boolean isActive) {
        return new CreditBureauLoanProductMappingData(id, creditBureauData, loanProductId, loanProductName, isCreditcheckMandatory,
                skipCreditcheckInFailure, stalePeriod, isActive);
    }
}