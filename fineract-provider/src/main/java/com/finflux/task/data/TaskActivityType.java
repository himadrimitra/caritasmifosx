package com.finflux.task.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * Created by dhirendra on 06/09/16.
 */

public enum TaskActivityType {
    SURVEY(1, "taskActivityType.survey"), //
    DATATABLE(2, "taskActivityType.question"), //
    MASTER(3, "taskActivityType.data");

    private final Integer value;
    private final String code;

    private TaskActivityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, TaskActivityType> intToEnumMap = new HashMap<>();
    static {
        for (final TaskActivityType type : TaskActivityType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static TaskActivityType fromInt(final int i) {
        final TaskActivityType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }
}
