package com.finflux.infrastructure.external.authentication.service;

public interface SecondLevelAuthenticationService {
	public String getKey();

	public Object authenticateUser(final String aadhaarNumber, final String authData);

	public void responseValidation(final Object response);
}
