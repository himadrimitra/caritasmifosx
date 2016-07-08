package com.finflux.loanapplicationreference.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanApplicationChargeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanApplicationChargeNotFoundException(final Long id) {
        super("error.msg.loan.application.charge.does.not.exist", "Loan application charge with identifier " + id + " does not exist", id);
    }
}
