package com.finflux.transaction.execution.data;

import java.util.HashMap;
import java.util.Map;

public enum AccountTransferEntityType {

    INVALID(0, "bankAccountDetailEntityType.invalid"), //
    LOANS(1, "bankAccountDetailEntityType.loans");

    private final Integer value;
    private final String code;

    private AccountTransferEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, AccountTransferEntityType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final AccountTransferEntityType entityType : AccountTransferEntityType.values()) {
            if (i == 0) {
                minValue = entityType.value;
            }
            intToEnumMap.put(entityType.value, entityType);
            if (minValue >= entityType.value) {
                minValue = entityType.value;
            }
            if (maxValue < entityType.value) {
                maxValue = entityType.value;
            }
            i = i + 1;
        }
    }

    public static AccountTransferEntityType fromInt(final int i) {
        final AccountTransferEntityType entityType = intToEnumMap.get(Integer.valueOf(i));
        return entityType;
    }

    public static int getMinValue() {
        return minValue;
    }

    public static int getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return name().toString();
    }

    private static final Map<String, AccountTransferEntityType> entityNameToEnumMap = new HashMap<>();

    static {
        for (final AccountTransferEntityType entityType : AccountTransferEntityType.values()) {
            entityNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static AccountTransferEntityType getEntityType(String entityType) {
        return entityNameToEnumMap.get(entityType.toLowerCase());
    }

    public boolean isLoan() {
        return this.value.equals(AccountTransferEntityType.LOANS.getValue());
    }

}
