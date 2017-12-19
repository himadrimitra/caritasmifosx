package com.finflux.portfolio.investmenttracker.Exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidDateException  extends AbstractPlatformDomainRuleException  {
	
	public InvalidDateException(final String action, final String base) {
        super("error.msg."+action+".date.cannot.be.before." + base+".date" , action+" date can not be before "+base+" date.", action,base);
    }

}