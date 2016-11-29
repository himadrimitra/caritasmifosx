package com.finflux.workflow.execution.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum WorkFlowExecutionEntityType {

    INVALID(0, "workFlowExecutionEntityType.invalid"), //
    LOAN_APPLICATION(1, "workFlowExecutionEntityType.loan.application"),//
    ADHOC(2, "workFlowExecutionEntityType.adhoc");//

    private final Integer value;
    private final String code;

    private WorkFlowExecutionEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static WorkFlowExecutionEntityType fromInt(final Integer frequency) {
        WorkFlowExecutionEntityType workFlowEntityType = WorkFlowExecutionEntityType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    workFlowEntityType = WorkFlowExecutionEntityType.LOAN_APPLICATION;
                break;
                default:
                break;
            }
        }
        return workFlowEntityType;
    }

    public static EnumOptionData workFlowEntityType(final WorkFlowExecutionEntityType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case LOAN_APPLICATION:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Loan Application");
            break;
            default:
            break;
        }
        return optionData;
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> workFlowEntityTypeOptions = new ArrayList<>();
        for (final WorkFlowExecutionEntityType enumType : values()) {
            final EnumOptionData enumOptionData = workFlowEntityType(fromInt(enumType.getValue()));
            if (enumOptionData != null) {
                workFlowEntityTypeOptions.add(enumOptionData);
            }
        }
        return workFlowEntityTypeOptions;
    }
}