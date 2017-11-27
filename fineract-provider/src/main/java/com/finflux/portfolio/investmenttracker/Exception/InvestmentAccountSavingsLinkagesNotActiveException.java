package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InvestmentAccountSavingsLinkagesNotActiveException  extends AbstractPlatformDomainRuleException {

    public InvestmentAccountSavingsLinkagesNotActiveException() {
        super("error.msg.investmentaccount.not.active" , "Investment Account is not active.");
    }

}
