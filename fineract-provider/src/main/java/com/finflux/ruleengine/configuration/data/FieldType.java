package com.finflux.ruleengine.configuration.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */


public enum FieldType {
    SURVEY(1, "fieldType.survey"),
    QUESTION(2, "fieldType.question"),
    DATA(3, "fieldType.data"),
    CLIENTIDENTIFIER(4, "fieldType.identifier");

    private final Integer value;
    private final String code;


    private FieldType(final Integer value, final String code) {

        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, FieldType> intToEnumMap = new HashMap<>();
    static {
        for (final FieldType type : FieldType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static FieldType fromInt(final int i) {
        final FieldType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

}
