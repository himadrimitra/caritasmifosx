package com.finflux.ruleengine.eligibility.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoanProductEligibilityApiConstants {

    public static final String FACTOR_CONFIGURATION_RESOURCE_NAME = "riskFactorConfiguration";
    /**
     * Loan Purpose Group
     */


    public static final String loanProductIdParamName = "loanProductId";
    public static final String isActiveParamName = "isActive";
    public static final String criteriasParamName = "criterias";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_LOAN_PRODUCT_ELIGIBILITY_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            loanProductIdParamName, isActiveParamName, criteriasParamName));

}