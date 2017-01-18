package com.finflux.portfolio.loanemipacks.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LoanEMIPackNotFoundException  extends AbstractPlatformResourceNotFoundException {
        public LoanEMIPackNotFoundException(final Long id) {
                super("error.msg.loanemipacks.id.invalid", "Loan EMI Pack with identifier " + id + " does not exist", id);
        }

}
