/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CalendarHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GlobalConfigurationHelper;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

import junit.framework.Assert;

public class SyncRepaymentFrequencyAndCentreMeetingFrequencyTest {

	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private LoanTransactionHelper loanTransactionHelper;
	private ResponseSpecification responseForbiddenError;
	private ResponseSpecification responseSpecForStatusCode403;
	private GlobalConfigurationHelper globalConfigurationHelper;
	private Integer clientId;
	private Integer groupId;
	private Integer loanId;
	private Integer calendarId;
	private Integer loanProductID;
	private String loanApplicationJSON;
	private boolean enabled;
	private String configName;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.responseForbiddenError = new ResponseSpecBuilder().expectStatusCode(403).build();
		this.responseSpecForStatusCode403 = new ResponseSpecBuilder().expectStatusCode(403).build();
	}

	// "force-loan-repayment-frequency-match-with-meeting-frequency" enabled
	// frequency of jlg loan is multiple of meeting frequency and frequency type
	// is same
	// when the loan application time itself it should throw an error
	@Test
	public void synchRepaymentFrequencyOfjlgLoanWithMeetingIntervalConfigurationEnabled() {
		this.enabled = true;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// create client,group with calendar attached, associate client with
		// group and loan product
		this.createClientGroupAndLoanProduct();
		// create loan application json
		this.createjlgLoanApplicationJson(this.groupId, this.loanProductID);
		this.responseSpecForStatusCode403 = new ResponseSpecBuilder().expectStatusCode(403).build();
		// submit loan application
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpecForStatusCode403);
		List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(this.loanApplicationJSON,
				CommonConstants.RESPONSE_ERROR);
		assertEquals("error.msg.calendar.loanapplication.repayment.interval.not.the.same.as.meeting.frequency",
				error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
	}

	// "force-loan-repayment-frequency-match-with-meeting-frequency" disabled
	// frequency of jlg loan is multiple of meeting frequency and frequency type
	// is same
	// it should allow to submit and modify the loan
	@Test
	public void synchRepaymentFrequencyOfjlgLoanWithMeetingIntervalConfigurationDisabled() {
		this.enabled = false;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// create client,group with calendar attached, associate client with
		// group and loan product
		this.createClientGroupAndLoanProduct();
		// create loan application json
		this.createjlgLoanApplicationJson(this.groupId, this.loanProductID);
		// submit loan application
		this.loanId = this.loanTransactionHelper.getLoanId(this.loanApplicationJSON);
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		// modify the loan application
		this.loanId = this.loanTransactionHelper.updateLoan(this.loanId, this.loanApplicationJSON);
		HashMap loanStatusHashMapAfterEdit = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec,
				this.loanId);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMapAfterEdit);
	}

	// "force-loan-repayment-frequency-match-with-meeting-frequency" disabled
	// frequency of loan is multiple of meeting frequency and frequency type is
	// same
	// when the jlg loan application it should allow to create a loan
	// "force-loan-repayment-frequency-match-with-meeting-frequency" enabled
	// modify the loan with frequency of loan is multiple of meeting frequency
	// and frequency type is same
	// it should not allow to create the loan
	@Test
	public void synchRepaymentFrequencyOfjlgLoanWithMeetingIntervalConfigurationEnabledMofifyTest() {
		// disable the configuration
		this.enabled = false;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// create client,group with calendar attached, associate client with
		// group and loan product
		this.createClientGroupAndLoanProduct();
		// create loan application json
		this.createjlgLoanApplicationJson(this.groupId, this.loanProductID);
		// submit loan application
		this.loanId = this.loanTransactionHelper.getLoanId(this.loanApplicationJSON);
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		// enable the configuration
		this.enabled = true;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// modify loan application
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpecForStatusCode403);
		List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.updateLoan(this.loanId,
				this.loanApplicationJSON, CommonConstants.RESPONSE_ERROR);
		assertEquals("error.msg.calendar.loanapplication.repayment.interval.not.the.same.as.meeting.frequency",
				error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
	}

	// "force-loan-repayment-frequency-match-with-meeting-frequency" disabled
	// frequency of group loan is multiple of meeting frequency and frequency
	// type is same
	// it should allow to submit and modify the loan
	@Test
	public void synchRepaymentFrequencyOfGoupLoanWithMeetingIntervalConfigurationDisabled() {
		this.enabled = false;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// create client,group with calendar attached, associate client with
		// group and loan product
		this.createClientGroupAndLoanProduct();
		// create loan application json
		this.createGroupLoanApplicationJson(this.groupId, this.loanProductID);
		// submit loan application
		this.loanId = this.loanTransactionHelper.getLoanId(this.loanApplicationJSON);
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		// modify the loan application
		this.loanId = this.loanTransactionHelper.updateLoan(this.loanId, this.loanApplicationJSON);
		HashMap loanStatusHashMapAfterEdit = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec,
				this.loanId);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMapAfterEdit);
	}

	// "force-loan-repayment-frequency-match-with-meeting-frequency" enabled
	// frequency of group loan is multiple of meeting frequency and frequency
	// type is same
	// when the loan application time itself it should throw an error
	@Test
	public void synchRepaymentFrequencyOfGroupLoanWithMeetingIntervalConfigurationEnabled() {
		this.enabled = true;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// create client,group with calendar attached, associate client with
		// group and loan product
		this.createClientGroupAndLoanProduct();
		// create loan application json
		this.createGroupLoanApplicationJson(this.groupId, this.loanProductID);
		// submit loan application
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpecForStatusCode403);
		List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(this.loanApplicationJSON,
				CommonConstants.RESPONSE_ERROR);
		assertEquals("error.msg.calendar.loanapplication.repayment.interval.not.the.same.as.meeting.frequency",
				error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
	}

	// "force-loan-repayment-frequency-match-with-meeting-frequency" disabled
	// frequency of group loan is multiple of meeting frequency and frequency
	// type is same
	// when the group loan application it should allow to create a loan
	// "force-loan-repayment-frequency-match-with-meeting-frequency" enabled
	// modify the group loan with frequency of loan is multiple of meeting
	// frequency and frequency type is same
	// it should not allow to create the loan
	@Test
	public void synchRepaymentFrequencyOfGroupLoanWithMeetingIntervalConfigurationEnabledMofifyTest() {
		this.enabled = false;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// create client,group with calendar attached ,loan product
		this.createClientGroupAndLoanProduct();
		// create loan application json
		this.createGroupLoanApplicationJson(this.groupId, this.loanProductID);
		// submit loan application
		this.loanId = this.loanTransactionHelper.getLoanId(this.loanApplicationJSON);
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);
		this.enabled = true;
		this.configName = "force-loan-repayment-frequency-match-with-meeting-frequency";
		this.setConfiguration();
		// modify the loan application
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpecForStatusCode403);
		List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.updateLoan(this.loanId,
				this.loanApplicationJSON, CommonConstants.RESPONSE_ERROR);
		assertEquals("error.msg.calendar.loanapplication.repayment.interval.not.the.same.as.meeting.frequency",
				error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
	}

	// setting the configuration as enabled or disabled
	private void setConfiguration() {
		this.globalConfigurationHelper = new GlobalConfigurationHelper(this.requestSpec, this.responseSpec);
		// Retrieving All Global Configuration details
		final ArrayList<HashMap> globalConfig = this.globalConfigurationHelper
				.getAllGlobalConfigurations(this.requestSpec, this.responseSpec);
		Assert.assertNotNull(globalConfig);
		for (Integer configIndex = 0; configIndex < (globalConfig.size()); configIndex++) {
			if (globalConfig.get(configIndex).get("name").equals(this.configName)) {
				String configId = (globalConfig.get(configIndex).get("id")).toString();
				Integer updateConfigId = this.globalConfigurationHelper.updateEnabledFlagForGlobalConfiguration(
						this.requestSpec, this.responseSpec, configId.toString(), this.enabled);
				Assert.assertNotNull(updateConfigId);
				break;
			}
		}
	}

	// create client, create group with meeting attached
	// associate client with group
	// create loan product
	private void createClientGroupAndLoanProduct() {
		String groupActivationDate = "1 August 2014";
		this.createGroupEntityWithCalendar(groupActivationDate);
		this.createClientEntity();
		this.associateClientToGroup(this.groupId, this.clientId);
		this.loanProductID = createLoanProduct();
	}

	// create a client
	private void createClientEntity() {
		this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
		ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, this.clientId);
	}

	// create group with calendar attached
	private void createGroupEntityWithCalendar(String groupActivationDate) {
		this.groupId = GroupHelper.createGroup(this.requestSpec, this.responseSpec, groupActivationDate);
		GroupHelper.verifyGroupCreatedOnServer(this.requestSpec, this.responseSpec, this.groupId);

		final String startDate = groupActivationDate;
		final String frequency = "3"; // 2:Weekly
		final String interval = "1"; // Every one week
		this.setCalenderId(CalendarHelper.createMeetingForGroup(requestSpec, responseSpec, this.groupId, startDate,
				frequency, interval, null));
	}

	// setting calendar id
	private void setCalenderId(Integer groupCalendarId) {
		this.calendarId = groupCalendarId;
	}

	// associate group with calendar
	private void associateClientToGroup(final Integer groupId, final Integer clientId) {
		GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupId.toString(), clientId.toString());
		GroupHelper.verifyGroupMembers(this.requestSpec, this.responseSpec, groupId, clientId);
	}

	// create a loan product
	private Integer createLoanProduct() {
		System.out.println(
				"------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
		final String loanProductJSON = new LoanProductTestBuilder() //
				.withPrincipal("12,000.00") //
				.withNumberOfRepayments("4") //
				.withRepaymentAfterEvery("1") //
				.withRepaymentTypeAsMonth() //
				.withinterestRatePerPeriod("1") //
				.withInterestRateFrequencyTypeAsMonths() //
				.withAmortizationTypeAsEqualInstallments() //
				.withInterestTypeAsDecliningBalance() //
				.build(null);
		return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
	}

	// create a loanApplicationJson for jlg loan
	private void createjlgLoanApplicationJson(final Integer groupID, final Integer loanProductID) {
		System.out.println(
				"--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
		this.loanApplicationJSON = new LoanApplicationTestBuilder() //
				.withPrincipal("12,000.00") //
				.withLoanTermFrequency("8") //
				.withLoanTermFrequencyAsMonths() //
				.withNumberOfRepayments("4") //
				.withRepaymentEveryAfter("2") //
				.withRepaymentFrequencyTypeAsMonths() //
				.withInterestRatePerPeriod("2") //
				.withAmortizationTypeAsEqualInstallments() //
				.withInterestTypeAsDecliningBalance() //
				.withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
				.withExpectedDisbursementDate("1 August 2014") //
				.withSubmittedOnDate("1 August 2014") //
				.withCalendarID(this.calendarId.toString())//
				.withLoanType("jlg")
				.build(this.clientId.toString(), groupID.toString(), loanProductID.toString(), null);

	}

	//// create a loanApplicationJson for group loan
	private void createGroupLoanApplicationJson(final Integer groupID, final Integer loanProductID) {
		System.out.println(
				"--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
		this.loanApplicationJSON = new LoanApplicationTestBuilder() //
				.withPrincipal("12,000.00") //
				.withLoanTermFrequency("8") //
				.withLoanTermFrequencyAsMonths() //
				.withNumberOfRepayments("4") //
				.withRepaymentEveryAfter("2") //
				.withRepaymentFrequencyTypeAsMonths() //
				.withInterestRatePerPeriod("2") //
				.withAmortizationTypeAsEqualInstallments() //
				.withInterestTypeAsDecliningBalance() //
				.withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
				.withExpectedDisbursementDate("1 August 2014") //
				.withSubmittedOnDate("1 August 2014") //
				.withCalendarID(this.calendarId.toString())//
				.withLoanType("group").build(groupID.toString(), loanProductID.toString(), null);

	}
}
