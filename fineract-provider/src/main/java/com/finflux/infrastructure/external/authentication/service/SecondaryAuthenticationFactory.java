package com.finflux.infrastructure.external.authentication.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class SecondaryAuthenticationFactory {
	private Map<String, SecondLevelAuthenticationService> secondLevelAuthenticationHashMap = new HashMap<>();
	
	@Autowired
	public SecondaryAuthenticationFactory(final Set<SecondLevelAuthenticationService> secondLevelAuthenticationServices) {
		for(SecondLevelAuthenticationService service : secondLevelAuthenticationServices){
			this.secondLevelAuthenticationHashMap.put(service.getKey(), service);
		}
	}
	
	public SecondLevelAuthenticationService getSecondLevelAuthenticationService(final String key) {
		return this.secondLevelAuthenticationHashMap.get(key);
	}
}
