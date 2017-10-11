package com.finflux.risk.existingloans.data;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

import com.finflux.risk.creditbureau.configuration.data.CreditBureauData;

public class ExistingLoanTemplateData {

    private final Collection<CodeValueData> existingLoanSourceOption;
    private final Collection<CreditBureauData> creditBureauProductsOption;
    private final Collection<CodeValueData> lenderOption;
    private final Collection<CodeValueData> loanTypeOption;
    private final Collection<CodeValueData> externalLoanPurposeOption;
    private final Collection<EnumOptionData> loanTenaureOption;
    private final Collection<EnumOptionData> termPeriodFrequencyType;
    private final Collection<LoanStatusEnumData> loanStatusOption;

    private ExistingLoanTemplateData(final List<CodeValueData> existingLoanSourceOption,
            final Collection<CreditBureauData> creditBureauProductsOption, final List<CodeValueData> lenderOption,
            final List<CodeValueData> loanTypeOption, final List<CodeValueData> externalLoanPurposeOption,
            final Collection<EnumOptionData> loanTenaureOption, final Collection<EnumOptionData> termPeriodFrequencyType,
            final Collection<LoanStatusEnumData> loanStatusOption) {
        this.existingLoanSourceOption = existingLoanSourceOption;
        this.creditBureauProductsOption = creditBureauProductsOption;
        this.lenderOption = lenderOption;
        this.loanTypeOption = loanTypeOption;
        this.externalLoanPurposeOption = externalLoanPurposeOption;
        this.loanTenaureOption = loanTenaureOption;
        this.termPeriodFrequencyType = termPeriodFrequencyType;
        this.loanStatusOption = loanStatusOption;
    }

    public static ExistingLoanTemplateData template(final List<CodeValueData> existingLoanSourceOption,
            final Collection<CreditBureauData> creditBureauProductsOption, final List<CodeValueData> lenderOption,
            final List<CodeValueData> loanTypeOption, final List<CodeValueData> externalLoanPurposeOption,
            final Collection<EnumOptionData> loanTenaureOption, final Collection<EnumOptionData> termPeriodFrequencyType,
            final Collection<LoanStatusEnumData> loanStatusOption) {
        return new ExistingLoanTemplateData(existingLoanSourceOption, creditBureauProductsOption, lenderOption, loanTypeOption,
                externalLoanPurposeOption, loanTenaureOption, termPeriodFrequencyType, loanStatusOption);
    }

    public Collection<CodeValueData> getExistingLoanSourceOption() {
        return this.existingLoanSourceOption;
    }

    public Collection<CreditBureauData> getCreditBureauProductsOption() {
        return this.creditBureauProductsOption;
    }

    public Collection<CodeValueData> getLenderOption() {
        return this.lenderOption;
    }

    public Collection<CodeValueData> getLoanTypeOption() {
        return this.loanTypeOption;
    }

    public Collection<CodeValueData> getExternalLoanPurposeOption() {
        return this.externalLoanPurposeOption;
    }

    public Collection<EnumOptionData> getLoanTenaureOption() {
        return this.loanTenaureOption;
    }

    public Collection<EnumOptionData> getTermPeriodFrequencyType() {
        return this.termPeriodFrequencyType;
    }

    public Collection<LoanStatusEnumData> getLoanStatusOption() {
        return this.loanStatusOption;
    }
}
