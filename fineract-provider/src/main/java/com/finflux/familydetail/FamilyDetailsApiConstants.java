package com.finflux.familydetail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FamilyDetailsApiConstants {

    // Resource Name
    public static final String FAMILY_DETAIL_RESOURCE_NAME = "familydetail";

    // Input Params
    public static final String clientIdParamName = "clientId";
    public static final String salutationIdParamName = "salutationId";
    public static final String firstNameParamName = "firstname";
    public static final String middleNameParamName = "middlename";
    public static final String lastNameParamName = "lastname";
    public static final String relationshipIdParamName = "relationshipId";
    public static final String genderIdParamName = "genderId";
    public static final String dateOfBirthParamName = "dateOfBirth";
    public static final String ageParamName = "age";
    public static final String occupationDetailsIdParamName = "occupationDetailsId";
    public static final String educationIdParamName = "educationId";
    public static final String isDependentParamName = "isDependent";
    public static final String isSeriousIllnessParamName = "isSeriousIllness";
    public static final String isDeceasedParamName = "isDeceased";

    public static final String idParamName = "id";

    public static final String clientParamName = "client";
    public static final String salutationParamName = "Salutation";
    public static final String relationshipParamName = "Relationship";
    public static final String genderParamName = "Gender";
    public static final String occupationalDetailsParamName = "Occupation";
    public static final String educationParamName = "Education";

    public static final String familyMembersParamName = "familyMembers";

    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";
    public static final String ClientReference = "clientReference";
    public static final String removeFamilyMemberClientAssociation = "removeFamilyMemberClientAssociation";

    public static final Set<String> CREATE_FAMILYDETAILS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(clientParamName,
            salutationIdParamName, firstNameParamName, middleNameParamName, lastNameParamName, relationshipIdParamName, genderIdParamName,
            dateOfBirthParamName, ageParamName, occupationDetailsIdParamName, educationParamName, educationIdParamName,
            isDependentParamName, isSeriousIllnessParamName, isDeceasedParamName, localeParamName, dateFormatParamName,
            ClientReference));

    public static final Set<String> UPDATE_FAMILYDETAILS_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(clientParamName,
            salutationIdParamName, firstNameParamName, middleNameParamName, lastNameParamName, relationshipIdParamName, genderIdParamName,
            dateOfBirthParamName, ageParamName, occupationDetailsIdParamName, educationParamName, educationIdParamName,
            isDependentParamName, isSeriousIllnessParamName, isDeceasedParamName, localeParamName, dateFormatParamName,
            ClientReference));

}
