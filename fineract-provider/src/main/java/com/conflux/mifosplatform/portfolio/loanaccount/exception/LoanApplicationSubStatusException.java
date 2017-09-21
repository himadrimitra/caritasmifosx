package com.conflux.mifosplatform.portfolio.loanaccount.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class LoanApplicationSubStatusException extends AbstractPlatformDomainRuleException {

    public LoanApplicationSubStatusException(
    		final String postFix, 
    		final String defaultUserMessage, 
    		final Object... defaultUserMessageArgs) {
    	
        super("error.msg.loan.application." + postFix, 
        		defaultUserMessage, 
        		defaultUserMessageArgs);
    }

}
