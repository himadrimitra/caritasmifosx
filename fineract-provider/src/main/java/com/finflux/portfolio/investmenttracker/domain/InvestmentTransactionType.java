package com.finflux.portfolio.investmenttracker.domain;

public enum InvestmentTransactionType {

    INVALID(0, "investmentTransactionType.invalid"), //
    DEPOSIT(1, "investmentTransactionType.deposit"), //
    WITHDRAWAL(2, "investmentTransactionType.withdrawal"), //
    INTEREST_POSTING(3, "investmentTransactionType.interestPosting"), 
    PAY_CHARGE(4, "investmentTransactionType.payCharge");

    private final Integer value;
    private final String code;

    private InvestmentTransactionType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static InvestmentTransactionType fromInt(final Integer transactionType) {

        if (transactionType == null) { return InvestmentTransactionType.INVALID; }

        InvestmentTransactionType investmentTransactionType = InvestmentTransactionType.INVALID;
        switch (transactionType) {
            case 1:
                investmentTransactionType = InvestmentTransactionType.DEPOSIT;
            break;
            case 2:
                investmentTransactionType = InvestmentTransactionType.WITHDRAWAL;
            break;
            case 3:
                investmentTransactionType = InvestmentTransactionType.INTEREST_POSTING;
            break;
            case 4:
                investmentTransactionType = InvestmentTransactionType.PAY_CHARGE;
            break;
        }
        return investmentTransactionType;
    }

    public boolean isDeposit() {
        return this.value.equals(InvestmentTransactionType.DEPOSIT.getValue());
    }

    public boolean isWithdrawal() {
        return this.value.equals(InvestmentTransactionType.WITHDRAWAL.getValue());
    }

    public boolean isInterestPosting() {
        return this.value.equals(InvestmentTransactionType.INTEREST_POSTING.getValue());
    }

    public boolean isPayCharge() {
        return this.value.equals(InvestmentTransactionType.PAY_CHARGE.getValue());
    }

}
