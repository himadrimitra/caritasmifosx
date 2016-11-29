package com.finflux.workflow.execution.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum WorkFlowEntityType {

    INVALID(0, "workFlowEntityType.invalid"), //
    LOAN_PRODUCT(1, "workFlowEntityType.loan.product"),//
    ADHOC(2, "workFlowEntityType.adhoc");//

    private final Integer value;
    private final String code;

    private WorkFlowEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static WorkFlowEntityType fromInt(final Integer frequency) {
        WorkFlowEntityType workFlowEntityType = WorkFlowEntityType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    workFlowEntityType = WorkFlowEntityType.LOAN_PRODUCT;
                break;
                default:
                break;
            }
        }
        return workFlowEntityType;
    }

    public static EnumOptionData workFlowEntityType(final WorkFlowEntityType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case LOAN_PRODUCT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Loan Product");
            break;
            default:
            break;
        }
        return optionData;
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> workFlowEntityTypeOptions = new ArrayList<>();
        for (final WorkFlowEntityType enumType : values()) {
            final EnumOptionData enumOptionData = workFlowEntityType(fromInt(enumType.getValue()));
            if (enumOptionData != null) {
                workFlowEntityTypeOptions.add(enumOptionData);
            }
        }
        return workFlowEntityTypeOptions;
    }
}