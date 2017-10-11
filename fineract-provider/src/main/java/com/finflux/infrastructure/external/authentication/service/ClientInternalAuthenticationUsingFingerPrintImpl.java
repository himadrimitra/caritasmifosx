package com.finflux.infrastructure.external.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.finflux.organisation.transaction.authentication.exception.InternalFingerPrintAuthenticationFailedException;
@Service
public class ClientInternalAuthenticationUsingFingerPrintImpl implements SecondLevelAuthenticationService {
	
	private final FingerPrintAuthenticationServices fingerPrintAuthenticationServices;
	private final String KEY = "ClientInternalAuthenticationUsingFingerPrint";
	
	@Autowired
	public ClientInternalAuthenticationUsingFingerPrintImpl(final FingerPrintAuthenticationServices fingerPrintAuthenticationServices) {
		this.fingerPrintAuthenticationServices = fingerPrintAuthenticationServices;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Object authenticateUser(String aadhaarNumber, String authData) {
		
		return this.fingerPrintAuthenticationServices.authenticateUserByAuthKey(aadhaarNumber, authData);
	}

	@Override
	public void responseValidation(final Object response) {
		if(response instanceof Boolean){
		Boolean	authKeyResponse = (Boolean) response;
				if (!authKeyResponse) {
					throw new InternalFingerPrintAuthenticationFailedException(authKeyResponse);
				}
			}
		}
		
	}

