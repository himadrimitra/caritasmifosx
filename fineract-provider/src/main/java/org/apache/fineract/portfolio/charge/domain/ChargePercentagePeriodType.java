package org.apache.fineract.portfolio.charge.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum ChargePercentagePeriodType {

    INVALID(0, "ChargePercentagePeriodType.invalid"), //
    DAILY(1, "ChargePercentagePeriodType.daily"), //
    SAME_AS_FREQUENCY(2, "ChargePercentagePeriodType.same.as.frequency");

    private final Integer value;
    private final String code;

    private ChargePercentagePeriodType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ChargePercentagePeriodType fromInt(final Integer chargePercentagePeriodType) {
        ChargePercentagePeriodType chargeCalculationType = ChargePercentagePeriodType.INVALID;
        switch (chargePercentagePeriodType) {
            case 1:
                chargeCalculationType = DAILY;
            break;
            case 2:
                chargeCalculationType = SAME_AS_FREQUENCY;
            break;
        }
        return chargeCalculationType;
    }

    public boolean isSameAsFrequency() {
        return this.value.equals(ChargePercentagePeriodType.SAME_AS_FREQUENCY.getValue());
    }

    public boolean isDaily() {
        return this.value.equals(ChargePercentagePeriodType.DAILY.getValue());
    }

    public static Collection<EnumOptionData> chargePercentagePeriodTypeOptions() {
        final Collection<EnumOptionData> periodFrequencyTypeOptions = new ArrayList<>();
        for (final ChargePercentagePeriodType enumType : values()) {
            final EnumOptionData enumOptionData = chargePercentagePeriodType(enumType.getValue());
            if (enumOptionData != null) {
                periodFrequencyTypeOptions.add(enumOptionData);
            }
        }
        return periodFrequencyTypeOptions;
    }

    public static EnumOptionData chargePercentagePeriodType(final int id) {
        return chargePercentagePeriodType(ChargePercentagePeriodType.fromInt(id));
    }

    public static EnumOptionData chargePercentagePeriodType(final ChargePercentagePeriodType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case SAME_AS_FREQUENCY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "same as frequency");
            break;
            case DAILY:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "daily");
            break;
            default:
            break;
        }
        return optionData;
    }

}