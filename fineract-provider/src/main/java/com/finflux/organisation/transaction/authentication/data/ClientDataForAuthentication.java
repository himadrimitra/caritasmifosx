package com.finflux.organisation.transaction.authentication.data;

import com.finflux.infrastructure.external.authentication.data.ExternalAuthenticationServiceData;

public class ClientDataForAuthentication {
	private final ExternalAuthenticationServiceData externalAuthenticationServiceData;
	private final String clientAadhaarNumber;
	private final String authenticationType;
	private final String clientAuthdata;
	private final String locationType;
	private final String pincode;
	private final String longitude;
	private final String latitude;

	private ClientDataForAuthentication(final ExternalAuthenticationServiceData externalAuthenticationServiceData,
			final String clientAadhaarNumber, final String authenticationType, final String clientAuthdata,
			final String locationType, final String pincode, final String longitude, final String latitude) {
		this.externalAuthenticationServiceData = externalAuthenticationServiceData;
		this.clientAadhaarNumber = clientAadhaarNumber;
		this.authenticationType = authenticationType;
		this.clientAuthdata = clientAuthdata;
		this.locationType = locationType;
		this.pincode = pincode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static ClientDataForAuthentication newInsatance(
			final ExternalAuthenticationServiceData externalAuthenticationServiceData, final String clientAadhaarNumber,
			final String authenticationType, final String clientAuthdata, final String locationType,
			final String pincode, final String longitude, final String latitude) {
		return new ClientDataForAuthentication(externalAuthenticationServiceData, clientAadhaarNumber,
				authenticationType, clientAuthdata, locationType, pincode, longitude, latitude);
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

	public String getLocationType() {
		return locationType;
	}

	public String getPincode() {
		return pincode;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

}
