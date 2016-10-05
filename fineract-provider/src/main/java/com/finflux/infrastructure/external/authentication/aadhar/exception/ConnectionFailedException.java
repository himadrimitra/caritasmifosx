package com.finflux.infrastructure.external.authentication.aadhar.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformServiceUnavailableException;

public class ConnectionFailedException extends AbstractPlatformServiceUnavailableException {

	public ConnectionFailedException(final String message) {
		super("error.msg.aadhaar.server.connection.failed", "Aadhaar server connection failed", message);
	}
}
