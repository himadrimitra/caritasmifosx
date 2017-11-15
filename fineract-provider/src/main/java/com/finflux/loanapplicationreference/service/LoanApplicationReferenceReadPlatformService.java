package com.finflux.loanapplicationreference.service;

import java.util.Collection;
import java.util.Map;

import com.finflux.loanapplicationreference.data.*;

public interface LoanApplicationReferenceReadPlatformService {

    LoanApplicationReferenceTemplateData templateData(final boolean onlyActive, final Integer productApplicableForLoanType,
            final Integer entityType, final Long entityId);

    LoanApplicationReferenceTemplateData templateData(final boolean onlyActive, final Long loanApplicationReferenceId,
            final Integer productApplicableForLoanType, final Integer entityType, final Long entityId);

    Collection<LoanApplicationReferenceData> retrieveAll(final Long clientId);

    LoanApplicationReferenceData retrieveOne(final Long loanApplicationReferenceId);

    Collection<LoanApplicationChargeData> retrieveChargesByLoanAppRefId(final Long loanApplicationReferenceId);

    LoanApplicationSanctionData retrieveSanctionDataByLoanAppRefId(final Long loanApplicationReferenceId);
    
    Map<String, Object> retrieveLoanProductIdApprovedAmountClientId(final Long loanApplicationReferenceId);

    Collection<CoApplicantData> retrieveCoApplicants(Long loanApplicationReferenceId);

    CoApplicantData retrieveOneCoApplicant(Long loanApplicationReferenceId, Long coApplicantId);
}