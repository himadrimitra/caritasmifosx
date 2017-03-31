package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LoanApplicationCannotbeSubmittedException extends AbstractPlatformDomainRuleException {

    public LoanApplicationCannotbeSubmittedException(final Long productId) {
        super("error.msg.loan.cannot.be.submit.as.loan.product.is.not.mapped.to.this.office",
                "Loan can't be submited as " + productId + " is not mapped to this office");
    }

}
