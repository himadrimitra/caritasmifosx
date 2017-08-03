package com.finflux.organisation.transaction.authentication.data;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;

public class ClientDataForAuthentication {
	private final ExternalAuthenticationServiceData externalAuthenticationServiceData;
	private final String clientAadhaarNumber;
	private final String authenticationType;
	private final String clientAuthdata;


	private ClientDataForAuthentication(final ExternalAuthenticationServiceData externalAuthenticationServiceData,
			final String clientAadhaarNumber, final String authenticationType, final String clientAuthdata) {
		this.externalAuthenticationServiceData = externalAuthenticationServiceData;
		this.clientAadhaarNumber = clientAadhaarNumber;
		this.authenticationType = authenticationType;
		this.clientAuthdata = clientAuthdata;
	}

	public static ClientDataForAuthentication newInsatance(
			final ExternalAuthenticationServiceData externalAuthenticationServiceData, final String clientAadhaarNumber,
			final String authenticationType, final String clientAuthdata) {
		return new ClientDataForAuthentication(externalAuthenticationServiceData, clientAadhaarNumber,
				authenticationType, clientAuthdata);
	}

	public ExternalAuthenticationServiceData getExternalAuthenticationServiceData() {
		return externalAuthenticationServiceData;
	}

	public String getClientAadhaarNumber() {
		return clientAadhaarNumber;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public String getClientAuthdata() {
		return clientAuthdata;
	}

}
