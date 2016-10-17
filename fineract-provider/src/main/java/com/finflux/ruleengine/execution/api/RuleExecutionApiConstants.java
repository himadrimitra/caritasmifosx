package com.finflux.ruleengine.execution.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RuleExecutionApiConstants {

    public static final String RULE_EXECUTION_RESOURCE_NAME = "riskFactorConfiguration";
    public static final String LOAN_PRODUCT_ELIGIBILITY_EXECUTION_RESOURCE_NAME = "loanProductEligibilityExecution";
    /**
     * Loan Purpose Group
     */
    public static final String grouping = "Grouping";
    public static final String consumption = "Consumption";


    public static final String nameParamName = "name";
    public static final String unameParamName = "uname";
    public static final String descriptionParamName = "description";
    public static final String outputConfigurationParamName = "outputConfiguration";
    public static final String isActiveParamName = "isActive";
    public static final String bucketsParamName = "buckets";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_RULE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            nameParamName, unameParamName, descriptionParamName,outputConfigurationParamName, bucketsParamName,isActiveParamName));

}