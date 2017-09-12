package org.apache.fineract.portfolio.common.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum LoanPeriodFrequencyType {
    DAYS(PeriodFrequencyType.DAYS), //
    WEEKS(PeriodFrequencyType.WEEKS), //
    MONTHS(PeriodFrequencyType.MONTHS), //
    YEARS(PeriodFrequencyType.YEARS), //
    INVALID(PeriodFrequencyType.INVALID), //
    SAME_AS_REPAYMENT_PERIOD(5, "overduePeriodFrequencyType.same.as.repayment.period"); //

    private final Integer value;
    private final String code;

    private LoanPeriodFrequencyType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    private LoanPeriodFrequencyType(final PeriodFrequencyType periodFrequencyType) {
        this.value = periodFrequencyType.getValue();
        this.code = periodFrequencyType.getCode();
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static LoanPeriodFrequencyType fromInt(final Integer frequency) {
        LoanPeriodFrequencyType repaymentFrequencyType = LoanPeriodFrequencyType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 0:
                    repaymentFrequencyType = LoanPeriodFrequencyType.DAYS;
                break;
                case 1:
                    repaymentFrequencyType = LoanPeriodFrequencyType.WEEKS;
                break;
                case 2:
                    repaymentFrequencyType = LoanPeriodFrequencyType.MONTHS;
                break;
                case 3:
                    repaymentFrequencyType = LoanPeriodFrequencyType.YEARS;
                break;
                case 5:
                    repaymentFrequencyType = LoanPeriodFrequencyType.SAME_AS_REPAYMENT_PERIOD;
                break;
            }
        }
        return repaymentFrequencyType;
    }

    public boolean isMonthly() {
        return this.value.equals(LoanPeriodFrequencyType.MONTHS.getValue());
    }

    public boolean isYearly() {
        return this.value.equals(LoanPeriodFrequencyType.YEARS.getValue());
    }

    public boolean isWeekly() {
        return this.value.equals(LoanPeriodFrequencyType.WEEKS.getValue());
    }

    public boolean isDaily() {
        return this.value.equals(LoanPeriodFrequencyType.DAYS.getValue());
    }

    public boolean isInvalid() {
        return this.value.equals(LoanPeriodFrequencyType.INVALID.getValue());
    }

    public boolean isSameAsRepayment() {
        return this.value.equals(LoanPeriodFrequencyType.SAME_AS_REPAYMENT_PERIOD.getValue());
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final LoanPeriodFrequencyType enumType : values()) {
            if (!enumType.isInvalid()) {
                values.add(enumType.getValue());
            }
        }

        return values.toArray();
    }

    public static Collection<EnumOptionData> overduePeriodFrequencyTypeOptions() {
        final Collection<EnumOptionData> periodFrequencyTypeOptions = new ArrayList<>();
        for (final LoanPeriodFrequencyType enumType : values()) {
            final EnumOptionData enumOptionData = overduePeriodFrequencyType(enumType.getValue());
            if (enumOptionData != null) {
                periodFrequencyTypeOptions.add(enumOptionData);
            }
        }
        return periodFrequencyTypeOptions;
    }

    public static EnumOptionData overduePeriodFrequencyType(final int id) {
        return overduePeriodFrequencyType(LoanPeriodFrequencyType.fromInt(id));
    }

    public static EnumOptionData overduePeriodFrequencyType(final LoanPeriodFrequencyType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case DAYS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "DAYS");
            break;
            case WEEKS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "WEEKS");
            break;
            case MONTHS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "MONTHS");
            break;
            case YEARS:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "YEARS");
            break;
            case SAME_AS_REPAYMENT_PERIOD:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "SAME AS REPAYMENT PERIOD");
            break;
            default:
            break;
        }
        return optionData;
    }

    private static final Map<String, LoanPeriodFrequencyType> periodFrequencyTypes = new HashMap<>();

    static {
        for (final LoanPeriodFrequencyType periodFrequencyType : LoanPeriodFrequencyType.values()) {
            periodFrequencyTypes.put(periodFrequencyType.name().toLowerCase(), periodFrequencyType);
        }
    }

    public static LoanPeriodFrequencyType getOverduePeriodFrequencyType(final String periodFrequencyType) {
        return periodFrequencyTypes.get(periodFrequencyType.toLowerCase());
    }
}
