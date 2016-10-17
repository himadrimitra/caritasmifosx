package com.finflux.ruleengine.execution.service.impl;

import com.finflux.ruleengine.configuration.service.RuleCacheService;
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
    private final RuleCacheService ruleCacheService;

    @Autowired
    public RuleExecutionServiceImpl(final BasicRuleExecutor ruleExecutor,
                                    final RuleCacheService ruleCacheService){
        this.ruleExecutor = ruleExecutor;
        this.ruleCacheService = ruleCacheService;
    }

    @Override
    public RuleResult executeCriteria(Long ruleId, DataLayer dataLayer) {
        Rule rule = ruleCacheService.getRuleById(ruleId);
        List<String> requiredKeys = ruleExecutor.getRequiredFields(rule);
        Map<String, Object> keyValueMap = dataLayer.getValues(requiredKeys);
        return ruleExecutor.executeRule(rule,keyValueMap);
    }
}
