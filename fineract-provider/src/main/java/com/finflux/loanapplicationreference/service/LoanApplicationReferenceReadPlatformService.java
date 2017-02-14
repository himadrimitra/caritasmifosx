package com.finflux.loanapplicationreference.service;

import java.util.Collection;
import java.util.Map;

import com.finflux.loanapplicationreference.data.*;

public interface LoanApplicationReferenceReadPlatformService {

    LoanApplicationReferenceTemplateData templateData(final boolean onlyActive);
    
    LoanApplicationReferenceTemplateData templateData(final boolean onlyActive, final Long loanApplicationReferenceId);

    Collection<LoanApplicationReferenceData> retrieveAll(final Long clientId);

    LoanApplicationReferenceData retrieveOne(final Long loanApplicationReferenceId);

    Collection<LoanApplicationChargeData> retrieveChargesByLoanAppRefId(final Long loanApplicationReferenceId);

    LoanApplicationSanctionData retrieveSanctionDataByLoanAppRefId(final Long loanApplicationReferenceId);
    
    Map<String, Object> retrieveLoanProductIdApprovedAmountClientId(final Long loanApplicationReferenceId);

    Collection<CoApplicantData> retrieveCoApplicants(Long loanApplicationReferenceId);
}