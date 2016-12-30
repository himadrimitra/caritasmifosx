package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsProductHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class WalletTransactionsIntegrationTest {
	public static final String DEPOSIT_AMOUNT = "2000";
	public static final String WITHDRAW_AMOUNT = "1000";
	public static final String MINIMUM_OPENING_BALANCE = null;
	public static final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";

	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private SavingsAccountHelper savingsAccountHelper;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testSavingsAccount() {
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);

		String activationDate = "01 March 2015";
		String officeId = "1";
		String mobileno = Utils.randomNameGenerator("91", 8);
		final String minBalanceForInterestCalculation = null;
		final String minRequiredBalance = null;
		final String enforceMinRequiredBalance = "false";
		final boolean allowOverdraft = false;
		final Integer savingsProductId = createSavingsProduct(this.requestSpec, this.responseSpec,
				MINIMUM_OPENING_BALANCE, minBalanceForInterestCalculation, minRequiredBalance,
				enforceMinRequiredBalance, allowOverdraft);
		Assert.assertNotNull(savingsProductId);

		final Integer clientID = ClientHelper.createClientWithMobileAndSavings(requestSpec, responseSpec,
				activationDate, officeId, mobileno, savingsProductId);
		Assert.assertNotNull(clientID);

		this.savingsAccountHelper.depositToWalletAccount(mobileno, DEPOSIT_AMOUNT, "01 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);

		ArrayList depositTransactions = this.savingsAccountHelper.getWalletTransactionsByLimit(mobileno, "1");
		HashMap depositTransaction = (HashMap) depositTransactions.get(0);
		assertEquals("Verifying Deposit Amount", new Float(DEPOSIT_AMOUNT), depositTransaction.get("amount"));

		this.savingsAccountHelper.withdrawalFromWalletAccount(mobileno, WITHDRAW_AMOUNT, "01 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		ArrayList withdrawTransactions = this.savingsAccountHelper.getWalletTransactionsByLimit(mobileno, "1");
		HashMap withdrawTransaction = (HashMap) withdrawTransactions.get(0);
		assertEquals("Verifying Withdrawal Amount", new Float(WITHDRAW_AMOUNT), withdrawTransaction.get("amount"));

		this.savingsAccountHelper.depositToWalletAccount(mobileno, DEPOSIT_AMOUNT, "05 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		this.savingsAccountHelper.depositToWalletAccount(mobileno, DEPOSIT_AMOUNT, "08 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		this.savingsAccountHelper.depositToWalletAccount(mobileno, DEPOSIT_AMOUNT, "10 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		this.savingsAccountHelper.withdrawalFromWalletAccount(mobileno, DEPOSIT_AMOUNT, "14 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		this.savingsAccountHelper.depositToWalletAccount(mobileno, DEPOSIT_AMOUNT, "17 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		this.savingsAccountHelper.withdrawalFromWalletAccount(mobileno, DEPOSIT_AMOUNT, "19 March 2015",
				CommonConstants.RESPONSE_RESOURCE_ID);
		ArrayList transactions = this.savingsAccountHelper.getWalletTransactionsByDateRange(mobileno, "01 March 2015",
				"15 March 2015");
		assertEquals(6, transactions.size());
		transactions = this.savingsAccountHelper.getWalletTransactionsByDateRange(mobileno, "15 March 2015",
				"25 March 2015");
		assertEquals(2, transactions.size());
	}

	private Integer createSavingsProduct(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final String minOpenningBalance,
			String minBalanceForInterestCalculation, String minRequiredBalance, String enforceMinRequiredBalance,
			final boolean allowOverdraft) {
		final String taxGroupId = null;
		return createSavingsProduct(requestSpec, responseSpec, minOpenningBalance, minBalanceForInterestCalculation,
				minRequiredBalance, enforceMinRequiredBalance, allowOverdraft, taxGroupId, false);
	}

	private Integer createSavingsProduct(final RequestSpecification requestSpec,
			final ResponseSpecification responseSpec, final String minOpenningBalance,
			String minBalanceForInterestCalculation, String minRequiredBalance, String enforceMinRequiredBalance,
			final boolean allowOverdraft, final String taxGroupId, boolean withDormancy) {
		System.out.println(
				"------------------------------CREATING NEW SAVINGS PRODUCT ---------------------------------------");
		SavingsProductHelper savingsProductHelper = new SavingsProductHelper();
		if (allowOverdraft) {
			final String overDraftLimit = "2000.0";
			savingsProductHelper = savingsProductHelper.withOverDraft(overDraftLimit);
		}
		if (withDormancy) {
			savingsProductHelper = savingsProductHelper.withDormancy();
		}

		final String savingsProductJSON = savingsProductHelper
				//
				.withInterestCompoundingPeriodTypeAsDaily()
				//
				.withInterestPostingPeriodTypeAsMonthly()
				//
				.withInterestCalculationPeriodTypeAsDailyBalance()
				//
				.withMinBalanceForInterestCalculation(minBalanceForInterestCalculation)
				//
				.withMinRequiredBalance(minRequiredBalance).withEnforceMinRequiredBalance(enforceMinRequiredBalance)
				.withMinimumOpenningBalance(minOpenningBalance).withWithHoldTax(taxGroupId).build();
		return SavingsProductHelper.createSavingsProduct(savingsProductJSON, requestSpec, responseSpec);
	}

}
