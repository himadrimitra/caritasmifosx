package com.finflux.portfolio.loan.purpose.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LoanPurposeGroupWritePlatformService {

    CommandProcessingResult createLoanPurposeGroup(final JsonCommand command);

    CommandProcessingResult updateLoanPurposeGroup(final Long loanPurposeGroupId, final JsonCommand command);

    CommandProcessingResult activateLoanPurposeGroup(final Long loanPurposeId, final JsonCommand command);

    CommandProcessingResult inActivateLoanPurposeGroup(final Long loanPurposeId, final JsonCommand command);

    CommandProcessingResult createLoanPurpose(final JsonCommand command);

    CommandProcessingResult updateLoanPurpose(final Long loanPurposeId, final JsonCommand command);

    CommandProcessingResult activateLoanPurpose(final Long loanPurposeId, final JsonCommand command);

    CommandProcessingResult inActivateLoanPurpose(final Long loanPurposeId, final JsonCommand command);
}