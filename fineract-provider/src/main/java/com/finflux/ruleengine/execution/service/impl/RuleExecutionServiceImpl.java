package com.finflux.ruleengine.execution.service.impl;

import com.finflux.ruleengine.configuration.service.RiskConfigReadPlatformService;
import com.finflux.ruleengine.execution.service.DataLayer;
import com.finflux.ruleengine.execution.service.RuleExecutionService;
import com.finflux.ruleengine.lib.data.Rule;
import com.finflux.ruleengine.lib.data.RuleResult;
import com.finflux.ruleengine.lib.service.RuleExecutor;
import com.finflux.ruleengine.lib.service.impl.BasicRuleExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 22/09/16.
 */
@Service
@Scope("singleton")
public class RuleExecutionServiceImpl implements RuleExecutionService {

    private final RuleExecutor ruleExecutor;
    private final RiskConfigReadPlatformService riskConfigReadPlatformService;

    @Autowired
    public RuleExecutionServiceImpl(final BasicRuleExecutor ruleExecutor,
                                    final RiskConfigReadPlatformService riskConfigReadPlatformService){
        this.ruleExecutor = ruleExecutor;
        this.riskConfigReadPlatformService = riskConfigReadPlatformService;
    }

    @Override
    public RuleResult executeARule(Long ruleId, DataLayer dataLayer) {
        Rule rule = riskConfigReadPlatformService.getRuleById(ruleId);
        List<String> requiredKeys = ruleExecutor.getRequiredFields(rule);
        Map<String, Object> keyValueMap = dataLayer.getValues(requiredKeys);
        return ruleExecutor.executeRule(rule,keyValueMap);
    }
}
