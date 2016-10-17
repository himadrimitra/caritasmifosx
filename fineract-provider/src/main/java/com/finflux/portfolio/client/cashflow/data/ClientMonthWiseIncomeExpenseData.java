package com.finflux.portfolio.client.cashflow.data;

import java.math.BigDecimal;

public class ClientMonthWiseIncomeExpenseData {

    private final Long id;
    private final Integer month;
    private final Integer year;
    private final BigDecimal incomeAmount;
    private final BigDecimal expenseAmount;
    private final Boolean isActive;

    private ClientMonthWiseIncomeExpenseData(final Long id, final Integer month, final Integer year, final BigDecimal incomeAmount,
            final BigDecimal expenseAmount, final Boolean isActive) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.incomeAmount = incomeAmount;
        this.expenseAmount = expenseAmount;
        this.isActive = isActive;
    }

    public static ClientMonthWiseIncomeExpenseData instance(final Long id, final Integer month, final Integer year,
            final BigDecimal incomeAmount, final BigDecimal expenseAmount, final Boolean isActive) {
        return new ClientMonthWiseIncomeExpenseData(id, month, year, incomeAmount, expenseAmount, isActive);
    }
}