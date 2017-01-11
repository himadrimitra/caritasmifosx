package com.finflux.portfolio.loan.purpose.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import com.finflux.portfolio.loan.purpose.api.LoanPurposeGroupApiConstants;

public enum LoanPurposeGroupTypeEnums {

    INVALID(0, "loanPurposeGroupType.invalid"), //
    CONSUMPTION_WISE(1, "loanPurposeGroupType.consumption.wise"), //
    SECTOR_WISE(2, "loanPurposeGroupType.sector.wise"), //
    PSL(3, "loanPurposeGroupType.psl"); // PSL - Priority Sector Lending

    private final Integer value;
    private final String code;

    private LoanPurposeGroupTypeEnums(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static LoanPurposeGroupTypeEnums fromInt(final Integer frequency) {
        LoanPurposeGroupTypeEnums loanPurposeGroupTypeEnums = LoanPurposeGroupTypeEnums.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    loanPurposeGroupTypeEnums = LoanPurposeGroupTypeEnums.CONSUMPTION_WISE;
                break;
                case 2:
                    loanPurposeGroupTypeEnums = LoanPurposeGroupTypeEnums.SECTOR_WISE;
                break;
                case 3:
                    loanPurposeGroupTypeEnums = LoanPurposeGroupTypeEnums.PSL;
                break;
            }
        }
        return loanPurposeGroupTypeEnums;
    }

    public static Object[] integerValues() {
        final List<Integer> values = new ArrayList<>();
        for (final LoanPurposeGroupTypeEnums enumType : values()) {
            values.add(enumType.getValue());
        }
        return values.toArray();
    }

    public static Object[] codeValues() {
        final List<String> codes = new ArrayList<>();
        for (final LoanPurposeGroupTypeEnums enumType : values()) {
            codes.add(enumType.getCode());
        }
        return codes.toArray();
    }

    public static Collection<EnumOptionData> loanPurposeGroupTypeOptions() {
        final Collection<EnumOptionData> loanPurposeGroupTypeOptions = new ArrayList<>();
        for (final LoanPurposeGroupTypeEnums enumType : values()) {
            final EnumOptionData enumOptionData = loanPurposeGroupType(enumType.getValue());
            if (enumOptionData != null) {
                loanPurposeGroupTypeOptions.add(enumOptionData);
            }
        }
        return loanPurposeGroupTypeOptions;
    }

    public static EnumOptionData loanPurposeGroupType(final int id) {
        return loanPurposeGroupType(LoanPurposeGroupTypeEnums.fromInt(id));
    }

    public static EnumOptionData loanPurposeGroupType(final LoanPurposeGroupTypeEnums type) {
        EnumOptionData optionData = null;
        switch (type) {
            case CONSUMPTION_WISE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanPurposeGroupApiConstants.CONSUMPTION_WISE);
            break;
            case SECTOR_WISE:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanPurposeGroupApiConstants.SECTOR_WISE);
            break;
            case PSL:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanPurposeGroupApiConstants.PSL);
            break;
            default:
            break;
        }
        return optionData;
    }

}
