package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.execution.data.EligibilityResult;

/**
 * Created by dhirendra on 22/09/16.
 */
public interface LoanProductEligibilityExecutionService {

    public EligibilityResult checkLoanEligibility(Long loanApplicationId);

    public EligibilityResult getLoanEligibility(Long loanApplicationId);
}
