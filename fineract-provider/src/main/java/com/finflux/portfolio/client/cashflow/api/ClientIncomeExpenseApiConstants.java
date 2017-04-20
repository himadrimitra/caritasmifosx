package com.finflux.portfolio.client.cashflow.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClientIncomeExpenseApiConstants {

    public static final String CLIENT_INCOME_EXPENSE = "clientincomeexpense";

    public static final String familyDetailsIdParamName = "familyDetailsId";
    public static final String incomeExpenseIdParamName = "incomeExpenseId";
    public static final String quintityParamName = "quintity";
    public static final String totalIncomeParamName = "totalIncome";
    public static final String totalExpenseParamName = "totalExpense";
    public static final String isMonthWiseIncomeParamName = "isMonthWiseIncome";
    public static final String isPrimaryIncomeParamName = "isPrimaryIncome";
    public static final String isRemmitanceIncomeParamName = "isRemmitanceIncome";
    public static final String isActiveParamName = "isActive";

    public static final String clientMonthWiseIncomeExpenseParamName = "clientMonthWiseIncomeExpense";
    public static final String monthParamName = "month";
    public static final String yearParamName = "year";
    public static final String incomeAmountParamName = "incomeAmount";
    public static final String expenseAmountParamName = "expenseAmount";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_CLIENT_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            familyDetailsIdParamName, incomeExpenseIdParamName, quintityParamName, totalIncomeParamName, totalExpenseParamName,
            isMonthWiseIncomeParamName, isPrimaryIncomeParamName, isActiveParamName, clientMonthWiseIncomeExpenseParamName,
            localeParamName, dateFormatParamName,isRemmitanceIncomeParamName));

    public static final Set<String> UPDATE_CLIENT_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            familyDetailsIdParamName, incomeExpenseIdParamName, quintityParamName, totalIncomeParamName, totalExpenseParamName,
            isMonthWiseIncomeParamName, isPrimaryIncomeParamName, isActiveParamName, clientMonthWiseIncomeExpenseParamName,
            localeParamName, dateFormatParamName,isRemmitanceIncomeParamName));

}