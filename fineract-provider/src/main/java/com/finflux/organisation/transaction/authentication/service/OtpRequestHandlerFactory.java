package com.finflux.organisation.transaction.authentication.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class OtpRequestHandlerFactory {

	private Map<String, OtpRequestHandlerService> otprequestHandlerServiceMap = new HashMap<>();
	
	@Autowired
	public OtpRequestHandlerFactory(final Set<OtpRequestHandlerService> otpRequestHandlerServices) {
		for(OtpRequestHandlerService otpRequestHandlerService: otpRequestHandlerServices ){
			this.otprequestHandlerServiceMap.put(otpRequestHandlerService.getKey(), otpRequestHandlerService);
		}
	}
	
	public OtpRequestHandlerService getOtpRequestHandlerService(final String key){
		return otprequestHandlerServiceMap.get(key);
	}
}
