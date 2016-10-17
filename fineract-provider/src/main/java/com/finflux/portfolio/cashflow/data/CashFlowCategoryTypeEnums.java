package com.finflux.portfolio.cashflow.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.cashflow.api.CashFlowCategoryApiConstants;

public enum CashFlowCategoryTypeEnums {

    INVALID(0, "cashFlowCategoryType.invalid"), //
    OCCUPATION(1, "cashFlowCategoryType.occupation"), //
    ASSET(2, "cashFlowCategoryType.asset"), //
    HOUSEHOLDEXPENSE(3, "cashFlowCategoryType.householdexpense");

    private final Integer value;
    private final String code;

    private CashFlowCategoryTypeEnums(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static CashFlowCategoryTypeEnums fromInt(final Integer frequency) {
        CashFlowCategoryTypeEnums cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.OCCUPATION;
                break;
                case 2:
                    cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.ASSET;
                break;
                case 3:
                    cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.HOUSEHOLDEXPENSE;
                break;
            }
        }
        return cashFlowCategoryTypeEnums;
    }

    public static CashFlowCategoryTypeEnums fromString(final String frequency) {
        CashFlowCategoryTypeEnums cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case "OCCUPATION":
                    cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.OCCUPATION;
                break;
                case "ASSET":
                    cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.ASSET;
                break;
                case "HOUSEHOLDEXPENSE":
                    cashFlowCategoryTypeEnums = CashFlowCategoryTypeEnums.HOUSEHOLDEXPENSE;
                break;
            }
        }
        return cashFlowCategoryTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final CashFlowCategoryTypeEnums enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final CashFlowCategoryTypeEnums enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> cashFlowCategoryTypeOptions() {
        final Collection<EnumOptionData> cashFlowCategoryTypeOptions = new ArrayList<>();
        for (final CashFlowCategoryTypeEnums enumType : values()) {
            final EnumOptionData enumOptionData = cashFlowCategoryType(enumType.getValue());
            if (enumOptionData != null) {
                cashFlowCategoryTypeOptions.add(enumOptionData);
            }
        }
        return cashFlowCategoryTypeOptions;
    }

    public static EnumOptionData cashFlowCategoryType(final int id) {
        return cashFlowCategoryType(CashFlowCategoryTypeEnums.fromInt(id));
    }

    public static EnumOptionData cashFlowCategoryType(final CashFlowCategoryTypeEnums type) {
        EnumOptionData optionData = null;
        switch (type) {
            case OCCUPATION:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), CashFlowCategoryApiConstants.OCCUPATION);
            break;
            case ASSET:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), CashFlowCategoryApiConstants.ASSET);
            break;
            case HOUSEHOLDEXPENSE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), CashFlowCategoryApiConstants.HOUSEHOLDEXPENSE);
            break;
            default:
            break;
        }
        return optionData;
    }

}