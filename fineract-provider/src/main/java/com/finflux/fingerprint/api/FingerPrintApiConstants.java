package com.finflux.fingerprint.api;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FingerPrintApiConstants {
    
    public static final String FINGER_PRINT_RESOURCE_NAME = "fingerPrint";
    
    public static final String FINGER_PRINT_AUTH_KEY = "45678";
    /**
     * Finger print Entity Types Enumerations
     */
    
    public static final String enumTypeRightThumb = "RIGHT_THUMB";
    public static final String enumTypeRightIndex = "RIGHT_INDEX";
    public static final String enumTypeRightMiddle = "RIGHT_MIDDLE";
    public static final String enumTypeRightRing = "RIGHT_RING";
    public static final String enumTypeRightPinky = "RIGHT_PINKY";
    public static final String enumTypeLeftThumb = "LEFT_THUMB";
    public static final String enumTypeLeftIndex = "LEFT_INDEX";
    public static final String enumTypeLeftMiddle = "LEFT_MIDDLE";
    public static final String enumTypeLeftRing = "LEFT_RING";
    public static final String enumTypeLeftPinky = "LEFT_PINKY";
    
    /**
     * Common Parameters
     */
    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    

    /**
     * Finger Print Data parameters
     */
    public static final String clientIdParamName = "clientId";
    public static final String fingerPrintResourceName = "fingerPrintData";
    public static final String fingerIdParamName = "fingerId";
    public static final String fingerDataParamName = "fingerData";
    
    public static final Set<String>CREATE_FINGER_PRINT_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(fingerPrintResourceName,fingerIdParamName,fingerDataParamName,localeParamName,dateFormatParamName));
}
