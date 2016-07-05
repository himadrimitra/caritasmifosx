package com.finflux.loanapplicationreference.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanApplicationReferenceNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanApplicationReferenceNotFoundException(final Long id) {
        super("error.msg.loan.application.reference.does.not.exist", "Loan application reference with identifier " + id + " does not exist", id);
    }
}
