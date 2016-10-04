package com.finflux.infrastructure.external.authentication.aadhar.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;

public class AadhaarApiConstants {

	public static final String AADHAAR_SERIVICE_RESOURCE_NAME = "Aadhaar service";
	public static final String AADHAAR_NUMBER = "aadhaarNumber";
	public static final String OTP = "otp";
	public static final String SUCCESS = "success";

	public static final String FINGERPRINT = "fingerprint";

	public static final String AUTH_TYPE = "authType";
	public static final String AUTH_DATA = "authData";
	public static final String LOCATION = "location";
	public static final String LOCATION_TYPE = "locationType";

	public static Set<String> OTP_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_NUMBER));
	public static Set<String> KYC_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_NUMBER, AUTH_TYPE, AUTH_DATA,
			LOCATION, LOCATION_TYPE, TransactionAuthenticationApiConstants.PINCODE,
			TransactionAuthenticationApiConstants.LONGITUDE, TransactionAuthenticationApiConstants.LATITUDE));

}
