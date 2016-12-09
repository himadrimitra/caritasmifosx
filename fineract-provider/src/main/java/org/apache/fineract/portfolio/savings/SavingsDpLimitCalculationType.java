package org.apache.fineract.portfolio.savings;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum SavingsDpLimitCalculationType {

    INVALID(0, "SavingsDpLimitCalculationType.invalid"), //
    FLAT(1, "SavingsDpLimitCalculationType.flat"), //
    PERCENT_OF_AMOUNT(2, "SavingsDpLimitCalculationType.percent.of.amount");

    private final Integer value;
    private final String code;

    private SavingsDpLimitCalculationType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final SavingsDpLimitCalculationType enumType : values()) {
            if (enumType.getValue() > 0) {
                values.add(enumType.getValue());
            }
        }

        return values.toArray();
    }

    public static SavingsDpLimitCalculationType fromInt(final Integer type) {

        SavingsDpLimitCalculationType dpLimitCalculationType = SavingsDpLimitCalculationType.INVALID;
        switch (type) {
            case 1:
                dpLimitCalculationType = FLAT;
            break;
            case 2:
                dpLimitCalculationType = PERCENT_OF_AMOUNT;
            break;
        }
        return dpLimitCalculationType;
    }
    
    public static EnumOptionData savingsDpLimitCalculationType(final SavingsDpLimitCalculationType type) {
        final String codePrefix = "savings.calculationType.";
        EnumOptionData optionData = new EnumOptionData(SavingsDpLimitCalculationType.INVALID.getValue().longValue(),
                SavingsDpLimitCalculationType.INVALID.getCode(), "Invalid");
        switch (type) {
            case INVALID:
            break;
            case FLAT:
                optionData = new EnumOptionData(SavingsDpLimitCalculationType.FLAT.getValue().longValue(), codePrefix
                        + SavingsDpLimitCalculationType.FLAT.getCode(), "Flat");
            break;
            case PERCENT_OF_AMOUNT:
                optionData = new EnumOptionData(SavingsDpLimitCalculationType.PERCENT_OF_AMOUNT.getValue().longValue(), codePrefix
                        + SavingsDpLimitCalculationType.PERCENT_OF_AMOUNT.getCode(), "PercentOfAmount");
            break;
            
        }
        return optionData;
    }
}
