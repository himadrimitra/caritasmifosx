package com.finflux.portfolio.external;


import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum ExternalServiceType {

    INVALID(0,"externalServiceType.invalid"), BANK_TRANSFER(1,"externalServiceType.banktransfer");

    private final Integer value;

    private final String code;

    private ExternalServiceType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public static ExternalServiceType fromInt(final Integer statusValue) {
        ExternalServiceType externalServiceTypes = INVALID;
        switch (statusValue) {
            case 1:
                externalServiceTypes = BANK_TRANSFER;
            break;

            default:
            break;
        }
        return externalServiceTypes;
    }

    public EnumOptionData getEnumOptionData() {
        return new EnumOptionData(this.getValue().longValue(), this.getCode(), this.name().toLowerCase());
    }
    
    public boolean isBankTransfer() {
        return this.value.equals(ExternalServiceType.BANK_TRANSFER.getValue());
    }

    public String getCode() {
        return code;
    }
}
