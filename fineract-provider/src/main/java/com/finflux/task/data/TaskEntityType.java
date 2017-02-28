package com.finflux.task.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum TaskEntityType {

    INVALID(0, "taskEntityType.invalid","invalid"), //
    LOAN_APPLICATION(1, "taskEntityType.loanapplication", "loanApplication"), //
    ADHOC(2, "taskEntityType.adhoc","adhoc"),
    BANK_TRANSACTION(3, "taskEntityType.banktransaction","bankTransaction"),//
    CENTER(4,"taskEntityType.center","center"),
    CLIENT(5,"taskEntityType.client","client"),
    VILLAGE(6,"taskEntityType.village","village"),
    BANK(7,"taskEntityType.bank","bank"),
    OFFICE(8,"taskEntityType.office","office"),
    TEMPLATE_TASK_CENTER(9,"taskEntityType.template_task_center","center"),
    TEMPLATE_TASK_CLIENT(10,"taskEntityType.template_task_client","client"),
    TEMPLATE_TASK_VILLAGE(11,"taskEntityType.template_task_village","village"),
    TEMPLATE_TASK_BANK(12,"taskEntityType.template_task_bank","bank"),
    TEMPLATE_TASK_OFFICE(13,"taskEntityType.template_task_office","office"),
    GROUP_ONBOARDING(14, "taskEntityType.grouponbarding","groupOnboarding"),
    LOAN_APPLICATION_APPLICANT(15, "taskEntityType.loanapplicationMainClient", "loanApplicationApplicant"),
    LOAN_APPLICATION_COAPPLICANT(16, "taskEntityType.loanapplicationCoApplicant", "loanApplicationCoApplicant"),//
	CLIENT_ONBOARDING(17,"taskEntityType.client_onboarding","client_onboarding");

    private final Integer value;
    private final String code;
    private final String name;

    private TaskEntityType(final Integer value, final String code, final String  name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    private static final Map<Integer, TaskEntityType> intToEnumMap = new HashMap<>();
    private static final Map<String, TaskEntityType> entityNameToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final TaskEntityType entityType : TaskEntityType.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            entityNameToEnumMap.put(entityType.getName().toLowerCase(), entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;

        }

    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static TaskEntityType fromInt(final int i) {
        final TaskEntityType entityType = intToEnumMap.get(Integer.valueOf(i));
        return entityType;
    }

    public static TaskEntityType fromString(final String str) {
        final TaskEntityType entityType = entityNameToEnumMap.get(str.toLowerCase());
        return entityType;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name());
    }

    public static Collection<EnumOptionData> entityTypeOptions() {
        final Collection<EnumOptionData> taskEntityTypeOptions = new ArrayList<>();
        for (final TaskEntityType enumType : values()) {
            final EnumOptionData enumOptionData = enumType.getEnumOptionData();
            if (enumOptionData != null) {
                taskEntityTypeOptions.add(enumOptionData);
            }
        }
        return taskEntityTypeOptions;
    }

    public String getName() {
        return name;
    }
}