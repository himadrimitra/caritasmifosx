package com.finflux.portfolio.cashflow.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.cashflow.api.IncomeExpenseApiConstants;

public enum StabilityTypeEnums {

    INVALID(0, "stabilityType.invalid"), //
    LOW(1, "stabilityType.low"), //
    MEDIUM(2, "stabilityType.medium"), //
    HIGH(3, "stabilityType.high");

    private final Integer value;
    private final String code;

    private StabilityTypeEnums(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static StabilityTypeEnums fromInt(final Integer frequency) {
        StabilityTypeEnums stabilityTypeEnums = StabilityTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    stabilityTypeEnums = StabilityTypeEnums.LOW;
                break;
                case 2:
                    stabilityTypeEnums = StabilityTypeEnums.MEDIUM;
                break;
                case 3:
                    stabilityTypeEnums = StabilityTypeEnums.HIGH;
                break;
            }
        }
        return stabilityTypeEnums;
    }

    public static StabilityTypeEnums fromString(final String frequency) {
        StabilityTypeEnums stabilityTypeEnums = StabilityTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case "LOW":
                    stabilityTypeEnums = StabilityTypeEnums.LOW;
                break;
                case "MEDIUM":
                    stabilityTypeEnums = StabilityTypeEnums.MEDIUM;
                break;
                case "HIGH":
                    stabilityTypeEnums = StabilityTypeEnums.HIGH;
                break;
            }
        }
        return stabilityTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final StabilityTypeEnums enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final StabilityTypeEnums enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> stabilityEnumOptions() {
        final Collection<EnumOptionData> stabilityEnumOptions = new ArrayList<>();
        for (final StabilityTypeEnums enumType : values()) {
            final EnumOptionData enumOptionData = stabilityType(enumType.getValue());
            if (enumOptionData != null) {
                stabilityEnumOptions.add(enumOptionData);
            }
        }
        return stabilityEnumOptions;
    }

    public static EnumOptionData stabilityType(final int id) {
        return stabilityType(StabilityTypeEnums.fromInt(id));
    }

    public static EnumOptionData stabilityType(final StabilityTypeEnums type) {
        EnumOptionData optionData = null;
        switch (type) {
            case LOW:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), IncomeExpenseApiConstants.LOW);
            break;
            case MEDIUM:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), IncomeExpenseApiConstants.MEDIUM);
            break;
            case HIGH:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), IncomeExpenseApiConstants.HIGH);
            break;
            default:
            break;
        }
        return optionData;
    }
}