package com.finflux.portfolio.cashflow.service;

import java.util.Collection;

import com.finflux.portfolio.cashflow.data.CashFlowCategoryData;
import com.finflux.portfolio.cashflow.data.CashFlowCategoryTemplateData;

public interface CashFlowCategoryReadPlatformService {

    CashFlowCategoryTemplateData retrieveCashFlowTemplate();

    Collection<CashFlowCategoryData> retrieveAll(final Integer categoryEnumId, final Integer typeEnumId, final Boolean isActive,
            final Boolean isFetchIncomeExpenseDatas);

    CashFlowCategoryData retrieveOne(final Long cashFlowCategoryId, final Boolean isFetchIncomeExpenseDatas);
}