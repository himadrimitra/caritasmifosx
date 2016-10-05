package com.finflux.infrastructure.external.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ExternalAuthenticationServiceNotFoundException extends AbstractPlatformResourceNotFoundException {

	public ExternalAuthenticationServiceNotFoundException(final Long id) {
		super("error.msg.External.Authentication.Service.id.invalid",
				"External Authentication Service with identifier " + id + " does not exist", id);
	}
}
