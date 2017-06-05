package org.apache.fineract.portfolio.loanproduct.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class LoanProductTemplateData {

    private final Collection<EnumOptionData> loanProductApplicableForLoanTypeOptions;
    private final Collection<EnumOptionData> clientProfileTypeOptions;
    private final Collection<EnumOptionData> legalFormOptions;
    private final Collection<CodeValueData> clientTypeOptions;
    private final Collection<CodeValueData> clientClassificationOptions;

    private LoanProductTemplateData(final Collection<EnumOptionData> loanProductApplicableForLoanTypeOptions,
            final Collection<EnumOptionData> clientProfileTypeOptions, final Collection<EnumOptionData> legalFormOptions,
            final Collection<CodeValueData> clientTypeOptions, final Collection<CodeValueData> clientClassificationOptions) {
        this.loanProductApplicableForLoanTypeOptions = loanProductApplicableForLoanTypeOptions;
        this.clientProfileTypeOptions = clientProfileTypeOptions;
        this.legalFormOptions = legalFormOptions;
        this.clientTypeOptions = clientTypeOptions;
        this.clientClassificationOptions = clientClassificationOptions;
    }

    public static LoanProductTemplateData template(final Collection<EnumOptionData> loanProductApplicableForLoanTypeOptions,
            final Collection<EnumOptionData> clientProfileTypeOptions, final Collection<EnumOptionData> legalFormOptions,
            final Collection<CodeValueData> clientTypeOptions, final Collection<CodeValueData> clientClassificationOptions) {
        return new LoanProductTemplateData(loanProductApplicableForLoanTypeOptions, clientProfileTypeOptions, legalFormOptions,
                clientTypeOptions, clientClassificationOptions);
    }

    public Collection<EnumOptionData> getLoanProductApplicableForLoanTypeOptions() {
        return this.loanProductApplicableForLoanTypeOptions;
    }

    public Collection<EnumOptionData> getClientProfileTypeOptions() {
        return this.clientProfileTypeOptions;
    }

    public Collection<EnumOptionData> getLegalFormOptions() {
        return this.legalFormOptions;
    }

    public Collection<CodeValueData> getClientTypeOptions() {
        return this.clientTypeOptions;
    }

    public Collection<CodeValueData> getClientClassificationOptions() {
        return this.clientClassificationOptions;
    }
}
