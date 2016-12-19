package com.finflux.task.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskEntityType {

    INVALID(0, "taskEntityType.invalid","invalid"), //
    LOAN_APPLICATION(1, "taskEntityType.loan.application", "loanApplication"), //
    ADHOC(2, "taskEntityType.adhoc","adhoc");//

    private final Integer value;
    private final String code;
    private final String name;

    private TaskEntityType(final Integer value, final String code, final String  name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    private static final Map<Integer, TaskEntityType> intToEnumMap = new HashMap<>();
    private static final Map<String, TaskEntityType> entityNameToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final TaskEntityType entityType : TaskEntityType.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            entityNameToEnumMap.put(entityType.getName().toLowerCase(), entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;

        }

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

    public static TaskEntityType fromInt(final int i) {
        final TaskEntityType entityType = intToEnumMap.get(Integer.valueOf(i));
        return entityType;
    }

    public static TaskEntityType fromString(final String str) {
        final TaskEntityType entityType = entityNameToEnumMap.get(str.toLowerCase());
        return entityType;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name());
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> taskEntityTypeOptions = new ArrayList<>();
        for (final TaskEntityType enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                taskEntityTypeOptions.add(enumOptionData);
            }
        }
        return taskEntityTypeOptions;
    }

    public String getName() {
        return name;
    }
}