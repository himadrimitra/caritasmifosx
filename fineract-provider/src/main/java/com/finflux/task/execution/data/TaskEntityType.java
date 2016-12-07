package com.finflux.task.execution.data;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskEntityType {

    INVALID(0, "taskEntityType.invalid"), //
    LOAN_APPLICATION(1, "taskEntityType.loan.application"), //
    ADHOC(2, "taskEntityType.adhoc");//

    private final Integer value;
    private final String code;

    private TaskEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static TaskEntityType fromInt(final Integer frequency) {
        TaskEntityType taskEntityType = TaskEntityType.INVALID;
        if (frequency != null) {
            switch (frequency) {
                case 1:
                    taskEntityType = TaskEntityType.LOAN_APPLICATION;
                break;
                default:
                break;
            }
        }
        return taskEntityType;
    }

    public static EnumOptionData taskEntityType(final TaskEntityType type) {
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

    public static EnumOptionData taskEntityType(final int id) {
        return taskEntityType(TaskEntityType.fromInt(id));
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> taskEntityTypeOptions = new ArrayList<>();
        for (final TaskEntityType enumType : values()) {
            final EnumOptionData enumOptionData = taskEntityType(fromInt(enumType.getValue()));
            if (enumOptionData != null) {
                taskEntityTypeOptions.add(enumOptionData);
            }
        }
        return taskEntityTypeOptions;
    }
}