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
	public static final String SACODE = "saCode";
	public static final String SALTKEY = "saltKey";
	public static final String AADHAARID = "aadhaarId";
	public static final String SUCCESSURL = "successUrl";
	public static final String FAILUREURL = "failureUrl";
	public static final String REQUESTID = "requestId";
	public static final String PURPOSE = "purpose";
	public static final String MODALITY = "modality";
	public static final String HASH = "hash";
	public static final String REQUESTPURPOSE = "bankAccount";
	public static final String AADHAARUUID = "uuid";
	public static final String EKYC = "ekyc";
	public static final String REQUESTSTATUS = "status";
	public static final String GENERATE_OTP = "GENERATE_OTP";
	public static final String READ_KYC_DETAILS = "READ_KYC_DETAILS";
	

	public static Set<String> OTP_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_NUMBER));
	public static Set<String> KYC_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_NUMBER, AUTH_TYPE, AUTH_DATA,
			LOCATION, LOCATION_TYPE, TransactionAuthenticationApiConstants.PINCODE,
			TransactionAuthenticationApiConstants.LONGITUDE, TransactionAuthenticationApiConstants.LATITUDE));
	public static Set<String> OTP_REQUEST_EKYC_DATA = new HashSet<>(Arrays.asList(REQUESTID,AADHAARUUID,HASH,REQUESTSTATUS));

}
