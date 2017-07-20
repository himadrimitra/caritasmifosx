package com.finflux.pdcm.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.office.data.OfficeData;

public class PostDatedChequeDetailSearchTemplateData {

    private final Collection<OfficeData> officeOptions;
    private final Collection<EnumOptionData> pdcTypeOptions;
    private final Collection<EnumOptionData> chequeStatusOptions;

    private PostDatedChequeDetailSearchTemplateData(final Collection<OfficeData> officeOptions,
            final Collection<EnumOptionData> pdcTypeOptions, final Collection<EnumOptionData> chequeStatusOptions) {
        this.officeOptions = officeOptions;
        this.pdcTypeOptions = pdcTypeOptions;
        this.chequeStatusOptions = chequeStatusOptions;
    }

    public static PostDatedChequeDetailSearchTemplateData template(final Collection<OfficeData> officeOptions,
            final Collection<EnumOptionData> pdcTypeOptions, final Collection<EnumOptionData> chequeStatusOptions) {
        return new PostDatedChequeDetailSearchTemplateData(officeOptions, pdcTypeOptions, chequeStatusOptions);
    }

    public Collection<OfficeData> getOfficeOptions() {
        return this.officeOptions;
    }

    public Collection<EnumOptionData> getPdcTypeOptions() {
        return this.pdcTypeOptions;
    }

    public Collection<EnumOptionData> getChequeStatusOptions() {
        return this.chequeStatusOptions;
    }
}
