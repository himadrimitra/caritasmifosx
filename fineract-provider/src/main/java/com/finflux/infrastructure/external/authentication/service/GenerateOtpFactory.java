package com.finflux.infrastructure.external.authentication.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class GenerateOtpFactory {

	private Map<String, GenerateOtpService> generateOtpServiceMap = new HashMap<>();

	@Autowired
	public GenerateOtpFactory(final Set<GenerateOtpService> generateOtpServices) {
		for (GenerateOtpService generateOtpService : generateOtpServices) {
			this.generateOtpServiceMap.put(generateOtpService.getKey(), generateOtpService);
		}
	}

	public GenerateOtpService getOtpService(final String key) {
		return this.generateOtpServiceMap.get(key);
	}
}
