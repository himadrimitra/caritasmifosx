package com.finflux.task.execution.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */

public enum TaskType {
    WORKFLOW(1, "taskType.workflow"), //
    SINGLE(2, "taskType.single");

    private final Integer value;
    private final String code;

    private TaskType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, TaskType> intToEnumMap = new HashMap<>();
    static {
        for (final TaskType type : TaskType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static TaskType fromInt(final int i) {
        final TaskType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }
}
