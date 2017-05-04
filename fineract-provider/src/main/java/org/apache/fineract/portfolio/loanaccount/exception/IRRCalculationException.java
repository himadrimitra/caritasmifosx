
package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class IRRCalculationException extends AbstractPlatformDomainRuleException {

    public IRRCalculationException() {
        super("error.msg.loan.unable.to.calcualte.irr", "Not able to calculate IRR with the provided details");
    }

}
