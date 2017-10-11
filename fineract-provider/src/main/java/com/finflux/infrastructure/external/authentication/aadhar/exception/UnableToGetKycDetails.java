package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class UnableToGetKycDetails extends AbstractPlatformResourceNotFoundException {

	public UnableToGetKycDetails(final String aadhaarNumber, final String authenticationType) {
		super("error.failed.to.get.kyc", "failed to get kyc details. Check if the Aadhaar number is valid or the authentication data "+authenticationType+" is valid." , aadhaarNumber);
	}
}
