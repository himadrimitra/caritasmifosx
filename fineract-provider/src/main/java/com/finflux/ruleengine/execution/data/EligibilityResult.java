package com.finflux.ruleengine.execution.data;

import com.finflux.ruleengine.lib.data.RuleResult;

/**
 * Created by dhirendra on 30/09/16.
 */
public class EligibilityResult {

    private EligibilityStatus status;

    private RuleResult criteriaOutput;

    public EligibilityStatus getStatus() {
        return status;
    }

    public void setStatus(EligibilityStatus status) {
        this.status = status;
    }

    public RuleResult getCriteriaOutput() {
        return criteriaOutput;
    }

    public void setCriteriaOutput(RuleResult criteriaOutput) {
        this.criteriaOutput = criteriaOutput;
    }
}
