package org.apache.fineract.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.ClientHelper;
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

public class AdjustFirstInstallmentTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private LoanTransactionHelper loanTransactionHelper;

    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstDecliningDailyTest_True() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_DECLINING_DAILY
         */
        final Boolean isadjustFirstEMIAmount = true;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;
        final Boolean isIntersetTypeFlat = false;
        final Boolean isDailyInterestMethod = true;

        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("6851", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("6851"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("6837.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6837.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6445.69", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6555.56", String.valueOf(thirdInstallment.get("principalDue")));

        validateNumberForEqual("391.31", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("281.44", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("6837.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("6837.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("6837.00", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6699.34", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("137.66", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("6837.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstDecliningDailyTest_False() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_DECLINING_DAILY
         */
        final Boolean isadjustFirstEMIAmount = false;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;
        final Boolean isIntersetTypeFlat = false;
        final Boolean isDailyInterestMethod = true;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("6837.00", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("6837.00"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("6837.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6837.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("391.6", String.valueOf(secondInstallment.get("interestDue")));

        validateNumberForEqual("6445.40", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6555.25", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("281.75", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("6837.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("6837.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("6852.37", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6714.40", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("137.97", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("6852.37"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstDecliningSameasRepaymentTest_False() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_DECLINING_SAMEASREPAYMENT
         */
        final Boolean isadjustFirstEMIAmount = false;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;

        final Boolean isIntersetTypeFlat = false;
        final Boolean isDailyInterestMethod = false;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("6842.00", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("6842.00"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("6842.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6842.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6431.59", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6565.58", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("410.41", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("276.42", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("6842.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("6842.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("6842.14", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6702.5", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("139.64", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("6842.14"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstDecliningSameasRepaymentTest_True() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_DECLINING_SAMEASREPAYMENT
         */
        final Boolean isadjustFirstEMIAmount = true;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;

        final Boolean isIntersetTypeFlat = false;
        final Boolean isDailyInterestMethod = false;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("6842.00", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("6842.00"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("6842.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6842.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("410.41", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("276.42", String.valueOf(thirdInstallment.get("interestDue")));

        validateNumberForEqual("6431.59", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6565.58", String.valueOf(thirdInstallment.get("principalDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("6842.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("6842.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("6842.00", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6702.37", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("139.63", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("6842.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstFlatDailyTest_False() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_FLAT_DAILY
         */
        final Boolean isadjustFirstEMIAmount = false;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;

        final Boolean isIntersetTypeFlat = true;
        final Boolean isDailyInterestMethod = true;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        @SuppressWarnings("rawtypes")
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("7039.00", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("7039.00"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("7039.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("7039.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("538.7", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("538.7", String.valueOf(thirdInstallment.get("interestDue")));

        validateNumberForEqual("6500.30", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6500.30", String.valueOf(thirdInstallment.get("principalDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("7039.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("7039.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("7037.79", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6499.10", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("538.69", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("7037.79"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstFlatDailyTest_True() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_FLAT_DAILY
         */
        final Boolean isadjustFirstEMIAmount = true;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;

        final Boolean isIntersetTypeFlat = true;
        final Boolean isDailyInterestMethod = true;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("7038", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("7038"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("7039", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("7039", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6500.25", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6500.25", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("538.75", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("538.75", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("7039"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("7039"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("7039", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6500.25", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("538.75", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("7039"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstFlatSameasRepaymentTest_False() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_FLAT_SAMEASREPAYMENT
         */
        final Boolean isadjustFirstEMIAmount = false;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;

        final Boolean isIntersetTypeFlat = true;
        final Boolean isDailyInterestMethod = false;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("7042", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("7042"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("7042", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("7042", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6500.33", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6500.33", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("541.67", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("541.67", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("7042"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("7042"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("7040.67", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6499.01", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("541.66", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("7040.67"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void AdjustFirstFlatSameasRepaymentTest_True() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with ADJUST_FIRST_FLAT_SAMEASREPAYMENT
         */
        final Boolean isadjustFirstEMIAmount = true;
        final Boolean canDefineInstallmentAmount = false;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = null;

        final Boolean isIntersetTypeFlat = true;
        final Boolean isDailyInterestMethod = false;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("7041", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("7041"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("7042", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("7042", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6500.25", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6500.25", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("541.75", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("541.75", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("7042"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("7042"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("7042", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6500.25", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("541.75", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("7042"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void FixedInstallmentDecliningSameasRepaymentTest_False() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with FixedInstallmentDecliningSameasRepaymentTest
         */
        final Boolean isadjustFirstEMIAmount = false;
        final Boolean canDefineInstallmentAmount = true;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = "6840";

        final Boolean isIntersetTypeFlat = false;
        final Boolean isDailyInterestMethod = false;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("6840.00", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("6840.00"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("6840.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6840.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6429.55", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6563.50", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("410.45", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("276.50", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("6840.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("6840.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("6848.38", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6708.62", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("139.76", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("6848.38"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void FixedInstallmentDecliningSameasRepaymentTest_True() {
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);

        /***
         * Create loan product with FixedInstallmentDecliningSameasRepaymentTest
         */
        final Boolean isadjustFirstEMIAmount = true;
        final Boolean canDefineInstallmentAmount = true;
        final Integer loanProductID = createLoanProduct("1", "2", 1, 1, isadjustFirstEMIAmount, canDefineInstallmentAmount,
                LoanProductTestBuilder.DEFAULT_STRATEGY);
        Assert.assertNotNull(loanProductID);

        /***
         * Apply for loan application and verify loan status
         */
        final String fixedEmiAmount = "6840";

        final Boolean isIntersetTypeFlat = false;
        final Boolean isDailyInterestMethod = false;
        final String savingsId = null;
        final String principal = "26,000.00";
        final Integer loanID = applyForLoanApplicationWithPaymentStrategy(clientID, loanProductID, null, savingsId, principal,
                isDailyInterestMethod, isIntersetTypeFlat, fixedEmiAmount);
        Assert.assertNotNull(loanID);
        HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

        System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.approveLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
        LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

        System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
        loanStatusHashMap = this.loanTransactionHelper.disburseLoan("01 January 2016", loanID);
        LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

        ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        HashMap firstInstallment = loanSchedule.get(1);

        validateNumberForEqual("6848.00", String.valueOf(firstInstallment.get("totalDueForPeriod")));

        /***
         * Make payment for installment #1
         */
        this.loanTransactionHelper.makeRepayment("01 February 2016", Float.valueOf("6848.00"), loanID);

        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        firstInstallment = loanSchedule.get(1);
        validateNumberForEqual("0.00", String.valueOf(firstInstallment.get("totalOutstandingForPeriod")));

        HashMap secondInstallment = loanSchedule.get(2);
        HashMap thirdInstallment = loanSchedule.get(3);

        validateNumberForEqual("6840.00", String.valueOf(secondInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6840.00", String.valueOf(thirdInstallment.get("totalDueForPeriod")));

        validateNumberForEqual("6429.71", String.valueOf(secondInstallment.get("principalDue")));
        validateNumberForEqual("6563.67", String.valueOf(thirdInstallment.get("principalDue")));
        validateNumberForEqual("410.29", String.valueOf(secondInstallment.get("interestDue")));
        validateNumberForEqual("276.33", String.valueOf(thirdInstallment.get("interestDue")));

        /***
         * Make payment for installment #2
         */
        this.loanTransactionHelper.makeRepayment("01 March 2016", Float.valueOf("6840.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        secondInstallment = loanSchedule.get(2);
        validateNumberForEqual("0.00", String.valueOf(secondInstallment.get("totalOutstandingForPeriod")));

        /***
         * Make payment for installment #3
         */
        this.loanTransactionHelper.makeRepayment("01 April 2016", Float.valueOf("6840.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        thirdInstallment = loanSchedule.get(3);
        validateNumberForEqual("0.00", String.valueOf(thirdInstallment.get("totalOutstandingForPeriod")));

        HashMap fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("6840.00", String.valueOf(fourthInstallment.get("totalDueForPeriod")));
        validateNumberForEqual("6700.41", String.valueOf(fourthInstallment.get("principalDue")));
        validateNumberForEqual("139.59", String.valueOf(fourthInstallment.get("interestDue")));

        /***
         * Make payment for installment #4
         */
        this.loanTransactionHelper.makeRepayment("01 May 2016", Float.valueOf("6840.00"), loanID);
        loanSchedule.clear();
        loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
        fourthInstallment = loanSchedule.get(4);
        validateNumberForEqual("0.00", String.valueOf(fourthInstallment.get("totalOutstandingForPeriod")));

        loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
        LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);

    }

    private Integer createLoanProduct(final String inMultiplesOf, String digitsAfterDecimal, Integer installmentAmountInMultiplesOf,
            Integer adjustedInstallmentInMultiplesOf, Boolean isadjustFirstEMIAmount, Boolean canDefineInstallmentAmount,
            final String repaymentStrategy) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("10000000.00") //
                .withNumberOfRepayments("4") //
                .withRepaymentAfterEvery("1") //
                .withRepaymentTypeAsMonth() //
                .withinterestRatePerPeriod("25") //
                .withInterestRateFrequencyTypeAsYear() //
                .withRepaymentStrategy(repaymentStrategy) //
                .withAmortizationTypeAsEqualPrincipalPayment() //
                .withcanDefineInstallmentAmount(canDefineInstallmentAmount).withInterestTypeAsDecliningBalance() //
                .withinstallmentAmountInMultiplesOfType(installmentAmountInMultiplesOf).withAdjustFirstEMi(isadjustFirstEMIAmount)
                .withadjustedInstallmentInMultiplesOf(adjustedInstallmentInMultiplesOf).currencyDetails(digitsAfterDecimal, inMultiplesOf)
                .build(null);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private Integer applyForLoanApplicationWithPaymentStrategy(final Integer clientID, final Integer loanProductID, List<HashMap> charges,
            final String savingsId, String principal, Boolean isDailyInterestMethod, Boolean isIntersetTypeFlat, String fixedEmiAmount) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");

        LoanApplicationTestBuilder loanApplicationTestBuilder = new LoanApplicationTestBuilder() //
                .withPrincipal(principal) //
                .withLoanTermFrequency("4") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("4") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("25") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsFlatBalance() //
                .withInterestCalculationPeriodTypeAsDays() //
                .withExpectedDisbursementDate("01 January 2016") //
                .withSubmittedOnDate("01 January 2016") //
                .withwithRepaymentStrategy(LoanApplicationTestBuilder.DEFAULT_STRATEGY) //
                .withCharges(charges);

        if (!isDailyInterestMethod) {
            loanApplicationTestBuilder.withInterestCalculationPeriodTypeSameAsRepaymentPeriod();
        }
        if (!isIntersetTypeFlat) {
            loanApplicationTestBuilder.withInterestTypeAsDecliningBalance();
        }
        if (fixedEmiAmount != null) {
            loanApplicationTestBuilder.withFixedEmiAmount(fixedEmiAmount);
        }

        final String loanApplicationJSON = loanApplicationTestBuilder.build(clientID.toString(), loanProductID.toString(), savingsId);

        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    public void validateNumberForEqual(String val, String val2) {
        Assert.assertTrue(new Float(val).compareTo(new Float(val2)) == 0);
    }

}
