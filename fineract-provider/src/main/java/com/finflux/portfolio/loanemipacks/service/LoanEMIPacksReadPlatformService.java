package com.finflux.portfolio.loanemipacks.service;

import com.finflux.portfolio.loanemipacks.data.LoanEMIPackData;

import java.util.Collection;

public interface LoanEMIPacksReadPlatformService {

        Collection<LoanEMIPackData> retrieveActiveLoanProductsWithoutEMIPacks();

        LoanEMIPackData retrieveEMIPackTemplate(Long loanProductId);

        Collection<LoanEMIPackData> retrieveActiveLoanProductsWithEMIPacks();

        Collection<LoanEMIPackData> retrieveEMIPackDetails(Long loanProductId);

        LoanEMIPackData retrieveEMIPackDetails(Long loanProductId, Long loanEMIPackId);
}
