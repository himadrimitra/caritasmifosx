package com.finflux.ruleengine.eligibility.service;

import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityData;

import java.util.List;

/**
 * Created by dhirendra on 15/09/16.
 */
public interface LoanProductEligibilityReadPlatformService {

    List<LoanProductEligibilityData> getAllLoanProductEligibility();

    LoanProductEligibilityData retrieveOneLoanProductEligibility(Long loanProductId);
}
