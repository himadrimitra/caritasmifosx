package com.finflux.task.execution.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

/**
 * 
 * @author CT
 *
 */
public enum ActivityType {
    SURVEY(1, "activityType.survey"), //
    DATATABLE(2, "activityType.question"), //
    MASTER(3, "activityType.data");

    private final Integer value;
    private final String code;

    private ActivityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, ActivityType> intToEnumMap = new HashMap<>();
    static {
        for (final ActivityType type : ActivityType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static ActivityType fromInt(final int i) {
        final ActivityType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.toString());
    }
}
