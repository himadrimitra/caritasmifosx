package com.finflux.ruleengine.execution.service.impl;

import com.finflux.loanapplicationreference.data.LoanApplicationReferenceData;
import com.finflux.loanapplicationreference.service.LoanApplicationReferenceReadPlatformService;
import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityCriteriaData;
import com.finflux.ruleengine.eligibility.data.LoanProductEligibilityData;
import com.finflux.ruleengine.eligibility.service.LoanProductEligibilityReadPlatformService;
import com.finflux.ruleengine.execution.data.DataLayerKey;
import com.finflux.ruleengine.execution.data.EligibilityResult;
import com.finflux.ruleengine.execution.data.EligibilityStatus;
import com.finflux.ruleengine.execution.service.*;
import com.finflux.ruleengine.lib.FieldUndefinedException;
import com.finflux.ruleengine.lib.InvalidExpressionException;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.ruleengine.lib.service.ExpressionExecutor;
import com.finflux.ruleengine.lib.service.impl.MyExpressionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class LoanProductEligibilityExecutionServiceImpl implements LoanProductEligibilityExecutionService {

    private final RuleExecutionService ruleExecutionService;
    private final ExpressionExecutor expressionExecutor;
    private final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService;
    private final LoanProductEligibilityReadPlatformService loanProductEligibilityReadPlatformService;
    private final EligibilityCheckReadPlatformService eligibilityCheckReadPlatformService;
    private final EligibilityCheckWritePlatformService eligibilityCheckWritePlatformService;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;

    @Autowired
    public LoanProductEligibilityExecutionServiceImpl(final RuleExecutionService ruleExecutionService,
                                                      final MyExpressionExecutor expressionExecutor,
                                                      final LoanApplicationReferenceReadPlatformService loanApplicationReferenceReadPlatformService,
                                                      final LoanProductEligibilityReadPlatformService loanProductEligibilityReadPlatformService,
                                                      final DataLayerReadPlatformService dataLayerReadPlatformService,
                                                      final EligibilityCheckWritePlatformService eligibilityCheckWritePlatformService,
                                                      final EligibilityCheckReadPlatformService eligibilityCheckReadPlatformService){
        this.ruleExecutionService = ruleExecutionService;
        this.expressionExecutor = expressionExecutor;
        this.loanApplicationReferenceReadPlatformService = loanApplicationReferenceReadPlatformService;
        this.loanProductEligibilityReadPlatformService = loanProductEligibilityReadPlatformService;
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.eligibilityCheckReadPlatformService = eligibilityCheckReadPlatformService;
        this.eligibilityCheckWritePlatformService = eligibilityCheckWritePlatformService;
    }

    @Override
    public EligibilityResult checkLoanEligibility(Long loanApplicationId) {
        EligibilityResult eligibilityResult = new EligibilityResult();
        eligibilityResult.setStatus(EligibilityStatus.TO_BE_REVIEWED);
        LoanApplicationReferenceData loanApplicationReference = loanApplicationReferenceReadPlatformService.retrieveOne(loanApplicationId);
        Long loanProductId = loanApplicationReference.getLoanProductId();
        Long clientId = loanApplicationReference.getClientId();
        Double loanAmount = loanApplicationReference.getLoanAmountRequested().doubleValue();
        LoanProductEligibilityData loanProductEligibilityData= loanProductEligibilityReadPlatformService.retrieveOneLoanProductEligibility(loanProductId);
        if(loanProductEligibilityData!=null){
            List<LoanProductEligibilityCriteriaData> criterias = loanProductEligibilityData.getCriterias();
            for(LoanProductEligibilityCriteriaData criteria: criterias){
                if(loanAmount >= criteria.getMinAmount() && loanAmount <=criteria.getMaxAmount()){
                    LoanApplicationDataLayer dataLayer = new LoanApplicationDataLayer(dataLayerReadPlatformService);
                    Map<String,Object> dataLayerKeyLongMap = new HashMap<>();
                    dataLayerKeyLongMap.put(DataLayerKey.CLIENT_ID.getValue(),clientId);
                    dataLayerKeyLongMap.put(DataLayerKey.LOANAPPLICATION_ID.getValue(),loanApplicationId);
                    dataLayer.build(dataLayerKeyLongMap);
                    RuleResult ruleResult = ruleExecutionService.executeARule(criteria.getRiskCriteriaId(),dataLayer);
                    eligibilityResult.setCriteriaOutput(ruleResult);
                    if(ruleResult !=null && ruleResult.getOutput().getValue()!=null){
                        Map<String, Object> map = new HashMap();
                        map.put("criteria", ruleResult.getOutput().getValue());
                        boolean rejectionResult = false;
                        boolean approvalResult = false;
                        try {
                            rejectionResult = expressionExecutor.executeExpression(criteria.getApprovalLogic(),map);
                        } catch (FieldUndefinedException e) {
                            e.printStackTrace();
                        } catch (InvalidExpressionException e) {
                            e.printStackTrace();
                        }

                        if(rejectionResult){
                            eligibilityResult.setStatus(EligibilityStatus.REJECTED);
                        }else{
                            try {
                                approvalResult = expressionExecutor.executeExpression(criteria.getApprovalLogic(),map);
                            } catch (FieldUndefinedException e) {
                                e.printStackTrace();
                            } catch (InvalidExpressionException e) {
                                e.printStackTrace();
                            }
                        }

                        if(approvalResult){
                            eligibilityResult.setStatus(EligibilityStatus.APPROVED);
                        }
                    }
                }
            }
        }
        this.eligibilityCheckWritePlatformService.createOrUpdateEligibilityCheck(loanApplicationId,eligibilityResult);
        return eligibilityResult;
    }

    @Override
    public EligibilityResult getLoanEligibility(Long loanApplicationId) {
        return this.eligibilityCheckReadPlatformService.getEligibilityCheckResult(loanApplicationId);
    }
}
