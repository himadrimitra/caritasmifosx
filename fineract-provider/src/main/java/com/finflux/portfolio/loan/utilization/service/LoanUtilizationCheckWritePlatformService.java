package com.finflux.portfolio.loan.utilization.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LoanUtilizationCheckWritePlatformService {

    CommandProcessingResult create(final Long loanId, final JsonCommand command);

    CommandProcessingResult update(final Long loanId, final Long loanUtilizationCheckId, final JsonCommand command);
}
