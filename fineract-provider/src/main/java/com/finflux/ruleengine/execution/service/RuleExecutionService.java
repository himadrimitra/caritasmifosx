package com.finflux.ruleengine.execution.service;

import com.finflux.ruleengine.lib.data.RuleResult;

/**
 * Created by dhirendra on 22/09/16.
 */
public interface RuleExecutionService {

    public RuleResult executeARule(Long ruleId, DataLayer dataLayerService);
}
