package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class UnableToSendOtpException extends AbstractPlatformResourceNotFoundException {

	public UnableToSendOtpException(final String aadhaarNumber) {
		super("error.failed.to.send.otp", "failed to send otp. Check if the Aadhaar number is valid ", aadhaarNumber);
	}

}
