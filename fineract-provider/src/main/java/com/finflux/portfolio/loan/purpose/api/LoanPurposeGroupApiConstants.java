package com.finflux.portfolio.loan.purpose.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoanPurposeGroupApiConstants {

    public static final String LOAN_PURPOSE_GROUP_RESOURCE_NAME = "loanpurposegroup";
    public static final String LOAN_PURPOSE_RESOURCE_NAME = "loanpurpose";

    public static final String loanPurposeGroupType = "LoanPurposeGroupType";

    /**
     * Loan Purpose Group
     */
    public static final String CONSUMPTION_WISE = "Consumption Wise";
    public static final String SECTOR_WISE = "Sector Wise";
    public static final String PSL = "Priority Sector Lending";

    public static final String nameParamName = "name";
    public static final String systemCodeParamName = "systemCode";
    public static final String descriptionParamName = "description";
    public static final String loanPurposeGroupTypeIdParamName = "loanPurposeGroupTypeId";
    public static final String loanPurposeGroupIdsParamName = "loanPurposeGroupIds";
    public static final String isActiveParamName = "isActive";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_LOAN_PURPOSE_GROUP_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            systemCodeParamName, descriptionParamName, loanPurposeGroupTypeIdParamName, isActiveParamName, localeParamName,
            dateFormatParamName));

    public static final Set<String> UPDATE_LOAN_PURPOSE_GROUP_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            descriptionParamName, loanPurposeGroupTypeIdParamName, isActiveParamName, localeParamName, dateFormatParamName));

    public static final Set<String> DELETE_LOAN_PURPOSE_GROUP_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(isActiveParamName));

    public static final Set<String> CREATE_LOAN_PURPOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            systemCodeParamName, descriptionParamName, loanPurposeGroupIdsParamName, isActiveParamName, localeParamName,
            dateFormatParamName));

    public static final Set<String> UPDATE_LOAN_PURPOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(nameParamName,
            descriptionParamName, loanPurposeGroupIdsParamName, isActiveParamName, localeParamName, dateFormatParamName));

    public static final Set<String> DELETE_LOAN_PURPOSE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(isActiveParamName));
}