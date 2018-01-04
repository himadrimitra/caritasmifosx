package com.finflux.portfolio.investmenttracker.data;

import com.finflux.portfolio.investmenttracker.domain.InvestmentTransactionType;

public class InvestmentTransactionEnumData {

    private final Long id;
    private final String code;
    private final String value;

    private final boolean deposit;
    private final boolean withdrawal;
    private final boolean interestPosting;
    private final boolean feeDeduction;
    private final boolean accrualInterest;

    public InvestmentTransactionEnumData(Long id, String code, String value) {
        this.id = id;
        this.code = code;
        this.value = value;
        this.deposit = Long.valueOf(InvestmentTransactionType.DEPOSIT.getValue()).equals(this.id);
        this.withdrawal = Long.valueOf(InvestmentTransactionType.WITHDRAWAL.getValue()).equals(this.id);
        this.interestPosting = Long.valueOf(InvestmentTransactionType.INTEREST_POSTING.getValue()).equals(this.id);
        this.feeDeduction = Long.valueOf(InvestmentTransactionType.PAY_CHARGE.getValue()).equals(this.id);
        this.accrualInterest = Long.valueOf(InvestmentTransactionType.ACCRUAL_INTEREST.getValue()).equals(this.id);
    }

    public boolean isDeposit() {
        return this.deposit;
    }

    public boolean isWithdrawal() {
        return this.withdrawal;
    }

    public boolean isInterestPosting() {
        return this.interestPosting;
    }

    public boolean isFeeDeduction() {
        return this.feeDeduction;
    }

    
    public Long getId() {
        return this.id;
    }

    
    public String getCode() {
        return this.code;
    }

    
    public String getValue() {
        return this.value;
    }
    
    public boolean isAccrualInterest() {
        return this.accrualInterest;
    }

}
