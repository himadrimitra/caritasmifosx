package com.finflux.loanapplicationreference.service;

import java.util.Collection;
import java.util.Map;

import com.finflux.loanapplicationreference.data.LoanApplicationChargeData;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceData;
import com.finflux.loanapplicationreference.data.LoanApplicationReferenceTemplateData;
import com.finflux.loanapplicationreference.data.LoanApplicationSanctionData;

public interface LoanApplicationReferenceReadPlatformService {

    LoanApplicationReferenceTemplateData templateData(final boolean onlyActive);
    
    LoanApplicationReferenceTemplateData templateData(final boolean onlyActive, final Long loanApplicationReferenceId);

    Collection<LoanApplicationReferenceData> retrieveAll(final Long clientId);

    LoanApplicationReferenceData retrieveOne(final Long loanApplicationReferenceId);

    Collection<LoanApplicationChargeData> retrieveChargesByLoanAppRefId(final Long loanApplicationReferenceId);

    LoanApplicationSanctionData retrieveSanctionDataByLoanAppRefId(final Long loanApplicationReferenceId);
    
    Map<String, Object> retrieveLoanProductIdApprovedAmountClientId(final Long loanApplicationReferenceId);
}