package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.fineract.integrationtests.common.CalendarHelper;
import org.apache.fineract.integrationtests.common.CenterHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GlobalConfigurationHelper;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.apache.fineract.integrationtests.common.recurringdeposit.RecurringDepositAccountHelper;
import org.apache.fineract.integrationtests.common.recurringdeposit.RecurringDepositProductHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsAccountHelper;
import org.apache.fineract.integrationtests.common.savings.SavingsProductHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class CollectionSheetIntegrationTest {

	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private LoanTransactionHelper loanTransactionHelper;
	private ResponseSpecification responseSpec403;
	private GlobalConfigurationHelper globalConfigurationHelper;
	public static final String ACCOUNT_TYPE_INDIVIDUAL = "INDIVIDUAL";
	private SavingsAccountHelper savingsAccountHelper;
	private static final String NONE = "1";
	private static final String CASH_BASED = "2";
	private RecurringDepositAccountHelper recurringDepositAccountHelper;
	private CollectionSheetHelper collectionSheetHelper;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
		this.responseSpec403 = new ResponseSpecBuilder().expectStatusCode(403).build();
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void collectionSheetWithSavingsEnabled() {
		this.collectionSheetHelper = new CollectionSheetHelper(this.requestSpec, this.responseSpec);
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
		setConfiguration("savings_account_included_in_collection_sheet", true);
		Integer centerId = CenterHelper.createCenter(requestSpec, responseSpec, "29 December 2014");
		CenterHelper.verifyCenterCreatedOnServer(requestSpec, responseSpec, centerId);
		// CenterHelper.activateCenter(requestSpec, responseSpec,
		// centerId.toString(),"29 December 2014");
		CenterHelper.verifyCenterActivatedOnServer(requestSpec, responseSpec, centerId, true);
		Integer calendarId = CalendarHelper.createMeetingCalendarForCenter(requestSpec, responseSpec, centerId,
				"29 December 2014", "3", "1", "29");
		int[] groupMembers = new int[1];
		Integer groupId = GroupHelper.createGroup(requestSpec, responseSpec, "29 December 2014");
		groupMembers[0] = groupId;
		CenterHelper.associateGroups(centerId, groupMembers, requestSpec, responseSpec);
		Integer clientId = ClientHelper.createClient(requestSpec, responseSpec);
		ClientHelper.verifyClientCreatedOnServer(requestSpec, responseSpec, clientId);
		GroupHelper.associateClient(requestSpec, responseSpec, groupId.toString(), clientId.toString());
		Integer loanProductId = createLoanProduct(false, NONE);
		Integer loanId = applyForClientJlgLoanApplication(clientId, loanProductId, "10000", calendarId.toString());
		this.loanTransactionHelper.approveLoan("29 December 2014", loanId);
		this.loanTransactionHelper.disburseLoan("29 December 2014", loanId);
		Integer savingsProductID = createSavingsProduct(requestSpec, responseSpec, null, null, null, null, false, null,
				false);
		Integer savingsAccountId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId,
				savingsProductID, ACCOUNT_TYPE_INDIVIDUAL, "29 December 2014");
		this.savingsAccountHelper.approveSavingsOnDate(savingsAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(savingsAccountId, "29 December 2014");
		Integer recurringDepositProductId = createRecurringDepositProduct("29 December 2014", "29 December 2020", NONE);
		Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientId.toString(),
				recurringDepositProductId.toString(), "29 December 2014", NONE, "29 January 2015");
		this.savingsAccountHelper.approveSavingsOnDate(recurringDepositAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(recurringDepositAccountId, "29 December 2014");
		ArrayList<HashMap<String, Object>> groups = this.collectionSheetHelper
				.getCenterCollectionSheet(this.requestSpec, this.responseSpec, calendarId, "29 January 2015", centerId);
		for (HashMap<String, Object> group : groups) {
			ArrayList<HashMap<String, Object>> clients = (ArrayList<HashMap<String, Object>>) group.get("clients");
			for (HashMap<String, Object> client : clients) {
				ArrayList<HashMap<String, Object>> savings = (ArrayList<HashMap<String, Object>>) client.get("savings");
				for (HashMap<String, Object> saving : savings) {
					Assert.assertTrue(saving.get("savingsId").equals(savingsAccountId)
							|| saving.get("savingsId").equals(recurringDepositAccountId));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void collectionSheetWithSavingsDisabled() {
		this.collectionSheetHelper = new CollectionSheetHelper(this.requestSpec, this.responseSpec);
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
		setConfiguration("savings_account_included_in_collection_sheet", false);
		Integer centerId = CenterHelper.createCenter(requestSpec, responseSpec, "29 December 2014");
		CenterHelper.verifyCenterCreatedOnServer(requestSpec, responseSpec, centerId);
		// CenterHelper.activateCenter(requestSpec, responseSpec,
		// centerId.toString(),"29 December 2014");
		CenterHelper.verifyCenterActivatedOnServer(requestSpec, responseSpec, centerId, true);
		Integer calendarId = CalendarHelper.createMeetingCalendarForCenter(requestSpec, responseSpec, centerId,
				"29 December 2014", "3", "1", "29");
		int[] groupMembers = new int[1];
		Integer groupId = GroupHelper.createGroup(requestSpec, responseSpec, "29 December 2014");
		groupMembers[0] = groupId;
		CenterHelper.associateGroups(centerId, groupMembers, requestSpec, responseSpec);
		Integer clientId = ClientHelper.createClient(requestSpec, responseSpec);
		ClientHelper.verifyClientCreatedOnServer(requestSpec, responseSpec, clientId);
		GroupHelper.associateClient(requestSpec, responseSpec, groupId.toString(), clientId.toString());
		Integer loanProductId = createLoanProduct(false, NONE);
		Integer loanId = applyForClientJlgLoanApplication(clientId, loanProductId, "10000", calendarId.toString());
		this.loanTransactionHelper.approveLoan("29 December 2014", loanId);
		this.loanTransactionHelper.disburseLoan("29 December 2014", loanId);
		Integer savingsProductID = createSavingsProduct(requestSpec, responseSpec, null, null, null, null, false, null,
				false);
		Integer savingsAccountId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId,
				savingsProductID, ACCOUNT_TYPE_INDIVIDUAL, "29 December 2014");
		this.savingsAccountHelper.approveSavingsOnDate(savingsAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(savingsAccountId, "29 December 2014");
		Integer recurringDepositProductId = createRecurringDepositProduct("29 December 2014", "29 December 2020", NONE);
		Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientId.toString(),
				recurringDepositProductId.toString(), "29 December 2014", NONE, "29 January 2015");
		this.savingsAccountHelper.approveSavingsOnDate(recurringDepositAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(recurringDepositAccountId, "29 December 2014");
		ArrayList<HashMap<String, Object>> groups = this.collectionSheetHelper
				.getCenterCollectionSheet(this.requestSpec, this.responseSpec, calendarId, "29 January 2015", centerId);
		for (HashMap<String, Object> group : groups) {
			ArrayList<HashMap<String, Object>> clients = (ArrayList<HashMap<String, Object>>) group.get("clients");
			for (HashMap<String, Object> client : clients) {
				ArrayList<HashMap<String, Object>> savings = (ArrayList<HashMap<String, Object>>) client.get("savings");
				for (HashMap<String, Object> saving : savings) {
					Assert.assertTrue(saving.get("savingsId").equals(recurringDepositAccountId));
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void collectionSheetWithSavingsEnabledSumbit() {
		this.collectionSheetHelper = new CollectionSheetHelper(this.requestSpec, this.responseSpec);
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
		setConfiguration("savings_account_included_in_collection_sheet", true);
		Integer centerId = CenterHelper.createCenter(requestSpec, responseSpec, "29 December 2014");
		CenterHelper.verifyCenterCreatedOnServer(requestSpec, responseSpec, centerId);
		// CenterHelper.activateCenter(requestSpec, responseSpec,
		// centerId.toString(),"29 December 2014");
		CenterHelper.verifyCenterActivatedOnServer(requestSpec, responseSpec, centerId, true);
		Integer calendarId = CalendarHelper.createMeetingCalendarForCenter(requestSpec, responseSpec, centerId,
				"29 December 2014", "3", "1", "29");
		int[] groupMembers = new int[1];
		Integer groupId = GroupHelper.createGroup(requestSpec, responseSpec, "29 December 2014");
		groupMembers[0] = groupId;
		CenterHelper.associateGroups(centerId, groupMembers, requestSpec, responseSpec);
		Integer clientId = ClientHelper.createClient(requestSpec, responseSpec);
		ClientHelper.verifyClientCreatedOnServer(requestSpec, responseSpec, clientId);
		GroupHelper.associateClient(requestSpec, responseSpec, groupId.toString(), clientId.toString());
		Integer loanProductId = createLoanProduct(false, NONE);
		Integer loanId = applyForClientJlgLoanApplication(clientId, loanProductId, "10000", calendarId.toString());
		this.loanTransactionHelper.approveLoan("29 December 2014", loanId);
		this.loanTransactionHelper.disburseLoan("29 December 2014", loanId);
		Integer savingsProductID = createSavingsProduct(requestSpec, responseSpec, null, null, null, null, false, null,
				false);
		Integer savingsAccountId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId,
				savingsProductID, ACCOUNT_TYPE_INDIVIDUAL, "29 December 2014");
		this.savingsAccountHelper.approveSavingsOnDate(savingsAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(savingsAccountId, "29 December 2014");
		Integer recurringDepositProductId = createRecurringDepositProduct("29 December 2014", "29 December 2020", NONE);
		Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientId.toString(),
				recurringDepositProductId.toString(), "29 December 2014", NONE, "29 January 2015");
		this.savingsAccountHelper.approveSavingsOnDate(recurringDepositAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(recurringDepositAccountId, "29 December 2014");
		ArrayList<HashMap<String, Object>> groups = this.collectionSheetHelper.getCenterCollectionSheet(this.requestSpec,
				this.responseSpec, calendarId, "29 January 2015", centerId);
		for(HashMap<String, Object> group : groups){
			ArrayList<HashMap<String, Object>> clients = (ArrayList<HashMap<String, Object>>) group.get("clients");
			for(HashMap<String, Object> client : clients){
				ArrayList<HashMap<String, Object>> savings = (ArrayList<HashMap<String, Object>>) client.get("savings");
				for(HashMap<String, Object> saving : savings){
					Assert.assertTrue(saving.get("savingsId").equals(savingsAccountId) || saving.get("savingsId").equals(recurringDepositAccountId));
				}
			}
		}
		HashMap<String, Object> json = new HashMap<String,Object>();
		json.put("dateFormat",  "dd MMMM yyyy");
		json.put("locale", "en");
		json.put( "calendarId", calendarId);
		json.put( "transactionDate", "29 January 2015");
		ArrayList<HashMap<String, Object>> bulkRepaymentTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> loanTransactions = new HashMap<String,Object>();
		loanTransactions.put("loanId", loanId);
		loanTransactions.put("transactionAmount",2626.24);
		bulkRepaymentTransactions.add(loanTransactions);
		json.put("bulkRepaymentTransactions", bulkRepaymentTransactions);
		ArrayList<HashMap<String, Object>> bulkSavingsTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> savingTransaction = new HashMap<String, Object>();
		savingTransaction.put("savingsId", savingsAccountId);
		savingTransaction.put("transactionAmount", 100);
		bulkSavingsTransactions.add(savingTransaction);
		HashMap<String, Object> rdTransaction = new HashMap<String, Object>();
		rdTransaction.put("savingsId", recurringDepositAccountId);
		rdTransaction.put("transactionAmount", 2000);
		bulkSavingsTransactions.add(rdTransaction);
		json.put("bulkSavingsTransactions", bulkSavingsTransactions);
		this.collectionSheetHelper.saveCollectionSheet(requestSpec, responseSpec, new Gson().toJson(json), centerId);
		final HashMap<String, Object> loanSummary = (HashMap<String, Object>) this.loanTransactionHelper.getLoanDetail(requestSpec, responseSpec, loanId, "summary");
		Assert.assertEquals(new Float(2626.24), loanSummary.get("totalRepayment"));
		final HashMap<String, Object> rdSummary = (HashMap<String, Object>) this.recurringDepositAccountHelper.getRecurringDepositAccountById(requestSpec, responseSpec, recurringDepositAccountId).get( "summary");
		Assert.assertEquals(new Float(2000), rdSummary.get("totalDeposits"));
		final HashMap<String, Object> savingsSummary = (HashMap<String, Object>) this.savingsAccountHelper.getSavingsAccountDetail(savingsAccountId, "summary");
		Assert.assertEquals(new Float(100), savingsSummary.get("totalDeposits"));
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void collectionSheetWithSavingsDisabledSumbit() {
		this.collectionSheetHelper = new CollectionSheetHelper(this.requestSpec, this.responseSpec);
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
		setConfiguration("savings_account_included_in_collection_sheet", false);
		Integer centerId = CenterHelper.createCenter(requestSpec, responseSpec, "29 December 2014");
		CenterHelper.verifyCenterCreatedOnServer(requestSpec, responseSpec, centerId);
		// CenterHelper.activateCenter(requestSpec, responseSpec,
		// centerId.toString(),"29 December 2014");
		CenterHelper.verifyCenterActivatedOnServer(requestSpec, responseSpec, centerId, true);
		Integer calendarId = CalendarHelper.createMeetingCalendarForCenter(requestSpec, responseSpec, centerId,
				"29 December 2014", "3", "1", "29");
		int[] groupMembers = new int[1];
		Integer groupId = GroupHelper.createGroup(requestSpec, responseSpec, "29 December 2014");
		groupMembers[0] = groupId;
		CenterHelper.associateGroups(centerId, groupMembers, requestSpec, responseSpec);
		Integer clientId = ClientHelper.createClient(requestSpec, responseSpec);
		ClientHelper.verifyClientCreatedOnServer(requestSpec, responseSpec, clientId);
		GroupHelper.associateClient(requestSpec, responseSpec, groupId.toString(), clientId.toString());
		Integer loanProductId = createLoanProduct(false, NONE);
		Integer loanId = applyForClientJlgLoanApplication(clientId, loanProductId, "10000", calendarId.toString());
		this.loanTransactionHelper.approveLoan("29 December 2014", loanId);
		this.loanTransactionHelper.disburseLoan("29 December 2014", loanId);
		Integer savingsProductID = createSavingsProduct(requestSpec, responseSpec, null, null, null, null, false, null,
				false);
		Integer savingsAccountId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId,
				savingsProductID, ACCOUNT_TYPE_INDIVIDUAL, "29 December 2014");
		this.savingsAccountHelper.approveSavingsOnDate(savingsAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(savingsAccountId, "29 December 2014");
		Integer recurringDepositProductId = createRecurringDepositProduct("29 December 2014", "29 December 2020", NONE);
		Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientId.toString(),
				recurringDepositProductId.toString(), "29 December 2014", NONE, "29 January 2015");
		this.savingsAccountHelper.approveSavingsOnDate(recurringDepositAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(recurringDepositAccountId, "29 December 2014");
		ArrayList<HashMap<String, Object>> groups = this.collectionSheetHelper.getCenterCollectionSheet(this.requestSpec,
				this.responseSpec, calendarId, "29 January 2015", centerId);
		for(HashMap<String, Object> group : groups){
			ArrayList<HashMap<String, Object>> clients = (ArrayList<HashMap<String, Object>>) group.get("clients");
			for(HashMap<String, Object> client : clients){
				ArrayList<HashMap<String, Object>> savings = (ArrayList<HashMap<String, Object>>) client.get("savings");
				for(HashMap<String, Object> saving : savings){
					Assert.assertTrue(saving.get("savingsId").equals(recurringDepositAccountId));
				}
			}
		}
		HashMap<String, Object> json = new HashMap<String,Object>();
		json.put("dateFormat",  "dd MMMM yyyy");
		json.put("locale", "en");
		json.put( "calendarId", calendarId);
		json.put( "transactionDate", "29 January 2015");
		ArrayList<HashMap<String, Object>> bulkRepaymentTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> loanTransactions = new HashMap<String,Object>();
		loanTransactions.put("loanId", loanId);
		loanTransactions.put("transactionAmount",2626.24);
		bulkRepaymentTransactions.add(loanTransactions);
		json.put("bulkRepaymentTransactions", bulkRepaymentTransactions);
		ArrayList<HashMap<String, Object>> bulkSavingsTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> savingTransaction = new HashMap<String, Object>();
		savingTransaction.put("savingsId", savingsAccountId);
		savingTransaction.put("transactionAmount", 100);
		bulkSavingsTransactions.add(savingTransaction);
		HashMap<String, Object> rdTransaction = new HashMap<String, Object>();
		rdTransaction.put("savingsId", recurringDepositAccountId);
		rdTransaction.put("transactionAmount", 2000);
		bulkSavingsTransactions.add(rdTransaction);
		json.put("bulkSavingsTransactions", bulkSavingsTransactions);
		ArrayList<HashMap<String,Object>> error = this.collectionSheetHelper.saveCollectionSheet(requestSpec, responseSpec403, new Gson().toJson(json), centerId,CommonConstants.RESPONSE_ERROR);
		assertEquals("error.msg.deposit.for.account." + savingsAccountId + ".not.allowed.due.to.configiration",error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
		final HashMap<String, Object> loanSummary = (HashMap<String, Object>) this.loanTransactionHelper.getLoanDetail(requestSpec, responseSpec, loanId, "summary");
		Assert.assertEquals(new Float(0), loanSummary.get("totalRepayment"));
		final HashMap<String, Object> rdSummary = (HashMap<String, Object>) this.recurringDepositAccountHelper.getRecurringDepositAccountById(requestSpec, responseSpec, recurringDepositAccountId).get( "summary");
		Assert.assertEquals(null, rdSummary.get("totalDeposits"));
		final HashMap<String, Object> savingsSummary = (HashMap<String, Object>) this.savingsAccountHelper.getSavingsAccountDetail(savingsAccountId, "summary");
		Assert.assertEquals(null, savingsSummary.get("totalDeposits"));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void collectionSheetWithSavingsEnabledWithdrawSumbit() {
		this.collectionSheetHelper = new CollectionSheetHelper(this.requestSpec, this.responseSpec);
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
		setConfiguration("savings_account_included_in_collection_sheet", true);
		setConfiguration("savings_withdraw_included_in_collection_sheet", true);
		Integer centerId = CenterHelper.createCenter(requestSpec, responseSpec, "29 December 2014");
		CenterHelper.verifyCenterCreatedOnServer(requestSpec, responseSpec, centerId);
		// CenterHelper.activateCenter(requestSpec, responseSpec,
		// centerId.toString(),"29 December 2014");
		CenterHelper.verifyCenterActivatedOnServer(requestSpec, responseSpec, centerId, true);
		Integer calendarId = CalendarHelper.createMeetingCalendarForCenter(requestSpec, responseSpec, centerId,
				"29 December 2014", "3", "1", "29");
		int[] groupMembers = new int[1];
		Integer groupId = GroupHelper.createGroup(requestSpec, responseSpec, "29 December 2014");
		groupMembers[0] = groupId;
		CenterHelper.associateGroups(centerId, groupMembers, requestSpec, responseSpec);
		Integer clientId = ClientHelper.createClient(requestSpec, responseSpec);
		ClientHelper.verifyClientCreatedOnServer(requestSpec, responseSpec, clientId);
		GroupHelper.associateClient(requestSpec, responseSpec, groupId.toString(), clientId.toString());
		Integer loanProductId = createLoanProduct(false, NONE);
		Integer loanId = applyForClientJlgLoanApplication(clientId, loanProductId, "10000", calendarId.toString());
		this.loanTransactionHelper.approveLoan("29 December 2014", loanId);
		this.loanTransactionHelper.disburseLoan("29 December 2014", loanId);
		Integer savingsProductID = createSavingsProduct(requestSpec, responseSpec, null, null, null, null, false, null,
				false);
		Integer savingsAccountId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId,
				savingsProductID, ACCOUNT_TYPE_INDIVIDUAL, "29 December 2014");
		this.savingsAccountHelper.approveSavingsOnDate(savingsAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(savingsAccountId, "29 December 2014");
		Integer recurringDepositProductId = createRecurringDepositProduct("29 December 2014", "29 December 2020", NONE);
		Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientId.toString(),
				recurringDepositProductId.toString(), "29 December 2014", NONE, "29 January 2015");
		this.savingsAccountHelper.approveSavingsOnDate(recurringDepositAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(recurringDepositAccountId, "29 December 2014");
		ArrayList<HashMap<String, Object>> groups = this.collectionSheetHelper.getCenterCollectionSheet(this.requestSpec,
				this.responseSpec, calendarId, "29 January 2015", centerId);
		for(HashMap<String, Object> group : groups){
			ArrayList<HashMap<String, Object>> clients = (ArrayList<HashMap<String, Object>>) group.get("clients");
			for(HashMap<String, Object> client : clients){
				ArrayList<HashMap<String, Object>> savings = (ArrayList<HashMap<String, Object>>) client.get("savings");
				for(HashMap<String, Object> saving : savings){
					Assert.assertTrue(saving.get("savingsId").equals(savingsAccountId) || saving.get("savingsId").equals(recurringDepositAccountId));
				}
			}
		}
		HashMap<String, Object> json = new HashMap<String,Object>();
		json.put("dateFormat",  "dd MMMM yyyy");
		json.put("locale", "en");
		json.put( "calendarId", calendarId);
		json.put( "transactionDate", "29 January 2015");
		ArrayList<HashMap<String, Object>> bulkRepaymentTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> loanTransactions = new HashMap<String,Object>();
		loanTransactions.put("loanId", loanId);
		loanTransactions.put("transactionAmount",2626.24);
		bulkRepaymentTransactions.add(loanTransactions);
		json.put("bulkRepaymentTransactions", bulkRepaymentTransactions);
		ArrayList<HashMap<String, Object>> bulkSavingsTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> savingTransaction = new HashMap<String, Object>();
		savingTransaction.put("savingsId", savingsAccountId);
		savingTransaction.put("transactionAmount", 100);
		savingTransaction.put("withdrawAmount", 10);
		bulkSavingsTransactions.add(savingTransaction);
		HashMap<String, Object> rdTransaction = new HashMap<String, Object>();
		rdTransaction.put("savingsId", recurringDepositAccountId);
		rdTransaction.put("transactionAmount", 2000);
		rdTransaction.put("withdrawAmount", 1000);
		bulkSavingsTransactions.add(rdTransaction);
		json.put("bulkSavingsTransactions", bulkSavingsTransactions);
		this.collectionSheetHelper.saveCollectionSheet(requestSpec, responseSpec, new Gson().toJson(json), centerId);
		final HashMap<String, Object> loanSummary = (HashMap<String, Object>) this.loanTransactionHelper.getLoanDetail(requestSpec, responseSpec, loanId, "summary");
		Assert.assertEquals(new Float(2626.24), loanSummary.get("totalRepayment"));
		final HashMap<String, Object> rdSummary = (HashMap<String, Object>) this.recurringDepositAccountHelper.getRecurringDepositAccountById(requestSpec, responseSpec, recurringDepositAccountId).get( "summary");
		Assert.assertEquals(new Float(1000), rdSummary.get("accountBalance"));
		final HashMap<String, Object> savingsSummary = (HashMap<String, Object>) this.savingsAccountHelper.getSavingsAccountDetail(savingsAccountId, "summary");
		Assert.assertEquals(new Float(90), savingsSummary.get("accountBalance"));
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void collectionSheetWithSavingsDisabledWithdrawSumbit() {
		this.collectionSheetHelper = new CollectionSheetHelper(this.requestSpec, this.responseSpec);
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.savingsAccountHelper = new SavingsAccountHelper(this.requestSpec, this.responseSpec);
		setConfiguration("savings_account_included_in_collection_sheet", true);
		setConfiguration("savings_withdraw_included_in_collection_sheet", false);
		Integer centerId = CenterHelper.createCenter(requestSpec, responseSpec, "29 December 2014");
		CenterHelper.verifyCenterCreatedOnServer(requestSpec, responseSpec, centerId);
		// CenterHelper.activateCenter(requestSpec, responseSpec,
		// centerId.toString(),"29 December 2014");
		CenterHelper.verifyCenterActivatedOnServer(requestSpec, responseSpec, centerId, true);
		Integer calendarId = CalendarHelper.createMeetingCalendarForCenter(requestSpec, responseSpec, centerId,
				"29 December 2014", "3", "1", "29");
		int[] groupMembers = new int[1];
		Integer groupId = GroupHelper.createGroup(requestSpec, responseSpec, "29 December 2014");
		groupMembers[0] = groupId;
		CenterHelper.associateGroups(centerId, groupMembers, requestSpec, responseSpec);
		Integer clientId = ClientHelper.createClient(requestSpec, responseSpec);
		ClientHelper.verifyClientCreatedOnServer(requestSpec, responseSpec, clientId);
		GroupHelper.associateClient(requestSpec, responseSpec, groupId.toString(), clientId.toString());
		Integer loanProductId = createLoanProduct(false, NONE);
		Integer loanId = applyForClientJlgLoanApplication(clientId, loanProductId, "10000", calendarId.toString());
		this.loanTransactionHelper.approveLoan("29 December 2014", loanId);
		this.loanTransactionHelper.disburseLoan("29 December 2014", loanId);
		Integer savingsProductID = createSavingsProduct(requestSpec, responseSpec, null, null, null, null, false, null,
				false);
		Integer savingsAccountId = this.savingsAccountHelper.applyForSavingsApplicationOnDate(clientId,
				savingsProductID, ACCOUNT_TYPE_INDIVIDUAL, "29 December 2014");
		this.savingsAccountHelper.approveSavingsOnDate(savingsAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(savingsAccountId, "29 December 2014");
		Integer recurringDepositProductId = createRecurringDepositProduct("29 December 2014", "29 December 2020", NONE);
		Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientId.toString(),
				recurringDepositProductId.toString(), "29 December 2014", NONE, "29 January 2015");
		this.savingsAccountHelper.approveSavingsOnDate(recurringDepositAccountId, "29 December 2014");
		this.savingsAccountHelper.activateSavingsAccount(recurringDepositAccountId, "29 December 2014");
		ArrayList<HashMap<String, Object>> groups = this.collectionSheetHelper.getCenterCollectionSheet(this.requestSpec,
				this.responseSpec, calendarId, "29 January 2015", centerId);
		for(HashMap<String, Object> group : groups){
			ArrayList<HashMap<String, Object>> clients = (ArrayList<HashMap<String, Object>>) group.get("clients");
			for(HashMap<String, Object> client : clients){
				ArrayList<HashMap<String, Object>> savings = (ArrayList<HashMap<String, Object>>) client.get("savings");
				for(HashMap<String, Object> saving : savings){
					Assert.assertTrue(saving.get("savingsId").equals(savingsAccountId) || saving.get("savingsId").equals(recurringDepositAccountId));
				}
			}
		}
		HashMap<String, Object> json = new HashMap<String,Object>();
		json.put("dateFormat",  "dd MMMM yyyy");
		json.put("locale", "en");
		json.put( "calendarId", calendarId);
		json.put( "transactionDate", "29 January 2015");
		ArrayList<HashMap<String, Object>> bulkRepaymentTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> loanTransactions = new HashMap<String,Object>();
		loanTransactions.put("loanId", loanId);
		loanTransactions.put("transactionAmount",2626.24);
		bulkRepaymentTransactions.add(loanTransactions);
		json.put("bulkRepaymentTransactions", bulkRepaymentTransactions);
		ArrayList<HashMap<String, Object>> bulkSavingsTransactions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> savingTransaction = new HashMap<String, Object>();
		savingTransaction.put("savingsId", savingsAccountId);
		savingTransaction.put("transactionAmount", 100);
		savingTransaction.put("withdrawAmount", 10);
		bulkSavingsTransactions.add(savingTransaction);
		HashMap<String, Object> rdTransaction = new HashMap<String, Object>();
		rdTransaction.put("savingsId", recurringDepositAccountId);
		rdTransaction.put("transactionAmount", 2000);
		rdTransaction.put("withdrawAmount", 1000);
		bulkSavingsTransactions.add(rdTransaction);
		json.put("bulkSavingsTransactions", bulkSavingsTransactions);
		ArrayList<HashMap<String,Object>> error = this.collectionSheetHelper.saveCollectionSheet(requestSpec, responseSpec403, new Gson().toJson(json), centerId, CommonConstants.RESPONSE_ERROR);
		assertEquals("error.msg.withdraw.for.account." + savingsAccountId + ".not.allowed.due.to.configiration",error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
		final HashMap<String, Object> loanSummary = (HashMap<String, Object>) this.loanTransactionHelper.getLoanDetail(requestSpec, responseSpec, loanId, "summary");
		Assert.assertEquals(new Float(0), loanSummary.get("totalRepayment"));
		final HashMap<String, Object> rdSummary = (HashMap<String, Object>) this.recurringDepositAccountHelper.getRecurringDepositAccountById(requestSpec, responseSpec, recurringDepositAccountId).get( "summary");
		Assert.assertEquals(new Float(0), rdSummary.get("accountBalance"));
		final HashMap<String, Object> savingsSummary = (HashMap<String, Object>) this.savingsAccountHelper.getSavingsAccountDetail(savingsAccountId, "summary");
		Assert.assertEquals(new Float(0), savingsSummary.get("accountBalance"));
	}
	
	private Integer applyForRecurringDepositApplication(final String clientID, final String productID,
			final String submittedOnDate, final String penalInterestType, final String expectedFirstDepositOnDate) {
		System.out.println(
				"--------------------------------APPLYING FOR RECURRING DEPOSIT ACCOUNT --------------------------------");
		final String recurringDepositApplicationJSON = new RecurringDepositAccountHelper(this.requestSpec,
				this.responseSpec).withSubmittedOnDate(submittedOnDate)
						.withExpectedFirstDepositOnDate(expectedFirstDepositOnDate)
						.withCalanderInherited()
						.build(clientID, productID, penalInterestType);
		return this.recurringDepositAccountHelper.applyRecurringDepositApplication(recurringDepositApplicationJSON,
				this.requestSpec, this.responseSpec);
	}

	private Integer createRecurringDepositProduct(final String validFrom, final String validTo,
			final String accountingRule, Account... accounts) {
		System.out.println(
				"------------------------------CREATING NEW RECURRING DEPOSIT PRODUCT ---------------------------------------");
		RecurringDepositProductHelper recurringDepositProductHelper = new RecurringDepositProductHelper(
				this.requestSpec, this.responseSpec);
		if (accountingRule.equals(CASH_BASED)) {
			recurringDepositProductHelper = recurringDepositProductHelper.withAccountingRuleAsCashBased(accounts);
		} else if (accountingRule.equals(NONE)) {
			recurringDepositProductHelper = recurringDepositProductHelper.withAccountingRuleAsNone();
		}
		final String recurringDepositProductJSON = recurringDepositProductHelper.withPeriodRangeChart().withMandatoryDeposit().build(validFrom,
				validTo);
		return RecurringDepositProductHelper.createRecurringDepositProduct(recurringDepositProductJSON, requestSpec,
				responseSpec);
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

	private Integer applyForClientJlgLoanApplication(final Integer clientID, final Integer loanProductID,
			String principal, String calendarId) {
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
				.withExpectedDisbursementDate("29 December 2014") //
				.withSubmittedOnDate("29 December 2014") //
				.withCalendarID(calendarId).build(clientID.toString(), loanProductID.toString(), null);
		return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
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

	private void setConfiguration(String configurationName, boolean enabled) {
		this.globalConfigurationHelper = new GlobalConfigurationHelper(this.requestSpec, this.responseSpec);
		// Retrieving All Global Configuration details
		final ArrayList<HashMap> globalConfig = this.globalConfigurationHelper
				.getAllGlobalConfigurations(this.requestSpec, this.responseSpec);
		Assert.assertNotNull(globalConfig);
		for (Integer configIndex = 0; configIndex < (globalConfig.size()); configIndex++) {
			if (globalConfig.get(configIndex).get("name").equals(configurationName)) {
				String configId = (globalConfig.get(configIndex).get("id")).toString();
				Integer updateConfigId = this.globalConfigurationHelper.updateEnabledFlagForGlobalConfiguration(
						this.requestSpec, this.responseSpec, configId.toString(), enabled);
				Assert.assertNotNull(updateConfigId);
				break;
			}
		}
	}
}