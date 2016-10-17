package com.finflux.portfolio.cashflow.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CashFlowCategoryApiConstants {

    public static final String CASH_FLOW_RESOURCE_NAME = "cashflow";

    public static final String OCCUPATION = "Occupation";
    public static final String ASSET = "Asset";
    public static final String HOUSEHOLDEXPENSE = "House Hold Expense";

    public static final String INCOME = "Income";
    public static final String EXPENSE = "Expense";

    public static final String nameParamName = "name";
    public static final String shortNameParamName = "shortName";
    public static final String descriptionParamName = "description";
    public static final String categoryEnumIdParamName = "categoryEnumId";
    public static final String typeEnumIdParamName = "typeEnumId";
    public static final String isActiveParamName = "isActive";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_CASH_FLOW_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            shortNameParamName, descriptionParamName, categoryEnumIdParamName, typeEnumIdParamName, isActiveParamName, localeParamName,
            dateFormatParamName));

    public static final Set<String> UPDATE_CASH_FLOW_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            descriptionParamName, categoryEnumIdParamName, typeEnumIdParamName, isActiveParamName, localeParamName, dateFormatParamName));

    public static final Set<String> DELETE_CASH_FLOW_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(isActiveParamName));

}