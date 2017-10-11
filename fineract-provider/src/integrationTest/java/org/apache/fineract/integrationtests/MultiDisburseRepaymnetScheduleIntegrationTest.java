package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanStatusChecker;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class MultiDisburseRepaymnetScheduleIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;

    private static final String CREATE_CLIENT_URL = "/fineract-provider/api/v1/clients?" + Utils.TENANT_IDENTIFIER;
    public static final String DATE_FORMAT = "dd MMMM yyyy";
    final String digitsAfterDecimal = "2";
    final String inMultiplesOf = "1";
    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        dateFormat.setTimeZone(Utils.getTimeZoneOfTenant());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testClientRepaymnetSchedule_WITH_INTEREST_RECALCULATION() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        boolean enableInterestRecalculation = true;
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        boolean isEmiBasedOnDisbursements = false;
        final Integer loanProductID = createLoanProductwithFutureDisbursements("1", "2", LoanProductTestBuilder.RBI_INDIA_STRATEGY,
                enableInterestRecalculation, isEmiBasedOnDisbursements);

        Assert.assertNotNull(loanProductID);

        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        todaysDate.add(Calendar.DAY_OF_MONTH, -14);
        final String LOAN_DISBURSEMENT_DATE_1 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_2 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_3 = dateFormat.format(todaysDate.getTime());

        List<HashMap> tranches = new ArrayList<>();
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_1, "5000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_2, "3000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_3, "2000"));

        final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "10000",
                LoanApplicationTestBuilder.RBI_INDIA_STRATEGY, tranches, LOAN_DISBURSEMENT_DATE_1);
        Assert.assertNotNull(loanID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        List<Map<String, Object>> expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan(LOAN_DISBURSEMENT_DATE_1, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        // DISBURSE first Tranche
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan(LOAN_DISBURSEMENT_DATE_1, loanID, "5000");
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        Map loanDetails = this.loanTransactionHelper.getLoanRepaymentScheduleWithOriginalSchedule(this.requestSpec, this.responseSpec,
                loanID);

        loanSchedule = (ArrayList<HashMap>) ((Map) loanDetails.get("repaymentSchedule")).get("periods");
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1658.78", "7.66", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = (ArrayList<HashMap>) ((Map) loanDetails.get("originalSchedule")).get("periods");
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1651.07", "7.62", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedulePreview(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1676.84", "16.85", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1684.58", "9.11", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "289.65", "1.34", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testClientRepaymnetSchedule_WITH_INTEREST_RECALCULATION_WITH_OUT_FUTURE_TRANCHES() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        boolean enableInterestRecalculation = true;
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        boolean isEmiBasedOnDisbursements = false;
        final Integer loanProductID = createLoanProductwithFutureDisbursements("1", "2", LoanProductTestBuilder.RBI_INDIA_STRATEGY,
                enableInterestRecalculation, isEmiBasedOnDisbursements);

        Assert.assertNotNull(loanProductID);

        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        todaysDate.add(Calendar.DAY_OF_MONTH, -21);
        final String LOAN_DISBURSEMENT_DATE_1 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_2 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_3 = dateFormat.format(todaysDate.getTime());

        List<HashMap> tranches = new ArrayList<>();
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_1, "5000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_2, "3000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_3, "2000"));

        final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "10000",
                LoanApplicationTestBuilder.RBI_INDIA_STRATEGY, tranches, LOAN_DISBURSEMENT_DATE_1);
        Assert.assertNotNull(loanID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        List<Map<String, Object>> expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan(LOAN_DISBURSEMENT_DATE_1, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        // DISBURSE first Tranche
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan(LOAN_DISBURSEMENT_DATE_1, loanID, "5000");
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        Map loanDetails = this.loanTransactionHelper.getLoanRepaymentScheduleWithOriginalSchedule(this.requestSpec, this.responseSpec,
                loanID);

        loanSchedule = (ArrayList<HashMap>) ((Map) loanDetails.get("repaymentSchedule")).get("periods");
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1658.78", "23.08", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = (ArrayList<HashMap>) ((Map) loanDetails.get("originalSchedule")).get("periods");
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1651.07", "7.62", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedulePreview(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1651.07", "7.62", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testClientRepaymnetSchedule_WITH_OUT_INTEREST_RECALCULATION() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        boolean enableInterestRecalculation = false;
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        boolean isEmiBasedOnDisbursements = false;
        final Integer loanProductID = createLoanProductwithFutureDisbursements("1", "2", LoanProductTestBuilder.RBI_INDIA_STRATEGY,
                enableInterestRecalculation, isEmiBasedOnDisbursements);

        Assert.assertNotNull(loanProductID);

        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        todaysDate.add(Calendar.DAY_OF_MONTH, -14);
        final String LOAN_DISBURSEMENT_DATE_1 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_2 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_3 = dateFormat.format(todaysDate.getTime());

        List<HashMap> tranches = new ArrayList<>();
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_1, "5000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_2, "3000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_3, "2000"));

        final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "10000",
                LoanApplicationTestBuilder.RBI_INDIA_STRATEGY, tranches, LOAN_DISBURSEMENT_DATE_1);
        Assert.assertNotNull(loanID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        List<Map<String, Object>> expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan(LOAN_DISBURSEMENT_DATE_1, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        // DISBURSE first Tranche
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan(LOAN_DISBURSEMENT_DATE_1, loanID, "5000");
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1651.07", "7.62", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedulePreview(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1676.84", "16.85", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1684.58", "9.11", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "289.65", "1.34", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testClientRepaymnetSchedule_WITH_OUT_INTEREST_RECALCULATION_FUTURE_TRANCHES() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        boolean enableInterestRecalculation = false;
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        boolean isEmiBasedOnDisbursements = false;
        final Integer loanProductID = createLoanProductwithFutureDisbursements("1", "2", LoanProductTestBuilder.RBI_INDIA_STRATEGY,
                enableInterestRecalculation, isEmiBasedOnDisbursements);

        Assert.assertNotNull(loanProductID);

        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        todaysDate.add(Calendar.DAY_OF_MONTH, -21);
        final String LOAN_DISBURSEMENT_DATE_1 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_2 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_3 = dateFormat.format(todaysDate.getTime());

        List<HashMap> tranches = new ArrayList<>();
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_1, "5000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_2, "3000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_3, "2000"));

        final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "10000",
                LoanApplicationTestBuilder.RBI_INDIA_STRATEGY, tranches, LOAN_DISBURSEMENT_DATE_1);
        Assert.assertNotNull(loanID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        List<Map<String, Object>> expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan(LOAN_DISBURSEMENT_DATE_1, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1664.48", "29.21", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1662.93", "30.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.6", "23.09", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.31", "15.38", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1653.07", "7.63", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        // DISBURSE first Tranche
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan(LOAN_DISBURSEMENT_DATE_1, loanID, "5000");
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1651.07", "7.62", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedulePreview(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -3, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1670.61", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1678.32", "15.37", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1651.07", "7.62", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testClientRepaymnetSchedule_WITH_INTEREST_RECALCULATION_EMI_BASED_ON_DISBURSEMENTS() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        boolean enableInterestRecalculation = true;
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
        boolean isEmiBasedOnDisbursements = true;
        final Integer loanProductID = createLoanProductwithFutureDisbursements("1", "2", LoanProductTestBuilder.RBI_INDIA_STRATEGY,
                enableInterestRecalculation, isEmiBasedOnDisbursements);

        Assert.assertNotNull(loanProductID);

        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        todaysDate.add(Calendar.DAY_OF_MONTH, -14);
        final String LOAN_DISBURSEMENT_DATE_1 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_2 = dateFormat.format(todaysDate.getTime());

        todaysDate.add(Calendar.DAY_OF_MONTH, 7);
        final String LOAN_DISBURSEMENT_DATE_3 = dateFormat.format(todaysDate.getTime());

        List<HashMap> tranches = new ArrayList<>();
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_1, "5000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_2, "3000"));
        tranches.add(createTrancheDetail(LOAN_DISBURSEMENT_DATE_3, "2000"));

        final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "10000",
                LoanApplicationTestBuilder.RBI_INDIA_STRATEGY, tranches, LOAN_DISBURSEMENT_DATE_1);
        Assert.assertNotNull(loanID);

        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        List<Map<String, Object>> expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "823.77", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1422.06", "33.12", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1925.17", "35.79", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1934.06", "26.9", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1942.98", "17.98", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1951.96", "9.01", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan(LOAN_DISBURSEMENT_DATE_1, loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "3000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "823.77", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "1422.06", "33.12", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1925.17", "35.79", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1934.06", "26.9", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1942.98", "17.98", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1951.96", "9.01", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        // DISBURSE first Tranche
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan(LOAN_DISBURSEMENT_DATE_1, loanID, "5000");
        System.out.println("DISBURSE " + loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        Map loanDetails = this.loanTransactionHelper.getLoanRepaymentScheduleWithOriginalSchedule(this.requestSpec, this.responseSpec,
                loanID);

        loanSchedule = (ArrayList<HashMap>) ((Map) loanDetails.get("repaymentSchedule")).get("periods");
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "823.77", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "823.77", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "831.38", "15.47", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "835.21", "11.64", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "839.07", "7.78", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "846.8", "3.91", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = (ArrayList<HashMap>) ((Map) loanDetails.get("originalSchedule")).get("periods");
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "823.77", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "827.58", "19.27", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "831.39", "15.46", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "835.23", "11.62", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "839.09", "7.76", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "842.94", "3.89", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedulePreview(this.requestSpec, this.responseSpec, loanID);
        expectedvalues = new ArrayList<>();
        todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        addRepaymentValues(expectedvalues, todaysDate, -2, false, null, null, null, null, "5000.0");
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "823.77", "23.08", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, null, null, null, null, "2000.0");
        addRepaymentValues(expectedvalues, todaysDate, 0, false, "827.58", "19.27", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1327.94", "24.69", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1334.07", "18.56", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1340.23", "12.4", "0.0", "0.0", null);
        addRepaymentValues(expectedvalues, todaysDate, 1, false, "1346.41", "6.21", "0.0", "0.0", null);
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
    }

    private void addRepaymentValues(List<Map<String, Object>> expectedvalues, Calendar todaysDate, int addPeriod, boolean isAddDays,
            String principalDue, String interestDue, String feeChargesDue, String penaltyChargesDue, String principalDisbursed) {
        Map<String, Object> values = new HashMap<>(3);
        if (isAddDays) {
            values.put("dueDate", getDateAsArray(todaysDate, addPeriod));
        } else {
            values.put("dueDate", getDateAsArray(todaysDate, addPeriod * 7));
        }
        System.out.println("Updated date " + values.get("dueDate"));
        if (principalDisbursed == null) {
            values.put("principalDue", principalDue);
            values.put("interestDue", interestDue);
            values.put("feeChargesDue", feeChargesDue);
            values.put("penaltyChargesDue", penaltyChargesDue);
        } else {
            values.put("principalDisbursed", principalDisbursed);
        }
        expectedvalues.add(values);
    }

    private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule, List<Map<String, Object>> expectedvalues) {
        int index = 0;
        verifyLoanRepaymentSchedule(loanSchedule, expectedvalues, index);

    }

    private List getDateAsArray(Calendar todaysDate, int addPeriod) {
        return getDateAsArray(todaysDate, addPeriod, Calendar.DAY_OF_MONTH);
    }

    private List getDateAsArray(Calendar todaysDate, int addvalue, int type) {
        todaysDate.add(type, addvalue);
        return new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH) + 1,
                todaysDate.get(Calendar.DAY_OF_MONTH)));
    }

    private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule, List<Map<String, Object>> expectedvalues, int index) {
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");
        for (Map<String, Object> values : expectedvalues) {
            assertEquals("Checking for Due Date for  installment " + index, values.get("dueDate"), loanSchedule.get(index).get("dueDate"));
            if (values.containsKey("principalDisbursed")) {
                validateNumberForEqualWithMsg("Checking for Principal Disbursed  " + index,
                        String.valueOf(values.get("principalDisbursed")), String.valueOf(loanSchedule.get(index).get("principalDisbursed")));
            } else {
                validateNumberForEqualWithMsg("Checking for Principal Due for installment " + index,
                        String.valueOf(values.get("principalDue")), String.valueOf(loanSchedule.get(index).get("principalDue")));
                validateNumberForEqualWithMsg("Checking for Interest Due for installment " + index,
                        String.valueOf(values.get("interestDue")), String.valueOf(loanSchedule.get(index).get("interestDue")));
                validateNumberForEqualWithMsg("Checking for Fee charge Due for installment " + index,
                        String.valueOf(values.get("feeChargesDue")), String.valueOf(loanSchedule.get(index).get("feeChargesDue")));
                validateNumberForEqualWithMsg("Checking for Penalty charge Due for installment " + index,
                        String.valueOf(values.get("penaltyChargesDue")), String.valueOf(loanSchedule.get(index).get("penaltyChargesDue")));
            }

            index++;
        }
    }

    public void validateNumberForEqualWithMsg(String msg, String val, String val2) {
        Assert.assertTrue(msg + "expected " + val + " but was " + val2, new Float(val).compareTo(new Float(val2)) == 0);
    }

    private Integer createLoanProductwithFutureDisbursements(final String inMultiplesOf, final String digitsAfterDecimal,
            final String repaymentStrategy, final boolean enableInterestRecalculation, boolean isEmiBasedOnDisbursements) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");

        Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
        todaysDate.add(Calendar.DAY_OF_MONTH, -14);

        LoanProductTestBuilder builder = new LoanProductTestBuilder()
        //
                .withPrincipal("5000")
                //
                .withNumberOfRepayments("6")
                //
                .withRepaymentAfterEvery("1")
                //
                .withRepaymentTypeAsWeek()
                //
                .withinterestRatePerPeriod("24")
                //
                .withInterestRateFrequencyTypeAsYear()
                //
                .withAmortizationTypeAsEqualInstallments()
                //
                .withInterestTypeAsDecliningBalance()
                //
                .withTranches(true)
                //
                .withMoratorium("0", "0")
                // .withAccounting(accountingRule, accounts)
                .currencyDetails(digitsAfterDecimal, inMultiplesOf)
                //
                .withEmiBasedOnDisbursements(isEmiBasedOnDisbursements)
                .withRepaymentStrategy(repaymentStrategy).withInterestCalculationPeriodTypeAsRepaymentPeriod(true);

        if (enableInterestRecalculation) {
            builder.withInterestRecalculationRestFrequencyDetails(LoanProductTestBuilder.RECALCULATION_FREQUENCY_TYPE_DAILY, "1", null,
                    null).withInterestRecalculation(true);
        }

        final String loanProductJSON = builder.build(null);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private HashMap createTrancheDetail(final String date, final String amount) {
        HashMap detail = new HashMap();
        detail.put("expectedDisbursementDate", date);
        detail.put("principal", amount);

        return detail;
    }

    @SuppressWarnings("rawtypes")
    private Integer applyForLoanApplicationWithTranchesWithFutureDisbursements(final Integer clientID, final Integer loanProductID,
            List<HashMap> charges, final String savingsId, String principal, final String repaymentStrategy, List<HashMap> tranches,
            final String FIRST_LOAN_DISBURSEMENT_DATE) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");

        LoanApplicationTestBuilder builder = new LoanApplicationTestBuilder()
        //
                .withPrincipal(principal)
                //
                .withLoanTermFrequency("6")
                //
                .withLoanTermFrequencyAsWeeks()
                //
                .withNumberOfRepayments("6")
                //
                .withRepaymentEveryAfter("1")
                //
                .withRepaymentFrequencyTypeAsWeeks()
                //
                .withInterestRatePerPeriod("24")
                //
                .withAmortizationTypeAsEqualInstallments()
                //
                .withInterestTypeAsDecliningBalance()
                //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod()
                //
                .withExpectedDisbursementDate(FIRST_LOAN_DISBURSEMENT_DATE)
                //
                .withTranches(tranches)
                //
                .withSubmittedOnDate(FIRST_LOAN_DISBURSEMENT_DATE)
                //
                .withwithRepaymentStrategy(repaymentStrategy).withFixedEmiAmount(null).withCharges(charges);
        final String loanApplicationJSON = builder.build(clientID.toString(), loanProductID.toString(), savingsId);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    public void validateNumberForEqual(String val, String val2) {
        Assert.assertTrue(new Float(val).compareTo(new Float(val2)) == 0);
    }

    public static Integer createClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec) {
        return createClient(requestSpec, responseSpec, "04 March 2015");
    }

    public static Integer createClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String activationDate) {
        return createClient(requestSpec, responseSpec, activationDate, "1");
    }

    public static Integer createClient(final RequestSpecification requestSpec, final ResponseSpecification responseSpec,
            final String activationDate, final String officeId) {
        System.out.println("---------------------------------CREATING A CLIENT---------------------------------------------");
        return Utils.performServerPost(requestSpec, responseSpec, CREATE_CLIENT_URL, getTestClientAsJSON(activationDate, officeId),
                "clientId");
    }

    public static String getTestClientAsJSON(final String dateOfJoining, final String officeId) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("officeId", officeId);
        map.put("firstname", Utils.randomNameGenerator("Client_FirstName_", 5));
        map.put("lastname", Utils.randomNameGenerator("Client_LastName_", 4));
        map.put("externalId", randomIDGenerator("ID_", 7));
        map.put("dateFormat", DATE_FORMAT);
        map.put("locale", "en");
        map.put("active", "true");
        map.put("activationDate", dateOfJoining);
        System.out.println("map : " + map);
        return new Gson().toJson(map);
    }

    private static String randomIDGenerator(final String prefix, final int lenOfRandomSuffix) {
        return Utils.randomStringGenerator(prefix, lenOfRandomSuffix, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
}