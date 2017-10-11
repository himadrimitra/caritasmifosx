/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.CalendarHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

/**
 * Test the creation, approval and rejection of a loan reschedule request
 **/
@SuppressWarnings({ "rawtypes" })
public class LoanTenureValidationTest {

    private ResponseSpecification responseSpec;
    private ResponseSpecification responseSpecForStatusCode403;
    private RequestSpecification requestSpecc;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;
    private Integer clientId;
    private Integer groupId;
    private Integer groupCalendarId;
    private Integer loanProductId;
    private Integer loanId;
    private final String loanPrincipalAmount = "100000.00";
    private final String numberOfRepayments = "12";
    private final String interestRatePerPeriod = "18";
    private final String groupActivationDate = "1 August 2014";
    private final Integer minimumPeriodBetweenDisbursalAndFirstRepayment =1;
    private ResponseSpecification responseSpecForStatusCode400;
    private Boolean canDefineInstallmentAmount ;
    
    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpecc = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpecc.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpecc, this.responseSpec);
        this.canDefineInstallmentAmount = true;
    }

    /*
     * MinimumDaysBetweenDisbursalAndFirstRepayment is set to 7 days and days
     * between disbursal date and first repayment is set as 7. system should
     * allow to create this loan and allow to disburse
     */
    @Test
    public void createLoanEntity_WITH_DAY_BETWEEN_DISB_DATE_AND_REPAY_START_DATE_GREATER_THAN_MIN_DAY_CRITERIA() {

        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        // create all required entities
        this.createRequiredEntities();

        final String disbursalDate = "4 September 2014";
        final String firstRepaymentDate = "11 September 2014";

        final String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(loanPrincipalAmount)
                .withLoanTermFrequency(numberOfRepayments).withLoanTermFrequencyAsWeeks().withNumberOfRepayments(numberOfRepayments)
                .withRepaymentEveryAfter("1").withRepaymentFrequencyTypeAsMonths().withAmortizationTypeAsEqualInstallments()
                .withInterestCalculationPeriodTypeAsDays().withInterestRatePerPeriod(interestRatePerPeriod)
                .withRepaymentFrequencyTypeAsWeeks().withSubmittedOnDate(disbursalDate).withExpectedDisbursementDate(disbursalDate)
                .withPrincipalGrace("2").withInterestGrace("2").withFirstRepaymentDate(firstRepaymentDate)
                .build(this.clientId.toString(), this.loanProductId.toString(), null);

        this.loanId = this.loanTransactionHelper.getLoanId(loanApplicationJSON);

        // Test for loan account is created
        Assert.assertNotNull(this.loanId);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        // Test for loan account is created, can be approved
        this.loanTransactionHelper.approveLoan(disbursalDate, this.loanId);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);

        // Test for loan account approved can be disbursed
        this.loanTransactionHelper.disburseLoan(disbursalDate, this.loanId);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

    }

    /*
     * MinimumDaysBetweenDisbursalAndFirstRepayment is set to 7 days and days
     * between disbursal date and first repayment is set as 7. system should
     * allow to create this loan and allow to disburse
     */
    @SuppressWarnings("unchecked")
    @Test
    public void createLoanEntity_WITH_DAY_BETWEEN_DISB_DATE_AND_REPAY_START_DATE_LESS_THAN_MIN_DAY_CRITERIA() {

        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.responseSpecForStatusCode403 = new ResponseSpecBuilder().expectStatusCode(403).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        // create all required entities
        this.createRequiredEntities();

        // loanTransactionHelper is reassigned to accept 403 status code from
        // server
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpecForStatusCode403);

        final String disbursalDate = "4 September 2014";
        final String firstRepaymentDate = "5 September 2014";

        final String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(loanPrincipalAmount)
                .withLoanTermFrequency(numberOfRepayments).withLoanTermFrequencyAsWeeks().withNumberOfRepayments(numberOfRepayments)
                .withRepaymentEveryAfter("1").withRepaymentFrequencyTypeAsMonths().withAmortizationTypeAsEqualInstallments()
                .withInterestCalculationPeriodTypeAsDays().withInterestRatePerPeriod(interestRatePerPeriod)
                .withRepaymentFrequencyTypeAsWeeks().withSubmittedOnDate(disbursalDate).withExpectedDisbursementDate(disbursalDate)
                .withPrincipalGrace("2").withInterestGrace("2").withFirstRepaymentDate(firstRepaymentDate)
                .build(this.clientId.toString(), this.loanProductId.toString(), null);

        List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(loanApplicationJSON,
                CommonConstants.RESPONSE_ERROR);
        assertEquals("error.msg.loan.days.between.first.repayment.and.disbursal.are.less.than.minimum.days.or.periods.allowed",
                error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

    }
    
    /*
     * MinimumPeriodbetweeDisbursalAndFirstRepayment is set to one loan period (repay every one week)
     * and days between first repayment and disbursal set to one  period (7 days)
     * system should  allow to create a loan and allow to disburse
     * */
    @SuppressWarnings("unchecked")
    @Test
    public void createLoanEntity_WITH_PERIOD_BETWEEN_DISB_DATE_AND_REPAY_START_DATE_GREATER_THAN_MIN_DAY_CRITERIA() {

        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        // create all required entities
        this.createRequiredEntities(minimumPeriodBetweenDisbursalAndFirstRepayment);

        final String disbursalDate = "4 September 2014";
        final String firstRepaymentDate = "11 September 2014";

        final String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(loanPrincipalAmount)
                .withLoanTermFrequency(numberOfRepayments).withLoanTermFrequencyAsWeeks().withNumberOfRepayments(numberOfRepayments)
                .withRepaymentEveryAfter("1").withRepaymentFrequencyTypeAsMonths().withAmortizationTypeAsEqualInstallments()
                .withInterestCalculationPeriodTypeAsDays().withInterestRatePerPeriod(interestRatePerPeriod)
                .withRepaymentFrequencyTypeAsWeeks().withSubmittedOnDate(disbursalDate).withExpectedDisbursementDate(disbursalDate)
                .withPrincipalGrace("2").withInterestGrace("2").withFirstRepaymentDate(firstRepaymentDate)
                .build(this.clientId.toString(), this.loanProductId.toString(), null);

        this.loanId = this.loanTransactionHelper.getLoanId(loanApplicationJSON);

        // Test for loan account is created
        Assert.assertNotNull(this.loanId);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        // Test for loan account is created, can be approved
        this.loanTransactionHelper.approveLoan(disbursalDate, this.loanId);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);

        // Test for loan account approved can be disbursed
        this.loanTransactionHelper.disburseLoan(disbursalDate, this.loanId);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

    }
    
    /*
     * MinimumPeriodbetweeDisbursalAndFirstRepayment is set to one period
     * and days between first repayment and disbursal set to one day
     * at the time of loan application loan frequency changed to repay every one day
     * system should allow to create a loan to disburse the loan 
     * */
    @SuppressWarnings("unchecked")
    @Test
    public void createLoanEntity_WITH_DAYS_BETWEEN_DISB_DATE_AND_REPAY_START_DATE_GTATER_THAN_MIN_DAY_CRITERIA_WITH_REPAYMENT_FREEQUENCY_CHANGED() {

        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        // create all required entities
        this.createRequiredEntities(minimumPeriodBetweenDisbursalAndFirstRepayment);


        final String disbursalDate = "4 September 2014";
        final String firstRepaymentDate = "05 September 2014";
        final String numberOfRepayments = "84";

        final String loanApplicationJSON = new LoanApplicationTestBuilder().withPrincipal(loanPrincipalAmount)
                .withLoanTermFrequency(numberOfRepayments).withLoanTermFrequencyAsDays().withNumberOfRepayments(numberOfRepayments)
                .withRepaymentEveryAfter("1").withRepaymentFrequencyTypeAsMonths().withAmortizationTypeAsEqualInstallments()
                .withInterestCalculationPeriodTypeAsDays().withInterestRatePerPeriod(interestRatePerPeriod)
                .withRepaymentFrequencyTypeAsDays().withSubmittedOnDate(disbursalDate).withExpectedDisbursementDate(disbursalDate)
                .withPrincipalGrace("2").withInterestGrace("2").withFirstRepaymentDate(firstRepaymentDate)
                .build(this.clientId.toString(), this.loanProductId.toString(), null);
        
        this.loanId = this.loanTransactionHelper.getLoanId(loanApplicationJSON);

        // Test for loan account is created
        Assert.assertNotNull(this.loanId);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        // Test for loan account is created, can be approved
        this.loanTransactionHelper.approveLoan(disbursalDate, this.loanId);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);

        // Test for loan account approved can be disbursed
        this.loanTransactionHelper.disburseLoan(disbursalDate, this.loanId);
        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, this.loanId);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

    }
    
    @Test
    public void Laoan_Appliation_With_Actual_Number_Of_Repayment_Grater_Than_maxLoanterm() {

        final String proposedAmount = "1000";
        final Integer minLoanTerm = 8;
        final Integer maxLoanTerm = 12;
        final Integer loanTenureFrequencyType = 2;
        final String installmentAmount = "60";
        final String principal = "1000";
        final String interestRatePerPeriod = "2";
        final String repaymentAfterEvery = "1";
        final String numberOfRepayment = "10";
        this.responseSpecForStatusCode400 = new ResponseSpecBuilder().expectStatusCode(400).build();
        
        // CREATE CLIENT
        final Integer clientID = ClientHelper.createClient(this.requestSpecc, this.responseSpec, "01 January 2014");
        System.out.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
                + clientID);

        // CREATE LOAN  PRODUCT
        final Integer loanProductID = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().withPrincipal(principal).
        		withinterestRatePerPeriod(interestRatePerPeriod).withRepaymentTypeAsMonth().withRepaymentAfterEvery(repaymentAfterEvery).
        		withNumberOfRepayments(numberOfRepayment).withInterestRateFrequencyTypeAsYear().withLoanTenureFrequencyType(loanTenureFrequencyType).
        		withLoanTerms(minLoanTerm, maxLoanTerm).withCanDefineInstallmentAmount(canDefineInstallmentAmount).build(null));
        System.out.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
                + loanProductID);

        // APPLY FOR LOAN 
        String loanApplicationJson = createLoanApplicationJson(clientID, loanProductID, proposedAmount, installmentAmount);
        
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpecc, this.responseSpecForStatusCode400);
        List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(loanApplicationJson,
                CommonConstants.RESPONSE_ERROR);
        
        Assert.assertEquals("validation.msg.loan.number.of.repayments.greater.than.max.loan.term",
        		error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));   
           
    }
    
    @Test
    public void Laoan_Appliation_With_Actual_Number_Of_Repayment_Lesser_Than_minLoanterm() {

        final String proposedAmount = "1000";
        final Integer minLoanTerm = 8;
        final Integer maxLoanTerm = 12;
        final Integer loanTenureFrequencyType = 2;
        final String installmentAmount = "200";
        final String principal = "1000";
        final String interestRatePerPeriod = "2";
        final String repaymentAfterEvery = "1";
        final String numberOfRepayment = "10";
        this.responseSpecForStatusCode400 = new ResponseSpecBuilder().expectStatusCode(400).build();
        
        // CREATE CLIENT
        final Integer clientID = ClientHelper.createClient(this.requestSpecc, this.responseSpec, "01 January 2014");
        System.out.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
                + clientID);

        // CREATE LOAN  PRODUCT
        final Integer loanProductID = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().withPrincipal(principal).
        		withinterestRatePerPeriod(interestRatePerPeriod).withRepaymentTypeAsMonth().withRepaymentAfterEvery(repaymentAfterEvery).
        		withNumberOfRepayments(numberOfRepayment).withInterestRateFrequencyTypeAsYear().withLoanTenureFrequencyType(loanTenureFrequencyType).
        		withLoanTerms(minLoanTerm, maxLoanTerm).withCanDefineInstallmentAmount(canDefineInstallmentAmount).build(null));
        System.out.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
                + loanProductID);

        // APPLY FOR LOAN 
        String loanApplicationJson = createLoanApplicationJson(clientID, loanProductID, proposedAmount, installmentAmount);
        
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpecc, this.responseSpecForStatusCode400);
        List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(loanApplicationJson,
                CommonConstants.RESPONSE_ERROR);
        
        Assert.assertEquals("validation.msg.loan.number.of.repayments.lesser.than.min.loan.term",
        		error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));   
           
    }
    
    @Test
    public void Laoan_Appliation_With_Actual_Number_Of_Repayment_Lesser_Than_Minimum_Number_Of_Repayments() {

        final String proposedAmount = "1000";
        final Integer minNumberOfRepayments = 8;
        final Integer maxNumberOfRepayments = 12;
        final String installmentAmount = "200";
        final String principal = "1000";
        final String interestRatePerPeriod = "2";
        final String repaymentAfterEvery = "1";
        final String numberOfRepayment = "10";
        this.responseSpecForStatusCode400 = new ResponseSpecBuilder().expectStatusCode(400).build();
        // CREATE CLIENT
        final Integer clientID = ClientHelper.createClient(this.requestSpecc, this.responseSpec, "01 January 2014");
        System.out.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
                + clientID);

        // CREATE LOAN  PRODUCT
        final Integer loanProductID = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().withPrincipal(principal).
        		withinterestRatePerPeriod(interestRatePerPeriod).withRepaymentTypeAsMonth().withRepaymentAfterEvery(repaymentAfterEvery).
        		withNumberOfRepayments(numberOfRepayment).withInterestRateFrequencyTypeAsYear().
        		withMininmumAndMaximumNumberOfRepayments(minNumberOfRepayments, maxNumberOfRepayments).withCanDefineInstallmentAmount(canDefineInstallmentAmount).build(null));
        System.out.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
                + loanProductID);

        // APPLY FOR LOAN 
        String loanApplicationJson = createLoanApplicationJson(clientID, loanProductID, proposedAmount, installmentAmount);
        
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpecc, this.responseSpecForStatusCode400);
        List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(loanApplicationJson,
                CommonConstants.RESPONSE_ERROR);
        
        Assert.assertEquals("validation.msg.loan.number.of.repayments.lesser.than.minimum.number.of.repayments",
        		error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));   
           
    }

    @Test
    public void Laoan_Appliation_With_Actual_Number_Of_Repayment_Grater_Than_Maximum_Number_Of_Repayments() {

        final String proposedAmount = "1000";
        final Integer minNumberOfRepayments = 8;
        final Integer maxNumberOfRepayments = 12;
        final String installmentAmount = "60";
        final String principal = "1000";
        final String interestRatePerPeriod = "2";
        final String repaymentAfterEvery = "1";
        final String numberOfRepayment = "10";
        this.responseSpecForStatusCode400 = new ResponseSpecBuilder().expectStatusCode(400).build();
        // CREATE CLIENT
        final Integer clientID = ClientHelper.createClient(this.requestSpecc, this.responseSpec, "01 January 2014");
        System.out.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
                + clientID);

        // CREATE LOAN  PRODUCT
        final Integer loanProductID = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().withPrincipal(principal).
        		withinterestRatePerPeriod(interestRatePerPeriod).withRepaymentTypeAsMonth().withRepaymentAfterEvery(repaymentAfterEvery).
        		withNumberOfRepayments(numberOfRepayment).withInterestRateFrequencyTypeAsYear().
        		withMininmumAndMaximumNumberOfRepayments(minNumberOfRepayments, maxNumberOfRepayments).withCanDefineInstallmentAmount(canDefineInstallmentAmount).build(null));
        System.out.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
                + loanProductID);

        // APPLY FOR LOAN 
        String loanApplicationJson = createLoanApplicationJson(clientID, loanProductID, proposedAmount, installmentAmount);
        
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpecc, this.responseSpecForStatusCode400);
        List<HashMap> error = (List<HashMap>) this.loanTransactionHelper.createLoanAccount(loanApplicationJson,
                CommonConstants.RESPONSE_ERROR);
        
        Assert.assertEquals("validation.msg.loan.number.of.repayments.greater.than.maximum.number.of.repayments",
        		error.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));   
           
    }
    
    @Test
    public void Laoan_Appliation_With_Actual_Number_Of_Repayment_Between_Min_Max_Number_Of_Repayments_And_Min_Max_LoanTerm() {

        final String proposedAmount = "1000";
        final Integer minNumberOfRepayments = 6;
        final Integer maxNumberOfRepayments = 15;
        final Integer minLoanTerm = 8;
        final Integer maxLoanTerm = 12;
        final Integer loanTenureFrequencyType = 2;
        final String installmentAmount = "100";
        final String principal = "1000";
        final String interestRatePerPeriod = "2";
        final String repaymentAfterEvery = "1";
        final String numberOfRepayment = "10";
        // CREATE CLIENT
        final Integer clientID = ClientHelper.createClient(this.requestSpecc, this.responseSpec, "01 January 2014");
        System.out.println("---------------------------------CLIENT CREATED WITH ID---------------------------------------------------"
                + clientID);

        // CREATE LOAN  PRODUCT
        final Integer loanProductID = this.loanTransactionHelper.getLoanProductId(new LoanProductTestBuilder().withPrincipal(principal).
        		withinterestRatePerPeriod(interestRatePerPeriod).withRepaymentTypeAsMonth().withRepaymentAfterEvery(repaymentAfterEvery).
        		withNumberOfRepayments(numberOfRepayment).withInterestRateFrequencyTypeAsYear().withLoanTerms(minLoanTerm, maxLoanTerm).
        		withMininmumAndMaximumNumberOfRepayments(minNumberOfRepayments, maxNumberOfRepayments).withLoanTenureFrequencyType(loanTenureFrequencyType).
        		withCanDefineInstallmentAmount(canDefineInstallmentAmount).build(null));
        System.out.println("----------------------------------LOAN PRODUCT CREATED WITH ID-------------------------------------------"
                + loanProductID);

        // APPLY FOR LOAN 
        String loanApplicationJson = createLoanApplicationJson(clientID, loanProductID, proposedAmount, installmentAmount);
         final Integer loanId = this.loanTransactionHelper.getLoanId(loanApplicationJson);
         System.out.println("-----------------------------------LOAN CREATED WITH LOANID-------------------------------------------------"
         		+ loanId);
         HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpecc, this.responseSpec, loanId);
         LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);
         
    }
    
    /**
     * Creates the client, loan product, and loan entities
     **/
    private void createRequiredEntities() {
        final String minimumDaysBetweenDisbursalAndFirstRepayment = "7"; // &
                                                                         // days
        this.createGroupEntityWithCalendar();
        this.createClientEntity();
        this.associateClientToGroup(this.groupId, this.clientId);
        this.createLoanProductEntity(minimumDaysBetweenDisbursalAndFirstRepayment);

    }
    
    /**
     * Creates the client, loan product, and loan entities(with minimum 
     * days between disbursal and first repayment as as one loan period)
     **/
    private void createRequiredEntities(final Integer minimumPeriodBetweenDisbursalAndFirstRepayment) {
        this.createGroupEntityWithCalendar();
        this.createClientEntity();
        this.associateClientToGroup(this.groupId, this.clientId);
        this.createLoanProductEntity(minimumPeriodBetweenDisbursalAndFirstRepayment);

    }

    /*
     * Associate client to the group
     */

    private void associateClientToGroup(final Integer groupId, final Integer clientId) {
        GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupId.toString(), clientId.toString());
        GroupHelper.verifyGroupMembers(this.requestSpec, this.responseSpec, groupId, clientId);
    }

    /*
     * Create a new group
     */

    private void createGroupEntityWithCalendar() {
        this.groupId = GroupHelper.createGroup(this.requestSpec, this.responseSpec, this.groupActivationDate);
        GroupHelper.verifyGroupCreatedOnServer(this.requestSpec, this.responseSpec, this.groupId);

        final String startDate = this.groupActivationDate;
        final String frequency = "2"; // 2:Weekly
        final String interval = "1"; // Every one week
        final String repeatsOnDay = "1"; // 1:Monday

        this.setGroupCalendarId(CalendarHelper.createMeetingCalendarForGroup(this.requestSpec, this.responseSpec, this.groupId, startDate,
                frequency, interval, repeatsOnDay));
    }

    /**
     * create a new client
     **/
    private void createClientEntity() {
        this.clientId = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, this.clientId);
    }

    /**
     * create a new loan product
     **/
    private void createLoanProductEntity(final String minimumDaysBetweenDisbursalAndFirstRepayment) {
        final String loanProductJSON = new LoanProductTestBuilder().withPrincipal(loanPrincipalAmount)
                .withNumberOfRepayments(numberOfRepayments).withinterestRatePerPeriod(interestRatePerPeriod)
                .withInterestRateFrequencyTypeAsYear()
                .withMinimumDaysBetweenDisbursalAndFirstRepayment(minimumDaysBetweenDisbursalAndFirstRepayment).build(null);
        this.loanProductId = this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }
    
    /**
     * create a new loan product with minimum days as period
     **/
    private void createLoanProductEntity(final Integer minimumPeriodBetweenDisbursalAndFirstRepayment) {
        final String loanProductJSON = new LoanProductTestBuilder().withPrincipal(loanPrincipalAmount)
                .withNumberOfRepayments(numberOfRepayments).withinterestRatePerPeriod(interestRatePerPeriod)
                .withInterestRateFrequencyTypeAsYear()
                .withMinimumPeriodsBetweenDisbursalAndFirstRepayment(minimumPeriodBetweenDisbursalAndFirstRepayment).build(null);
        this.loanProductId = this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    public Integer getGroupCalendarId() {
        return groupCalendarId;
    }

    public void setGroupCalendarId(Integer groupCalendarId) {
        this.groupCalendarId = groupCalendarId;
    }
    
    private String createLoanApplicationJson(final Integer clientID, final Integer loanProductID, final String proposedAmount,
    		final String installmentAmount) {
        final String loanApplication = new LoanApplicationTestBuilder()
        		.withPrincipal(proposedAmount).withLoanTermFrequency("10")
        		.withAmortizationTypeAsEqualInstallments()
        		.withInterestTypeAsDecliningBalance()
                .withLoanTermFrequencyAsMonths().withNumberOfRepayments("10")
                .withRepaymentEveryAfter("1")
                .withRepaymentFrequencyTypeAsMonths()
                .withInterestRatePerPeriod("2")
                .withExpectedDisbursementDate("1 March 2014")
                .withSubmittedOnDate("26 February 2014").
                withFixedEmiAmount(installmentAmount). 
                withCanDefineInstallmentAmount( canDefineInstallmentAmount).
                build(clientID.toString(), loanProductID.toString(), null);
        return loanApplication;
    }
}