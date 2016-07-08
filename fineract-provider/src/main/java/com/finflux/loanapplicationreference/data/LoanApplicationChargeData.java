package com.finflux.loanapplicationreference.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class LoanApplicationChargeData {

    private final Long loanAppChargeId;
    private final Long loanApplicationReferenceId;
    private final Long chargeId;
    private final LocalDate dueDate;
    private final BigDecimal amount;

    private LoanApplicationChargeData(final Long loanAppChargeId, final Long loanApplicationReferenceId, final Long chargeId,
            final LocalDate dueDate, final BigDecimal amount) {
        this.loanAppChargeId = loanAppChargeId;
        this.loanApplicationReferenceId = loanApplicationReferenceId;
        this.chargeId = chargeId;
        this.dueDate = dueDate;
        this.amount = amount;
    }

    public static LoanApplicationChargeData instance(final Long loanAppChargeId, final Long loanApplicationReferenceId,
            final Long chargeId, final LocalDate dueDate, final BigDecimal amount) {
        return new LoanApplicationChargeData(loanAppChargeId, loanApplicationReferenceId, chargeId, dueDate, amount);
    }

    public Long getLoanAppChargeId() {
        return this.loanAppChargeId;
    }

    public Long getLoanApplicationReferenceId() {
        return this.loanApplicationReferenceId;
    }

    public Long getChargeId() {
        return this.chargeId;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}
