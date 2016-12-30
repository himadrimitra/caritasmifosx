package com.finflux.risk.profilerating.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;

public enum ProfileRatingType {

    INVALID(0, "profileRatingType.invalid"), //
    CLIENT(1, "profileRatingType.client"), //
    GROUP(2, "profileRatingType.group"), //
    CENTER(3, "profileRatingType.center"), //
    OFFICE(4, "profileRatingType.office"), //
    STAFF(5, "profileRatingType.staff"), //
    VILLAGE(6, "profileRatingType.village");// ;

    private final Integer value;
    private final String code;

    private ProfileRatingType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ProfileRatingType fromInt(final Integer frequency) {
        ProfileRatingType typeEnums = ProfileRatingType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    typeEnums = ProfileRatingType.CLIENT;
                break;
                case 2:
                    typeEnums = ProfileRatingType.GROUP;
                break;
                case 3:
                    typeEnums = ProfileRatingType.CENTER;
                break;
                case 4:
                    typeEnums = ProfileRatingType.OFFICE;
                break;
                case 5:
                    typeEnums = ProfileRatingType.STAFF;
                break;
                case 6:
                    typeEnums = ProfileRatingType.VILLAGE;
                break;
            }
        }
        return typeEnums;
    }

    public static ProfileRatingType fromString(final String frequency) {
        ProfileRatingType typeEnums = ProfileRatingType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case ProfileRatingConfigApiConstants.enumTypeClient:
                    typeEnums = ProfileRatingType.CLIENT;
                break;
                case ProfileRatingConfigApiConstants.enumTypeGroup:
                    typeEnums = ProfileRatingType.GROUP;
                break;
                case ProfileRatingConfigApiConstants.enumTypeCenter:
                    typeEnums = ProfileRatingType.CENTER;
                break;
                case ProfileRatingConfigApiConstants.enumTypeOffice:
                    typeEnums = ProfileRatingType.OFFICE;
                break;
                case ProfileRatingConfigApiConstants.enumTypeStaff:
                    typeEnums = ProfileRatingType.STAFF;
                break;
                case ProfileRatingConfigApiConstants.enumTypeVillage:
                    typeEnums = ProfileRatingType.VILLAGE;
                break;

            }
        }
        return typeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final ProfileRatingType enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final ProfileRatingType enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> typeOptions = new ArrayList<>();
        for (final ProfileRatingType enumType : values()) {
            final EnumOptionData enumOptionData = profileRatingType(enumType.getValue());
            if (enumOptionData != null) {
                typeOptions.add(enumOptionData);
            }
        }
        return typeOptions;
    }

    public static EnumOptionData profileRatingType(final int id) {
        return profileRatingType(ProfileRatingType.fromInt(id));
    }

    public static EnumOptionData profileRatingType(final ProfileRatingType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CLIENT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeClient);
            break;
            case GROUP:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeGroup);
            break;
            case CENTER:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeCenter);
            break;
            case OFFICE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeOffice);
            break;
            case STAFF:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeStaff);
            break;
            case VILLAGE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(),
                        ProfileRatingConfigApiConstants.enumTypeVillage);
            break;
            default:
            break;

        }
        return optionData;
    }

    private static final Map<String, ProfileRatingType> entityTypeNameToEnumMap = new HashMap<>();

    static {
        for (final ProfileRatingType entityType : ProfileRatingType.values()) {
            entityTypeNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static ProfileRatingType getEntityType(String entityType) {
        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
    }
}
