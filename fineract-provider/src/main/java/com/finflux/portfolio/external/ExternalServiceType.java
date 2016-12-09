package com.finflux.portfolio.external;



public enum ExternalServiceType {

    INVALID(0), BANK_TRANSFER(1);

    private final Integer value;

    private ExternalServiceType(final Integer value) {
        this.value = value;
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
    
    public boolean isBankTransfer() {
        return this.value.equals(ExternalServiceType.BANK_TRANSFER.getValue());
    }
}
