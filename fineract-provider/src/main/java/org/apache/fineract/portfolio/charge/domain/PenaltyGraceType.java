package org.apache.fineract.portfolio.charge.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum PenaltyGraceType {

    INVALID(0, "penaltyGraceType.invalid"), //
    FIRST_OVERDUE_INSTALLEMNT(1, "penaltyGraceType.first.overdue.installment"), //
    EACH_OVERDUE_INSTALLEMNT(2, "penaltyGraceType.each.overdue.installment");

    private final Integer value;
    private final String code;

    private PenaltyGraceType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static PenaltyGraceType fromInt(final Integer penaltyGraceType) {
        PenaltyGraceType chargeCalculationType = PenaltyGraceType.INVALID;
        switch (penaltyGraceType) {
            case 1:
                chargeCalculationType = FIRST_OVERDUE_INSTALLEMNT;
            break;
            case 2:
                chargeCalculationType = EACH_OVERDUE_INSTALLEMNT;
            break;
        }
        return chargeCalculationType;
    }

    public boolean isApplyGraceForFirstOverdue() {
        return this.value.equals(PenaltyGraceType.FIRST_OVERDUE_INSTALLEMNT.getValue());
    }

    public boolean isApplyGraceForEachInstallment() {
        return this.value.equals(PenaltyGraceType.EACH_OVERDUE_INSTALLEMNT.getValue());
    }

    public static Collection<EnumOptionData> penaltyGraceTypeOptions() {
        final Collection<EnumOptionData> periodFrequencyTypeOptions = new ArrayList<>();
        for (final PenaltyGraceType enumType : values()) {
            final EnumOptionData enumOptionData = penaltyGraceType(enumType.getValue());
            if (enumOptionData != null) {
                periodFrequencyTypeOptions.add(enumOptionData);
            }
        }
        return periodFrequencyTypeOptions;
    }

    public static EnumOptionData penaltyGraceType(final int id) {
        return penaltyGraceType(PenaltyGraceType.fromInt(id));
    }

    public static EnumOptionData penaltyGraceType(final PenaltyGraceType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case FIRST_OVERDUE_INSTALLEMNT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "First overdue installment");
            break;
            case EACH_OVERDUE_INSTALLEMNT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Each overdue installment");
            break;
            default:
            break;
        }
        return optionData;
    }

}