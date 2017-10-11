package com.finflux.ruleengine.eligibility.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LoanProductEligibilityWritePlatformService {

    CommandProcessingResult createLoanProductEligibility(Long loanProductId, JsonCommand command);

    CommandProcessingResult updateLoanProductEligibility(Long loanProductEligibilityId, JsonCommand command);
}