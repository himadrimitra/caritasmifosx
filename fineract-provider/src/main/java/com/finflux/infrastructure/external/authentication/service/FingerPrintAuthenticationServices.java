package com.finflux.infrastructure.external.authentication.service;

public interface FingerPrintAuthenticationServices {

	public Boolean authenticateUserByAuthKey(final String userKey);
}
