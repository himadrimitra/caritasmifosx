package com.finflux.task.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskConfigEntityType {

    INVALID(0, "taskConfigEntityType.invalid", "invalid"), //
    LOANPRODUCT(1, "taskConfigEntityType.loanproduct", "loanproduct"), //
    ADHOC(2, "taskConfigEntityType.adhoc", "adhoc"), //
    BANKTRANSACTION(3, "taskConfigEntityType.banktransaction", "banktransaction"), //
    GROUPONBARDING(4, "taskConfigEntityType.grouponboarding", "grouponboarding"), //
    LOAN_APPLICANTION(5, "taskConfigEntityType.loanproductapplicant", "loanproductapplicant"), //
    LOANPRODUCT_COAPPLICANT(6, "taskConfigEntityType.loanproductcoapplicant", "loanproductcoapplicant"), //
    CLIENTONBOARDING(7, "taskConfigEntityType.clientonboarding", "clientonboarding"), //
    CLIENTBANKACCOUNT(8, "taskConfigEntityType.clientbankaccount", "clientbankaccount"), //
    VILLAGEONBOARDING(9, "taskConfigEntityType.villageonboarding", "villageonboarding"), //
    OFFICEONBOARDING(10, "taskConfigEntityType.officeonboarding", "officeonboarding"), //
    CENTERONBOARDING(11, "taskConfigEntityType.centeronboarding", "centeronboarding"), //
    DISTRICTONBOARDING(12, "taskConfigEntityType.districtonboarding", "districtonboarding");

    private final Integer value;
    private final String code;
    private final String name;

    private TaskConfigEntityType(final Integer value, final String code, final String name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<String, TaskConfigEntityType> entityTypeNameToEnumMap = new HashMap<>();
    private static final Map<Integer, TaskConfigEntityType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;

    static {
        int i = 0;
        for (final TaskConfigEntityType entityType : TaskConfigEntityType.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            entityTypeNameToEnumMap.put(entityType.getName().toLowerCase(), entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;

        }

    }

    public static TaskConfigEntityType getEntityType(String entityType) {
        return entityTypeNameToEnumMap.get(entityType.toLowerCase());
    }

    public static TaskConfigEntityType fromInt(final int i) {
        return intToEnumMap.get(Integer.valueOf(i));
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name());
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> taskConfigEntityTypeOptions = new ArrayList<>();
        for (final TaskConfigEntityType enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                taskConfigEntityTypeOptions.add(enumOptionData);
            }
        }
        return taskConfigEntityTypeOptions;
    }

    public String getName() {
        return this.name;
    }

}