package com.finflux.infrastructure.external.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.capture.model.common.request.CertificateType;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.aadhaarconnect.bridge.gateway.model.KycResponse;
import com.aadhaarconnect.bridge.gateway.model.OtpResponse;
import com.finflux.infrastructure.external.authentication.aadhar.service.AadhaarBridgeProvidedService;
import com.finflux.organisation.transaction.authentication.exception.SecondaryAuthenticationFailedException;

@Service
public class SecondLevelAuthenticationServiceUsingAadhaarOtpImpl implements SecondLevelAuthenticationService {

	private final AadhaarBridgeProvidedService aadharReadPlatformService;
	private final String KEY = "SecondLevelAuthenticationServiceUsingAadhaarOtp";

	@Autowired
	public SecondLevelAuthenticationServiceUsingAadhaarOtpImpl(final AadhaarBridgeProvidedService aadhaarBridgeProvidedService) {
		this.aadharReadPlatformService = aadhaarBridgeProvidedService;
	}

	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public Object authenticateUser(final String aadhaarNumber, final String otp) {
		return this.aadharReadPlatformService.authenticateUserByOtp(aadhaarNumber, otp);
	}

	@Override
	public void responseValidation(final Object response) {
		if (response instanceof AuthResponse) {
			AuthResponse authResponse = (AuthResponse) response;
			if (authResponse != null) {
				if (!authResponse.isSuccess()) {
					throw new SecondaryAuthenticationFailedException(authResponse);
				}
			}
		}
	}

}
