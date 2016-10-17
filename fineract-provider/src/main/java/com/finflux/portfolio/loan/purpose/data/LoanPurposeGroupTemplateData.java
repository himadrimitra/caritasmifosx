package com.finflux.portfolio.loan.purpose.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;

public class LoanPurposeGroupTemplateData {

    private final Collection<CodeValueData> loanPurposeGroupTypeOptions;

    public LoanPurposeGroupTemplateData(final Collection<CodeValueData> loanPurposeGroupTypeOptions) {
        this.loanPurposeGroupTypeOptions = loanPurposeGroupTypeOptions;
    }

    public static LoanPurposeGroupTemplateData template(final Collection<CodeValueData> loanPurposeGroupTypeOptions) {
        return new LoanPurposeGroupTemplateData(loanPurposeGroupTypeOptions);
    }

    public Collection<CodeValueData> getLoanPurposeGroupTypeOptions() {
        return this.loanPurposeGroupTypeOptions;
    }
}