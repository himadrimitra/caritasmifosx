package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.data.EligibilityStatus;
import com.finflux.ruleengine.execution.domain.LoanApplicationEligibilityCheck;
import com.finflux.ruleengine.execution.domain.LoanApplicationEligibilityCheckRepository;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.google.gson.reflect.TypeToken;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
public class EligibilityCheckReadPlatformServiceImpl implements EligibilityCheckReadPlatformService {

    private final FromJsonHelper fromJsonHelper;
    private final LoanApplicationEligibilityCheckRepository loanApplicationEligibilityCheckRepository;

    @Autowired
    public EligibilityCheckReadPlatformServiceImpl(final FromJsonHelper fromJsonHelper,
                                                   final LoanApplicationEligibilityCheckRepository loanApplicationEligibilityCheckRepository) {
        this.fromJsonHelper = fromJsonHelper;
        this.loanApplicationEligibilityCheckRepository = loanApplicationEligibilityCheckRepository;
    }

    @Override
    public EligibilityResult getEligibilityCheckResult(Long loanApplicationId) {
        LoanApplicationEligibilityCheck loanApplicationEligibilityCheck = loanApplicationEligibilityCheckRepository.findOneByLoanApplicationId(loanApplicationId);
        if(loanApplicationEligibilityCheck == null){
            return null;
        }
        Type type = new TypeToken<RuleResult>(){}.getType();
        EligibilityResult eligibilityResult = new EligibilityResult();
        eligibilityResult.setStatus(EligibilityStatus.fromInt(loanApplicationEligibilityCheck.getEligibilityStatus()));
        if (loanApplicationEligibilityCheck.getEligibilityResult()!= null) {
            RuleResult criteriaOutput = fromJsonHelper.getGsonConverter().fromJson(loanApplicationEligibilityCheck.getEligibilityResult(), type);
            eligibilityResult.setCriteriaOutput(criteriaOutput);
        }
        return eligibilityResult;
    }

}