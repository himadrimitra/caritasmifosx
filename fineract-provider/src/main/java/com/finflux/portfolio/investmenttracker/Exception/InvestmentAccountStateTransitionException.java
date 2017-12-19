package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InvestmentAccountStateTransitionException extends AbstractPlatformDomainRuleException {

    public InvestmentAccountStateTransitionException(final String action, final String msg) {
        super("error.msg.investmentaccount.not.in."+action+".state" , "Investment account not in "+msg+" state.", msg);
    }

}
