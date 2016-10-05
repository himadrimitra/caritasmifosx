package org.apache.fineract.integrationtests.common;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class ExternalServiceConnectivityHelper {

	private static final String DefaultApiPath = "/fineract-provider/api/v1/";
	private static final String GenerateOtp = DefaultApiPath + "transactions/authentication/generate/otp";
	private static final String AuthenticateUser = DefaultApiPath + "transactions/authentication/user";
	private static final String AuthenticationServices = DefaultApiPath + "external/authentications/services";
	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;

	public ExternalServiceConnectivityHelper(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec) {
		this.responseSpec = responseSpec;
		this.requestSpec = requestSpec;
	}

	public String generateOtp(final Integer loanId, final Integer transactionAuthenticationTypeId) {
		String json = getAsJSON(loanId, transactionAuthenticationTypeId);
		System.out.println("--------------------SEND OTP---------------------------");
		System.out.println("The json is "+json);
		return Utils.performServerPost(this.requestSpec, this.responseSpec, GenerateOtp + "?" + Utils.TENANT_IDENTIFIER,
				json, null);
	}

	public String getTheListOfSecondaryServices() {
		return Utils.performServerGet(this.requestSpec, this.responseSpec,
				this.AuthenticationServices + "?" + Utils.TENANT_IDENTIFIER, null);
	}

	public Map<String,Boolean> updateTheExternalAuthenticationService(final long serviceId, final boolean isAcctive) {
		final HashMap<String, Boolean> map = new HashMap<>();
		map.put("isActive", isAcctive);
		String jsonBody = new Gson().toJson(map);
		System.out.println("the serviec id " + serviceId + " the json is " + jsonBody);
		String apiPath = AuthenticationServices + "/" + serviceId + "?" + Utils.TENANT_IDENTIFIER;
		System.out.println("the api path is " + apiPath);
		return Utils.performServerPut(this.requestSpec, this.responseSpec, apiPath ,
				jsonBody, "changes");
	}

	public String getAsJSON(final Integer loanId, final Integer transactionAuthenticationTypeId) {
		final HashMap<String, Object> map = new HashMap<>();
		map.put("loanId", loanId);
		map.put("authenticationRuleId", transactionAuthenticationTypeId);
		return new Gson().toJson(map);
	}

}
