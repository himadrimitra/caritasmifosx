package org.apache.fineract.integrationtests.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TransactionAuthenticationHelper {

	private static final String CREATE_TRANSACTION_AUTHENTICATION_URL = "/fineract-provider/api/v1/transactions/authentication?"
			+ Utils.TENANT_IDENTIFIER;
	private static final String TRANSACTION_AUTHENTICATION_URL = "/fineract-provider/api/v1/transactions/authentication";

	public static Integer createTransactionAuthentication(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final String locale, final Integer portfolioTypeId,
			final Integer transactionTypeId, final Integer paymentTypeId, final BigDecimal amountGreaterThan, final Long authenticationTypeId) {
		System.out.println(
				"---------------------------------CREATING A TransactionAuthentication TYPE---------------------------------------------");
		return Utils.performServerPost(requestSpec, responseSpec, CREATE_TRANSACTION_AUTHENTICATION_URL,
				getJsonToCreateTransactionAuthentication(locale, portfolioTypeId, transactionTypeId, paymentTypeId, amountGreaterThan, authenticationTypeId), "resourceId");
	}
	
	public static Object deleteTransactionAuthentication(final RequestSpecification requestSpec, final ResponseSpecification responseSpec, final Integer transactionAuthenticationId){
		System.out.println("-----------------DELETE TRANSACTION AUTHENTICATION WITH ID "+ transactionAuthenticationId+"---------------------");
		return Utils.performServerDelete(requestSpec, responseSpec, TRANSACTION_AUTHENTICATION_URL+"/"+transactionAuthenticationId+"?"+ Utils.TENANT_IDENTIFIER, "resourceId");
	}

	public static ArrayList<HashMap> getTransactionAuthenticationRules(final RequestSpecification requestSpec, final ResponseSpecification responseSpec){
		return Utils.performServerGet(requestSpec, responseSpec, TRANSACTION_AUTHENTICATION_URL + "?"+Utils.TENANT_IDENTIFIER, "");
	}
	
	public static String getJsonToCreateTransactionAuthentication(final String locale, final Integer productTypeId,
			final Integer transactionTypeId, final Integer paymentTypeId, final BigDecimal amountGreaterThan, final Long authenticationTypeId) {
		HashMap hm = new HashMap();
		hm.put("locale", locale);
		hm.put("portfolioTypeId", productTypeId);
		hm.put("transactionTypeId", transactionTypeId);
		hm.put("paymentTypeId", paymentTypeId);
		hm.put("amount", amountGreaterThan);
		hm.put("authenticationTypeId", authenticationTypeId);
		System.out.println("the json is "+new Gson().toJson(hm));
		System.out.println("------------------------CREATING Transaction Authentication TYPE-------------------------" + hm);
		
		return new Gson().toJson(hm);
	}
}
