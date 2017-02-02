package com.finflux.risk.creditbureau.provider.data;

public enum CreditBureauEntityType {

    INVALID(0, "creditBureauEntityType.invalid"), //
    LOANAPPLICATION(1, "creditBureauEntityType.loanApplication"), //
    LOAN(2, "creditBureauEntityType.loan"), //
    CLIENT(3, "creditBureauEntityType.client");

    final private Integer value;
    final private String code;

    CreditBureauEntityType(Integer value, String code) {
        this.value = value;
        this.code = code;
    }
}