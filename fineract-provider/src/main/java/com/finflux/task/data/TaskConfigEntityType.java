package com.finflux.task.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskConfigEntityType {

    INVALID(0, "taskConfigEntityType.invalid"), //
    LOANPRODUCT(1, "taskConfigEntityType.loanproduct"), //
    ADHOC(2, "taskConfigEntityType.adhoc"),//
    BANKTRANSACTION(3, "taskConfigEntityType.banktransaction"),
    GROUPONBARDING(4, "taskConfigEntityType.grouponboarding"),
    LOANPRODUCT_APPLICANT(5, "taskConfigEntityType.loanproductapplicant"),
    LOANPRODUCT_COAPPLICANT(6, "taskConfigEntityType.loanproductcoapplicant");//

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
                    taskConfigEntityType = TaskConfigEntityType.LOANPRODUCT;
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
            case LOANPRODUCT:
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

    private static final Map<String, TaskConfigEntityType> entityTypeNameToEnumMap = new HashMap<>();

    static {
        for (final TaskConfigEntityType entityType : TaskConfigEntityType.values()) {
            entityTypeNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static TaskConfigEntityType getEntityType(String entityType) {
        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
    }
}