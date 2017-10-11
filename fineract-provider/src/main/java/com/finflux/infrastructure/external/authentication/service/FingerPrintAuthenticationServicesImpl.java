package com.finflux.infrastructure.external.authentication.service;

import org.springframework.stereotype.Service;

import com.finflux.fingerprint.api.FingerPrintApiConstants;

@Service
public class FingerPrintAuthenticationServicesImpl implements FingerPrintAuthenticationServices {
	
	@Override
	public Boolean authenticateUserByAuthKey(final String aadharNumber, final String userKey) {
		final String authKey = FingerPrintApiConstants.FINGER_PRINT_AUTH_KEY;
		boolean isKeyMatch = false;
		if(authKey.equals(userKey)){
			isKeyMatch = true;
		}
		return isKeyMatch;
	}
	

}
