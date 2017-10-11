package com.finflux.portfolio.client.cashflow.service;

import java.util.Collection;

import com.finflux.portfolio.client.cashflow.data.ClientIncomeExpenseData;

public interface ClientIncomeExpenseReadPlatformService {

    Collection<ClientIncomeExpenseData> retrieveAll(final Long clientId, final Boolean isFetchFamilyDeatilsIncomeAndExpense,
            final Boolean isActive);

    ClientIncomeExpenseData retrieveOne(final Long clientIncomeExpenseId);
}