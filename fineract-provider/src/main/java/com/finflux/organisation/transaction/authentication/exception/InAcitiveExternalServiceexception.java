package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InAcitiveExternalServiceexception extends AbstractPlatformDomainRuleException {

	public InAcitiveExternalServiceexception(String serviceName, final Long serviceId) {
		super("error.msg.External.Authentication.Service.inactive", "selected external service " + serviceName
				+ " is inactive. Select active external authentication service.", serviceId);
	}

}
