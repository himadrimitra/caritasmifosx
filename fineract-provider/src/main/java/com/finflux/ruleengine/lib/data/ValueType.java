package com.finflux.ruleengine.lib.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */
public enum ValueType {
    NUMBER(0,"valueType.number"),
    STRING(1,"valueType.string"),
    BOOLEAN(2,"valueType.boolean");

    private final Integer value;
    private final String code;

    private ValueType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }


    private static final Map<Integer, ValueType> intToEnumMap = new HashMap<>();

    static {
        for (final ValueType type : ValueType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static ValueType fromInt(final Integer val) {
        final ValueType type = intToEnumMap.get(val);
        return type;
    }

}
