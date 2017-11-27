package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InvestmentAccountSavingsLinkagesNotFoundException  extends AbstractPlatformDomainRuleException {

    public InvestmentAccountSavingsLinkagesNotFoundException(Long id) {
        super("error.msg.investment.saving.linkage.id.invalid", "Investment saving linkage "+id+" invalid", id);
    }

}
