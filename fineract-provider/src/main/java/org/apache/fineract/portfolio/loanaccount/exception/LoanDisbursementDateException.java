package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class LoanDisbursementDateException extends AbstractPlatformDomainRuleException {

    public LoanDisbursementDateException(final String defaultUserMessage, final Object... defaultUserMessageArgs) {
        super("error.msg.other.loan.disbursement.detail.with.same.date", defaultUserMessage, defaultUserMessageArgs);
    }
}
