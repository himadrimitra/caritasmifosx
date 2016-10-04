package com.finflux.infrastructure.external.authentication.service;

import com.aadhaarconnect.bridge.capture.model.common.Location;
import com.aadhaarconnect.bridge.gateway.model.AuthResponse;
import com.aadhaarconnect.bridge.gateway.model.KycResponse;
import com.aadhaarconnect.bridge.gateway.model.OtpResponse;

public interface SecondLevelAuthenticationService {
	public String getKey();

	public Object authenticateUser(final String aadhaarNumber, final String authData,
			final Location location);


}
