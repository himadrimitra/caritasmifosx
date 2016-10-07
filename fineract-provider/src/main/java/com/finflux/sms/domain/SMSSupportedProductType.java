package com.finflux.sms.domain;

public enum SMSSupportedProductType {

    INVALID(0, "productType.invalid"), //
    LOANPRODUCT(1, "productType.loanProduct"), //
    SAVINGSPRODUCT(2, "productType.savingsProduct"); //

    private final Integer value;
    private final String code;

    private SMSSupportedProductType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
}