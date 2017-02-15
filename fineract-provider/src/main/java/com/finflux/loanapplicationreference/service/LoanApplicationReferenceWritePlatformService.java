package com.finflux.loanapplicationreference.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LoanApplicationReferenceWritePlatformService {

    CommandProcessingResult create(final JsonCommand command);

    CommandProcessingResult update(final Long loanApplicationReferenceId, final JsonCommand command);

    CommandProcessingResult requestForApproval(final Long loanApplicationReferenceId, final JsonCommand command);

    CommandProcessingResult reject(final Long loanApplicationReferenceId, final JsonCommand command);

    CommandProcessingResult approve(final Long loanApplicationReferenceId, final JsonCommand command);

    CommandProcessingResult undoApprove(final Long loanApplicationReferenceId, final JsonCommand command);

    CommandProcessingResult disburse(final Long loanApplicationReferenceId, final JsonCommand command);

    CommandProcessingResult addCoApplicant(final JsonCommand command);

    CommandProcessingResult deleteCoApplicant(final JsonCommand command);

}