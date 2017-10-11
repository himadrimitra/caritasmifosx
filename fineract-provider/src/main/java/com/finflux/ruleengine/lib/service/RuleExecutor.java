package com.finflux.ruleengine.lib.service;

import com.finflux.ruleengine.lib.data.Rule;
import com.finflux.ruleengine.lib.data.RuleResult;

import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */
public interface RuleExecutor {
     public RuleResult executeRule(Rule rule, Map<String, Object> keyValueMap);

     public List<String> getRequiredFields(Rule rule);

}
