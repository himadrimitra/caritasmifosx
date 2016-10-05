package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AuthenticationTypeDoesNotExistException extends AbstractPlatformDomainRuleException {

	public AuthenticationTypeDoesNotExistException() {
		super("authentication.type.doesnot.exist", "Authentication Type is not present in josn", "");
	}

}
