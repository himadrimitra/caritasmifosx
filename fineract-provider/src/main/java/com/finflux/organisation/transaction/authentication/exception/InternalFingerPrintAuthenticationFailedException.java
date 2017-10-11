package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InternalFingerPrintAuthenticationFailedException extends AbstractPlatformDomainRuleException {

	public InternalFingerPrintAuthenticationFailedException(Boolean serviceName) {
		super("error.msg.Internal.FingerPrint.Authentication.Service.failed", "Client aunthentication failed", serviceName);
	}
}
