package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SubsidyNotApplicableException extends AbstractPlatformDomainRuleException {

    public SubsidyNotApplicableException(final Long id) {
        super("error.msg.subsidy.for.loan.not.applicable", "Subsidy for Loan with identifier " + id + " not applicable.");
    }
}
