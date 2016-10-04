package com.finflux.infrastructure.external.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.finflux.infrastructure.external.authentication.aadhar.service.AadhaarBridgeProvidedService;

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
	public Object authenticateUser(final  String aadhaarNumber, final String authData, final Location location) {
		return this.aadharReadPlatformService.authenticateUserByFingerPrintUsingAadhaarService(aadhaarNumber, authData, location);
	}

}
