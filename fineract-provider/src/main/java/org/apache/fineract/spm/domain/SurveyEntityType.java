package org.apache.fineract.spm.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.survey.api.SurveyApiConstants;

public enum SurveyEntityType {

    INVALID(0, "surveyEntityType.invalid"), //
    CLIENTS(1, "surveyEntityType.clients"), //
    GROUPS(2, "surveyEntityType.groups"), //
    CENTERS(3, "surveyEntityType.centers"), //
    OFFICES(4, "surveyEntityType.offices"), //
    STAFFS(5, "surveyEntityType.staffs"), //
    LOANAPPLICATIONS(6, "surveyEntityType.loanApplications"), //
    LOANS(7, "surveyEntityType.loans"),//
    VILLAGE(8, "surveyEntityType.villages") ;
    
    private final Integer value;
    private final String code;

    private SurveyEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static SurveyEntityType fromInt(final Integer frequency) {
        SurveyEntityType surveyEntityType = SurveyEntityType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    surveyEntityType = SurveyEntityType.CLIENTS;
                break;
                case 2:
                    surveyEntityType = SurveyEntityType.GROUPS;
                break;
                case 3:
                    surveyEntityType = SurveyEntityType.CENTERS;
                break;
                case 4:
                    surveyEntityType = SurveyEntityType.OFFICES;
                break;
                case 5:
                    surveyEntityType = SurveyEntityType.STAFFS;
                break;
                case 6:
                    surveyEntityType = SurveyEntityType.LOANAPPLICATIONS;
                break;
                case 7:
                    surveyEntityType = SurveyEntityType.LOANS;
                break;
                case 8:
                    surveyEntityType = VILLAGE ;
                break ;
            }
        }
        return surveyEntityType;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final SurveyEntityType enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final SurveyEntityType enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> entityOptions = new ArrayList<>();
        for (final SurveyEntityType enumType : values()) {
            final EnumOptionData enumOptionData = surveyEntityType(enumType.getValue());
            if (enumOptionData != null) {
                entityOptions.add(enumOptionData);
            }
        }
        return entityOptions;
    }

    public static EnumOptionData surveyEntityType(final int id) {
        return surveyEntityType(SurveyEntityType.fromInt(id));
    }

    public static EnumOptionData surveyEntityType(final SurveyEntityType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CLIENTS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeClients);
            break;
            case GROUPS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeGroups);
            break;
            case CENTERS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeCenters);
            break;
            case OFFICES:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeOffices);
            break;
            case STAFFS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeStaffs);
            break;
            case LOANAPPLICATIONS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeLoanApplications);
            break;
            case LOANS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeLoans);
            break;
            case VILLAGE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), SurveyApiConstants.enumTypeVillages);
            break;
            default:
            break;
        }
        return optionData;
    }
}