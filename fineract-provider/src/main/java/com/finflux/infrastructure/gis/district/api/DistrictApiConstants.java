package com.finflux.infrastructure.gis.district.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DistrictApiConstants {

    public static final String DISTRICT_RESOURCE_NAME = "district";

    public static final String REJECT_COMMAND_NAME = "reject";
    public static final String ACTIVATE_COMMAND_NAME = "activate";

    public static final String districtNameParamName = "districtName";
    public static final String districtCodeParamName = "districtCode";
    public static final String stateIdParamName = "stateId";

    public static final Set<String> supportedParameters = new HashSet<>(
            Arrays.asList(districtCodeParamName, districtNameParamName, stateIdParamName));
}
