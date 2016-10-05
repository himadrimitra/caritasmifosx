package com.finflux.infrastructure.external.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.external.authentication.aadhar.service.AadhaarBridgeProvidedService;

@Service
public class GenerateOtpServiceUsingAadhaarServiceImpl implements GenerateOtpService {
	private final AadhaarBridgeProvidedService aadharReadPlatformService;
	private final String KEY = "SecondLevelAuthenticationServiceUsingAadhaarOtp";

	@Autowired
	public GenerateOtpServiceUsingAadhaarServiceImpl(final AadhaarBridgeProvidedService aadhaarBridgeProvidedService) {
		this.aadharReadPlatformService = aadhaarBridgeProvidedService;
	}

	@Override
	public Object generateOtp(String identifier) {
		return this.aadharReadPlatformService.generateOtp(identifier);
	}

	@Override
	public String getKey() {
		return this.KEY;
	}

}
