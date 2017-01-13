package com.finflux.ruleengine.execution.data;

import com.finflux.ruleengine.lib.data.RuleResult;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 30/09/16.
 */
public class EligibilityResult {

    private EligibilityStatus status;

    private EnumOptionData eligibiltyStatus;

    private RuleResult criteriaOutput;

    public EligibilityStatus getStatus() {
        return status;
    }

    public void setStatus(EligibilityStatus status) {
        this.status = status;
        if(this.status!=null) {
            this.eligibiltyStatus = status.getEnumOptionData();
        }
    }

    public RuleResult getCriteriaOutput() {
        return criteriaOutput;
    }

    public void setCriteriaOutput(RuleResult criteriaOutput) {
        this.criteriaOutput = criteriaOutput;
    }
}
