package org.apache.fineract.integrationtests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
        public void testClientRepaymnetSchedule_With_FutureDisbursement() {
            this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

            final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
            ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
            final boolean considerFutureDisbursmentsInSchedule = true;
           final Integer loanProductID = createLoanProductwithFutureDisbursements("1","2",considerFutureDisbursmentsInSchedule, LoanProductTestBuilder.RBI_INDIA_STRATEGY);
                   
            Assert.assertNotNull(loanProductID);
            
           

            Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
            todaysDate.add(Calendar.DAY_OF_MONTH, -7);
            final String FIRST_LOAN_DISBURSEMENT_DATE = dateFormat.format(todaysDate.getTime());
            
            todaysDate.add(Calendar.DAY_OF_MONTH, 14);
            final String SECOND_LOAN_DISBURSEMENT_DATE = dateFormat.format(todaysDate.getTime());
            
            
            List<HashMap> tranches = new ArrayList<>();
            tranches.add(createTrancheDetail(FIRST_LOAN_DISBURSEMENT_DATE,"4000"));
            tranches.add(createTrancheDetail(SECOND_LOAN_DISBURSEMENT_DATE, "1000"));

            final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "5000", LoanApplicationTestBuilder.RBI_INDIA_STRATEGY,tranches, FIRST_LOAN_DISBURSEMENT_DATE);
            Assert.assertNotNull(loanID);

            HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
            LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);
           
            System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
            loanStatusHashMap = this.loanTransactionHelper.approveLoan(FIRST_LOAN_DISBURSEMENT_DATE, loanID);
            LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
            LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
            ArrayList<HashMap> loanScheduleAppproval = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
            verifyLoanScheduleAtApproval(loanScheduleAppproval); 
            // DISBURSE first Tranche
            loanStatusHashMap = this.loanTransactionHelper.disburseLoan(FIRST_LOAN_DISBURSEMENT_DATE, loanID, "4000");
            System.out.println("DISBURSE " + loanStatusHashMap);
            LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
                    
            
            ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
            verifyLoanRepaymentScheduleFutureDisbursement(loanSchedule); 
                    
}     
       
        private Integer createLoanProductwithFutureDisbursements(final String inMultiplesOf, final String digitsAfterDecimal,final boolean considerFutureDisbursmentsInSchedule, final String repaymentStrategy) {
            System.out.println("------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
          
            Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
            todaysDate.add(Calendar.DAY_OF_MONTH, -7);
            final String FIRST_LOAN_DISBURSEMENT_DATE = dateFormat.format(todaysDate.getTime());
            
            LoanProductTestBuilder builder = new LoanProductTestBuilder() //
                    .withPrincipal("5000") //
                    .withNumberOfRepayments("6") //
                    .withRepaymentAfterEvery("1") //
                    .withRepaymentTypeAsWeek() //
                    .withinterestRatePerPeriod("24") //
                    .withInterestRateFrequencyTypeAsYear() //
                    .withAmortizationTypeAsEqualInstallments() //
                    .withInterestTypeAsDecliningBalance() //
                    .withTranches(true) //
                    .withMoratorium("0", "0")
                    //.withAccounting(accountingRule, accounts)
                    .currencyDetails(digitsAfterDecimal, inMultiplesOf)//
                    .withRepaymentStrategy(repaymentStrategy)       
                    .withInterestRecalculationRestFrequencyDetails(LoanProductTestBuilder.RECALCULATION_FREQUENCY_TYPE_DAILY, "1", null, null)
                    .withFutureDisbursements(considerFutureDisbursmentsInSchedule)
                    .withInterestRecalculation(true).withInterestCalculationPeriodTypeAsRepaymentPeriod(true);
            
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
        private Integer applyForLoanApplicationWithTranchesWithFutureDisbursements(final Integer clientID, final Integer loanProductID, List<HashMap> charges,
                final String savingsId, String principal,final String repaymentStrategy, List<HashMap> tranches, final String FIRST_LOAN_DISBURSEMENT_DATE ) {
            System.out.println("--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
            
            final String loanApplicationJSON = new LoanApplicationTestBuilder() //
                    .withPrincipal(principal) //
                    .withLoanTermFrequency("6") //
                    .withLoanTermFrequencyAsWeeks() //
                    .withNumberOfRepayments("6") //
                    .withRepaymentEveryAfter("1") //
                    .withRepaymentFrequencyTypeAsWeeks() //
                    .withInterestRatePerPeriod("24") //
                    .withAmortizationTypeAsEqualInstallments() //
                    .withInterestTypeAsDecliningBalance() //
                    .withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
                    .withExpectedDisbursementDate(FIRST_LOAN_DISBURSEMENT_DATE) //
                    .withTranches(tranches) //
                    .withSubmittedOnDate(FIRST_LOAN_DISBURSEMENT_DATE) //
                    .withwithRepaymentStrategy(repaymentStrategy)
                    .withFixedEmiAmount(null)
                    .withCharges(charges).build(clientID.toString(), loanProductID.toString(), savingsId);
            return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
        }
        
        public void validateNumberForEqual(String val, String val2) {
                  Assert.assertTrue(new Float(val).compareTo(new Float(val2)) == 0);
        }
            
        @SuppressWarnings("rawtypes")
        private void verifyLoanScheduleAtApproval(final ArrayList<HashMap> loanScheduleAppproval) {
            Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
            todaysDate.add(Calendar.DAY_OF_MONTH, -7);
            System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");
           assertEquals("Balance of loan for 1st Installment", new Float("4000"), loanScheduleAppproval.get(0).get("principalDisbursed"));
           assertEquals("Disbursement Date for 1st Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(0)
                    .get("dueDate"));
           todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 1st Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(1)
                    .get("dueDate"));
            assertEquals("Principal Due for 1st Installment", new Float("828.39"), loanScheduleAppproval.get(1).get("principalDue"));
            assertEquals("Interest Due for 1st Installment", new Float("18.46"), loanScheduleAppproval.get(1).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Disbursement Date for 2nd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(2)
                    .get("dueDate"));
            assertEquals("Balance of loan for 2nd Installment", new Float("1000"), loanScheduleAppproval.get(2).get("principalDisbursed"));
            assertEquals("Due Date for 2nd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(3)
                    .get("dueDate"));
            assertEquals("Principal Due for 2nd Installment", new Float("832.21"), loanScheduleAppproval.get(3).get("principalDue"));
            assertEquals("Interest Due for 2nd Installment", new Float("14.64"), loanScheduleAppproval.get(3).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Date for 3rd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(4)
                    .get("dueDate"));
            assertEquals("Principal Due for 3rd Installment", new Float("831.44"), loanScheduleAppproval.get(4).get("principalDue"));
            assertEquals("Interest Due for 3rd Installment", new Float("15.41"), loanScheduleAppproval.get(4).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 4th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(5)
                    .get("dueDate"));
            assertEquals("Principal Due for 4th Installment", new Float("835.27"), loanScheduleAppproval.get(5).get("principalDue"));
            assertEquals("Interest Due for 4th Installment", new Float("11.58"), loanScheduleAppproval.get(5).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 5th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(6)
                    .get("dueDate"));
            assertEquals("Principal Due for 5th Installment", new Float("839.13"), loanScheduleAppproval.get(6).get("principalDue"));
            assertEquals("Interest Due for 5th Installment", new Float("7.72"), loanScheduleAppproval.get(6).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 6th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(7)
                    .get("dueDate"));
            assertEquals("Principal Due for 6th Installment", new Float("833.56"), loanScheduleAppproval.get(7).get("principalDue"));
            assertEquals("Interest Due for 6th Installment", new Float("3.85"), loanScheduleAppproval.get(7).get("interestDue"));
            
         }
        
        
        @SuppressWarnings("rawtypes")
        private void verifyLoanRepaymentScheduleFutureDisbursement(final ArrayList<HashMap> loanScheduleAppproval) {
            System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");
            Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
            todaysDate.add(Calendar.DAY_OF_MONTH, -7);
           assertEquals("Balance of loan for 1st Installment", new Float("4000"), loanScheduleAppproval.get(0).get("principalDisbursed"));
           assertEquals("Disbursement Date for 1st Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(0)
                    .get("dueDate"));
           todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 1st Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(1)
                    .get("dueDate"));
            assertEquals("Principal Due for 1st Installment", new Float("828.39"), loanScheduleAppproval.get(1).get("principalDue"));
            assertEquals("Interest Due for 1st Installment", new Float("18.46"), loanScheduleAppproval.get(1).get("interestDue"));
            
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Disbursement Date for 2nd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(2)
                    .get("dueDate"));
            assertEquals("Balance of loan for 2nd Installment", new Float("1000"), loanScheduleAppproval.get(2).get("principalDisbursed"));
            
         
            assertEquals("Due Date for 2nd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(3)
                    .get("dueDate"));
            assertEquals("Principal Due for 2nd Installment", new Float("832.21"), loanScheduleAppproval.get(3).get("principalDue"));
            assertEquals("Interest Due for 2nd Installment", new Float("14.64"), loanScheduleAppproval.get(3).get("interestDue"));
            
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Date for 3rd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(4)
                    .get("dueDate"));
            assertEquals("Principal Due for 3rd Installment", new Float("831.44"), loanScheduleAppproval.get(4).get("principalDue"));
            assertEquals("Interest Due for 3rd Installment", new Float("15.41"), loanScheduleAppproval.get(4).get("interestDue"));
           
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 4th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(5)
                    .get("dueDate"));
            assertEquals("Principal Due for 4th Installment", new Float("835.27"), loanScheduleAppproval.get(5).get("principalDue"));
            assertEquals("Interest Due for 4th Installment", new Float("11.58"), loanScheduleAppproval.get(5).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 5th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(6)
                    .get("dueDate"));
            assertEquals("Principal Due for 5th Installment", new Float("839.13"), loanScheduleAppproval.get(6).get("principalDue"));
            assertEquals("Interest Due for 5th Installment", new Float("7.72"), loanScheduleAppproval.get(6).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 6th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(7)
                    .get("dueDate"));
            assertEquals("Principal Due for 6th Installment", new Float("833.56"), loanScheduleAppproval.get(7).get("principalDue"));
            assertEquals("Interest Due for 6th Installment", new Float("3.85"), loanScheduleAppproval.get(7).get("interestDue"));
            
         }
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Test
        public void testClientRepaymnetSchedule_WithOut_FutureDisbursement() {
            this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);

            final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
            ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
            final boolean considerFutureDisbursmentsInSchedule = false;
           final Integer loanProductID = createLoanProductwithFutureDisbursements("1","2",considerFutureDisbursmentsInSchedule, LoanProductTestBuilder.RBI_INDIA_STRATEGY);
                   
            Assert.assertNotNull(loanProductID);
            
           

            Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
            todaysDate.add(Calendar.DAY_OF_MONTH, -7);
            final String FIRST_LOAN_DISBURSEMENT_DATE = dateFormat.format(todaysDate.getTime());
            
            todaysDate.add(Calendar.DAY_OF_MONTH, 14);
            final String SECOND_LOAN_DISBURSEMENT_DATE = dateFormat.format(todaysDate.getTime());
            
            
            List<HashMap> tranches = new ArrayList<>();
            tranches.add(createTrancheDetail(FIRST_LOAN_DISBURSEMENT_DATE,"4000"));
            tranches.add(createTrancheDetail(SECOND_LOAN_DISBURSEMENT_DATE, "1000"));

            final Integer loanID = applyForLoanApplicationWithTranchesWithFutureDisbursements(clientID, loanProductID, null, null, "5000", LoanApplicationTestBuilder.RBI_INDIA_STRATEGY,tranches,FIRST_LOAN_DISBURSEMENT_DATE);
            Assert.assertNotNull(loanID);

            HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
            LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);
           
            System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
            loanStatusHashMap = this.loanTransactionHelper.approveLoan(FIRST_LOAN_DISBURSEMENT_DATE, loanID);
            LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
            LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);
            ArrayList<HashMap> loanScheduleAppproval = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
            verifyLoanScheduleAtApproval(loanScheduleAppproval); 
            // DISBURSE first Tranche
            loanStatusHashMap = this.loanTransactionHelper.disburseLoan(FIRST_LOAN_DISBURSEMENT_DATE, loanID, "4000");
            System.out.println("DISBURSE " + loanStatusHashMap);
            LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);
                    
            
            ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
            verifyLoanRepaymentScheduleWithoutFutureDisbursement(loanSchedule); 

        }
        @SuppressWarnings("rawtypes")
        private void verifyLoanRepaymentScheduleWithoutFutureDisbursement(final ArrayList<HashMap> loanScheduleAppproval) {
            System.out.println("--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");
            Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
            todaysDate.add(Calendar.DAY_OF_MONTH, -7);
           assertEquals("Balance of loan for 1st Installment", new Float("4000"), loanScheduleAppproval.get(0).get("principalDisbursed"));
           assertEquals("Disbursement Date for 1st Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(0)
                    .get("dueDate"));
           todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 1st Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(1)
                    .get("dueDate"));
            assertEquals("Principal Due for 1st Installment", new Float("828.39"), loanScheduleAppproval.get(1).get("principalDue"));
            assertEquals("Interest Due for 1st Installment", new Float("18.46"), loanScheduleAppproval.get(1).get("interestDue"));
           
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 2nd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(2)
                    .get("dueDate"));
            assertEquals("Principal Due for 2nd Installment", new Float("832.21"), loanScheduleAppproval.get(2).get("principalDue"));
            assertEquals("Interest Due for 2nd Installment", new Float("14.64"), loanScheduleAppproval.get(2).get("interestDue"));
            
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Date for 3rd Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(3)
                    .get("dueDate"));
            assertEquals("Principal Due for 3rd Installment", new Float("836.05"), loanScheduleAppproval.get(3).get("principalDue"));
            assertEquals("Interest Due for 3rd Installment", new Float("10.80"), loanScheduleAppproval.get(3).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 4th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(4)
                    .get("dueDate"));
            assertEquals("Principal Due for 4th Installment", new Float("839.91"), loanScheduleAppproval.get(4).get("principalDue"));
            assertEquals("Interest Due for 4th Installment", new Float("6.94"), loanScheduleAppproval.get(4).get("interestDue"));
            todaysDate.add(Calendar.DAY_OF_MONTH, 7);
            assertEquals("Due Date for 5th Installment", new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH)+1, todaysDate.get(Calendar.DAY_OF_MONTH))), loanScheduleAppproval.get(5)
                    .get("dueDate"));
            assertEquals("Principal Due for 5th Installment", new Float("663.44"), loanScheduleAppproval.get(5).get("principalDue"));
            assertEquals("Interest Due for 5th Installment", new Float("3.06"), loanScheduleAppproval.get(5).get("interestDue"));
            
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
            return Utils.performServerPost(requestSpec, responseSpec,CREATE_CLIENT_URL, getTestClientAsJSON(activationDate, officeId),
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