package com.finflux.portfolio.investmenttracker.data;

import java.util.ArrayList;
import java.util.List;

public enum InvestmentCompoundingInterestPeriodType {

    INVALID(0, "investmentCompoundingInterestPeriodType.invalid"), //
    DAILY(1, "investmentCompoundingInterestPeriodType.daily"), //
    MONTHLY(2, "investmentCompoundingInterestPeriodType.monthly"), //
    QUATERLY(3, "investmentCompoundingInterestPeriodType.quarterly"), //
    BI_ANNUAL(4, "investmentCompoundingInterestPeriodType.biannual"), //
    ANNUAL(5, "investmentCompoundingInterestPeriodType.annual");

    private final Integer value;
    private final String code;

    private InvestmentCompoundingInterestPeriodType(final Integer value, final String code) {
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
        for (final InvestmentCompoundingInterestPeriodType enumType : values()) {
            if (enumType.getValue() > 0) {
                values.add(enumType.getValue());
            }
        }

        return values.toArray();
    }

    public static InvestmentCompoundingInterestPeriodType fromInt(final Integer type) {
        InvestmentCompoundingInterestPeriodType compoundingInterestPeriodType = InvestmentCompoundingInterestPeriodType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    compoundingInterestPeriodType = InvestmentCompoundingInterestPeriodType.DAILY;
                break;
                case 2:
                    compoundingInterestPeriodType = InvestmentCompoundingInterestPeriodType.MONTHLY;
                break;
                case 3:
                    compoundingInterestPeriodType = InvestmentCompoundingInterestPeriodType.QUATERLY;
                break;
                case 4:
                    compoundingInterestPeriodType = InvestmentCompoundingInterestPeriodType.BI_ANNUAL;
                break;
                case 5:
                    compoundingInterestPeriodType = InvestmentCompoundingInterestPeriodType.ANNUAL;
                break;
            }
        }
        return compoundingInterestPeriodType;
    }

}
