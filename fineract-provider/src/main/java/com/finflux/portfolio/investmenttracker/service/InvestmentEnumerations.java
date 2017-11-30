package com.finflux.portfolio.investmenttracker.service;

import com.finflux.portfolio.investmenttracker.data.InvestmentTransactionEnumData;
import com.finflux.portfolio.investmenttracker.domain.InvestmentTransactionType;


public class InvestmentEnumerations {
    
    public static InvestmentTransactionEnumData transactionType(final int transactionType) {
        return transactionType(InvestmentTransactionType.fromInt(transactionType));
    }

    private static InvestmentTransactionEnumData transactionType(InvestmentTransactionType type) {

        InvestmentTransactionEnumData optionData = new InvestmentTransactionEnumData(
                InvestmentTransactionType.INVALID.getValue().longValue(), InvestmentTransactionType.INVALID.getCode(), "Invalid");

        switch (type) {
            case INVALID:
                optionData = new InvestmentTransactionEnumData(InvestmentTransactionType.INVALID.getValue().longValue(),
                        InvestmentTransactionType.INVALID.getCode(), "Invalid");
            break;
            case DEPOSIT:
                optionData = new InvestmentTransactionEnumData(InvestmentTransactionType.DEPOSIT.getValue().longValue(),
                        InvestmentTransactionType.DEPOSIT.getCode(), "Deposit");
            break;
            case WITHDRAWAL:
                optionData = new InvestmentTransactionEnumData(InvestmentTransactionType.WITHDRAWAL.getValue().longValue(),
                        InvestmentTransactionType.WITHDRAWAL.getCode(), "Withdrawal");
            break;
            case INTEREST_POSTING:
                optionData = new InvestmentTransactionEnumData(InvestmentTransactionType.INTEREST_POSTING.getValue().longValue(),
                        InvestmentTransactionType.INTEREST_POSTING.getCode(), "Interest posting");
            break;
            case PAY_CHARGE:
                optionData = new InvestmentTransactionEnumData(InvestmentTransactionType.PAY_CHARGE.getValue().longValue(),
                        InvestmentTransactionType.PAY_CHARGE.getCode(), "Pay Charge");
            break;
            case ACCRUAL_INTEREST:
                optionData = new InvestmentTransactionEnumData(InvestmentTransactionType.ACCRUAL_INTEREST.getValue().longValue(),
                        InvestmentTransactionType.ACCRUAL_INTEREST.getCode(), "Accrual Interest");
            break;
        }
        return optionData;
    }
    
}
