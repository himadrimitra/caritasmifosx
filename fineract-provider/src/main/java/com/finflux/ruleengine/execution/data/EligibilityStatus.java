package com.finflux.ruleengine.execution.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhirendra on 30/09/16.
 */
public enum EligibilityStatus {
    APPROVED(1,"eligibilityStatus.approved"),
    TO_BE_REVIEWED(2,"eligibilityStatus.tobereviewed"),
    REJECTED(3,"eligibilityStatus.rejected"),
    ERROR(4,"eligibilityStatus.error");

    private final Integer value;
    private final String code;


    EligibilityStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, EligibilityStatus> intToEnumMap = new HashMap<>();
    static {
        for (final EligibilityStatus type : EligibilityStatus.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static EligibilityStatus fromInt(final int i) {
        final EligibilityStatus type = intToEnumMap.get(Integer.valueOf(i));
        return type;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name().toLowerCase());
    }

}
