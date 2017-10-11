package com.finflux.portfolio.cashflow.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class IncomeExpenseApiConstants {

    public static final String INCOME_EXPENSE_RESOURCE_NAME = "incomeexpense";

    public static final String LOW = "Low";
    public static final String MEDIUM = "Medium";
    public static final String HIGH = "High";

    public static final String cashFlowCategoryIdParamName = "cashflowCategoryId";
    public static final String nameParamName = "name";
    public static final String shortNameParamName = "shortName";
    public static final String descriptionParamName = "description";
    public static final String isQuantifierNeededParamName = "isQuantifierNeeded";
    public static final String quantifierLabelParamName = "quantifierLabel";
    public static final String isCaptureMonthWiseIncomeParamName = "isCaptureMonthWiseIncome";
    public static final String stabilityEnumIdParamName = "stabilityEnumId";
    public static final String defaultIncomeParamName = "defaultIncome";
    public static final String defaultExpenseParamName = "defaultExpense";
    public static final String isActiveParamName = "isActive";
    public static final String cashflowCategoryDataParamName="cashFlowCategoryData";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            cashFlowCategoryIdParamName, nameParamName, shortNameParamName, descriptionParamName, isQuantifierNeededParamName,
            quantifierLabelParamName, isCaptureMonthWiseIncomeParamName, stabilityEnumIdParamName, defaultIncomeParamName,
            defaultExpenseParamName, isActiveParamName, localeParamName, dateFormatParamName));

    public static final Set<String> UPDATE_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            shortNameParamName, descriptionParamName, isQuantifierNeededParamName, quantifierLabelParamName,
            isCaptureMonthWiseIncomeParamName, stabilityEnumIdParamName, defaultIncomeParamName, defaultExpenseParamName,
            isActiveParamName, localeParamName, dateFormatParamName,cashflowCategoryDataParamName,cashFlowCategoryIdParamName));

    public static final Set<String> DELETE_INCOME_EXPENSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(isActiveParamName));

}