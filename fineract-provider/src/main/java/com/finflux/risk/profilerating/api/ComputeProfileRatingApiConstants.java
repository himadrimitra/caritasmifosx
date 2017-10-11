package com.finflux.risk.profilerating.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ComputeProfileRatingApiConstants {

    public static final String COMPUTE_PROFILE_RATING_RESOURCE_NAME = "computeprofilerating";

    /**
     * Parameters
     */
    public final static String scopeEntityTypeParamName = "scopeEntityType";
    public final static String scopeEntityIdParamName = "scopeEntityId";
    public final static String entityTypeParamName = "entityType";
    public final static String entityIdParamName = "entityId";
    public final static String overriddenScoreParamName = "overriddenScore";

    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    public static final Set<String> CREATE_COMPUT_PROFILE_RATING_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            scopeEntityTypeParamName, scopeEntityIdParamName, entityTypeParamName, entityIdParamName, overriddenScoreParamName,
            localeParamName, dateFormatParamName));

}
