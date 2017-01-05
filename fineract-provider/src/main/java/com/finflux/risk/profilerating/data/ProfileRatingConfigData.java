package com.finflux.risk.profilerating.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.ruleengine.configuration.data.RuleData;

public class ProfileRatingConfigData {

    private Long id;
    private EnumOptionData type;
    private RuleData criteriaData;
    private boolean isActive;

    private ProfileRatingConfigData(final Long id, final EnumOptionData type, final RuleData criteriaData, final boolean isActive) {
        this.id = id;
        this.type = type;
        this.criteriaData = criteriaData;
        this.isActive = isActive;
    }

    public static ProfileRatingConfigData instance(final Long id, final EnumOptionData type, final RuleData criteriaData,
            final boolean isActive) {
        return new ProfileRatingConfigData(id, type, criteriaData, isActive);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumOptionData getType() {
        return this.type;
    }

    public void setType(EnumOptionData type) {
        this.type = type;
    }

    public RuleData getCriteriaData() {
        return this.criteriaData;
    }

    public void setCriteriaData(RuleData criteriaData) {
        this.criteriaData = criteriaData;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

}