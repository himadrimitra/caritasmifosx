package com.finflux.portfolio.cashflow.data;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public class CashFlowCategoryData {

    private final Long id;
    private final String name;
    private final String shortName;
    private final String description;
    private final EnumOptionData categoryEnum;
    private final EnumOptionData typeEnum;
    private final Boolean isActive;
    private Collection<IncomeExpenseData> incomeExpenseDatas;

    private CashFlowCategoryData(final Long id, final String name, final String shortName, final String description,
            final EnumOptionData categoryEnum, final EnumOptionData typeEnum, final Boolean isActive) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.categoryEnum = categoryEnum;
        this.typeEnum = typeEnum;
        this.isActive = isActive;
    }

    public static CashFlowCategoryData instance(final Long id, final String name, final String shortName, final String description,
            final EnumOptionData categoryEnum, final EnumOptionData typeEnum, final Boolean isActive) {
        return new CashFlowCategoryData(id, name, shortName, description, categoryEnum, typeEnum, isActive);
    }

    @SuppressWarnings("unused")
    public void addIncomeExpenseData(final IncomeExpenseData incomeExpenseData) {
        if (this.incomeExpenseDatas == null) {
            this.incomeExpenseDatas = new LinkedHashSet<IncomeExpenseData>();
        }
        this.incomeExpenseDatas.add(incomeExpenseData);
    }
}