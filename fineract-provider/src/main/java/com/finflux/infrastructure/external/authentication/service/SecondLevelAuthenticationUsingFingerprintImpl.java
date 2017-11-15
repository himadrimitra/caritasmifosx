package com.finflux.infrastructure.external.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.finflux.infrastructure.external.authentication.aadhar.service.AadhaarBridgeProvidedService;
import com.finflux.organisation.transaction.authentication.exception.SecondaryAuthenticationFailedException;

@Service
public class SecondLevelAuthenticationUsingFingerprintImpl implements SecondLevelAuthenticationService{

	private final AadhaarBridgeProvidedService aadharReadPlatformService;
	private final String KEY = "SecondLevelAuthenticationServiceUsingAadhaarFingerprint";
	
	@Autowired
	public SecondLevelAuthenticationUsingFingerprintImpl(final AadhaarBridgeProvidedService aadhaarBridgeProvidedService) {
		this.aadharReadPlatformService = aadhaarBridgeProvidedService;
	}
	
	@Override
	public String getKey() {
		return this.KEY;
	}

	@Override
	public Object authenticateUser(final  String aadhaarNumber, final String authData) {
		return this.aadharReadPlatformService.authenticateUserByBiometricDataUsingAadhaarService(aadhaarNumber, authData);
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