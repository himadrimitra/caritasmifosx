package com.finflux.organisation.transaction.authentication.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class OtpTypeNotSupported extends AbstractPlatformDomainRuleException{

	public OtpTypeNotSupported() {
		super("generate.Otp.type.not.supported", "Genereate OTP is not Supported.", "");
	}
}
