package com.finflux.task.data;

import java.util.HashMap;
import java.util.Map;

public enum TaskConfigEntityType {

    INVALID(0, "taskConfigEntityType.invalid"), //
    LOANPRODUCT(1, "taskConfigEntityType.loanproduct"), //
    ADHOC(2, "taskConfigEntityType.adhoc"), //
    BANKTRANSACTION(3, "taskConfigEntityType.banktransaction"), //
    GROUPONBARDING(4, "taskConfigEntityType.grouponboarding"), //
    LOAN_APPLICANTION(5, "taskConfigEntityType.loanproductapplicant"), //
    LOANPRODUCT_COAPPLICANT(6, "taskConfigEntityType.loanproductcoapplicant"), //
    CLIENTONBOARDING(7, "taskConfigEntityType.clientonboarding"), //
    CLIENTBANKACCOUNT(8, "taskConfigEntityType.clientbankaccount"), //
    VILLAGEONBOARDING(9, "taskConfigEntityType.villageonboarding"), //
    OFFICEONBOARDING(9, "taskConfigEntityType.officeonboarding"), //
    CENTERONBOARDING(9, "taskConfigEntityType.centeronboarding");
    
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