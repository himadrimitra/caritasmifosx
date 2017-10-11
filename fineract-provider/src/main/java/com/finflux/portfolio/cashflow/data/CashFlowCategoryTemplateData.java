package com.finflux.portfolio.cashflow.data;

import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class CashFlowCategoryTemplateData {

    private final Collection<EnumOptionData> cashFlowCategoryTypeOptions;
    private final Collection<EnumOptionData> cashFlowTypeOptions;

    private CashFlowCategoryTemplateData(final Collection<EnumOptionData> cashFlowCategoryTypeOptions,
            final Collection<EnumOptionData> cashFlowTypeOptions) {
        this.cashFlowCategoryTypeOptions = cashFlowCategoryTypeOptions;
        this.cashFlowTypeOptions = cashFlowTypeOptions;
    }

    public static CashFlowCategoryTemplateData template(final Collection<EnumOptionData> cashFlowCategoryTypeOptions,
            final Collection<EnumOptionData> cashFlowTypeOptions) {
        return new CashFlowCategoryTemplateData(cashFlowCategoryTypeOptions, cashFlowTypeOptions);
    }
}