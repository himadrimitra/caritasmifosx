package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.execution.data.EligibilityResult;

/**
 * Created by dhirendra on 15/09/16.
 */
public interface EligibilityCheckReadPlatformService {

    EligibilityResult getEligibilityCheckResult(Long loanApplicationId);
}
