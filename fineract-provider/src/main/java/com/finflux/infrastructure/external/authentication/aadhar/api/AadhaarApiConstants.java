package com.finflux.infrastructure.external.authentication.aadhar.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.finflux.organisation.transaction.authentication.api.TransactionAuthenticationApiConstants;

public class AadhaarApiConstants {

	public static final String AADHAAR_SERIVICE_RESOURCE_NAME = "Aadhaar service";
	public static final String AADHAAR_NUMBER = "aadhaarNumber";
	public static final String AUTHTYPE_OTP = "otp";
	public static final String AUTHTYPE_FINGERPRINT = "fingerprint";
	public static final String AUTHTYPE_IRIS = "iris" ;
	public static final String SUCCESS = "success";

	

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
	public static final String BIOMETRIC = "biometric";
	public static final String AUTH_CAPTURED_DATA = "auth-capture-data";
	public static final String PID = "pid";
	public static final String TYPE = "type";
	public static final String CERTIFICATE_ID = "cert-id";
	public static final String CERTIFICATE_VALUE = "value";
	public static final String HMAC = "hmac";
	public static final String SESSION_KEY = "session-key";
	public static final String UNIQUE_DEVICE_CODE = "unique-device-code";
	public static final String DPID = "dpId";
	public static final String RDSID = "rdsId";
	public static final String RDSVER = "rdsVer";
	public static final String DC = "dc";
	public static final String MI = "mi";
	public static final String MC = "mc";
	public static final String DEMOGRAPHIC = "demographics";
	public static final String FP_IMAGE = "fp-image";
	public static final String FP_MINUTAE = "fp-minutae";
	public static final String IRIS = "iris";
	public static final String PIN = "pin";
	public static final String CONSENT = "consent";
	public static final String PID_VALUE = "value";
	public static final String AADHAAR_ID = "aadhaar-id";
	public static final String CHANNEL = "channel";
	public static final String OTP_PIN_NUMBER = "OtpPinNumber";
	public static final String OTP_RESPONSE = "otpResponse";
	public static final String TRANSACTION_ID = "txn-id";
	

	
	
	

	public static Set<String> OTP_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_NUMBER));
	public static Set<String> KYC_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_NUMBER, AUTH_TYPE, AUTH_DATA,
			LOCATION, LOCATION_TYPE, TransactionAuthenticationApiConstants.PINCODE,
			TransactionAuthenticationApiConstants.LONGITUDE, TransactionAuthenticationApiConstants.LATITUDE));
	public static Set<String> OTP_REQUEST_EKYC_DATA = new HashSet<>(Arrays.asList(REQUESTID,AADHAARUUID,HASH,REQUESTSTATUS));
	public static Set<String> IRIS_REQUEST_DATA = new HashSet<>(Arrays.asList(AADHAAR_ID,MODALITY,PID,TYPE,CERTIFICATE_ID,
			HMAC,SESSION_KEY,UNIQUE_DEVICE_CODE,DPID,RDSID,RDSVER,DC,MI,MC,CONSENT,DEMOGRAPHIC,FP_IMAGE,FP_MINUTAE,IRIS,PIN,TYPE,PID_VALUE));
}
