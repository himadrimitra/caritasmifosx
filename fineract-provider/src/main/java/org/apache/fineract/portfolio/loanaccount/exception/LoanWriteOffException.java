
package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LoanWriteOffException extends AbstractPlatformDomainRuleException {

    public LoanWriteOffException(final Object... defaultUserMessageArgs) {
        super("error.msg.loan.cannot.writeoff", " Cannot writeoff loan with negative balance.", defaultUserMessageArgs);

    }

}
