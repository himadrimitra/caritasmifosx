package org.apache.fineract.portfolio.savings;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;


public enum SavingsDpLimitFrequencyType {
    DAYS(0, "SavingsDpLimitFrequencyType.days"), //
    WEEKS(1, "SavingsDpLimitFrequencyType.weeks"), //
    MONTHS(2, "SavingsDpLimitFrequencyType.months"), //
    YEARS(3, "SavingsDpLimitFrequencyType.years"), //
    INVALID(4, "SavingsDpLimitFrequencyType.invalid");
    
    private final Integer value;
    private final String code;
    
    private SavingsDpLimitFrequencyType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }
    
    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public static SavingsDpLimitFrequencyType fromInt(final Integer type) {
        SavingsDpLimitFrequencyType dpLimitFrequencyType = SavingsDpLimitFrequencyType.INVALID;
        if (type != null) {
            switch (type) {
                case 0:
                    dpLimitFrequencyType = SavingsDpLimitFrequencyType.DAYS;
                break;
                case 1:
                    dpLimitFrequencyType = SavingsDpLimitFrequencyType.WEEKS;
                break;
                case 2:
                    dpLimitFrequencyType = SavingsDpLimitFrequencyType.MONTHS;
                break;
                case 3:
                    dpLimitFrequencyType = SavingsDpLimitFrequencyType.YEARS;
                break;
            }
        }
        return dpLimitFrequencyType;
    }
    
    public boolean isInvalid() {
        return this.value.equals(SavingsDpLimitFrequencyType.INVALID.value);
    }
    
    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final SavingsDpLimitFrequencyType enumType : values()) {
            if (!enumType.isInvalid()) {
                values.add(enumType.getValue());
            }
        }

        return values.toArray();
    }
    
    public static EnumOptionData savingsDpLimitFrequencyType(final SavingsDpLimitFrequencyType type) {
        final String codePrefix = "savings.frequencyType.";
        EnumOptionData optionData = new EnumOptionData(SavingsDpLimitFrequencyType.INVALID.getValue().longValue(),
                SavingsDpLimitFrequencyType.INVALID.getCode(), "Invalid");
        switch (type) {
            case INVALID:
            break;
            case DAYS:
                optionData = new EnumOptionData(SavingsDpLimitFrequencyType.DAYS.getValue().longValue(), codePrefix
                        + SavingsDpLimitFrequencyType.DAYS.getCode(), "Days");
            break;
            case WEEKS:
                optionData = new EnumOptionData(SavingsDpLimitFrequencyType.WEEKS.getValue().longValue(), codePrefix
                        + SavingsDpLimitFrequencyType.WEEKS.getCode(), "Weeks");
            break;
            case MONTHS:
                optionData = new EnumOptionData(SavingsDpLimitFrequencyType.MONTHS.getValue().longValue(), codePrefix
                        + SavingsDpLimitFrequencyType.MONTHS.getCode(), "Months");
            break;
            case YEARS:
                optionData = new EnumOptionData(SavingsDpLimitFrequencyType.YEARS.getValue().longValue(), codePrefix
                        + SavingsDpLimitFrequencyType.YEARS.getCode(), "Years");
            break;
        }
        return optionData;
    }

}
