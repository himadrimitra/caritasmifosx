package com.finflux.infrastructure.external.authentication.aadhar.service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.aadhaarconnect.bridge.gateway.model.OtpResponse;

public interface AadhaarBridgeProvidedService {
	
	//to send otp for customer during client creation
	public OtpResponse processOtpRequest(final String json);
	
	//for ekyc during client creation
	public String processKycRequest(final String json);

	public OtpResponse generateOtp(final String aadhaarNumber);

	public AuthResponse authenticateUserByOtp(final String aadhaarNumber, final String otp,
			final Location location);

	public AuthResponse authenticateUserByFingerPrintUsingAadhaarService(final String aadharNumber,
			final String fingerPrintData, final Location location);

}
