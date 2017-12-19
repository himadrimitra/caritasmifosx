package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class FutureDateTransactionException extends AbstractPlatformDomainRuleException  {
	
	public FutureDateTransactionException(final String action) {
        super("error.msg.investment.account."+action+".date.cannot.be.in.future" , action+" date can not be in future.", action);
    }

}
