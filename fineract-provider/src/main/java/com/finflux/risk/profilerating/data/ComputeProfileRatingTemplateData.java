package com.finflux.risk.profilerating.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;

public class ComputeProfileRatingTemplateData {

    private final Collection<EnumOptionData> scopeEntityTypeOptions;
    private final Collection<EnumOptionData> entityTypeOptions;
    private final Collection<OfficeData> officeOptions;

    private ComputeProfileRatingTemplateData(final Collection<EnumOptionData> scopeEntityTypeOptions,
            final Collection<EnumOptionData> entityTypeOptions, final Collection<OfficeData> officeOptions) {
        this.scopeEntityTypeOptions = scopeEntityTypeOptions;
        this.entityTypeOptions = entityTypeOptions;
        this.officeOptions = officeOptions;
    }

    public static ComputeProfileRatingTemplateData template(final Collection<EnumOptionData> scopeEntityTypeOptions,
            final Collection<EnumOptionData> entityTypeOptions, final Collection<OfficeData> officeOptions) {
        return new ComputeProfileRatingTemplateData(scopeEntityTypeOptions, entityTypeOptions, officeOptions);
    }

    public Collection<EnumOptionData> getScopeEntityTypeOptions() {
        return this.scopeEntityTypeOptions;
    }

    public Collection<EnumOptionData> getEntityTypeOptions() {
        return this.entityTypeOptions;
    }

    public Collection<OfficeData> getOfficeOptions() {
        return this.officeOptions;
    }
}