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
package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.SearchParameters;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.floatingrates.data.InterestRatePeriodData;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApprovalData;
import org.apache.fineract.portfolio.loanaccount.data.LoanScheduleAccrualData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.data.PaidInAdvanceData;
import org.apache.fineract.portfolio.loanaccount.data.RepaymentScheduleRelatedLoanData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanSchedulePeriodData;
import org.joda.time.LocalDate;

public interface LoanReadPlatformService {

    LoanAccountData retrieveOne(Long loanId);

    LoanScheduleData retrieveRepaymentSchedule(Long loanId, RepaymentScheduleRelatedLoanData repaymentScheduleRelatedData,
            Collection<DisbursementData> disbursementData, BigDecimal totalPaidFeeCharges);

    Collection<LoanTransactionData> retrieveLoanTransactions(Long loanId);

    LoanAccountData retrieveTemplateWithClientAndProductDetails(Long clientId, Long productId);

    LoanAccountData retrieveTemplateWithGroupAndProductDetails(Long groupId, Long productId);

    LoanTransactionData retrieveLoanTransactionTemplate(Long loanId);

    LoanTransactionData retrieveWaiveInterestDetails(Long loanId,Boolean isTotalOutstandingInterest);

    LoanTransactionData retrieveLoanTransaction(Long loanId, Long transactionId);

    LoanTransactionData retrieveNewClosureDetails();

    LoanTransactionData retrieveDisbursalTemplate(Long loanId, boolean paymentDetailsRequired);

    LoanApprovalData retrieveApprovalTemplate(Long loanId);

    LoanAccountData retrieveTemplateWithCompleteGroupAndProductDetails(Long groupId, Long productId);

    LoanAccountData retrieveLoanProductDetailsTemplate(Long productId, Long clientId, Long groupId);

    LoanAccountData retrieveClientDetailsTemplate(Long clientId);

    LoanAccountData retrieveGroupDetailsTemplate(Long groupId);

    LoanAccountData retrieveGroupAndMembersDetailsTemplate(Long groupId);

    Collection<CalendarData> retrieveCalendars(Long groupId);

    Page<LoanAccountData> retrieveAll(SearchParameters searchParameters, boolean lookup);

    Collection<StaffData> retrieveAllowedLoanOfficers(Long selectedOfficeId, boolean staffInSelectedOfficeOnly);

    Integer retriveLoanCounter(Long groupId, Integer loanType, Long productId);

    Integer retriveLoanCounter(Long clientId, Long productId);

    Collection<DisbursementData> retrieveLoanDisbursementDetails(Long loanId);

    DisbursementData retrieveLoanDisbursementDetail(Long loanId, Long disbursementId);

    Collection<LoanTermVariationsData> retrieveLoanTermVariations(Long loanId, Integer termType);

    Collection<LoanScheduleAccrualData> retriveScheduleAccrualData();

    LoanTransactionData retrieveRecoveryPaymentTemplate(Long loanId);

    LoanTransactionData retrieveLoanWriteoffTemplate(Long loanId);

    Collection<LoanScheduleAccrualData> retrivePeriodicAccrualData(LocalDate tillDate, List<Long> loanList);

    List<Long> fetchLoansForInterestRecalculation();

    Collection<LoanTransactionData> retrieveWaiverLoanTransactions(Long loanId);

    Collection<LoanSchedulePeriodData> fetchWaiverInterestRepaymentData(Long loanId);

    boolean isGuaranteeRequired(Long loanId);

    Date retrieveMinimumDateOfRepaymentTransaction(Long loanId);

    PaidInAdvanceData retrieveTotalPaidInAdvance(Long loanId);

    LoanTransactionData retrieveRefundByCashTemplate(Long loanId);
    
    Collection<InterestRatePeriodData> retrieveLoanInterestRatePeriodData(LoanAccountData loanData);

    Collection<Long> retrieveLoanIdsWithPendingIncomePostingTransactions();
    
    Collection<Long> retrieveAllActiveSubmittedAprrovedGroupLoanIds(Long groupId);
    
    Collection<LoanAccountData> retrieveAllForTaskLookupBySearchParameters(SearchParameters searchParameters);
	
    LoanTransactionData refundTemplate(Long loanId);

    boolean isAnyActiveJLGLoanForClient(Long clientid, Long groupId);

    Long retrieveLoanApplicationReferenceId(final LoanAccountData loanBasicDetails);

    Long retrieveLoanProductIdByLoanId(final Long loanId);

    Collection<Long> retrieveLoansByOfficesAndHoliday(Long officeId, List<Holiday> holidays, Collection<Integer> status, LocalDate recalculateFrom);

    LoanAccountData retrieveOneWithBasicDetails(Long loanId);

    void validateForLoanExistence(Long loanId);

   // Collection<LoanAccountData> retrieveLoanDetailForHierarchy(ClientData clientData);
    
    Map<String, Object> retrieveLoanProductIdApprovedAmountClientId(final Long loanId);

    LoanTransactionData retrieveLoanInstallmentDetails(Long loanId);

    Collection<Long> retriveLoansForMarkingAsNonNPAWithPeriodicAccounding();

    Collection<Long> retriveLoansForMarkingAsNPAWithPeriodicAccounding();
    
    Collection<LoanSchedulePeriodData> lookUpLoanSchedulePeriodsByPeriodNumberAndDueDateAndDueAmounts(final Long loanId,
            final boolean excludeLoanScheduleMappedToPDC);
    
    Long retrivePaymentDetailsIdWithLoanAccountNumberAndLoanTransactioId(final long loanTransactionId, final String loanAccountNumber);

    List<LoanRepaymentScheduleInstallment> retrieveLoanRepaymentScheduleInstallments(Long loanId);

    List<LoanTransaction> retrieveLoanTransactions(Long loanId, Integer... types);

    List<LoanRecurringCharge> retrieveLoanOverdueRecurringCharge(Long loanId, boolean fetchApplicableChargesByState);
    
    MonetaryCurrency retrieveLoanCurrency(Long loanId);

    List<Long> fetchLoanIdsForOverdueCharge(boolean isRunForBrokenPeriod, boolean isInterestRecalculationLoans);
}