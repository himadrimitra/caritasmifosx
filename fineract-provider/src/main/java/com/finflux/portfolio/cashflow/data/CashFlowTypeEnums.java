package com.finflux.portfolio.cashflow.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.cashflow.api.CashFlowCategoryApiConstants;

public enum CashFlowTypeEnums {

    INVALID(0, "cashFlowType.invalid"), //
    INCOME(1, "cashFlowType.income"), //
    EXPENSE(2, "cashFlowType.expense");

    private final Integer value;
    private final String code;

    private CashFlowTypeEnums(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static CashFlowTypeEnums fromInt(final Integer frequency) {
        CashFlowTypeEnums cashFlowTypeEnums = CashFlowTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    cashFlowTypeEnums = CashFlowTypeEnums.INCOME;
                break;
                case 2:
                    cashFlowTypeEnums = CashFlowTypeEnums.EXPENSE;
                break;
            }
        }
        return cashFlowTypeEnums;
    }

    public static CashFlowTypeEnums fromString(final String frequency) {
        CashFlowTypeEnums cashFlowTypeEnums = CashFlowTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case "INCOME":
                    cashFlowTypeEnums = CashFlowTypeEnums.INCOME;
                break;
                case "EXPENSE":
                    cashFlowTypeEnums = CashFlowTypeEnums.EXPENSE;
                break;
            }
        }
        return cashFlowTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final CashFlowTypeEnums enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final CashFlowTypeEnums enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> cashFlowTypeOptions() {
        final Collection<EnumOptionData> cashFlowTypeOptions = new ArrayList<>();
        for (final CashFlowTypeEnums enumType : values()) {
            final EnumOptionData enumOptionData = cashFlowType(enumType.getValue());
            if (enumOptionData != null) {
                cashFlowTypeOptions.add(enumOptionData);
            }
        }
        return cashFlowTypeOptions;
    }

    public static EnumOptionData cashFlowType(final int id) {
        return cashFlowType(CashFlowTypeEnums.fromInt(id));
    }

    public static EnumOptionData cashFlowType(final CashFlowTypeEnums type) {
        EnumOptionData optionData = null;
        switch (type) {
            case INCOME:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), CashFlowCategoryApiConstants.INCOME);
            break;
            case EXPENSE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), CashFlowCategoryApiConstants.EXPENSE);
            break;
            default:
            break;
        }
        return optionData;
    }
}
