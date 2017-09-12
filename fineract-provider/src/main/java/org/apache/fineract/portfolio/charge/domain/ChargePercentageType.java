package org.apache.fineract.portfolio.charge.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum ChargePercentageType {

    INVALID(0, "ChargePercentageType.invalid"), //
    FLAT(1, "ChargePercentageType.flat"), //
    YEARLY(2, "ChargePercentageType.yearly");

    private final Integer value;
    private final String code;

    private ChargePercentageType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ChargePercentageType fromInt(final Integer chargePercentageType) {
        ChargePercentageType chargeCalculationType = ChargePercentageType.INVALID;
        switch (chargePercentageType) {
            case 1:
                chargeCalculationType = FLAT;
            break;
            case 2:
                chargeCalculationType = YEARLY;
            break;
        }
        return chargeCalculationType;
    }

    public boolean isYearlyPercentage() {
        return this.value.equals(ChargePercentageType.YEARLY.getValue());
    }

    public boolean isFlat() {
        return this.value.equals(ChargePercentageType.FLAT.getValue());
    }

    public static Collection<EnumOptionData> chargePercentageTypeOptions() {
        final Collection<EnumOptionData> periodFrequencyTypeOptions = new ArrayList<>();
        for (final ChargePercentageType enumType : values()) {
            final EnumOptionData enumOptionData = chargePercentageType(enumType.getValue());
            if (enumOptionData != null) {
                periodFrequencyTypeOptions.add(enumOptionData);
            }
        }
        return periodFrequencyTypeOptions;
    }

    public static EnumOptionData chargePercentageType(final int id) {
        return chargePercentageType(ChargePercentageType.fromInt(id));
    }

    public static EnumOptionData chargePercentageType(final ChargePercentageType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case FLAT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "flat");
            break;
            case YEARLY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "yearly");
            break;
            default:
            break;
        }
        return optionData;
    }

}