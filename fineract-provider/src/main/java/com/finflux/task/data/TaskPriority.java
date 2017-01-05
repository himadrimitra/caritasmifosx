package com.finflux.task.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskPriority {

    LOW(1, "taskPriority.low"), //
    MEDIUM(2, "taskPriority.medium"), //
    HIGH(3, "taskPriority.high");//

    private final Integer value;
    private final String code;

    private TaskPriority(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, TaskPriority> intToEnumMap = new HashMap<>();
    static {
        for (final TaskPriority type : TaskPriority.values()) {
            intToEnumMap.put(type.value, type);
        }
    }
    
    public static TaskPriority fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }
}