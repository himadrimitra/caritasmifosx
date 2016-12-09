package org.apache.fineract.portfolio.cgt.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CgtDayApiConstants {

    public static final String CGT_DAY_RESOURCE_NAME = "cgtDay";
    public static final String cgtDayIdParamName = "centerId";
    public static final String cgtIdParamName = "cgtId";
    public static final String scheduledDateParamName = "scheduledDate";
    public static final String completedDateParamName = "completedDate";
    public static final String loanOfficerIdParamName = "loanOfficerId";
    public static final String clientIdsParamName = "clientIds";
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String locationParamName = "location";
    public static final String noteParamName = "note";
    public static final String cgtDayStatusParamName = "cgtDayStatus";
    public static final String attendanceTypeParamName = "attendanceType";
    public static final String idParamName = "id";
    public static final String cgtDayCreationTypeParamName = "cgtDayCreationType";

    public static final Set<String> CGT_DAY_CREATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(cgtDayCreationTypeParamName));

    public static final Set<String> CGT_DAY_UPDATE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(scheduledDateParamName,
            loanOfficerIdParamName, localeParamName, dateFormatParamName, locationParamName));

    public static final Set<String> CGT_DAY_COMPLETE_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(completedDateParamName,
            noteParamName, localeParamName, dateFormatParamName, clientIdsParamName));

}
