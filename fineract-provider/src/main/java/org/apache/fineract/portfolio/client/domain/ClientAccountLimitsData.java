package org.apache.fineract.portfolio.client.domain;

import java.math.BigDecimal;

public class ClientAccountLimitsData {

    private final Long id;
    private final Long clientId;
    private final BigDecimal limitOnTotalDisbursementAmount;
    private final BigDecimal limitOnTotalLoanOutstandingAmount;
    private final BigDecimal dailyWithdrawalLimit;
    private final BigDecimal dailyTransferLimit;
    private final BigDecimal limitOnTotalOverdraftAmount;

    public ClientAccountLimitsData(Long id, BigDecimal limitOnTotalDisbursementAmount, BigDecimal limitOnTotalLoanOutstandingAmount,
            BigDecimal dailyWithdrawalLimit, BigDecimal dailyTransferLimit, BigDecimal limitOnTotalOverdraftAmount, Long clientId) {
        this.id = id;
        this.clientId = clientId;
        this.limitOnTotalDisbursementAmount = limitOnTotalDisbursementAmount;
        this.limitOnTotalLoanOutstandingAmount = limitOnTotalLoanOutstandingAmount;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.dailyTransferLimit = dailyTransferLimit;
        this.limitOnTotalOverdraftAmount = limitOnTotalOverdraftAmount;
    }

    public Long getId() {
        return this.id;
    }

    public BigDecimal getLimitOnTotalDisbursementAmount() {
        return this.limitOnTotalDisbursementAmount;
    }

    public BigDecimal getLimitOnTotalLoanOutstandingAmount() {
        return this.limitOnTotalLoanOutstandingAmount;
    }

    public BigDecimal getDailyWithdrawalLimit() {
        return this.dailyWithdrawalLimit;
    }

    public BigDecimal getDailyTransferLimit() {
        return this.dailyTransferLimit;
    }

    public BigDecimal getLimitOnTotalOverdraftAmount() {
        return this.limitOnTotalOverdraftAmount;
    }

    public Long getClientId() {
        return this.clientId;
    }
}
