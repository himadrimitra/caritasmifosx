package com.finflux.portfolio.loan.utilization.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoanUtilizationCheckApiConstants {

    public static final String LOAN_UTILIZATION_CHECK_RESOURCE_NAME = "loanutilizationcheck";

    public static final String loanUtilizationChecksParamName = "loanUtilizationChecks";
    public static final String idParamName = "id";
    public static final String isAuditeScheduledOnParamName = "isAuditeScheduledOn";
    public static final String toBeAuditedByIdParamName = "toBeAuditedById";
    public static final String auditeScheduledOnParamName = "auditeScheduledOn";
    public static final String auditDoneByIdParamName = "auditDoneById";
    public static final String auditDoneOnParamName = "auditDoneOn";
    public static final String commentParamName = "comment";
    public static final String loanPurposeIdParamName = "loanPurposeId";
    public static final String loanIdParamName = "loanId";
    public static final String isSameAsOriginalPurposeParamName = "isSameAsOriginalPurpose";
    public static final String amountParamName = "amount";
    public static final String loanUtilizationDetailsParamName = "utilizationDetails";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_LOAN_UTILIZATION_CHECK_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            loanUtilizationChecksParamName, loanUtilizationDetailsParamName, loanIdParamName, isAuditeScheduledOnParamName,
            toBeAuditedByIdParamName, auditeScheduledOnParamName, auditDoneByIdParamName, auditDoneOnParamName, loanPurposeIdParamName,
            isSameAsOriginalPurposeParamName, amountParamName, commentParamName, localeParamName, dateFormatParamName));

    public static final Set<String> UPDATE_LOAN_UTILIZATION_CHECK_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            loanUtilizationChecksParamName, loanUtilizationDetailsParamName, idParamName, loanIdParamName, isAuditeScheduledOnParamName,
            toBeAuditedByIdParamName, auditeScheduledOnParamName, auditDoneByIdParamName, auditDoneOnParamName, loanPurposeIdParamName,
            isSameAsOriginalPurposeParamName, amountParamName, commentParamName, localeParamName, dateFormatParamName));
}