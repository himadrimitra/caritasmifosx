package com.finflux.sms.domain;

import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;

public enum SMSSupportedTransactionType {

    INVALID(LoanTransactionType.INVALID.getValue(), LoanTransactionType.INVALID.getCode()), //
    DISBURSEMENT(LoanTransactionType.DISBURSEMENT.getValue(), LoanTransactionType.DISBURSEMENT.getCode()), //
    REPAYMENT(LoanTransactionType.REPAYMENT.getValue(), LoanTransactionType.REPAYMENT.getCode()); //

    private final Integer value;
    private final String code;

    private SMSSupportedTransactionType(final Integer value, final String code) {
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
