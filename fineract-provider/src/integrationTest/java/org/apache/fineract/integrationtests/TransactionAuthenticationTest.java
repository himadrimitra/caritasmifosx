package org.apache.fineract.integrationtests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.PaymentTypeHelper;
import org.apache.fineract.integrationtests.common.TransactionAuthenticationHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.system.CodeHelper;
import org.junit.After;
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
	private static final String NONE = "1";
	private LoanTransactionHelper loanTransactionHelper;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
	}
	
	
	@Test
	public void createAndDeleteTransactionAuthentication(){
		String name = PaymentTypeHelper.randomNameGenerator("P_T", 5);
		String description = PaymentTypeHelper.randomNameGenerator("PT_Desc", 15);
		Boolean isCashPayment = true;
		Integer position = 1;
		final Integer loanProductID = createLoanProduct(false, NONE);
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
		Integer aadhaarCodeValueId = generateIdentificationTypeId();
		Integer transactionAuthenticationId = TransactionAuthenticationHelper.createTransactionAuthentication(
				requestSpec, responseSpec, locale, productTypeId, transactionTypeId, paymentTypeId, amountGreaterThan,
				authenticationTypeId, loanProductID,aadhaarCodeValueId);
		Assert.assertNotNull(transactionAuthenticationId);
		
		Integer deleted = (Integer) TransactionAuthenticationHelper.deleteTransactionAuthentication(requestSpec, responseSpec, transactionAuthenticationId);
		Assert.assertNotNull(deleted);
	}
	
	
	private Integer createLoanProduct(final boolean multiDisburseLoan, final String accountingRule,
			final Account... accounts) {
		System.out.println(
				"------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
		LoanProductTestBuilder builder = new LoanProductTestBuilder() //
				.withPrincipal("12,000.00") //
				.withNumberOfRepayments("4") //
				.withRepaymentAfterEvery("1") //
				.withRepaymentTypeAsMonth() //
				.withinterestRatePerPeriod("1") //
				.withInterestRateFrequencyTypeAsMonths() //
				.withAmortizationTypeAsEqualInstallments() //
				.withInterestTypeAsDecliningBalance() //
				.withTranches(multiDisburseLoan) //
				.withAccounting(accountingRule, accounts);

		if (multiDisburseLoan) {
			builder = builder.withInterestCalculationPeriodTypeAsRepaymentPeriod(true);
		}
		final String loanProductJSON = builder.build(null);
		return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
	}
	private Integer generateIdentificationTypeId(){
		System.out.println("--------------------------------CREATE CODE VALUE-------------------");
		String aadhaar = "Aadhaar";
		HashMap code = CodeHelper.getCodeByName(requestSpec, responseSpec, "Customer Identifier");
		System.out.println("the code is " + code);
		Integer codeId = (Integer) code.get("id");
		HashMap<String, Object> codeValue = CodeHelper.retrieveOrCreateCodeValue(codeId, requestSpec, responseSpec);
		System.out.println("the code value is " + codeValue);
		boolean hasAadhaarCodeValue = false;
		Integer aadhaarCodeValueId = null;
		for (Map.Entry<String, Object> entry : codeValue.entrySet()) {
			if (entry.getKey().equals("name")) {
				if (entry.getValue().equals(aadhaar)) {
					hasAadhaarCodeValue = true;
				}
			}
		}

		if (!hasAadhaarCodeValue) {
			aadhaarCodeValueId = (Integer) CodeHelper.createCodeValue(requestSpec, responseSpec, codeId, aadhaar, 0,
					"subResourceId");
		} else {
			List<HashMap<String, Object>> codeValues = CodeHelper.getCodeValuesForCode(this.requestSpec,
					this.responseSpec, codeId, "");
			System.out.println("the code values are " + codeValues);
			for (HashMap<String, Object> value : codeValues) {
				if (value.get("name").equals(aadhaar)) {
					aadhaarCodeValueId = (Integer) value.get("id");
					if(!(Boolean)value.get("isActive")){
						activateTheCodeValue(codeId, aadhaarCodeValueId, value);
					}
				}
			}
		}
		return aadhaarCodeValueId;
	}
	
	private void activateTheCodeValue(final Integer codeId, final Integer aadhaarCodeValueId, 
			final HashMap<String, Object> codeValue){
		codeValue.put("isActive", true);
		codeValue.remove("mandatory");
		System.out.println("the code value is "+codeValue);
		Integer resourceId = (Integer) CodeHelper.updateCodeValue(requestSpec, responseSpec, codeId, aadhaarCodeValueId, codeValue, "resourceId");
		System.out.println("the resource Id is "+resourceId);
	}
	
	@After
	public void deleteAllTransactionAuthRules(){
		ArrayList<HashMap> listOfTransactionAuthenticationRules = TransactionAuthenticationHelper
				.getTransactionAuthenticationRules(requestSpec, responseSpec);
		deleteAllExistingTransactionAuthenticationRules(listOfTransactionAuthenticationRules);
	}
	
	public void deleteAllExistingTransactionAuthenticationRules(final ArrayList<HashMap> listOfExistingRules) {
		System.out.println("the transacton ruels are " + listOfExistingRules);
		for (int i = 0; i < listOfExistingRules.size(); i++) {
			System.out.println("the rules is " + listOfExistingRules.get(i));
			HashMap<String, Object> rules = listOfExistingRules.get(i);

			Integer ruleId = (Integer) rules.get("authenticationRuleId");
			System.out.println("the rule id is " + listOfExistingRules.get(i).get("id"));
			Integer deletedId = (Integer) TransactionAuthenticationHelper.deleteTransactionAuthentication(requestSpec,
					responseSpec, ruleId);
			Assert.assertNotNull(deletedId);
		}
	}
}
