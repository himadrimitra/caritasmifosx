package com.finflux.portfolio.bank.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum BankAccountType {
    
    INVALID(0, "bankAccountType.invalid"),
    
    SAVINGSACCOUNT(1, "bankAccountType.savingsaccount"),

    CURRENTACCOUNT(2, "bankAccountType.currentaccount");

    private final Integer value;
    private final String code;

    private BankAccountType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static BankAccountType fromInt(final Integer type) {
        BankAccountType bankAccount = BankAccountType.INVALID;
        switch (type) {
            case 1:
                bankAccount = BankAccountType.SAVINGSACCOUNT;
            break;
            case 2:
                bankAccount = BankAccountType.CURRENTACCOUNT;
            break;
        }
        return bankAccount;
    }

    public boolean isSavingaAccount() {
        return this.value.equals(BankAccountType.SAVINGSACCOUNT.getValue());
    }

    public boolean isCurrentAccount() {
        return this.value.equals(BankAccountType.CURRENTACCOUNT.getValue());
    }

    public static EnumOptionData bankAccountType(final BankAccountType type) {
        EnumOptionData optionData = null;
        switch (type) {
            case SAVINGSACCOUNT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Savings Account");
            break;
            case CURRENTACCOUNT:
                optionData = new EnumOptionData(type.getValue().longValue(), type.getCode(), "Current Account");
            break;
            default:
            break;
        }
        return optionData;
    }

    public static EnumOptionData bankAccountType(final int id) {
        return bankAccountType(BankAccountType.fromInt(id));
    }

}
