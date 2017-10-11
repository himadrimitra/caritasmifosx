package com.finflux.ruleengine.lib.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhirendra on 06/09/16.
 */


public enum EntityRuleType {
    FIELD(1, "ruleType.field"),
    FACTOR(2, "ruleType.factor"),
    DIMENSION(3, "ruleType.dimsionen"),
    CRITERIA(4, "ruleType.criteria");

    private final Integer value;
    private final String code;


    EntityRuleType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, EntityRuleType> intToEnumMap = new HashMap<>();
    static {
        for (final EntityRuleType type : EntityRuleType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static EntityRuleType fromInt(final int i) {
        final EntityRuleType type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

}
