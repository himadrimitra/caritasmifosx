package com.finflux.infrastructure.external.authentication.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExternalAuthenticationServicesDataConstants {
	public static final String TRANSACTION_AUTHENTICATION_SERVICE = "TransactionAuthenticationService";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String AUTH_SERVICE_CLASS_NAME = "auth_service_class_name";
	public static final String ISACTIVE = "isActive";
	public static final String IS_ACTIVE = "is_active";
	
	public static final String READ_AUTHENTICATIONSERVICE = "READ_AUTHENTICATIONSERVICE";
	public static final String UPDATE_AUTHENTICATIONSERVICE = "UPDATE_AUTHENTICATIONSERVICE";

	public static final Set<String> TRANSACTION_AUTHENTICATION_SERVICES_RESPONSE = new HashSet<>(
			Arrays.asList(ID, NAME, DESCRIPTION, AUTH_SERVICE_CLASS_NAME, ISACTIVE));
}
