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
import org.apache.fineract.integrationtests.common.accounting.Account;
import org.apache.fineract.integrationtests.common.accounting.AccountHelper;
import org.apache.fineract.integrationtests.common.accounting.JournalEntry;
import org.apache.fineract.integrationtests.common.accounting.JournalEntryHelper;
import org.apache.fineract.integrationtests.common.accounting.PeriodicAccrualAccountingHelper;
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

public class testRefund {
	private ResponseSpecification responseSpec;
	private RequestSpecification requestSpec;
	private LoanTransactionHelper loanTransactionHelper;
	private AccountHelper accountHelper;
	private JournalEntryHelper journalEntryHelper;
	private PeriodicAccrualAccountingHelper periodicAccrualAccountingHelper;

	@Before
	public void setup() {
		Utils.initializeRESTAssured();
		this.requestSpec = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
		this.requestSpec.header("Authorization",
				"Basic " + Utils.loginIntoServerAndGetBase64EncodedAuthenticationKey());
		this.responseSpec = new ResponseSpecBuilder().expectStatusCode(200).build();

		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.accountHelper = new AccountHelper(this.requestSpec, this.responseSpec);
		this.journalEntryHelper = new JournalEntryHelper(this.requestSpec, this.responseSpec);
		this.periodicAccrualAccountingHelper = new PeriodicAccrualAccountingHelper(this.requestSpec, this.responseSpec);
	}

