package com.finflux.integrationtests.organisation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.CommonConstants;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.WorkingDaysHelper;
import org.apache.fineract.integrationtests.common.loans.LoanApplicationTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanProductTestBuilder;
import org.apache.fineract.integrationtests.common.loans.LoanTransactionHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.finflux.integrationtests.client.builder.ClientTestBuilder;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class WorkingDaysIntegrationTest {

    private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
    private ResponseSpecification generalResponseSpec;
    private LoanTransactionHelper loanTransactionHelper;
    private ClientHelper clientHelper;

    private Integer officeId;
    private final String activationDate = "04 March 2016";
    private Integer clientId;
    private Integer loanProductId;
    private Integer loanProductIDIR;

    private boolean isInitialized = false ;
    
    @Before
    public void setUp() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
        this.generalResponseSpec = new ResponseSpecBuilder().build();
        this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
        this.clientHelper = new ClientHelper(requestSpec, responseSpec);
        if(!isInitialized) {
            initializeRequiredEntities();
            this.isInitialized = true ;
        }
    }

    private void initializeRequiredEntities() {
        this.officeId = 1;
        this.clientId = this.clientHelper
                .getClientId(new ClientTestBuilder().withOffice(this.officeId).withActivateOn(activationDate).build());
        loanProductId = createLoanProduct("1", "2", LoanProductTestBuilder.DEFAULT_STRATEGY);
        loanProductIDIR = createLoanProductWithIR("1", "2", LoanProductTestBuilder.DEFAULT_STRATEGY);

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @After
    public void tearDown() {
        WorkingDaysTestBuilder builderReset = new WorkingDaysTestBuilder()
                .withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU").withRepaymentRescheduleType("2");

        HashMap<String, Object> workingDays = WorkingDaysHelper.getAllWorkingDays(requestSpec, generalResponseSpec);
        List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) workingDays.get("advancedRescheduleDetail");

        for (HashMap<String, Object> detail : list) {
            builderReset.deleteAdvancedDetail(detail.get("id").toString());
        }
        HashMap responseReset = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builderReset.build());
        Assert.assertNotNull(responseReset.get("resourceId"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkingDays() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU")
                .withRepaymentRescheduleType("2");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 4, 1)), loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 7, 1)), loanSchedule.get(4).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkingDaysWithPaymentsDueOnNxtWorkingDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SU")
                .withRepaymentRescheduleType("2");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 4, 2)), loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 7, 2)), loanSchedule.get(4).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkingDaysWithPaymentsDueOnSameDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SU")
                .withRepaymentRescheduleType("1");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 4, 1)), loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 7, 1)), loanSchedule.get(4).get("dueDate"));

        WorkingDaysTestBuilder builderReset = new WorkingDaysTestBuilder()
                .withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU").withRepaymentRescheduleType("2");
        HashMap responseReset = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builderReset.build());
        Assert.assertNotNull(responseReset.get("resourceId"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkingDaysWithPaymentsDueOnPreviousWorkingDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SU")
                .withRepaymentRescheduleType("4");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 3, 31)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 6, 30)),
                loanSchedule.get(4).get("dueDate"));

        WorkingDaysTestBuilder builderReset = new WorkingDaysTestBuilder()
                .withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU").withRepaymentRescheduleType("2");
        HashMap responseReset = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builderReset.build());
        Assert.assertNotNull(responseReset.get("resourceId"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkingDaysWithPaymentsDueOnNxtRepaymentDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SU")
                .withRepaymentRescheduleType("3");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 8, 1)), loanSchedule.get(4).get("dueDate"));

        WorkingDaysTestBuilder builderReset = new WorkingDaysTestBuilder()
                .withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR,SA,SU").withRepaymentRescheduleType("2");
        HashMap responseReset = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builderReset.build());
        Assert.assertNotNull(responseReset.get("resourceId"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAdvancedWorkingDaysWithPaymentsDueNxtWorkingWeekDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")
                .withRepaymentRescheduleType("4");
        builder.addAdvancedDetail("SU", "6", "TU");
        builder.addAdvancedDetail("SA", "4", null);
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 3, 31)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 6, 30)),
                loanSchedule.get(4).get("dueDate"));
        assertEquals("Checking for Due Date for 5th Month", new ArrayList<>(Arrays.asList(2017, 8, 1)), loanSchedule.get(5).get("dueDate"));
        assertEquals("Checking for Due Date for 6th Month", new ArrayList<>(Arrays.asList(2017, 9, 1)), loanSchedule.get(6).get("dueDate"));
        assertEquals("Checking for Due Date for 7th Month", new ArrayList<>(Arrays.asList(2017, 10, 3)),
                loanSchedule.get(7).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAdvancedWorkingDaysWithPaymentsDuePreviousWorkingWeekDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")
                .withRepaymentRescheduleType("4");
        builder.addAdvancedDetail("SU", "3", null);
        builder.addAdvancedDetail("SA", "7", "WE");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 3, 29)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 6, 28)),
                loanSchedule.get(4).get("dueDate"));
        assertEquals("Checking for Due Date for 5th Month", new ArrayList<>(Arrays.asList(2017, 8, 1)), loanSchedule.get(5).get("dueDate"));
        assertEquals("Checking for Due Date for 6th Month", new ArrayList<>(Arrays.asList(2017, 9, 1)), loanSchedule.get(6).get("dueDate"));
        assertEquals("Checking for Due Date for 7th Month", new ArrayList<>(Arrays.asList(2017, 11, 1)),
                loanSchedule.get(7).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAdvancedWorkingDaysWithPaymentsDuePreviousAndNxtWorkingWeekDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")
                .withRepaymentRescheduleType("4");
        builder.addAdvancedDetail("SA", "6", "MO");
        builder.addAdvancedDetail("SU", "7", "WE");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 4, 3)), loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 7, 3)), loanSchedule.get(4).get("dueDate"));
        assertEquals("Checking for Due Date for 5th Month", new ArrayList<>(Arrays.asList(2017, 8, 1)), loanSchedule.get(5).get("dueDate"));
        assertEquals("Checking for Due Date for 6th Month", new ArrayList<>(Arrays.asList(2017, 9, 1)), loanSchedule.get(6).get("dueDate"));
        assertEquals("Checking for Due Date for 7th Month", new ArrayList<>(Arrays.asList(2017, 9, 27)),
                loanSchedule.get(7).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testWorkingDaysWithPaymentsDueOnSameDayAndPreviousWorkingDay() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")
                .withRepaymentRescheduleType("4");
        builder.addAdvancedDetail("SA", "1", null);
        builder.addAdvancedDetail("SU", "4", null);
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductId, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 4, 1)), loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 7, 1)), loanSchedule.get(4).get("dueDate"));
        assertEquals("Checking for Due Date for 5th Month", new ArrayList<>(Arrays.asList(2017, 8, 1)), loanSchedule.get(5).get("dueDate"));
        assertEquals("Checking for Due Date for 6th Month", new ArrayList<>(Arrays.asList(2017, 9, 1)), loanSchedule.get(6).get("dueDate"));
        assertEquals("Checking for Due Date for 7th Month", new ArrayList<>(Arrays.asList(2017, 9, 29)),
                loanSchedule.get(7).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAdvancedWorkingDaysWithPaymentsDuePreviousWorkingWeekDayWithIRProduct() {
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")
                .withRepaymentRescheduleType("4");
        builder.addAdvancedDetail("SU", "3", null);
        builder.addAdvancedDetail("SA", "7", "WE");
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        Assert.assertNotNull(response.get("resourceId"));
        final Integer loanID = applyForLoanApplication(clientId, loanProductIDIR, null, null, "8000.00");
        Assert.assertNotNull(loanID);

        final ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec,
                loanID);
        System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");

        assertEquals("Checking for Due Date for 1st Month", new ArrayList<>(Arrays.asList(2017, 3, 29)),
                loanSchedule.get(1).get("dueDate"));
        assertEquals("Checking for Due Date for 2nd Month", new ArrayList<>(Arrays.asList(2017, 5, 1)), loanSchedule.get(2).get("dueDate"));
        assertEquals("Checking for Due Date for 3rd Month", new ArrayList<>(Arrays.asList(2017, 6, 1)), loanSchedule.get(3).get("dueDate"));
        assertEquals("Checking for Due Date for 4th Month", new ArrayList<>(Arrays.asList(2017, 6, 28)),
                loanSchedule.get(4).get("dueDate"));
        assertEquals("Checking for Due Date for 5th Month", new ArrayList<>(Arrays.asList(2017, 8, 1)), loanSchedule.get(5).get("dueDate"));
        assertEquals("Checking for Due Date for 6th Month", new ArrayList<>(Arrays.asList(2017, 9, 1)), loanSchedule.get(6).get("dueDate"));
        assertEquals("Checking for Due Date for 7th Month", new ArrayList<>(Arrays.asList(2017, 11, 1)),
                loanSchedule.get(7).get("dueDate"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testAdvancedWorkingDaysWithPaymentsDueNxtWorkingWeekDay_dupliacate_advanced_detail() {
        ResponseSpecification responseSpec = new ResponseSpecBuilder().expectStatusCode(400).build();
        WorkingDaysTestBuilder builder = new WorkingDaysTestBuilder().withRecurrnce("FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,TU,WE,TH,FR")
                .withRepaymentRescheduleType("4");
        builder.addAdvancedDetail("SU", "6", "TU");
        builder.addAdvancedDetail("SU", "7", "TU");
        builder.addAdvancedDetail("SA", "4", null);
        HashMap response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        ArrayList<HashMap> errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.fromWeekDay.can.not.be.duplicated",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail("SU", "3", "TU");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.toWeekDay.not.supported.for.selected.repaymentRescheduleType",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail(null, "7", "TU");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.fromWeekDay.cannot.be.blank",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail(null, "7", null);
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.fromWeekDay.cannot.be.blank",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.toWeekDay.cannot.be.blank",
                errorData.get(1).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail("SU", null, "TU");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.repaymentRescheduleType.cannot.be.blank",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail("SU", "7", "TR");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.toWeekDay.is.not.one.of.expected.enumerations",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail(null, null, null);
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.repaymentRescheduleType.cannot.be.blank",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.fromWeekDay.cannot.be.blank",
                errorData.get(1).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail("TU", "7", "MO");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.fromWeekDay.can.not.be.working.day",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail("SU", "7", "SA");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.toWeekDay.must.be.working.day",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));

        builder.clearAdvancedDetail();
        builder.addAdvancedDetail("SU", "9", "TU");
        response = (HashMap) WorkingDaysHelper.updateAdvancedWorkingDays(requestSpec, responseSpec, builder.build());
        errorData = (ArrayList<HashMap>) response.get(CommonConstants.RESPONSE_ERROR);
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.repaymentRescheduleType.is.not.within.expected.range",
                errorData.get(0).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
        assertEquals("validation.msg.workingdays.advancedRescheduleDetail.toWeekDay.not.supported.for.selected.repaymentRescheduleType",
                errorData.get(1).get(CommonConstants.RESPONSE_ERROR_MESSAGE_CODE));
    }

    @SuppressWarnings("rawtypes")
    private Integer applyForLoanApplication(final Integer clientID, final Integer loanProductID, List<HashMap> charges,
            final String savingsId, String principal) {
        System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
        final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                .withPrincipal(principal) //
                .withLoanTermFrequency("7") //
                .withLoanTermFrequencyAsMonths() //
                .withNumberOfRepayments("7") //
                .withRepaymentEveryAfter("1") //
                .withRepaymentFrequencyTypeAsMonths() //
                .withInterestRatePerPeriod("2") //
                .withAmortizationTypeAsEqualInstallments() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                .withExpectedDisbursementDate("01 March 2017") //
                .withSubmittedOnDate("01 March 2017") //
                .withCharges(charges).build(clientID.toString(), loanProductID.toString(), savingsId);
        return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
    }

    private Integer createLoanProduct(final String inMultiplesOf, final String digitsAfterDecimal, final String repaymentStrategy) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final String loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("8000.00") //
                .withNumberOfRepayments("4") //
                .withRepaymentAfterEvery("1") //
                .withRepaymentTypeAsMonth() //
                .withinterestRatePerPeriod("2") //
                .withInterestRateFrequencyTypeAsMonths() //
                .withRepaymentStrategy(repaymentStrategy) //
                .withAmortizationTypeAsEqualPrincipalPayment() //
                .withInterestTypeAsDecliningBalance() //
                .currencyDetails(digitsAfterDecimal, inMultiplesOf).build(null);
        return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
    }

    private Integer createLoanProductWithIR(final String inMultiplesOf, final String digitsAfterDecimal, final String repaymentStrategy) {
        System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
        final LoanProductTestBuilder loanProductJSON = new LoanProductTestBuilder() //
                .withPrincipal("8000.00") //
                .withNumberOfRepayments("4") //
                .withRepaymentAfterEvery("1") //
                .withRepaymentTypeAsMonth() //
                .withinterestRatePerPeriod("2") //
                .withInterestRateFrequencyTypeAsMonths() //
                .withRepaymentStrategy(repaymentStrategy) //
                .withAmortizationTypeAsEqualPrincipalPayment() //
                .withInterestTypeAsDecliningBalance() //
                .withInterestCalculationPeriodTypeAsDays().withInterestRecalculation(true)
                .currencyDetails(digitsAfterDecimal, inMultiplesOf);
        final String IRloanProductJSON = loanProductJSON.build(null);
        return this.loanTransactionHelper.getLoanProductId(IRloanProductJSON);
    }
}
