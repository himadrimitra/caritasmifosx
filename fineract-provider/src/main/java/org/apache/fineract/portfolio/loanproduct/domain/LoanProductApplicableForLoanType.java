package org.apache.fineract.portfolio.loanproduct.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanproduct.LoanProductConstants;

public enum LoanProductApplicableForLoanType {

    INVALID(0, "loanProductApplicableForLoanType.invalid"), //
    ALL_TYPES(1, "loanProductApplicableForLoanType.all.types"), //
    INDIVIDUAL_CLIENT(2, "loanProductApplicableForLoanType.individual.client"), //
    GROUP(3, "loanProductApplicableForLoanType.groups");

    private final Integer value;
    private final String code;

    private LoanProductApplicableForLoanType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static LoanProductApplicableForLoanType fromInt(final Integer type) {
        LoanProductApplicableForLoanType loanProductApplicableForLoanType = LoanProductApplicableForLoanType.INVALID;
        if (type != null) {
            switch (type) {
                case 1:
                    loanProductApplicableForLoanType = LoanProductApplicableForLoanType.ALL_TYPES;
                break;
                case 2:
                    loanProductApplicableForLoanType = LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT;
                break;
                case 3:
                    loanProductApplicableForLoanType = LoanProductApplicableForLoanType.GROUP;
                break;
            }
        }
        return loanProductApplicableForLoanType;
    }

    public static Collection<EnumOptionData> typeOptions() {
        final Collection<EnumOptionData> typeOptions = new ArrayList<>();
        for (final LoanProductApplicableForLoanType enumType : values()) {
            final EnumOptionData enumOptionData = type(enumType.getValue());
            if (enumOptionData != null) {
                typeOptions.add(enumOptionData);
            }
        }
        return typeOptions;
    }

    public static EnumOptionData type(final int id) {
        return type(LoanProductApplicableForLoanType.fromInt(id));
    }

    public static EnumOptionData type(final LoanProductApplicableForLoanType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case ALL_TYPES:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanProductConstants.ALL_TYPES);
            break;
            case INDIVIDUAL_CLIENT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanProductConstants.INDIVIDUAL_CLIENT);
            break;
            case GROUP:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), LoanProductConstants.GROUP);
            break;
            default:
            break;

        }
        return optionData;
    }

    public boolean isAllTypes() {
        return this.value.equals(LoanProductApplicableForLoanType.ALL_TYPES.getValue());
    }

    public boolean isIndividualClient() {
        return this.value.equals(LoanProductApplicableForLoanType.INDIVIDUAL_CLIENT.getValue());
    }

    public boolean isGroups() {
        return this.value.equals(LoanProductApplicableForLoanType.GROUP.getValue());
    }

}