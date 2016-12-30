package com.finflux.risk.profilerating.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.ruleengine.configuration.data.RuleData;

public class ProfileRatingConfigTemplateData {

    private final Collection<EnumOptionData> typeOptions;
    private final Collection<RuleData> criteriaOptions;

    private ProfileRatingConfigTemplateData(final Collection<EnumOptionData> typeOptions, final Collection<RuleData> criteriaOptions) {
        this.typeOptions = typeOptions;
        this.criteriaOptions = criteriaOptions;
    }

    public static ProfileRatingConfigTemplateData template(final Collection<EnumOptionData> typeOptions,
            final Collection<RuleData> criteriaOptions) {
        return new ProfileRatingConfigTemplateData(typeOptions, criteriaOptions);
    }
}