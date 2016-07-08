package com.finflux.loanapplicationreference.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class LoanApplicationSanctionTrancheData {

    private final Long loanAppSanctionTrancheId;
    private final Long loanAppSanctionId;
    private final BigDecimal trancheAmount;
    private final BigDecimal fixedEmiAmount;
    private final LocalDate expectedTrancheDisbursementDate;

    private LoanApplicationSanctionTrancheData(final Long loanAppSanctionTrancheId, final Long loanAppSanctionId,
            final BigDecimal trancheAmount, final BigDecimal fixedEmiAmount, final LocalDate expectedTrancheDisbursementDate) {
        this.loanAppSanctionTrancheId = loanAppSanctionTrancheId;
        this.loanAppSanctionId = loanAppSanctionId;
        this.trancheAmount = trancheAmount;
        this.fixedEmiAmount = fixedEmiAmount;
        this.expectedTrancheDisbursementDate = expectedTrancheDisbursementDate;
    }

    public static LoanApplicationSanctionTrancheData instance(final Long loanAppSanctionTrancheId, final Long loanAppSanctionId,
            final BigDecimal trancheAmount, final BigDecimal fixedEmiAmount, final LocalDate expectedTrancheDisbursementDate) {
        return new LoanApplicationSanctionTrancheData(loanAppSanctionTrancheId, loanAppSanctionId, trancheAmount, fixedEmiAmount,
                expectedTrancheDisbursementDate);
    }

    public Long getLoanAppSanctionTrancheId() {
        return this.loanAppSanctionTrancheId;
    }

    public Long getLoanAppSanctionId() {
        return this.loanAppSanctionId;
    }

    public BigDecimal getTrancheAmount() {
        return this.trancheAmount;
    }

    public BigDecimal getFixedEmiAmount() {
        return this.fixedEmiAmount;
    }

    public LocalDate getExpectedTrancheDisbursementDate() {
        return this.expectedTrancheDisbursementDate;
    }
}