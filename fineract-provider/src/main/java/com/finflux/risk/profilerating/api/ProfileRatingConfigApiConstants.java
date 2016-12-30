package com.finflux.risk.profilerating.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProfileRatingConfigApiConstants {

    public static final String PROFILERATINGCONFIG_RESOURCE_NAME = "profileratingconfig";

    /**
     * Entity Types Enumerations
     */
    public static final String enumTypeClient = "CLIENT";
    public static final String enumTypeGroup = "GROUP";
    public static final String enumTypeCenter = "CENTER";
    public static final String enumTypeOffice = "OFFICE";
    public static final String enumTypeStaff = "STAFF";
    public static final String enumTypeVillage = "VILLAGE";

    /**
     * Parameters
     */
    public static final String typeParamName = "type";
    public static final String criteriaIdParamName = "criteriaId";
    public final static String isActiveParamName = "isActive";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    /**
     * Request Data Parameters
     */
    public static final Set<String> CREATE_PROFILE_RATING_CONFIG_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(typeParamName,
            criteriaIdParamName, isActiveParamName, localeParamName, dateFormatParamName));

    public static final Set<String> UPDATE_PROFILE_RATING_CONFIG_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(typeParamName,
            criteriaIdParamName, isActiveParamName, localeParamName, dateFormatParamName));

}
