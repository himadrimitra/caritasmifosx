package com.finflux.ruleengine.eligibility.form;

import com.finflux.ruleengine.eligibility.domain.LoanProductEligibility;
import com.finflux.ruleengine.eligibility.domain.LoanProductEligibilityCriteria;
import com.finflux.ruleengine.lib.data.Bucket;
import com.finflux.ruleengine.lib.data.OutputConfiguration;

import java.util.List;

/**
 * Created by dhirendra on 19/09/16.
 */
public class LoanProductEligibilityForm {

    private Long loanProductId;

    private List<LoanProductEligibilityCriteriaForm> criterias;

    private Boolean isActive;

    public Long getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Long loanProductId) {
        this.loanProductId = loanProductId;
    }

    public List<LoanProductEligibilityCriteriaForm> getCriterias() {
        return criterias;
    }

    public void setCriterias(List<LoanProductEligibilityCriteriaForm> criterias) {
        this.criterias = criterias;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
