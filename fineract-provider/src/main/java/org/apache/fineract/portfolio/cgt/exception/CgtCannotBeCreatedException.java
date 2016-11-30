package org.apache.fineract.portfolio.cgt.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class CgtCannotBeCreatedException extends AbstractPlatformDomainRuleException{

	public CgtCannotBeCreatedException(final String globalisationMessageCode) {
        super("error.msg.cgt.cannot.be.created", "CGT cannot be created until all existing CGT are in completed state", globalisationMessageCode);
    }
	
}
