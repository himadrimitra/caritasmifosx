package com.finflux.portfolio.cashflow.data;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class IncomeExpenseData {

    private final Long id;
    private final Long cashflowCategoryId;
    private final String name;
    private final String description;
    private final Boolean isQuantifierNeeded;
    private final String quantifierLabel;
    private final Boolean isCaptureMonthWiseIncome;
    private final EnumOptionData stabilityEnum;
    private final BigDecimal defaultIncome;
    private final BigDecimal defaultExpense;
    private final Boolean isActive;
    private CashFlowCategoryData cashFlowCategoryData;

    private IncomeExpenseData(final Long id, final Long cashflowCategoryId, final String name, final String description,
            final Boolean isQuantifierNeeded, final String quantifierLabel, final Boolean isCaptureMonthWiseIncome,
            final EnumOptionData stabilityEnum, final BigDecimal defaultIncome, final BigDecimal defaultExpense, final Boolean isActive) {
        this.id = id;
        this.cashflowCategoryId = cashflowCategoryId;
        this.name = name;
        this.description = description;
        this.isQuantifierNeeded = isQuantifierNeeded;
        this.quantifierLabel = quantifierLabel;
        this.isCaptureMonthWiseIncome = isCaptureMonthWiseIncome;
        this.stabilityEnum = stabilityEnum;
        this.defaultIncome = defaultIncome;
        this.defaultExpense = defaultExpense;
        this.isActive = isActive;
    }

    public static IncomeExpenseData instance(final Long id, final Long cashflowCategoryId, final String name, final String description,
            final Boolean isQuantifierNeeded, final String quantifierLabel, final Boolean isCaptureMonthWiseIncome,
            final EnumOptionData stabilityEnum, final BigDecimal defaultIncome, final BigDecimal defaultExpense, final Boolean isActive) {
        return new IncomeExpenseData(id, cashflowCategoryId, name, description, isQuantifierNeeded, quantifierLabel,
                isCaptureMonthWiseIncome, stabilityEnum, defaultIncome, defaultExpense, isActive);
    }

    public void addCashFlowCategoryData(final CashFlowCategoryData cashFlowCategoryData) {
        this.cashFlowCategoryData = cashFlowCategoryData;
    }
}