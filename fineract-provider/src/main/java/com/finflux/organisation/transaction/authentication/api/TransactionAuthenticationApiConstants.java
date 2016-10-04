package com.finflux.organisation.transaction.authentication.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TransactionAuthenticationApiConstants {
	public static final String TRANSACTION_AUTHENTICATION_RESOURCE_NAME = "transaction authentication";
	public static final String LOCALE = "locale";
	public static final String TRANSACTION_TYPE_ID = "transactionTypeId";
	public static final String PORTFOLIO_TYPE = "portfolioType";
	public static final String PORTFOLIO_TYPE_ID = "portfolioTypeId";
	public static final String PAYMENT_TYPE_ID = "paymentTypeId";
	public static final String AMOUNT_GREATER_THAN = "amountGreaterThan";
	public static final String AUTHENTICATION_TYPE_ID = "authenticationTypeId";
	public static final String SECOUND_APP_USER_ROLE_ID = "secondAppUserRoleId";
	public static final String ENABLE_SECOND_APP_USER = "enableSecondAppUser";

	public static final String CREATE_ACTION = "CREATE";
	public static final String UPDATE_ACTION = "UPDATE";
	public static final String DELETE_ACTION = "DELETE";
	public static final String TRANSACTION_AUTHENTICATION_SERVICE = "TRANSACTIONAUTHENTICATIONSERVICE";

	public static final String ID = "id";
	public static final String AMOUNT = "amount";
	public static final String IS_SECOND_APP_USER_ENABLED = "isSecondAppUserEnabled";
	
	public static final String AADHAAR_OTP = "Aadhaar OTP";
	public static final String AADHAAR_FINGERPRINT = "Aadhaar fingerprint";
	public static final String AADHAAR_NUMBER = "aadhaarNumber";
	public static final String OTP = "otp";
	public static final String FINGERPRINT = "fingerPrint";
	public static final String AUTHENTICATION_TYPE = "authenticationType";
	public static final String AADHAAR = "Aadhaar";
	public static final String CLIENT_AUTH_DATA = "clientAuthData";
	
	public static final String LOCATION = "location";
	public static final String LOCATION_TYPE = "locationType";
	public static final String PINCODE = "pincode";
	public static final String GPS = "gps";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	
	public static final String TRANSACTION_AMOUNT = "transactionAmount";
	public static final String TRANSACTION_AUTHENTICATION_ID = "transactionAuthenticationId";
	public static final String AUTHENTICAION_RULE_ID = "authenticationRuleId"; 
	
	public static final String READ_TRANSACTIONAUTHENTICATIONSERVICE = "READ_TRANSACTIONAUTHENTICATIONSERVICE";
	public static final String CREATE_TRANSACTIONAUTHENTICATIONSERVICE = "CREATE_TRANSACTIONAUTHENTICATIONSERVICE";
	public static final String UPDATE_TRANSACTIONAUTHENTICATIONSERVICE = "UPDATE_TRANSACTIONAUTHENTICATIONSERVICE";
	public static final String DELETE_TRANSACTIONAUTHENTICATIONSERVICE = "DELETE_TRANSACTIONAUTHENTICATIONSERVICE";
	public static final String GENERATE_OTP = "GENERATE_OTP";

	public static Set<String> TRANSACTION_AUTHENTICATION_REQUEST_DATA_PARAMETER = new HashSet<>(
			Arrays.asList(LOCALE, TRANSACTION_TYPE_ID, PORTFOLIO_TYPE_ID, PAYMENT_TYPE_ID, AMOUNT,
					AUTHENTICATION_TYPE_ID, SECOUND_APP_USER_ROLE_ID, ENABLE_SECOND_APP_USER));

	public static Set<String> TRANSACTION_AUTHENTICATIOM_SERVICE_RESPONSE = new HashSet<>(
			Arrays.asList(ID, PORTFOLIO_TYPE_ID, PAYMENT_TYPE_ID, AMOUNT, AUTHENTICATION_TYPE_ID,
					SECOUND_APP_USER_ROLE_ID, IS_SECOND_APP_USER_ENABLED));
}
