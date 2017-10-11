package com.finflux.infrastructure.external.authentication.service;

public interface GenerateOtpService {
	
	public String getKey();

	public Object generateOtp(final String identifier);
}
