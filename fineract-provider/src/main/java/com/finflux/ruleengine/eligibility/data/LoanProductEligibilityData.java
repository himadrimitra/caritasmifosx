package com.finflux.ruleengine.eligibility.data;

import com.finflux.ruleengine.lib.data.Bucket;
import com.finflux.ruleengine.lib.data.EntityRuleType;
import com.finflux.ruleengine.lib.data.Rule;
import com.finflux.ruleengine.lib.data.ValueType;

import java.util.List;

/**
 * Created by dhirendra on 16/09/16.
 */
public class LoanProductEligibilityData {

    private Long id;

    private Long loanProductId;

    private String loanProductName;

    private List<LoanProductEligibilityCriteriaData> criterias;

    private Boolean isActive;


    public LoanProductEligibilityData(Long id, Long loanProductId, String loanProductName,
                                      Boolean isActive) {
        this.id = id;
        this.loanProductId = loanProductId;
        this.loanProductName = loanProductName;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Long loanProductId) {
        this.loanProductId = loanProductId;
    }

    public String getLoanProductName() {
        return loanProductName;
    }

    public void setLoanProductName(String loanProductName) {
        this.loanProductName = loanProductName;
    }

    public List<LoanProductEligibilityCriteriaData> getCriterias() {
        return criterias;
    }

    public void setCriterias(List<LoanProductEligibilityCriteriaData> criterias) {
        this.criterias = criterias;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