	@Test
	public void testRefundWithInterestRecalculation_WITH_PERIODIC_ACCOUNTING() throws InterruptedException {
		this.loanTransactionHelper = new LoanTransactionHelper(this.requestSpec, this.responseSpec);
		this.periodicAccrualAccountingHelper = new PeriodicAccrualAccountingHelper(this.requestSpec, this.responseSpec);
		this.journalEntryHelper = new JournalEntryHelper(this.requestSpec, this.responseSpec);

		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		dateFormat.setTimeZone(Utils.getTimeZoneOfTenant());

		final Account assetAccount = this.accountHelper.createAssetAccount();
		final Account incomeAccount = this.accountHelper.createIncomeAccount();
		final Account expenseAccount = this.accountHelper.createExpenseAccount();
		final Account overpaymentAccount = this.accountHelper.createLiabilityAccount();

		Calendar todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
		System.out.println("Disbursal Date Calendar " + todaysDate.getTime());
		todaysDate.add(Calendar.DAY_OF_MONTH, -14);
		final String LOAN_DISBURSEMENT_DATE = dateFormat.format(todaysDate.getTime());

		final Integer clientID = ClientHelper.createClient(this.requestSpec, this.responseSpec);
		ClientHelper.verifyClientCreatedOnServer(this.requestSpec, this.responseSpec, clientID);
		Account[] accounts = { assetAccount, incomeAccount, expenseAccount, overpaymentAccount };
		final Integer loanProductID = createLoanProductWithInterestRecalculation(
				LoanProductTestBuilder.DEFAULT_STRATEGY, LoanProductTestBuilder.RECALCULATION_COMPOUNDING_METHOD_NONE,
				LoanProductTestBuilder.RECALCULATION_STRATEGY_REDUCE_EMI_AMOUN,
				LoanProductTestBuilder.RECALCULATION_FREQUENCY_TYPE_SAME_AS_REPAYMENT_PERIOD, "0", null,
				LoanProductTestBuilder.INTEREST_APPLICABLE_STRATEGY_ON_PRE_CLOSE_DATE, accounts, null, null);

		final Integer loanID = applyForLoanApplicationForInterestRecalculation(clientID, loanProductID,
				LOAN_DISBURSEMENT_DATE, null, LoanApplicationTestBuilder.DEFAULT_STRATEGY, new ArrayList<HashMap>(0));

		Assert.assertNotNull(loanID);
		HashMap loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
		LoanStatusChecker.verifyLoanIsPending(loanStatusHashMap);

		ArrayList<HashMap> loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec,
				this.responseSpec, loanID);
		List<Map<String, Object>> expectedvalues = new ArrayList<>();
		todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
		System.out.println("Date during repayment schedule" + todaysDate.getTime());
		addRepaymentValues(expectedvalues, todaysDate, -1, false, "2482.76", "46.15", "0.0", "0.0");
		addRepaymentValues(expectedvalues, todaysDate, 1, false, "2494.22", "34.69", "0.0", "0.0");
		addRepaymentValues(expectedvalues, todaysDate, 1, false, "2505.73", "23.18", "0.0", "0.0");
		addRepaymentValues(expectedvalues, todaysDate, 1, false, "2517.29", "11.62", "0.0", "0.0");
		verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);

		System.out.println("-----------------------------------APPROVE LOAN-----------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.approveLoan(LOAN_DISBURSEMENT_DATE, loanID);
		LoanStatusChecker.verifyLoanIsApproved(loanStatusHashMap);
		LoanStatusChecker.verifyLoanIsWaitingForDisbursal(loanStatusHashMap);

		System.out.println("-------------------------------DISBURSE LOAN-------------------------------------------");
		loanStatusHashMap = this.loanTransactionHelper.disburseLoan(LOAN_DISBURSEMENT_DATE, loanID);
		LoanStatusChecker.verifyLoanIsActive(loanStatusHashMap);

		loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
				expectedvalues = new ArrayList<>();
				todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
				addRepaymentValues(expectedvalues, todaysDate, -1, false, "2482.76", "46.15", "0.0", "0.0");
				addRepaymentValues(expectedvalues, todaysDate, 1, false, "2482.76", "46.15", "0.0", "0.0");
				addRepaymentValues(expectedvalues, todaysDate, 1, false, "2505.67", "23.24", "0.0", "0.0");
				addRepaymentValues(expectedvalues, todaysDate, 1, false, "2528.81", "11.67", "0.0", "0.0");
				verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
		
				final JournalEntry[] assetAccountInitialEntry = {
						new JournalEntry(10000.0f, JournalEntry.TransactionType.CREDIT),
						new JournalEntry(10000.0f, JournalEntry.TransactionType.DEBIT), };
				this.journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, LOAN_DISBURSEMENT_DATE,
						assetAccountInitialEntry);
				todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
				String runOndate = dateFormat.format(todaysDate.getTime());
				this.periodicAccrualAccountingHelper.runPeriodicAccrualAccounting(runOndate);
				this.loanTransactionHelper.checkAccrualTransactionForRepayment(Utils.getLocalDateOfTenant().minusDays(7),
						46.15f, 0f, 0f, loanID);
				this.loanTransactionHelper.checkAccrualTransactionForRepayment(Utils.getLocalDateOfTenant(), 46.15f, 0f, 0f,
						loanID);
		
				todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
				todaysDate.add(Calendar.DAY_OF_MONTH, -7);
				final String LOAN_FIRST_REPAYMENT_DATE = dateFormat.format(todaysDate.getTime());
				Float totalDueForCurrentPeriod = (Float) loanSchedule.get(1).get("totalDueForPeriod");
				this.loanTransactionHelper.makeRepayment(LOAN_FIRST_REPAYMENT_DATE, totalDueForCurrentPeriod, loanID);
		
				loanSchedule = this.loanTransactionHelper.getLoanRepaymentSchedule(this.requestSpec, this.responseSpec, loanID);
				expectedvalues = new ArrayList<>();
				todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
				addRepaymentValues(expectedvalues, todaysDate, -1, false, "2482.76", "46.15", "0.0", "0.0");
				addRepaymentValues(expectedvalues, todaysDate, 1, false, "2494.22", "34.69", "0.0", "0.0");
				addRepaymentValues(expectedvalues, todaysDate, 1, false, "2505.73", "23.18", "0.0", "0.0");
				addRepaymentValues(expectedvalues, todaysDate, 1, false, "2517.29", "11.62", "0.0", "0.0");
				verifyLoanRepaymentSchedule(loanSchedule, expectedvalues);
		
				this.loanTransactionHelper.checkAccrualTransactionForRepayment(Utils.getLocalDateOfTenant().minusDays(7),
						46.15f, 0f, 0f, loanID);
				this.loanTransactionHelper.checkAccrualTransactionForRepayment(Utils.getLocalDateOfTenant(), 34.69f, 0f, 0f,
						loanID);
		

		HashMap prepayDetail = this.loanTransactionHelper.getPrepayAmount(this.requestSpec, this.responseSpec, loanID);
		Float prepayAmount = ((float) prepayDetail.get("amount"));
		Float REFUND_AMOUNT = 500f;
		prepayAmount += REFUND_AMOUNT;
		todaysDate = Calendar.getInstance(Utils.getTimeZoneOfTenant());
		String LOAN_REPAYMENT_DATE = dateFormat.format(todaysDate.getTime());
		this.loanTransactionHelper.makeRepayment(LOAN_REPAYMENT_DATE, new Float(prepayAmount), loanID);
		System.out.println("-------------------------------REPAYMENTS DONE-------------------------------------------");
		loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
		LoanStatusChecker.verifyLoanAccountIsOverPaid(loanStatusHashMap);
		System.out.println("-------------------------------OVER PAID-------------------------------------------");
		this.loanTransactionHelper.makeRefund(LOAN_REPAYMENT_DATE, REFUND_AMOUNT, loanID);
		System.out.println("-------------------------------REFUNDED-------------------------------------------");
		loanStatusHashMap = LoanStatusChecker.getStatusOfLoan(this.requestSpec, this.responseSpec, loanID);
		LoanStatusChecker.verifyLoanAccountIsClosed(loanStatusHashMap);
		System.out.println("-------------------------------CLOSED-------------------------------------------");

		// this.loanTransactionHelper.checkAccrualTransactionForRepayment(Utils.getLocalDateOfTenant().minusDays(7),
		// 46.15f, 0f, 0f, loanID);
		// this.loanTransactionHelper.checkAccrualTransactionForRepayment(Utils.getLocalDateOfTenant(),
		// 34.69f, 0f, 0f,
		// loanID);

		this.journalEntryHelper.checkJournalEntryForAssetAccount(assetAccount, LOAN_REPAYMENT_DATE,
				new JournalEntry(500f, JournalEntry.TransactionType.CREDIT));
		this.journalEntryHelper.checkJournalEntryForLiabilityAccount(overpaymentAccount, LOAN_REPAYMENT_DATE,
				new JournalEntry(500f, JournalEntry.TransactionType.DEBIT));
	}

	private Integer createLoanProductWithInterestRecalculation(final String repaymentStrategy,
			final String interestRecalculationCompoundingMethod, final String rescheduleStrategyMethod,
			final String recalculationRestFrequencyType, final String recalculationRestFrequencyInterval,
			final String recalculationRestFrequencyDate, final String preCloseInterestCalculationStrategy,
			final Account[] accounts, final Integer recalculationRestFrequencyOnDayType,
			final Integer recalculationRestFrequencyDayOfWeekType) {
		final String recalculationCompoundingFrequencyType = null;
		final String recalculationCompoundingFrequencyInterval = null;
		final String recalculationCompoundingFrequencyDate = null;
		final Integer recalculationCompoundingFrequencyOnDayType = null;
		final Integer recalculationCompoundingFrequencyDayOfWeekType = null;
		return createLoanProductWithInterestRecalculation(repaymentStrategy, interestRecalculationCompoundingMethod,
				rescheduleStrategyMethod, recalculationRestFrequencyType, recalculationRestFrequencyInterval,
				recalculationRestFrequencyDate, recalculationCompoundingFrequencyType,
				recalculationCompoundingFrequencyInterval, recalculationCompoundingFrequencyDate,
				preCloseInterestCalculationStrategy, accounts, null, false, recalculationCompoundingFrequencyOnDayType,
				recalculationCompoundingFrequencyDayOfWeekType, recalculationRestFrequencyOnDayType,
				recalculationRestFrequencyDayOfWeekType);
	}

	private Integer createLoanProductWithInterestRecalculation(final String repaymentStrategy,
			final String interestRecalculationCompoundingMethod, final String rescheduleStrategyMethod,
			final String recalculationRestFrequencyType, final String recalculationRestFrequencyInterval,
			final String recalculationRestFrequencyDate, final String recalculationCompoundingFrequencyType,
			final String recalculationCompoundingFrequencyInterval, final String recalculationCompoundingFrequencyDate,
			final String preCloseInterestCalculationStrategy, final Account[] accounts, final String chargeId,
			boolean isArrearsBasedOnOriginalSchedule, final Integer recalculationCompoundingFrequencyOnDayType,
			final Integer recalculationCompoundingFrequencyDayOfWeekType,
			final Integer recalculationRestFrequencyOnDayType, final Integer recalculationRestFrequencyDayOfWeekType) {
		System.out.println(
				"------------------------------CREATING NEW LOAN PRODUCT ---------------------------------------");
		LoanProductTestBuilder builder = new LoanProductTestBuilder().withPrincipal("10000000.00")
				.withNumberOfRepayments("24").withRepaymentAfterEvery("1").withRepaymentTypeAsWeek()
				.withinterestRatePerPeriod("2").withInterestRateFrequencyTypeAsMonths()
				.withRepaymentStrategy(repaymentStrategy).withAmortizationTypeAsEqualPrincipalPayment()
				.withInterestCalculationPeriodTypeAsRepaymentPeriod(true).withInterestTypeAsDecliningBalance()
				.withInterestRecalculationDetails(interestRecalculationCompoundingMethod, rescheduleStrategyMethod,
						preCloseInterestCalculationStrategy)
				.withInterestRecalculationRestFrequencyDetails(recalculationRestFrequencyType,
						recalculationRestFrequencyInterval, recalculationRestFrequencyOnDayType,
						recalculationRestFrequencyDayOfWeekType)
				.withInterestRecalculationCompoundingFrequencyDetails(recalculationCompoundingFrequencyType,
						recalculationCompoundingFrequencyInterval, recalculationCompoundingFrequencyOnDayType,
						recalculationCompoundingFrequencyDayOfWeekType);
		if (accounts != null) {
			builder = builder.withAccountingRulePeriodicAccrual(accounts);
		}

		if (isArrearsBasedOnOriginalSchedule)
			builder = builder.withArrearsConfiguration();

		final String loanProductJSON = builder.build(chargeId);
		return this.loanTransactionHelper.getLoanProductId(loanProductJSON);
	}

	private Integer applyForLoanApplicationForInterestRecalculation(final Integer clientID, final Integer loanProductID,
			final String disbursementDate, final String compoundingStartDate, final String repaymentStrategy,
			final List<HashMap> charges) {
		return applyForLoanApplicationForInterestRecalculation(clientID, loanProductID, disbursementDate,
				repaymentStrategy, charges, null, null);
	}

	private Integer applyForLoanApplicationForInterestRecalculation(final Integer clientID, final Integer loanProductID,
			final String disbursementDate, final String repaymentStrategy, final List<HashMap> charges,
			final String graceOnInterestPayment, final String graceOnPrincipalPayment) {
		System.out.println(
				"--------------------------------APPLYING FOR LOAN APPLICATION--------------------------------");
		final String loanApplicationJSON = new LoanApplicationTestBuilder() //
				.withPrincipal("10000.00") //
				.withLoanTermFrequency("4") //
				.withLoanTermFrequencyAsWeeks() //
				.withNumberOfRepayments("4") //
				.withRepaymentEveryAfter("1") //
				.withRepaymentFrequencyTypeAsWeeks() //
				.withInterestRatePerPeriod("2") //
				.withAmortizationTypeAsEqualInstallments() //
				.withInterestTypeAsDecliningBalance() //
				.withInterestCalculationPeriodTypeSameAsRepaymentPeriod() //
				.withExpectedDisbursementDate(disbursementDate) //
				.withSubmittedOnDate(disbursementDate) //
				.withwithRepaymentStrategy(repaymentStrategy) //
				.withPrincipalGrace(graceOnPrincipalPayment) //
				.withInterestGrace(graceOnInterestPayment)//
				.withCharges(charges)//
				.build(clientID.toString(), loanProductID.toString(), null);
		return this.loanTransactionHelper.getLoanId(loanApplicationJSON);
	}

	private void addRepaymentValues(List<Map<String, Object>> expectedvalues, Calendar todaysDate, int addPeriod,
			boolean isAddDays, String principalDue, String interestDue, String feeChargesDue,
			String penaltyChargesDue) {
		Map<String, Object> values = new HashMap<>(3);
		if (isAddDays) {
			values.put("dueDate", getDateAsArray(todaysDate, addPeriod));
		} else {
			values.put("dueDate", getDateAsArray(todaysDate, addPeriod * 7));
		}
		System.out.println("Updated date " + values.get("dueDate"));
		values.put("principalDue", principalDue);
		values.put("interestDue", interestDue);
		values.put("feeChargesDue", feeChargesDue);
		values.put("penaltyChargesDue", penaltyChargesDue);
		expectedvalues.add(values);
	}

	private List getDateAsArray(Calendar todaysDate, int addPeriod) {
		return getDateAsArray(todaysDate, addPeriod, Calendar.DAY_OF_MONTH);
	}

	private List getDateAsArray(Calendar todaysDate, int addvalue, int type) {
		todaysDate.add(type, addvalue);
		return new ArrayList<>(Arrays.asList(todaysDate.get(Calendar.YEAR), todaysDate.get(Calendar.MONTH) + 1,
				todaysDate.get(Calendar.DAY_OF_MONTH)));
	}

	private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule,
			List<Map<String, Object>> expectedvalues) {
		int index = 1;
		verifyLoanRepaymentSchedule(loanSchedule, expectedvalues, index);

	}

	private void verifyLoanRepaymentSchedule(final ArrayList<HashMap> loanSchedule,
			List<Map<String, Object>> expectedvalues, int index) {
		System.out.println(
				"--------------------VERIFYING THE PRINCIPAL DUES,INTEREST DUE AND DUE DATE--------------------------");
		for (Map<String, Object> values : expectedvalues) {
			assertEquals("Checking for Due Date for  installment " + index, values.get("dueDate"),
					loanSchedule.get(index).get("dueDate"));
			validateNumberForEqualWithMsg("Checking for Principal Due for installment " + index,
					String.valueOf(values.get("principalDue")),
					String.valueOf(loanSchedule.get(index).get("principalDue")));
			validateNumberForEqualWithMsg("Checking for Interest Due for installment " + index,
					String.valueOf(values.get("interestDue")),
					String.valueOf(loanSchedule.get(index).get("interestDue")));
			validateNumberForEqualWithMsg("Checking for Fee charge Due for installment " + index,
					String.valueOf(values.get("feeChargesDue")),
					String.valueOf(loanSchedule.get(index).get("feeChargesDue")));
			validateNumberForEqualWithMsg("Checking for Penalty charge Due for installment " + index,
					String.valueOf(values.get("penaltyChargesDue")),
					String.valueOf(loanSchedule.get(index).get("penaltyChargesDue")));
			index++;
		}
	}

	public void validateNumberForEqualWithMsg(String msg, String val, String val2) {
		Assert.assertTrue(msg + "expected " + val + " but was " + val2, new Float(val).compareTo(new Float(val2)) == 0);
	}

}
