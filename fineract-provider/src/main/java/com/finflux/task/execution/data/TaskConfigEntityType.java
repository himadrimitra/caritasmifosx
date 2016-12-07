package com.finflux.task.execution.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskConfigEntityType {

    INVALID(0, "taskConfigEntityType.invalid"), //
    LOAN_PRODUCT(1, "taskConfigEntityType.loan.product"), //
    ADHOC(2, "taskConfigEntityType.adhoc");//

    private final Integer value;
    private final String code;

    private TaskConfigEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static TaskConfigEntityType fromInt(final Integer frequency) {
        TaskConfigEntityType taskConfigEntityType = TaskConfigEntityType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    taskConfigEntityType = TaskConfigEntityType.LOAN_PRODUCT;
                break;
                default:
                break;
            }
        }
        return taskConfigEntityType;
    }

    public static EnumOptionData taskConfigEntityType(final TaskConfigEntityType type) {
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
        final Collection<EnumOptionData> taskConfigEntityTypeOptions = new ArrayList<>();
        for (final TaskConfigEntityType enumType : values()) {
            final EnumOptionData enumOptionData = taskConfigEntityType(fromInt(enumType.getValue()));
            if (enumOptionData != null) {
                taskConfigEntityTypeOptions.add(enumOptionData);
            }
        }
        return taskConfigEntityTypeOptions;
    }
}