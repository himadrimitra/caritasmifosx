package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.fineract.integrationtests.common.CenterHelper;
import org.apache.fineract.integrationtests.common.ClientHelper;
import org.apache.fineract.integrationtests.common.GroupHelper;
import org.apache.fineract.integrationtests.common.HolidayHelper;
import org.apache.fineract.integrationtests.common.Utils;
import org.apache.fineract.integrationtests.common.WorkingDaysHelper;
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.organisation.StaffHelper;
import org.apache.fineract.integrationtests.common.recurringdeposit.RecurringDepositAccountHelper;
import org.apache.fineract.integrationtests.common.recurringdeposit.RecurringDepositAccountStatusChecker;
import org.apache.fineract.integrationtests.common.recurringdeposit.RecurringDepositProductHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class RDMeetingChangeTest {
	
     	
    public static final String WHOLE_TERM = "1";
    private static final String NONE = "1";
    private static final String CASH_BASED = "2";
	private ResponseSpecification responseSpec;
    private RequestSpecification requestSpec;
	private RecurringDepositAccountHelper recurringDepositAccountHelper;
	private HolidayHelper holidayHelper;
	
    @Before
    public void setup() {
        Utils.initializeRESTAssured();
        this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        this.requestSpec.header("Authorization", "Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
        this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    
    @SuppressWarnings("rawtypes")
	@Test
    public void testSavingsAccount() {
        this.recurringDepositAccountHelper = new RecurringDepositAccountHelper(this.requestSpec, this.responseSpec);
        
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);

        Calendar todaysDate = Calendar.getInstance();
        todaysDate.set(Calendar.DAY_OF_MONTH,1);
        final String VALID_FROM = dateFormat.format(todaysDate.getTime());
        todaysDate.add(Calendar.YEAR, 1);
        final String VALID_TO = dateFormat.format(todaysDate.getTime());

        todaysDate = Calendar.getInstance();
        todaysDate.set(Calendar.DAY_OF_MONTH,1);
        final String SUBMITTED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String APPROVED_ON_DATE = dateFormat.format(todaysDate.getTime());
        final String ACTIVATION_DATE = dateFormat.format(todaysDate.getTime());

        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        Integer centerID = CenterHelper.createCenter(this.requestSpec, this.responseSpec,"29 December 2014");
        Assert.assertNotNull(centerID);
        Integer staffId = StaffHelper.createStaff(this.requestSpec, this.responseSpec);
        System.out.println("--------------creating first staff with id-------------" + staffId);
        Assert.assertNotNull(staffId);
        
        Integer groupID = GroupHelper.createGroup(this.requestSpec, this.responseSpec,"29 December 2014");
        Assert.assertNotNull(groupID);
        GroupHelper.verifyGroupActivatedOnServer(this.requestSpec, this.responseSpec, groupID, true);
        
        int[] groupArray = {groupID};
        int[] associateResponse = CenterHelper.associateGroups( centerID, groupArray ,this.requestSpec, this.responseSpec);
        Assert.assertArrayEquals(associateResponse, groupArray);
        
        groupID = GroupHelper.associateClient(this.requestSpec, this.responseSpec, groupID.toString(), clientID.toString());
        GroupHelper.verifyGroupMembers(this.requestSpec, this.responseSpec, groupID, clientID);
        
       
        Integer calendarId = CenterHelper.attachCenterMeeting(requestSpec, responseSpec, "3", "1", SUBMITTED_ON_DATE, centerID);
        final String accountingRule = NONE;
        final String EXPECTED_FIRST_DEPOSIT_ON_DATE = null;

        Integer recurringDepositProductId = createRecurringDepositProduct(VALID_FROM, VALID_TO, accountingRule);
        Assert.assertNotNull(recurringDepositProductId);

        Integer recurringDepositAccountId = applyForRecurringDepositApplication(clientID.toString(), recurringDepositProductId.toString(),
                VALID_FROM, VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, EXPECTED_FIRST_DEPOSIT_ON_DATE);
        Assert.assertNotNull(recurringDepositAccountId);

		HashMap recurringDepositAccountStatusHashMap = RecurringDepositAccountStatusChecker
				.getStatusOfRecurringDepositAccount(this.requestSpec, this.responseSpec, recurringDepositAccountId.toString());
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsPending(recurringDepositAccountStatusHashMap);
		
		

		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.approveRecurringDeposit(recurringDepositAccountId, APPROVED_ON_DATE);
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsApproved(recurringDepositAccountStatusHashMap);

	        /***
	         * Activate the RD Account and verify whether account is activated
	         */
		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.activateRecurringDeposit(recurringDepositAccountId, ACTIVATION_DATE);
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsActive(recurringDepositAccountStatusHashMap);
		 HashMap depositSchedule = RecurringDepositAccountHelper.getRecurringDepositTemplate(this.requestSpec, this.responseSpec, recurringDepositAccountId);
		todaysDate = Calendar.getInstance();
		todaysDate.add(Calendar.MONTH, 1);
        todaysDate.set(Calendar.DAY_OF_MONTH,1);
        String currentMeetinDate = dateFormat.format(todaysDate.getTime());
        List beforeMeetingChange = getDateAsArray(todaysDate);
        assertEquals("Checking for Due Date for  installment " , beforeMeetingChange, depositSchedule.get("date"));
        
       
        todaysDate = Calendar.getInstance();
		todaysDate.add(Calendar.MONTH, 1);
        todaysDate.set(Calendar.DAY_OF_MONTH,10);
        List afetrMeetingChange = getDateAsArray(todaysDate);
        String futureMeetingDate = dateFormat.format(todaysDate.getTime());
        
        CenterHelper.updateCenterMeeting(requestSpec, responseSpec, currentMeetinDate, futureMeetingDate, centerID, calendarId);
        depositSchedule = RecurringDepositAccountHelper.getRecurringDepositTemplate(this.requestSpec, this.responseSpec, recurringDepositAccountId);
        assertEquals("Checking for Due Date for  installment " , afetrMeetingChange, depositSchedule.get("date"));
}
    
    @SuppressWarnings("rawtypes")
	@Test
    public void testRDaccountWithHolidays() {
    	this.holidayHelper = new HolidayHelper(this.requestSpec, this.responseSpec);
    	String holidayJson = this.holidayHelper.build("01 February 2016", "01 February 2016", "02 February 2016", false);
    	Integer holidayID = HolidayHelper.createHolidays(this.requestSpec, this.responseSpec, holidayJson);
    	HolidayHelper.activateHolidays(this.requestSpec, this.responseSpec, holidayID.toString());
        this.recurringDepositAccountHelper = new RecurringDepositAccountHelper(this.requestSpec, this.responseSpec);
        final String VALID_FROM = "01 January 2016";
        final String VALID_TO = "10 January 2016";
        final String SUBMITTED_ON_DATE = "01 January 2016";
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final String accountingRule = NONE;
        final String EXPECTED_FIRST_DEPOSIT_ON_DATE = "01 January 2016";
        final String APPROVED_ON_DATE = "01 January 2016";
        final String ACTIVATION_DATE = "01 January 2016";

        Integer recurringDepositProductId = createRecurringDepositProduct(VALID_FROM, VALID_TO, accountingRule);
        Assert.assertNotNull(recurringDepositProductId);

        Integer recurringDepositAccountId = applyForRecurringDepositApplicationWithoutCalander(clientID.toString(), recurringDepositProductId.toString(),
                VALID_FROM, VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, EXPECTED_FIRST_DEPOSIT_ON_DATE);
        Assert.assertNotNull(recurringDepositAccountId);

		HashMap recurringDepositAccountStatusHashMap = RecurringDepositAccountStatusChecker
				.getStatusOfRecurringDepositAccount(this.requestSpec, this.responseSpec, recurringDepositAccountId.toString());
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsPending(recurringDepositAccountStatusHashMap);
		
		

		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.approveRecurringDeposit(recurringDepositAccountId, APPROVED_ON_DATE);
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsApproved(recurringDepositAccountStatusHashMap);

	        /***
	         * Activate the RD Account and verify whether account is activated
	         */
		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.activateRecurringDeposit(recurringDepositAccountId, ACTIVATION_DATE);
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsActive(recurringDepositAccountStatusHashMap);
		RecurringDepositAccountHelper.depositIntoRecurringDeposit(requestSpec, responseSpec, recurringDepositAccountId, "2000", "01 January 2016");
		HashMap depositSchedule = RecurringDepositAccountHelper.getRecurringDepositTemplate(this.requestSpec, this.responseSpec, recurringDepositAccountId);
		
        List<Object> beforeMeetingChange = new ArrayList<>();
        beforeMeetingChange.add(2016);
        beforeMeetingChange.add(2);
        beforeMeetingChange.add(2);
        assertEquals("Checking for Due Date for  installment " , beforeMeetingChange, depositSchedule.get("date"));
        HolidayHelper.deleteHolidays(this.requestSpec, this.responseSpec, holidayID.toString());
}
    
    @SuppressWarnings("rawtypes")
 	@Test
     public void testRDaccountWithHolidaysExtendSchedule() {
     	this.holidayHelper = new HolidayHelper(this.requestSpec, this.responseSpec);
     	String holidayJson = this.holidayHelper.build("01 February 2016", "01 February 2016", "02 February 2016", true);
     	Integer holidayID = HolidayHelper.createHolidays(this.requestSpec, this.responseSpec, holidayJson);
     	HolidayHelper.activateHolidays(this.requestSpec, this.responseSpec, holidayID.toString());
         this.recurringDepositAccountHelper = new RecurringDepositAccountHelper(this.requestSpec, this.responseSpec);
         final String VALID_FROM = "01 January 2016";
         final String VALID_TO = "10 January 2016";
         final String SUBMITTED_ON_DATE = "01 January 2016";
         final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
         Assert.assertNotNull(clientID);

         final String accountingRule = NONE;
         final String EXPECTED_FIRST_DEPOSIT_ON_DATE = "01 January 2016";
         final String APPROVED_ON_DATE = "01 January 2016";
         final String ACTIVATION_DATE = "01 January 2016";

         Integer recurringDepositProductId = createRecurringDepositProduct(VALID_FROM, VALID_TO, accountingRule);
         Assert.assertNotNull(recurringDepositProductId);

         Integer recurringDepositAccountId = applyForRecurringDepositApplicationWithoutCalander(clientID.toString(), recurringDepositProductId.toString(),
                 VALID_FROM, VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, EXPECTED_FIRST_DEPOSIT_ON_DATE);
         Assert.assertNotNull(recurringDepositAccountId);

 		HashMap recurringDepositAccountStatusHashMap = RecurringDepositAccountStatusChecker
 				.getStatusOfRecurringDepositAccount(this.requestSpec, this.responseSpec, recurringDepositAccountId.toString());
 		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsPending(recurringDepositAccountStatusHashMap);
 		
 		

 		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.approveRecurringDeposit(recurringDepositAccountId, APPROVED_ON_DATE);
 		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsApproved(recurringDepositAccountStatusHashMap);

 	        /***
 	         * Activate the RD Account and verify whether account is activated
 	         */
 		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.activateRecurringDeposit(recurringDepositAccountId, ACTIVATION_DATE);
 		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsActive(recurringDepositAccountStatusHashMap);
 		RecurringDepositAccountHelper.depositIntoRecurringDeposit(requestSpec, responseSpec, recurringDepositAccountId, "2000", "01 January 2016");
 		RecurringDepositAccountHelper.depositIntoRecurringDeposit(requestSpec, responseSpec, recurringDepositAccountId, "2000", "02 February 2016");
 		HashMap depositSchedule = RecurringDepositAccountHelper.getRecurringDepositTemplate(this.requestSpec, this.responseSpec, recurringDepositAccountId);
 		
         List<Object> beforeMeetingChange = new ArrayList<>();
         beforeMeetingChange.add(2016);
         beforeMeetingChange.add(3);
         beforeMeetingChange.add(2);
         assertEquals("Checking for Due Date for  installment " , beforeMeetingChange, depositSchedule.get("date"));
         HolidayHelper.deleteHolidays(this.requestSpec, this.responseSpec, holidayID.toString());
 }
    
    @SuppressWarnings("rawtypes")
	@Test
    public void testRDaccountWithWorkingDays() {
    	String workingdayJson = WorkingDaysHelper.updateWorkingDaysSetSunDayAsJson();
    	WorkingDaysHelper.updateWorkingDays(requestSpec, responseSpec, workingdayJson);
        this.recurringDepositAccountHelper = new RecurringDepositAccountHelper(this.requestSpec, this.responseSpec);
        final String VALID_FROM = "01 January 2016";
        final String VALID_TO = "10 January 2016";
        final String SUBMITTED_ON_DATE = "01 January 2016";
        final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
        Assert.assertNotNull(clientID);

        final String accountingRule = NONE;
        final String EXPECTED_FIRST_DEPOSIT_ON_DATE = "07 January 2016";
        final String APPROVED_ON_DATE = "01 January 2016";
        final String ACTIVATION_DATE = "01 January 2016";

        Integer recurringDepositProductId = createRecurringDepositProduct(VALID_FROM, VALID_TO, accountingRule);
        Assert.assertNotNull(recurringDepositProductId);

        Integer recurringDepositAccountId = applyForRecurringDepositApplicationWithoutCalander(clientID.toString(), recurringDepositProductId.toString(),
                VALID_FROM, VALID_TO, SUBMITTED_ON_DATE, WHOLE_TERM, EXPECTED_FIRST_DEPOSIT_ON_DATE);
        Assert.assertNotNull(recurringDepositAccountId);

		HashMap recurringDepositAccountStatusHashMap = RecurringDepositAccountStatusChecker
				.getStatusOfRecurringDepositAccount(this.requestSpec, this.responseSpec, recurringDepositAccountId.toString());
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsPending(recurringDepositAccountStatusHashMap);
		
		

		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.approveRecurringDeposit(recurringDepositAccountId, APPROVED_ON_DATE);
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsApproved(recurringDepositAccountStatusHashMap);

	        /***
	         * Activate the RD Account and verify whether account is activated
	         */
		recurringDepositAccountStatusHashMap = this.recurringDepositAccountHelper.activateRecurringDeposit(recurringDepositAccountId, ACTIVATION_DATE);
		RecurringDepositAccountStatusChecker.verifyRecurringDepositIsActive(recurringDepositAccountStatusHashMap);
		RecurringDepositAccountHelper.depositIntoRecurringDeposit(requestSpec, responseSpec, recurringDepositAccountId, "2000", "07 January 2016");
		HashMap depositSchedule = RecurringDepositAccountHelper.getRecurringDepositTemplate(this.requestSpec, this.responseSpec, recurringDepositAccountId);
		
        List<Object> beforeMeetingChange = new ArrayList<>();
        beforeMeetingChange.add(2016);
        beforeMeetingChange.add(2);
        beforeMeetingChange.add(8);
        assertEquals("Checking for Due Date for  installment " , beforeMeetingChange, depositSchedule.get("date"));
        WorkingDaysHelper.updateWorkingDays(requestSpec, responseSpec);
}
    
	@SuppressWarnings("rawtypes")
	private List getDateAsArray(Calendar todaysDate) {
		return new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH) + 1,
				todaysDate.get(Calendar.DAY_OF_MONTH)));
	}

	 private Integer createRecurringDepositProduct(final String validFrom, final String validTo, final String accountingRule,
	            Account... accounts) {
	        System.out.println("------------------------------CREATING NEW RECURRING DEPOSIT PRODUCT ---------------------------------------");
	        RecurringDepositProductHelper recurringDepositProductHelper = new RecurringDepositProductHelper(this.requestSpec, this.responseSpec);
	        if (accountingRule.equals(CASH_BASED)) {
	            recurringDepositProductHelper = recurringDepositProductHelper.withAccountingRuleAsCashBased(accounts);
	        } else if (accountingRule.equals(NONE)) {
	            recurringDepositProductHelper = recurringDepositProductHelper.withAccountingRuleAsNone();
	        }
	        final String recurringDepositProductJSON = recurringDepositProductHelper.withPeriodRangeChart().build(validFrom, validTo);
	        return RecurringDepositProductHelper.createRecurringDepositProduct(recurringDepositProductJSON, requestSpec, responseSpec);
	    }

	 @SuppressWarnings("unused")
	private Integer applyForRecurringDepositApplication(final String clientID, final String productID, final String validFrom,
	            final String validTo, final String submittedOnDate, final String penalInterestType, final String expectedFirstDepositOnDate) {
	        System.out.println("--------------------------------APPLYING FOR RECURRING DEPOSIT ACCOUNT --------------------------------");
	        final String recurringDepositApplicationJSON = new RecurringDepositAccountHelper(this.requestSpec, this.responseSpec)
	                .withSubmittedOnDate(submittedOnDate)
	                .withCalanderInherited()
	                .withExpectedFirstDepositOnDate(expectedFirstDepositOnDate)
	                .build(clientID, productID, penalInterestType);
	        return RecurringDepositAccountHelper.applyRecurringDepositApplication(recurringDepositApplicationJSON, this.requestSpec,
	                this.responseSpec);
	    }
	 
	 @SuppressWarnings("unused")
		private Integer applyForRecurringDepositApplicationWithoutCalander(final String clientID, final String productID, final String validFrom,
		            final String validTo, final String submittedOnDate, final String penalInterestType, final String expectedFirstDepositOnDate) {
		        System.out.println("--------------------------------APPLYING FOR RECURRING DEPOSIT ACCOUNT --------------------------------");
		        final String recurringDepositApplicationJSON = new RecurringDepositAccountHelper(this.requestSpec, this.responseSpec)
		                .withSubmittedOnDate(submittedOnDate)
		                .withExpectedFirstDepositOnDate(expectedFirstDepositOnDate)
		                .build(clientID, productID, penalInterestType);
		        return RecurringDepositAccountHelper.applyRecurringDepositApplication(recurringDepositApplicationJSON, this.requestSpec,
		                this.responseSpec);
		    }


}
