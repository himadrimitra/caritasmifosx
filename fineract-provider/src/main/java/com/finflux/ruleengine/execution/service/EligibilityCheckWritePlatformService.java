package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.execution.data.EligibilityResult;

public interface EligibilityCheckWritePlatformService {

    Long createOrUpdateEligibilityCheck(Long loanApplicationId, EligibilityResult eligibilityResult);

}