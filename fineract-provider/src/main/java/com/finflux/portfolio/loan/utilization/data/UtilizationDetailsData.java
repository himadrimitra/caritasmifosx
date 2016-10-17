package com.finflux.portfolio.loan.utilization.data;

import java.math.BigDecimal;

import com.finflux.portfolio.loan.purpose.data.LoanPurposeData;

public class UtilizationDetailsData {

    private final LoanPurposeData loanPurposeData;
    private final Boolean isSameAsOroginalPurpose;
    private final BigDecimal amount;
    private final String comment;

    private UtilizationDetailsData(final LoanPurposeData loanPurposeData, final Boolean isSameAsOroginalPurpose, final BigDecimal amount,
            final String comment) {

        this.loanPurposeData = loanPurposeData;
        this.isSameAsOroginalPurpose = isSameAsOroginalPurpose;
        this.amount = amount;
        this.comment = comment;
    }

    public static UtilizationDetailsData instance(final LoanPurposeData loanPurposeData, final Boolean isSameAsOroginalPurpose,
            final BigDecimal amount, final String comment) {
        return new UtilizationDetailsData(loanPurposeData, isSameAsOroginalPurpose, amount, comment);
    }
}