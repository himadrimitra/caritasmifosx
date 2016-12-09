package com.finflux.portfolio.bank.domain;

import java.util.HashMap;
import java.util.Map;

public enum BankAccountDetailEntityType {

    INVALID(0, "bankAccountDetailEntityType.invalid"), //
    CLIENTS(1, "bankAccountDetailEntityType.clients"), //
    LOANS(2, "bankAccountDetailEntityType.loans"), //
    SAVINGS(3, "bankAccountDetailEntityType.savings"), //
    PAYMENTTYPES(4, "bankAccountDetailEntityType.paymenttypes");
    

    private final Integer value;
    private final String code;

    private BankAccountDetailEntityType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    private static final Map<Integer, BankAccountDetailEntityType> intToEnumMap = new HashMap<>();
    private static int minValue;
    private static int maxValue;
    static {
        int i = 0;
        for (final BankAccountDetailEntityType entityType : BankAccountDetailEntityType.values()) {
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

    public static BankAccountDetailEntityType fromInt(final int i) {
        final BankAccountDetailEntityType entityType = intToEnumMap.get(Integer.valueOf(i));
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

    private static final Map<String, BankAccountDetailEntityType> entityNameToEnumMap = new HashMap<>();

    static {
        for (final BankAccountDetailEntityType entityType : BankAccountDetailEntityType.values()) {
            entityNameToEnumMap.put(entityType.name().toLowerCase(), entityType);
        }
    }

    public static BankAccountDetailEntityType getEntityType(String entityType) {
        return entityNameToEnumMap.get(entityType.toLowerCase());
    }

    public boolean isLoan() {
        return this.value.equals(BankAccountDetailEntityType.LOANS.getValue());
    }

    public boolean isClient() {
        return this.value.equals(BankAccountDetailEntityType.CLIENTS.getValue());
    }

    public boolean isSavings() {
        return this.value.equals(BankAccountDetailEntityType.SAVINGS.getValue());
    }

    public boolean isPaymentType() {
        return this.value.equals(BankAccountDetailEntityType.PAYMENTTYPES.getValue());
    }

}
