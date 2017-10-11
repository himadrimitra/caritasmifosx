package com.finflux.portfolio.cashflow.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class IncomeExpenseTemplateData {

    private final Collection<EnumOptionData> stabilityEnumOptions;
    private final Collection<CashFlowCategoryData> cashFlowCategoryOptions;

    private IncomeExpenseTemplateData(final Collection<EnumOptionData> stabilityEnumOptions,
            final Collection<CashFlowCategoryData> cashFlowCategoryOptions) {
        this.stabilityEnumOptions = stabilityEnumOptions;
        this.cashFlowCategoryOptions = cashFlowCategoryOptions;
    }

    public static IncomeExpenseTemplateData template(final Collection<EnumOptionData> stabilityEnumOptions,
            final Collection<CashFlowCategoryData> cashFlowCategoryOptions) {
        return new IncomeExpenseTemplateData(stabilityEnumOptions, cashFlowCategoryOptions);
    }
}