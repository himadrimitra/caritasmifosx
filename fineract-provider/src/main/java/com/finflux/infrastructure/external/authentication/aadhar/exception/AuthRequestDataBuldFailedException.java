package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AuthRequestDataBuldFailedException extends AbstractPlatformDomainRuleException{

	public AuthRequestDataBuldFailedException(final String resource) {
		 super("error.msg.auth.data.build.failed",
	                "Unable to Authenticate using " + resource, resource);
		// TODO Auto-generated constructor stub
	}

}
