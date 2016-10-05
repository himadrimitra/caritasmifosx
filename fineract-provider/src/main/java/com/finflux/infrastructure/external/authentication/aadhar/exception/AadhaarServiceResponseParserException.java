package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class AadhaarServiceResponseParserException extends AbstractPlatformDomainRuleException {

	public AadhaarServiceResponseParserException() {
		super("error.msg.aadhaar.json.response.invalid", "Failed to communicate with Aadhaa service.");
	}

}
