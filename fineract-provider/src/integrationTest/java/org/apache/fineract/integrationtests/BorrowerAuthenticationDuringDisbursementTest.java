package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.PaymentTypeHelper;
import org.apache.fineract.integrationtests.common.TransactionAuthenticationHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.system.CodeHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class BorrowerAuthenticationDuringDisbursementTest {
	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private LoanTransactionHelper loanTransactionHelper;
	private static final String NONE = "1";

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
	public void TestAuthenticateClientDuringDisburesement() {
		ArrayList<HashMap> listOfTransactionAuthenticationRules = TransactionAuthenticationHelper
				.getTransactionAuthenticationRules(requestSpec, responseSpec);
		deleteAllExistingTransactionAuthenticationRules(listOfTransactionAuthenticationRules);
		System.out.println(listOfTransactionAuthenticationRules);
		String name = PaymentTypeHelper.randomNameGenerator("P_T", 5);
		String description = PaymentTypeHelper.randomNameGenerator("PT_Desc", 15);
		Boolean isCashPayment = true;
		Integer position = 1;
		System.out.println("----------------CREATE PAYMENT TYPE-----------------");
		Integer paymentTypeId = PaymentTypeHelper.createPaymentType(requestSpec, responseSpec, name, description,
				isCashPayment, position);
		Assert.assertNotNull(paymentTypeId);
		PaymentTypeHelper.verifyPaymentTypeCreatedOnServer(requestSpec, responseSpec, paymentTypeId);

		System.out.println("------------------------------------------------------------------------");
		System.out.println(
				"---------------------CREATE CLIENT--------------------------------------------------------------------");

		final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
		ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientId);
		final Integer loanProductID = createLoanProduct(false, NONE);
		System.out.println("-------------------------------Apply for loan-----------------------------");
		Integer loanId = applyForLoanApplication(clientId, loanProductID, null, null, "12,000.00");
		ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec,
				this.responseSpec, loanId);
		verifyLoanRepaymentSchedule(loanSchedule);
		System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
		HashMap loanStatusHashMap = this.loanTransactionHelper.approveLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

		System.out.println(
				"-------------------------------DISBURSE LOAN NORMAL WAY-------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.disburseLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

		System.out.println("------------------CREATE TRANSACTION AUTHENTICATION-----------------");
		String locale = "en";
		Integer portfolioTypeId = 1;
		Integer transactionTypeId = 1;
		BigDecimal amountGreaterThan = new BigDecimal(10000);
		Long authenticationTypeId = new Long(1);
		Integer transactionAuthenticationId = TransactionAuthenticationHelper.createTransactionAuthentication(
				requestSpec, responseSpec, locale, portfolioTypeId, transactionTypeId, paymentTypeId, amountGreaterThan,
				authenticationTypeId);
		Assert.assertNotNull(transactionAuthenticationId);
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
				}
			}
		}
		System.out.println("the client id is" + clientId);
		String aadhaarNumber = "594671960" + generate(3);
		Integer resourceId = (Integer) ClientHelper.addClientIdentifier(requestSpec, responseSpec, clientId,
				aadhaarCodeValueId, aadhaarNumber, "Active");
		System.out.println("the client id and resource id are " + clientId + " re " + resourceId);
		Assert.assertNotNull(resourceId);
		System.out
				.println("-----------------------------------APPLY FOR LOAN-----------------------------------------");
		loanId = null;
		loanSchedule.clear();
		loanStatusHashMap.clear();

		loanId = applyForLoanApplication(clientId, loanProductID, null, null, "12,000.00");
		loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanId);
		verifyLoanRepaymentSchedule(loanSchedule);
		System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.approveLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

		System.out.println(
				"------------------------------------------Disburse loan using secondary authentication-------------------------------");
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(503).build();
		this.loanTransactionHelper.disburseLoanOnClientAuthentication("20 September 2011", loanId, paymentTypeId,
				transactionAuthenticationId, "otp", "510266", new BigDecimal(11000), this.responseSpec);
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

		System.out.println("----------------CREATE PAYMENT TYPE-----------------");
		Integer paymentTypeId2 = PaymentTypeHelper.createPaymentType(requestSpec, responseSpec, name, description,
				isCashPayment, position);
		Assert.assertNotNull(paymentTypeId);
		PaymentTypeHelper.verifyPaymentTypeCreatedOnServer(requestSpec, responseSpec, paymentTypeId);

		System.out
				.println("-----------------------------------APPLY FOR LOAN-----------------------------------------");
		loanId = null;
		loanSchedule.clear();
		loanStatusHashMap.clear();

		loanId = applyForLoanApplication(clientId, loanProductID, null, null, "12,000.00");
		loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanId);
		verifyLoanRepaymentSchedule(loanSchedule);
		System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.approveLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

		System.out.println(
				"------------------------------------------Disburse loan using secondary authentication-------------------------------");
		this.loanTransactionHelper.disburseLoanOnClientAuthentication("20 September 2011", loanId, paymentTypeId2,
				transactionAuthenticationId, "otp", "510266", new BigDecimal(11000), this.responseSpec);

		System.out.println(
				"------------------------------------Delete the Transaction Authentication Rule----------------------------------");
		Integer deletedId = (Integer) TransactionAuthenticationHelper.deleteTransactionAuthentication(requestSpec,
				responseSpec, transactionAuthenticationId);
		Assert.assertNotNull(deletedId);

		System.out.println("-------------------------------Apply for loan-----------------------------");
		loanId = applyForLoanApplication(clientId, loanProductID, null, null, "12,000.00");
		loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanId);
		verifyLoanRepaymentSchedule(loanSchedule);
		System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.approveLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

		System.out.println(
				"-------------------------------DISBURSE LOAN NORMAL WAY-------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.disburseLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

	}

	private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID, List<HashMap> charges,
			final String savingsId, String principal) {
		System.out.println(
				"--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
		final String loanApplicationJSON = new LoanApplicationTestBuilder() //
				.withPrincipal(principal) //
				.withLoanTermFrequency("4") //
				.withLoanTermFrequencyAsMonths() //
				.withNumberOfRepayments("4") //
				.withRepaymentEveryAfter("1") //
				.withRepaymentFrequencyTypeAsMonths() //
				.withInterestRatePerPeriod("2") //
				.withAmortizationTypeAsEqualInstallments() //
				.withInterestTypeAsDecliningBalance() //
				.withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
				.withExpectedDisbursementDate("20 September 2011") //
				.withSubmittedOnDate("20 September 2011") //
				.withCharges(charges).build(clientID.toString(), loanProductID.toString(), savingsId);
		return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
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

	// select different payment type for which the authentication rule is not
	// present
	@Test
	public void loadDisbursementWithoutBorrowerAuthentication() {

		String name = PaymentTypeHelper.randomNameGenerator("P_T", 5);
		String description = PaymentTypeHelper.randomNameGenerator("PT_Desc", 15);
		Boolean isCashPayment = true;
		Integer position = 1;
		System.out.println("----------------CREATE PAYMENT TYPE-----------------");
		Integer paymentTypeId = PaymentTypeHelper.createPaymentType(requestSpec, responseSpec, name, description,
				isCashPayment, position);
		Assert.assertNotNull(paymentTypeId);
		PaymentTypeHelper.verifyPaymentTypeCreatedOnServer(requestSpec, responseSpec, paymentTypeId);

		System.out.println(
				"---------------------CREATE CLIENT--------------------------------------------------------------------");

		final Integer clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
		ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientId);
		final Integer loanProductID = createLoanProduct(false, NONE);
		System.out.println("-------------------------------Apply for loan-----------------------------");
		Integer loanId = applyForLoanApplication(clientId, loanProductID, null, null, "12,000.00");
		ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec,
				this.responseSpec, loanId);
		verifyLoanRepaymentSchedule(loanSchedule);
		System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
		HashMap loanStatusHashMap = this.loanTransactionHelper.approveLoan("20 September 2011", loanId);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

		loanStatusHashMap.clear();
		System.out.println("-------------------------------DISBURSE LOAN -------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.disburseLoan("20 September 2011", loanId, paymentTypeId, null,
				null, null, new BigDecimal(11000), this.responseSpec);
		System.out.println("the loan disbursement response " + loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
	}

	public static String generate(int length) {

		Random r = new Random();
		String number = "";
		int counter = 0;
		while (counter++ < length)
			number += r.nextInt(9);
		return number;

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

	private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule) {
		System.out.println(
				"--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

		assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2011, 10, 20)),
				loanSchedule.get(1).get("dueDate"));
		assertEquals("Checking for Principal Due for 1st Month", new Float("2911.49"),
				loanSchedule.get(1).get("principalOriginalDue"));
		assertEquals("Checking for Interest Due for 1st Month", new Float("240.00"),
				loanSchedule.get(1).get("interestOriginalDue"));

		assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2011, 11, 20)),
				loanSchedule.get(2).get("dueDate"));
		assertEquals("Checking for Principal Due for 2nd Month", new Float("2969.72"),
				loanSchedule.get(2).get("principalDue"));
		assertEquals("Checking for Interest Due for 2nd Month", new Float("181.77"),
				loanSchedule.get(2).get("interestOriginalDue"));

		assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2011, 12, 20)),
				loanSchedule.get(3).get("dueDate"));
		assertEquals("Checking for Principal Due for 3rd Month", new Float("3029.11"),
				loanSchedule.get(3).get("principalDue"));
		assertEquals("Checking for Interest Due for 3rd Month", new Float("122.38"),
				loanSchedule.get(3).get("interestOriginalDue"));

		assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2012, 1, 20)),
				loanSchedule.get(4).get("dueDate"));
		assertEquals("Checking for Principal Due for 4th Month", new Float("3089.68"),
				loanSchedule.get(4).get("principalDue"));
		assertEquals("Checking for Interest Due for 4th Month", new Float("61.79"),
				loanSchedule.get(4).get("interestOriginalDue"));
	}

}
