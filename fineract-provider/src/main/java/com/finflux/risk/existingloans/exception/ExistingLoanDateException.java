package com.finflux.risk.existingloans.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class ExistingLoanDateException extends  AbstractPlatformDomainRuleException {

    public ExistingLoanDateException(final String postFix, final String defaultUserMessage, final Object... defaultUserMessageArgs) {
        super("error.msg.loan.application." + postFix, defaultUserMessage, defaultUserMessageArgs);
    }
}
