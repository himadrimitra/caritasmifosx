package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;


public class InvestmentAccountStateTransitionException extends AbstractPlatformDomainRuleException {

    public InvestmentAccountStateTransitionException(final String action, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        super("error.msg.investmentaccount.cannot.be." + action , defaultUserMessage, defaultUserMessageArgs);
    }

}
