package com.finflux.loanapplicationreference.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidLoanApplicationReferenceStatusException extends AbstractPlatformDomainRuleException {

        public InvalidLoanApplicationReferenceStatusException() {
                super("error.msg.loan.application.reference.invalid.status", "Operation not supported as Loan application reference status has past APPROVAL status");
        }
}
