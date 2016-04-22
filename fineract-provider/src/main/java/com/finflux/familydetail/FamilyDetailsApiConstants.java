package com.finflux.familydetail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FamilyDetailsApiConstants {

    public static final String genderIdParamName = "genderId";
    public static final String occupationDetailsIdParamName = "OccupationDetailsId";
    public static final String salutationIdParamName = "salutationId";
    public static final String educationIdParamName = "educationId";
    public static final String relationshipIdParamName = "relationshipId";
    public static final String dateFormatParamName = "dateFormat";

    // Resource Name
    public static final String FAMILY_DETAIL_RESOURCE_NAME = "familydetail";

    // Input Params
    public static final String idParamName = "id";
    public static final String clientParamName = "client";
    public static final String salutationParamName = "Salutation";
    public static final String firstnameParamName = "firstname";
    public static final String middlenameParamName = "middlename";
    public static final String lastnameParamName = "lastname";
    public static final String relationshipParamName = "Relationship";
    public static final String genderParamName = "Gender";
    public static final String dobParamName = "dateOfBirth";
    public static final String ageParamName = "age";
    public static final String occupationalDetailsParamName = "Occupation";
    public static final String educationParamName = "Education";
    public static final String localeParamName = "locale";

    public static final Set<String> FAMILYDETAILS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(idParamName, clientParamName,
            salutationParamName, firstnameParamName, middlenameParamName, lastnameParamName, relationshipParamName, genderIdParamName,
            genderParamName, dobParamName, ageParamName, localeParamName, occupationalDetailsParamName, educationParamName,
            dateFormatParamName, occupationDetailsIdParamName, salutationIdParamName, educationIdParamName, relationshipIdParamName));

    public static final String familyMembersParamName = "familyMembers";

}
