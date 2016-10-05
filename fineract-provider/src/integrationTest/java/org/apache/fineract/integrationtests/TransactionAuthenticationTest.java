package org.apache.fineract.integrationtests;

import java.math.BigDecimal;

import org.apache.fineract.integrationtests.common.PaymentTypeHelper;
import org.apache.fineract.integrationtests.common.TransactionAuthenticationHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class TransactionAuthenticationTest {
	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
	}
	
	
	@Test
	public void createAndDeleteTransactionAuthentication(){
		String name = PaymentTypeHelper.randomNameGenerator("P_T", 5);
		String description = PaymentTypeHelper.randomNameGenerator("PT_Desc", 15);
		Boolean isCashPayment = true;
		Integer position = 1;
		System.out.println("----------------CREATE PAYMENT TYPE-----------------");
		Integer paymentTypeId = PaymentTypeHelper.createPaymentType(requestSpec, responseSpec, name, description,
				isCashPayment, position);
		Assert.assertNotNull(paymentTypeId);
		PaymentTypeHelper.verifyPaymentTypeCreatedOnServer(requestSpec, responseSpec, paymentTypeId);

		System.out.println("------------------CREATE TRANSACTION AUTHENTICATION-----------------");
		String locale = "en";
		Integer productTypeId = 1;
		Integer transactionTypeId = 1;
		BigDecimal amountGreaterThan = new BigDecimal(12000);
		Long authenticationTypeId = new Long(1);
		Integer transactionAuthenticationId = TransactionAuthenticationHelper.createTransactionAuthentication(
				requestSpec, responseSpec, locale, productTypeId, transactionTypeId, paymentTypeId, amountGreaterThan,
				authenticationTypeId);
		Assert.assertNotNull(transactionAuthenticationId);
		
		Integer deleted = (Integer) TransactionAuthenticationHelper.deleteTransactionAuthentication(requestSpec, responseSpec, transactionAuthenticationId);
		Assert.assertNotNull(deleted);
	}
	
}
