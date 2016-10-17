package com.finflux.portfolio.cashflow.service;

import java.util.Collection;

import com.finflux.portfolio.cashflow.data.IncomeExpenseData;
import com.finflux.portfolio.cashflow.data.IncomeExpenseTemplateData;

public interface IncomeExpenseReadPlatformService {

    IncomeExpenseTemplateData retrieveIncomeExpenseTemplate(final Boolean isActive);

    Collection<IncomeExpenseData> retrieveAll(final Long cashFlowCategoryId, final Boolean isActive,
            final Boolean isFetchCashflowCategoryData);

    IncomeExpenseData retrieveOne(final Long incomeExpenseId, final Boolean isFetchCashflowCategoryData);
}