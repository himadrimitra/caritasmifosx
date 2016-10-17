package com.finflux.familydetail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FamilyDetailsSummaryApiConstants {

    // Resource Name
    public static final String FAMILY_DETAILS_SUMMARY_RESOURCE_NAME = "familydetailssummary";

    // Input Params
    public static final String noOfFamilyMembersParamName = "noOfFamilyMembers";
    public static final String noOfDependentMinorsParamName = "noOfDependentMinors";
    public static final String noOfDependentAdultsParamName = "noOfDependentAdults";
    public static final String noOfDependentSeniorsParamName = "noOfDependentSeniors";
    public static final String noOfDependentsWithSeriousIllnessParamName = "noOfDependentsWithSeriousIllness";

    public static final String localeParamName = "locale";
    public static final String dateFormatParamName = "dateFormat";

    public static final Set<String> CREATE_FAMILY_DETAILS_SUMMARY_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            noOfFamilyMembersParamName, noOfDependentMinorsParamName, noOfDependentAdultsParamName, noOfDependentSeniorsParamName,
            noOfDependentsWithSeriousIllnessParamName, localeParamName, dateFormatParamName));

    public static final Set<String> UPDATE_FAMILY_DETAILS_SUMMARY_REQUEST_DATA_PARAMETERS = new HashSet<>(Arrays.asList(
            noOfFamilyMembersParamName, noOfDependentMinorsParamName, noOfDependentAdultsParamName, noOfDependentSeniorsParamName,
            noOfDependentsWithSeriousIllnessParamName, localeParamName, dateFormatParamName));
}