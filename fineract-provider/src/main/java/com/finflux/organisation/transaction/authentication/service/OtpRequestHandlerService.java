package com.finflux.organisation.transaction.authentication.service;

public interface OtpRequestHandlerService {
	public String getKey();
	public Object sendOtp(final Long entityId, final String  jsonCommand);
}
