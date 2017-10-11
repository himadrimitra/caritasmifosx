package com.finflux.portfolio.loan.purpose.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanPurposeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanPurposeNotFoundException(final Long id) {
        super("error.msg.loan.purpose.id.invalid", "Loan purpose with id " + id + " does not exist", id);
    }

    public LoanPurposeNotFoundException(final Long id, final String status) {
        super("error.msg.loan.purpose.id.is.already." + status, "Loan purpose with id " + id + " is already " + status, id, status);
    }
}