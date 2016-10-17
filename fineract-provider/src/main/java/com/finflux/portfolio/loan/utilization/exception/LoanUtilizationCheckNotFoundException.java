package com.finflux.portfolio.loan.utilization.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanUtilizationCheckNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanUtilizationCheckNotFoundException(final Long id) {
        super("error.msg.loan.utilization.check.id.invalid", "Loan utilization check with identifier " + id + " does not exist", id);
    }

    public LoanUtilizationCheckNotFoundException(final Long loanId, final Long id) {
        super("error.msg.loan.utilization.check.id.invalid", "Loan utilization check with identifier " + id + " does not exist", loanId,id);
    }

}
