package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.domain.LoanApplicationEligibilityCheck;
import com.finflux.ruleengine.execution.domain.LoanApplicationEligibilityCheckRepository;
import com.finflux.ruleengine.lib.data.RuleResult;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class EligibilityCheckWritePlatformServiceImpl implements EligibilityCheckWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(EligibilityCheckWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final LoanApplicationEligibilityCheckRepository loanApplicationEligibilityCheckRepository;
    private final ToApiJsonSerializer<RuleResult> toApiJsonSerializer;

    @Autowired
    public EligibilityCheckWritePlatformServiceImpl(final PlatformSecurityContext context,
                                                    final LoanApplicationEligibilityCheckRepository loanApplicationEligibilityCheckRepository,
                                                    final DefaultToApiJsonSerializer<RuleResult> toApiJsonSerializer) {
        this.context = context;
        this.loanApplicationEligibilityCheckRepository = loanApplicationEligibilityCheckRepository;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @Transactional
    @Override
    public Long createOrUpdateEligibilityCheck(Long loanApplicationId, EligibilityResult eligibilityResult) {
        AppUser  appUser= this.context.authenticatedUser();
        LoanApplicationEligibilityCheck loanApplicationEligibilityCheck = this.loanApplicationEligibilityCheckRepository.findOneByLoanApplicationId(loanApplicationId);
        if(loanApplicationEligibilityCheck==null){
            loanApplicationEligibilityCheck = new LoanApplicationEligibilityCheck();
            loanApplicationEligibilityCheck.setLoanApplicationId(loanApplicationId);
            loanApplicationEligibilityCheck.setEligibilityStatus(eligibilityResult.getStatus().getValue());
            loanApplicationEligibilityCheck.setEligibilityResult(toApiJsonSerializer.serialize(eligibilityResult.getCriteriaOutput()));
            loanApplicationEligibilityCheck.setCreatedBy(appUser);
            loanApplicationEligibilityCheck.setCreatedOn(new Date());
            loanApplicationEligibilityCheck.setUpdatedOn(new Date());
            loanApplicationEligibilityCheck.setUpdatedBy(appUser);
        }else{
            loanApplicationEligibilityCheck.setUpdatedOn(new Date());
            loanApplicationEligibilityCheck.setUpdatedBy(appUser);
        }
        LoanApplicationEligibilityCheck newCheck = this.loanApplicationEligibilityCheckRepository.save(loanApplicationEligibilityCheck);
        return newCheck.getId();
    }
}