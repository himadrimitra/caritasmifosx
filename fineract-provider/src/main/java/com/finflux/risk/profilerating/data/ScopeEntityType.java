package com.finflux.risk.profilerating.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.risk.profilerating.api.ProfileRatingConfigApiConstants;

public enum ScopeEntityType {

    INVALID(0, "scopeEntityType.invalid"), //
    GROUP(1, "scopeEntityType.group"), //
    CENTER(2, "scopeEntityType.center"), //
    OFFICE(3, "scopeEntityType.office"); //

    private final Integer value;
    private final String code;

    private ScopeEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ScopeEntityType fromInt(final Integer frequency) {
        ScopeEntityType typeEnums = ScopeEntityType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    typeEnums = ScopeEntityType.GROUP;
                break;
                case 2:
                    typeEnums = ScopeEntityType.CENTER;
                break;
                case 3:
                    typeEnums = ScopeEntityType.OFFICE;
                break;
            }
        }
        return typeEnums;
    }

    public static Collection<EnumOptionData> options() {
        final Collection<EnumOptionData> options = new ArrayList<>();
        for (final ScopeEntityType type : values()) {
            final EnumOptionData enumOptionData = scopeEntityType(type.getValue());
            if (enumOptionData != null) {
                options.add(enumOptionData);
            }
        }
        return options;
    }

    public static EnumOptionData scopeEntityType(final int id) {
        return scopeEntityType(ScopeEntityType.fromInt(id));
    }

    public static EnumOptionData scopeEntityType(final ScopeEntityType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case GROUP:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeGroup);
            break;
            case CENTER:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeCenter);
            break;
            case OFFICE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), ProfileRatingConfigApiConstants.enumTypeOffice);
            break;
            default:
            break;

        }
        return optionData;
    }
}