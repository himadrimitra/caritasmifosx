package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

import com.aadhaarconnect.bridge.gateway.model.AuthResponse;

public class SecondaryAuthenticationFailedException extends AbstractPlatformDomainRuleException {

	public SecondaryAuthenticationFailedException(AuthResponse serviceName) {
		super("error.msg.Secondary.Authentication.Service.failed", "Client aunthentication failed", serviceName);
	}
	
}
