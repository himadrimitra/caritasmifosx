package org.apache.fineract.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class SubsidyNotAppliedException extends AbstractPlatformDomainRuleException{

    public SubsidyNotAppliedException(final Long id) {
        super("error.msg.subsidy.for.loan.not.applied", "Subsidy for Loan with identifier " + id + " not applied.");
    }
}
