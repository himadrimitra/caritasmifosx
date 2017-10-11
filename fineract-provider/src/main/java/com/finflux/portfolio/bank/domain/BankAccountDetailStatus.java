package com.finflux.portfolio.bank.domain;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum BankAccountDetailStatus {

    INITIATED(100, "BankAccountDetailStatus.initiated"), //
    ACTIVE(200, "BankAccountDetailStatus.active"), //
    DELETED(300, "BankAccountDetailStatus.deleted"), //
    INVALID(0, "BankAccountDetailStatus.invalid");

    private final Integer value;
    private final String code;

    public static BankAccountDetailStatus fromInt(final Integer statusValue) {

        BankAccountDetailStatus enumeration = BankAccountDetailStatus.INVALID;
        switch (statusValue) {
            case 100:
                enumeration = BankAccountDetailStatus.INITIATED;
            break;
            case 200:
                enumeration = BankAccountDetailStatus.ACTIVE;
            break;
            case 300:
                enumeration = BankAccountDetailStatus.DELETED;
            break;
        }
        return enumeration;
    }

    private BankAccountDetailStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public boolean isInitiated() {
        return this.value.equals(BankAccountDetailStatus.INITIATED.getValue());
    }

    public boolean isActive() {
        return this.value.equals(BankAccountDetailStatus.ACTIVE.getValue());
    }

    public boolean isDeleted() {
        return this.value.equals(BankAccountDetailStatus.DELETED.getValue());
    }

    public static EnumOptionData bankAccountDetailStatusEnumDate(final Integer id) {
        return bankAccountDetailStatusEnumDate(BankAccountDetailStatus.fromInt(id));
    }

    public static EnumOptionData bankAccountDetailStatusEnumDate(final BankAccountDetailStatus status) {
        EnumOptionData optionData = null;
        switch (status) {
            case ACTIVE:
                optionData = new EnumOptionData(BankAccountDetailStatus.ACTIVE.getValue().longValue(),
                        BankAccountDetailStatus.ACTIVE.getCode(), "active");
            break;
            case INITIATED:
                optionData = new EnumOptionData(BankAccountDetailStatus.INITIATED.getValue().longValue(),
                        BankAccountDetailStatus.INITIATED.getCode(), "initiated");
            break;
            case DELETED:
                optionData = new EnumOptionData(BankAccountDetailStatus.DELETED.getValue().longValue(),
                        BankAccountDetailStatus.DELETED.getCode(), "deleted");
            break;
            default:
                optionData = new EnumOptionData(BankAccountDetailStatus.INVALID.getValue().longValue(),
                        BankAccountDetailStatus.INVALID.getCode(), "Invalid");
            break;
        }
        return optionData;
    }

}