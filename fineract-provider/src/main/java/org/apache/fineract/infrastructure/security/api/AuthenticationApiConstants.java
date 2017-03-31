package org.apache.fineract.infrastructure.security.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AuthenticationApiConstants {

    public static final String userNameParamName = "username";
    public static final String passwordParamName = "password";
    public static final String AUTHENICATION_RESOURCE_NAME = "authentication";

    public static final Set<String> AUTHENTICATION_PARAMETER_NAMES = new HashSet<>(Arrays.asList(userNameParamName, passwordParamName));
}
