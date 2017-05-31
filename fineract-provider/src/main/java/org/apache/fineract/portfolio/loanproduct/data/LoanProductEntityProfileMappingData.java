package org.apache.fineract.portfolio.loanproduct.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class LoanProductEntityProfileMappingData {

    private final Long id;
    private final EnumOptionData profileType;
    private final Collection<EnumOptionData> values;

    private LoanProductEntityProfileMappingData(final Long id, final EnumOptionData profileType, final Collection<EnumOptionData> values) {
        this.id = id;
        this.profileType = profileType;
        this.values = values;
    }

    public static LoanProductEntityProfileMappingData instance(final Long id, final EnumOptionData profileType,
            Collection<EnumOptionData> values) {
        return new LoanProductEntityProfileMappingData(id, profileType, values);
    }

    public Long getId() {
        return this.id;
    }

    public EnumOptionData getProfileType() {
        return this.profileType;
    }

    public Collection<EnumOptionData> getValues() {
        return this.values;
    }

    public void addValue(final EnumOptionData value) {
        this.values.add(value);
    }
}