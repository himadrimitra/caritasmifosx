package org.apache.fineract.portfolio.cgt.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CgtApiConstants {

    public static final String CGT_RESOURCE_NAME = "cgt";
    public static final String entityTypeParamName = "entityType";
    public static final String entityIdParamName = "entityId";
    public static final String cgtIdParamName = "cgtId";
    public static final String locationParamName = "location";
    public static final String loanOfficerIdParamName = "loanOfficerId";
    public static final String secondaryLoanOfficerIdParamName = "secondaryLoanOfficerId";
    public static final String clientIdsParamName = "clientIds";
    public static final String expectedStartDateParamName = "expectedStartDate";
    public static final String expectedEndDateParamName = "expectedEndDate";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String noteParamName = "note";
    public static final String cgtStatusParamName = "cgtStatus";
    public static final String cgtStatusRejectParamName = "reject";
    public static final String cgtStatusCompleteParamName = "complete";
    public static final String completedDateParamName = "completedDate";
    public static final String rejectedDateParamName = "rejectedDate";
    public static final String minumumCgtDaysParamName = "minumumCgtDays";
    public static final String maximumCgtDaysParamName = "maximumCgtDays";

    public static final Set<String> CGT_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(entityTypeParamName,
            locationParamName, loanOfficerIdParamName, secondaryLoanOfficerIdParamName, clientIdsParamName, expectedStartDateParamName,
            entityIdParamName, expectedEndDateParamName, localeParamName, noteParamName, dateFormatParamName));

    public static final Set<String> CGT_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(entityTypeParamName,
            locationParamName, loanOfficerIdParamName, secondaryLoanOfficerIdParamName, clientIdsParamName, expectedStartDateParamName,
            cgtIdParamName, expectedEndDateParamName, localeParamName, dateFormatParamName, noteParamName, entityIdParamName));

    public static final Set<String> CGT_REJECT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(noteParamName, rejectedDateParamName,
            dateFormatParamName, localeParamName));

    public static final Set<String> CGT_COMPLETE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(noteParamName,
            completedDateParamName, dateFormatParamName, localeParamName));

}
