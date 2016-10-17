package com.finflux.portfolio.loan.purpose.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanPurposeGroupNotFoundException extends AbstractPlatformResourceNotFoundException {

    public LoanPurposeGroupNotFoundException(final Long id) {
        super("error.msg.loan.purpose.group.id.invalid", "Loan purpose group id " + id + " does not exist", id);
    }

    public LoanPurposeGroupNotFoundException(final Long id, final String status) {
        super("error.msg.loan.purpose.group.id.is.already." + status, "Loan purpose group id " + id + " is already " + status, id, status);
    }
}