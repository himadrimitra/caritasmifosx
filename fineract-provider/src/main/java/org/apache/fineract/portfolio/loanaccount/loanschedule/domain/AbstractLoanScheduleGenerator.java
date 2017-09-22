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
package org.apache.fineract.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.domain.RepaymentRescheduleType;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanInterestRecalcualtionAdditionalDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleDTO;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleParams;
import org.apache.fineract.portfolio.loanaccount.loanschedule.exception.MultiDisbursementEmiAmountException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.exception.MultiDisbursementOutstandingAmoutException;
import org.apache.fineract.portfolio.loanaccount.loanschedule.exception.ScheduleDateException;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.util.CollectionUtils;

public abstract class AbstractLoanScheduleGenerator implements LoanScheduleGenerator {

    private final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
    private final PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();

    @Override
    public LoanScheduleModel generate(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO) {
        final LoanScheduleParams loanScheduleRecalculationDTO = null;
        EmiDetails emiDetails  = new EmiDetails();
        return generate(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, loanScheduleRecalculationDTO, emiDetails);
    }

    private void processSubsidyTransactions(final Map<LocalDate, Money> principalPortionMap,
            Collection<RecalculationDetail> applicableTransactions, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO) {
        final MonetaryCurrency currency = loanApplicationTerms.getCurrency();
        for (RecalculationDetail applicableTransaction : applicableTransactions) {
            LocalDate applicableDate = getNextRestScheduleDate(applicableTransaction.getTransactionDate().minusDays(1),
                    loanApplicationTerms, holidayDetailDTO);
            if (applicableTransaction.getTransaction().getTypeOf().equals(LoanTransactionType.ADD_SUBSIDY)) {
                updateMapWithAmount(principalPortionMap, applicableTransaction.getTransaction().getAmount(currency), applicableDate);
                applicableTransaction.setProcessed(true);
            } else if (applicableTransaction.getTransaction().getTypeOf().equals(LoanTransactionType.REVOKE_SUBSIDY)) {
                updateMapWithAmount(principalPortionMap, applicableTransaction.getTransaction().getAmount(currency).negated(),
                        applicableDate);
                applicableTransaction.setProcessed(true);
            }
        }
    }
    
    private LoanScheduleModel generate(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO, final LoanScheduleParams loanScheduleParams,
            EmiDetails emiDetails) {

        final ApplicationCurrency applicationCurrency = loanApplicationTerms.getApplicationCurrency();
        // generate list of proposed schedule due dates
        LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, holidayDetailDTO);
        LoanTermVariationsData lastDueDateVariation = loanApplicationTerms.getLoanTermVariations().fetchLoanTermDueDateVariationsData(
                loanEndDate);
        if (lastDueDateVariation != null) {
            loanEndDate = lastDueDateVariation.getDateValue();
        }
        loanApplicationTerms.updateLoanEndDate(loanEndDate);

        // determine the total charges due at time of disbursement
        final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(loanCharges,loanApplicationTerms);

        // setup variables for tracking important facts required for loan
        // schedule generation.

        final MonetaryCurrency currency = loanApplicationTerms.getCurrency();
        final int numberOfRepayments = loanApplicationTerms.fetchNumberOfRepaymentsAfterExceptions();

        LoanScheduleParams scheduleParams = null;
        if (loanScheduleParams == null) {
            scheduleParams = LoanScheduleParams.createLoanScheduleParams(currency, Money.of(currency, chargesDueAtTimeOfDisbursement),
                    loanApplicationTerms.getExpectedDisbursementDate(), getPrincipalToBeScheduled(loanApplicationTerms));
        } else if (!loanScheduleParams.isPartialUpdate()) {
            scheduleParams = LoanScheduleParams
                    .createLoanScheduleParams(currency, Money.of(currency, chargesDueAtTimeOfDisbursement),
                            loanApplicationTerms.getExpectedDisbursementDate(), getPrincipalToBeScheduled(loanApplicationTerms),
                            loanScheduleParams);
        } else {
            scheduleParams = loanScheduleParams;
        }

        boolean isFirstRepayment = true;
        LocalDate firstRepaymentdate = this.scheduledDateGenerator.generateNextRepaymentDate(
                loanApplicationTerms.getExpectedDisbursementDate(), loanApplicationTerms, isFirstRepayment);
        final LocalDate idealDisbursementDate = this.scheduledDateGenerator.idealDisbursementDateBasedOnFirstRepaymentDate(
                loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery(), firstRepaymentdate,
                loanApplicationTerms.getLoanCalendar(), loanApplicationTerms.getHolidayDetailDTO(), loanApplicationTerms);
        loanApplicationTerms.setIdealDisbursementDate(idealDisbursementDate);
        
        final Collection<RecalculationDetail> transactions = scheduleParams.getRecalculationDetails();
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = scheduleParams
                .getLoanRepaymentScheduleTransactionProcessor();
        Collection<LoanScheduleModelPeriod> periods = new ArrayList<>();
        if(!scheduleParams.isPartialUpdate()) {
         periods = createNewLoanScheduleListWithDisbursementDetails(numberOfRepayments,
                loanApplicationTerms, chargesDueAtTimeOfDisbursement);
        }

        // Determine the total interest owed over the full loan for FLAT
        // interest method .
        if (!scheduleParams.isPartialUpdate()) {
            Money totalInterestChargedForFullLoanTerm = loanApplicationTerms.calculateTotalInterestCharged(
                    this.paymentPeriodsInOneYearCalculator, mc);
            totalInterestChargedForFullLoanTerm = getGlimLoanTotalFlatInterestAmount(loanApplicationTerms, totalInterestChargedForFullLoanTerm);
            loanApplicationTerms.updateTotalInterestDue(totalInterestChargedForFullLoanTerm);
            
        }

        setCapitalizedChargesDetailesToLoanScheduleParams(scheduleParams, loanApplicationTerms);
        
        if (!scheduleParams.isPartialUpdate()) {
            updateAmortization(mc, loanApplicationTerms, scheduleParams.getPeriodNumber(),
                    scheduleParams.getOutstandingBalance().plus(scheduleParams.getTotalCapitalizedChargeAmount())
                            .minus(loanApplicationTerms.getAmountForUpfrontCollection()));
            
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                // fetches the first tranche amount and also updates other
                // tranche
                // details to map
                BigDecimal disburseAmt = getDisbursementAmount(loanApplicationTerms, scheduleParams.getPeriodStartDate(), periods,
                        scheduleParams.getDisburseDetailMap(),loanCharges);
                scheduleParams.setPrincipalToBeScheduled(Money.of(currency, disburseAmt));
                loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().zero().plus(disburseAmt));
                scheduleParams.setOutstandingBalance(Money.of(currency, disburseAmt));
                scheduleParams.setOutstandingBalanceAsPerRest(Money.of(currency, disburseAmt));
                if (loanApplicationTerms.isEmiBasedOnDisbursements()) {
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms,
                            Money.of(loanApplicationTerms.getCurrency(), loanApplicationTerms.getAmountForUpfrontCollection()),
                            scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled());
                }
            }
            
        }

        // charges which depends on total loan interest will be added to this
        // set and handled separately after all installments generated
        final Set<LoanCharge> nonCompoundingCharges = seperateTotalCompoundingPercentageCharges(loanCharges);

        LocalDate currentDate = DateUtils.getLocalDateOfTenant();
        LocalDate lastRestDate = currentDate;
        if (loanApplicationTerms.getRestCalendarInstance() != null) {
            lastRestDate = getNextRestScheduleDate(currentDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }

        boolean isNextRepaymentAvailable = true;
        Boolean extendTermForDailyRepayments = false;

        if (holidayDetailDTO.getWorkingDays().getExtendTermForDailyRepayments() == true
                && loanApplicationTerms.getRepaymentPeriodFrequencyType() == PeriodFrequencyType.DAYS
                && loanApplicationTerms.getRepaymentEvery() == 1) {
            holidayDetailDTO.getWorkingDays().setRepaymentReschedulingType(RepaymentRescheduleType.MOVE_TO_NEXT_WORKING_DAY.getValue());
            extendTermForDailyRepayments = true;
        }

        final Collection<LoanTermVariationsData> interestRates = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();
        final Collection<LoanTermVariationsData> interestRatesForInstallments = loanApplicationTerms.getLoanTermVariations().getInterestRateFromInstallment();

        // this block is to start the schedule generation from specified date
        if (scheduleParams.isPartialUpdate()) {
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                loanApplicationTerms.setPrincipal(scheduleParams.getPrincipalToBeScheduled());
            }

            applyLoanVariationsForPartialScheduleGenerate(loanApplicationTerms, scheduleParams, interestRates, interestRatesForInstallments);

            isFirstRepayment = false;
        }
        while (scheduleParams.getOutstandingBalance().isGreaterThanZero() || !scheduleParams.getDisburseDetailMap().isEmpty()) {
            final LocalDate previousRepaymentDate = scheduleParams.getActualRepaymentDate();
            scheduleParams.setActualRepaymentDate(this.scheduledDateGenerator.generateNextRepaymentDate(
                    scheduleParams.getActualRepaymentDate(), loanApplicationTerms, isFirstRepayment));
            AdjustedDateDetailsDTO adjustedDateDetailsDTO = this.scheduledDateGenerator.adjustRepaymentDate(
                    scheduleParams.getActualRepaymentDate(), loanApplicationTerms, holidayDetailDTO);
            scheduleParams.setActualRepaymentDate(adjustedDateDetailsDTO.getChangedActualRepaymentDate());
            LocalDate scheduledDueDate = adjustedDateDetailsDTO.getChangedScheduleDate();
            Money installmentCapitalizedChargeAmount = Money.zero(currency);
            // calculated interest start date for the period
            LocalDate periodStartDateApplicableForInterest = this.scheduledDateGenerator.calculateInterestStartDateForPeriod(loanApplicationTerms,
                    scheduleParams.getPeriodStartDate(), idealDisbursementDate, firstRepaymentdate);
            if (isFirstRepayment) {
                processCapitalizedTransactions(scheduleParams.getPrincipalPortionMap(), scheduleParams.getTotalCapitalizedChargeAmount()
                        .negated(), periodStartDateApplicableForInterest);
            }
            isFirstRepayment = false;
            // Loan Schedule Exceptions that need to be applied for Loan Account
            LoanTermVariationParams termVariationParams = null;
            do {
                termVariationParams = applyLoanTermVariations(loanApplicationTerms, scheduleParams, previousRepaymentDate,
                        adjustedDateDetailsDTO.getChangedScheduleDate(), interestRatesForInstallments, this.paymentPeriodsInOneYearCalculator, mc);
                scheduledDueDate = termVariationParams.getScheduledDueDate();
                adjustedDateDetailsDTO = this.scheduledDateGenerator.adjustRepaymentDate(scheduledDueDate, loanApplicationTerms,
                        holidayDetailDTO);
                if (!adjustedDateDetailsDTO.getChangedActualRepaymentDate().isEqual(scheduledDueDate)) {
                    scheduleParams.setActualRepaymentDate(adjustedDateDetailsDTO.getChangedActualRepaymentDate());
                }
            } while (!adjustedDateDetailsDTO.getChangedScheduleDate().isEqual(scheduledDueDate));


            scheduledDueDate = adjustedDateDetailsDTO.getChangedScheduleDate();
            
            // Updates total days in term
            scheduleParams.addLoanTermInDays(Days.daysBetween(scheduleParams.getPeriodStartDate(), scheduledDueDate).getDays());
            if (termVariationParams.isSkipPeriod()) {
                continue;
            }

            if (scheduleParams.getPeriodStartDate().isAfter(scheduledDueDate)) { throw new ScheduleDateException(
                    "Due date can't be before period start date", scheduledDueDate); }

            if (extendTermForDailyRepayments) {
                scheduleParams.setActualRepaymentDate(scheduledDueDate);
            }

            // this block is to generate the schedule till the specified
            // date(used for calculating preclosure)
            boolean isCompletePeriod = true;
            if (scheduleParams.getScheduleTillDate() != null && !scheduledDueDate.isBefore(scheduleParams.getScheduleTillDate())) {
            	if(!scheduledDueDate.isEqual(scheduleParams.getScheduleTillDate())){
            		isCompletePeriod = false;
            	}
                scheduledDueDate = scheduleParams.getScheduleTillDate();
                isNextRepaymentAvailable = false;
            }
            if (loanApplicationTerms.isInterestRecalculationEnabled()) {
                populateCompoundingDatesInPeriod(scheduleParams.getPeriodStartDate(), scheduledDueDate, loanApplicationTerms,
                        holidayDetailDTO, scheduleParams, loanCharges, currency);
            }

            // populates the collection with transactions till the due date of
            // the period for interest recalculation enabled loans
            Collection<RecalculationDetail> applicableTransactions = getApplicableTransactionsForPeriod(
                    scheduleParams.applyInterestRecalculation(), scheduledDueDate, transactions);
            
            if (loanApplicationTerms.isSubsidyApplicable()) {
                processSubsidyTransactions(scheduleParams.getPrincipalPortionMap(), applicableTransactions, loanApplicationTerms, holidayDetailDTO);
            }

            final double interestCalculationGraceOnRepaymentPeriodFraction = this.paymentPeriodsInOneYearCalculator
                    .calculatePortionOfRepaymentPeriodInterestChargingGrace(periodStartDateApplicableForInterest, scheduledDueDate,
                            loanApplicationTerms.getInterestChargedFromLocalDate(), loanApplicationTerms.getLoanTermPeriodFrequencyType(),
                            loanApplicationTerms.getRepaymentEvery());
            ScheduleCurrentPeriodParams currentPeriodParams = new ScheduleCurrentPeriodParams(currency,
                    interestCalculationGraceOnRepaymentPeriodFraction);
            
            applyUpfrontAmountCollection(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams, periods, scheduledDueDate,
                    currentPeriodParams);

            if (loanApplicationTerms.isMultiDisburseLoan()) {
                updateBalanceBasedOnDisbursement(mc, loanApplicationTerms, loanCharges, scheduleParams, periods,
                        scheduledDueDate);
            }

            // process repayments to the schedule as per the repayment
            // transaction processor configuration
            // will add a new schedule with interest till the transaction date
            // for a loan repayment which falls between the
            // two periods for interest first repayment strategies
            handleRecalculationForNonDueDateTransactions(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, scheduleParams, periods,
                    loanApplicationTerms.getTotalInterestDue(), idealDisbursementDate, firstRepaymentdate, lastRestDate, scheduledDueDate,
                    periodStartDateApplicableForInterest, applicableTransactions, currentPeriodParams);

            if (currentPeriodParams.isSkipCurrentLoop()) {
                continue;
            }
            periodStartDateApplicableForInterest = this.scheduledDateGenerator.calculateInterestStartDateForPeriod(loanApplicationTerms,
                    scheduleParams.getPeriodStartDate(), idealDisbursementDate, firstRepaymentdate);

            // backup for pre-close transaction
            updateCompoundingDetails(scheduleParams, periodStartDateApplicableForInterest);

            // 5 determine principal,interest of repayment period
            PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                    this.paymentPeriodsInOneYearCalculator, currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(),
                    scheduleParams.getTotalCumulativePrincipal().minus(scheduleParams.getReducePrincipal()),
                    scheduleParams.getTotalCumulativeInterest(), loanApplicationTerms.getTotalInterestDue(),
                    scheduleParams.getTotalOutstandingInterestPaymentDueToGrace(), scheduleParams.getOutstandingBalanceAsPerRest(),
                    loanApplicationTerms, scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams),
                    scheduleParams.getCompoundingMap(), periodStartDateApplicableForInterest, scheduledDueDate, interestRates);

            
            // will check for EMI amount greater than interest calculated
            if (loanApplicationTerms.getFixedEmiAmount() != null
                    && loanApplicationTerms.getFixedEmiAmount().compareTo(principalInterestForThisPeriod.interest().getAmount()) == -1) {
                String errorMsg = "EMI amount must be greater than : " + principalInterestForThisPeriod.interest().getAmount();
                throw new MultiDisbursementEmiAmountException(errorMsg, principalInterestForThisPeriod.interest().getAmount(),
                        loanApplicationTerms.getFixedEmiAmount());
            }
            
            // update cumulative fields for principal & interest
            currentPeriodParams.setInterestForThisPeriod(principalInterestForThisPeriod.interest());
            Money lastTotalOutstandingInterestPaymentDueToGrace = scheduleParams.getTotalOutstandingInterestPaymentDueToGrace();
            scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(principalInterestForThisPeriod.interestPaymentDueToGrace());
            currentPeriodParams.setPrincipalForThisPeriod(principalInterestForThisPeriod.principal());
            Money actualPrincipal =principalInterestForThisPeriod.principal();

            installmentCapitalizedChargeAmount = updateCapitalizedFee(scheduleParams, loanApplicationTerms, currency,
                    installmentCapitalizedChargeAmount, currentPeriodParams);
            
            // applies early payments on principal portion
            updatePrincipalPortionBasedOnPreviousEarlyPayments(currency, scheduleParams, currentPeriodParams);
            
            // updates amounts with current earlyPaidAmount
            updateAmountsBasedOnCurrentEarlyPayments(mc, loanApplicationTerms, scheduleParams, currentPeriodParams);
            currentPeriodParams.plusPrincipalForThisPeriod(currentPeriodParams.getPrepaymentAmount());
            

            if ((scheduleParams.getOutstandingBalance().isLessThanZero() && !loanApplicationTerms.isAllowNegativeBalance()) || !isNextRepaymentAvailable) {
                currentPeriodParams.plusPrincipalForThisPeriod(scheduleParams.getOutstandingBalance());
                scheduleParams.setOutstandingBalance(Money.zero(currency));
            }

            if (!isNextRepaymentAvailable) {
                scheduleParams.getDisburseDetailMap().clear();
            }

            // applies charges for the period
            boolean considerInstallmentFee = true;
            applyChargesForCurrentPeriod(loanCharges, currency, scheduleParams, scheduledDueDate, currentPeriodParams,
                    loanApplicationTerms, installmentCapitalizedChargeAmount, considerInstallmentFee);

            
            // adjust last installment interest for glim
            if (isLastRepaymentPeriod(numberOfRepayments, scheduleParams.getPeriodNumber())) {
                Money totalInterestForGlim = loanApplicationTerms.getTotalInterestForGlim();
                Money totalCumulativeInterest = scheduleParams.getTotalCumulativeInterest();
                if (totalInterestForGlim != null && totalInterestForGlim.isGreaterThanZero()) {
                    currentPeriodParams.setInterestForThisPeriod(totalInterestForGlim.minus(totalCumulativeInterest));
                }
            }
            
            // sum up real totalInstallmentDue from components
            final Money totalInstallmentDue = currentPeriodParams.fetchTotalAmountForPeriod();

            // if previous installment is last then add interest to same
            // installment
            if (currentPeriodParams.getLastInstallment() != null && currentPeriodParams.getPrincipalForThisPeriod().isZero()) {
                currentPeriodParams.getLastInstallment().addInterestAmount(currentPeriodParams.getInterestForThisPeriod());
                continue;
            }

            // create repayment period from parts
            LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                    scheduleParams.getPeriodStartDate(), scheduledDueDate, currentPeriodParams.getPrincipalForThisPeriod(),
                    scheduleParams.getOutstandingBalance(), currentPeriodParams.getInterestForThisPeriod(),
                    currentPeriodParams.getFeeChargesForInstallment(), currentPeriodParams.getPenaltyChargesForInstallment(),
                    totalInstallmentDue, !isCompletePeriod);
            installment.setCapitalChargeDue(installmentCapitalizedChargeAmount);
            emiDetails.setEmiAmount(loanApplicationTerms.getFixedEmiAmount());
            emiDetails.setLastEmiAmount(currentPeriodParams.getPrincipalForThisPeriod()
                    .plus(currentPeriodParams.getInterestForThisPeriod()).plus(installmentCapitalizedChargeAmount).getAmount());
            adjustInterestForRoundingEMIAmount(loanApplicationTerms, emiDetails,scheduleParams, installment, currentPeriodParams);

            addLoanRepaymentScheduleInstallment(scheduleParams.getInstallments(), installment);
            // apply loan transactions on installments to identify early/late
            // payments for interest recalculation
            installment = handleRecalculationForTransactions(mc, loanApplicationTerms, holidayDetailDTO, currency, scheduleParams,
                    loanRepaymentScheduleTransactionProcessor, loanApplicationTerms.getTotalInterestDue(), lastRestDate, scheduledDueDate,
                    periodStartDateApplicableForInterest, applicableTransactions, currentPeriodParams,
                    lastTotalOutstandingInterestPaymentDueToGrace, installment, loanCharges);
            periods.add(installment);
            Money advancePayment = Money.of(currency, installment.principalDue()).minus(actualPrincipal);
            if(advancePayment.isGreaterThanZero()){
                installment.setAdvancePayment(advancePayment);
            }
            
            emiDetails.setLastEmiAmount(currentPeriodParams.getPrincipalForThisPeriod().plus(currentPeriodParams.getInterestForThisPeriod()).getAmount());

            // Updates principal paid map with efective date for reducing
            // the amount from outstanding balance(interest calculation)
            if (!installment.isRecalculatedInterestComponent() || !scheduleParams.getOutstandingBalance().isGreaterThanZero()) {
                updateAmountsWithEffectiveDate(loanApplicationTerms, holidayDetailDTO, scheduleParams, scheduledDueDate,
                        currentPeriodParams, installment, lastRestDate);
            }

            // handle cumulative fields

            scheduleParams.addTotalCumulativePrincipal(currentPeriodParams.getPrincipalForThisPeriod());
            scheduleParams.addTotalRepaymentExpected(totalInstallmentDue);
            scheduleParams.addTotalCumulativeInterest(currentPeriodParams.getInterestForThisPeriod());            
            scheduleParams.setPeriodStartDate(scheduledDueDate);
            //Money installmentCapitalizedChargeAmount= LoanUtilService.getCapitalizedChargeBalance(loanApplicationTerms, capitalizedChargeForPeriods, scheduleParams.getInstalmentNumber());
            processCapitalizedTransactions(scheduleParams.getPrincipalPortionMap(), installmentCapitalizedChargeAmount, scheduledDueDate);
            scheduleParams.incrementInstalmentNumber();
            scheduleParams.incrementPeriodNumber();
            if (termVariationParams.isRecalculateAmounts()) {
                loanApplicationTerms.setCurrentPeriodFixedEmiAmount(null);
                loanApplicationTerms.setCurrentPeriodFixedPrincipalAmount(null);
                adjustInstallmentOrPrincipalAmount(loanApplicationTerms, scheduleParams.getTotalCumulativePrincipal(),
                        scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled());
            }
        }

        // this condition is to add the interest from grace period if not
        // already applied.
        if (scheduleParams.getTotalOutstandingInterestPaymentDueToGrace().isGreaterThanZero()) {
            LoanScheduleModelPeriod installment = ((List<LoanScheduleModelPeriod>) periods).get(periods.size() - 1);
            installment.addInterestAmount(scheduleParams.getTotalOutstandingInterestPaymentDueToGrace());
            scheduleParams.addTotalRepaymentExpected(scheduleParams.getTotalOutstandingInterestPaymentDueToGrace());
            scheduleParams.addTotalCumulativeInterest(scheduleParams.getTotalOutstandingInterestPaymentDueToGrace());
            scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(Money.zero(currency));
        }

        // determine fees and penalties for charges which depends on total
        // loan interest
        updatePeriodsWithCharges(currency, scheduleParams, periods, nonCompoundingCharges, loanApplicationTerms);

        // this block is to add extra re-payment schedules with interest portion
        // if the loan not paid with in loan term

        if (scheduleParams.getScheduleTillDate() != null) {
            currentDate = scheduleParams.getScheduleTillDate();
        }
        if (scheduleParams.applyInterestRecalculation() && scheduleParams.getLatePaymentMap().size() > 0
                && currentDate.isAfter(scheduleParams.getPeriodStartDate())) {
            Money totalInterest = addInterestOnlyRepaymentScheduleForCurrentdate(mc, loanApplicationTerms, holidayDetailDTO, currency,
                    periods, currentDate, loanRepaymentScheduleTransactionProcessor, transactions, loanCharges, scheduleParams);
            scheduleParams.addTotalCumulativeInterest(totalInterest);
        }

        //loanApplicationTerms.resetFixedEmiAmount();
        final BigDecimal totalPrincipalPaid = BigDecimal.ZERO;
        final BigDecimal totalOutstanding = BigDecimal.ZERO;

        updateCompoundingDetails(periods, scheduleParams, loanApplicationTerms);
        return LoanScheduleModel.from(periods, applicationCurrency, scheduleParams.getLoanTermInDays(),
                scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativePrincipal().getAmount(), totalPrincipalPaid,
                scheduleParams.getTotalCumulativeInterest().getAmount(), scheduleParams.getTotalFeeChargesCharged().getAmount(),
                scheduleParams.getTotalPenaltyChargesCharged().getAmount(), scheduleParams.getTotalRepaymentExpected().getAmount(),
                totalOutstanding);
    }

    private Money getGlimLoanTotalFlatInterestAmount(LoanApplicationTerms loanApplicationTerms, Money totalInterestChargedForFullLoanTerm) {
        if (loanApplicationTerms.isGlim() && loanApplicationTerms.getInterestMethod().isFlat()) {
            totalInterestChargedForFullLoanTerm = Money.zero(loanApplicationTerms.getCurrency());
            for (GroupLoanIndividualMonitoring glim : loanApplicationTerms.getGroupLoanIndividualMonitoring()) {
                totalInterestChargedForFullLoanTerm = totalInterestChargedForFullLoanTerm.plus(glim.getInterestAmount());
            }
            loanApplicationTerms.updateTotalInterestDueForGlim(totalInterestChargedForFullLoanTerm);
        }
        return totalInterestChargedForFullLoanTerm;
    }

    private void applyUpfrontAmountCollection(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final MonetaryCurrency currency, LoanScheduleParams scheduleParams, Collection<LoanScheduleModelPeriod> periods,
            LocalDate scheduledDueDate, ScheduleCurrentPeriodParams currentPeriodParams) {
        if (loanApplicationTerms.isAmountForUpfrontCollectionAvailable() && scheduleParams.getPeriodNumber() == 1) {
            Money amountForUpfrontCollection = Money.of(currency, loanApplicationTerms.getAmountForUpfrontCollection());
            if (!scheduledDueDate.isEqual(loanApplicationTerms.getExpectedDisbursementDate())) {
                addInstallmentForUpfrontAmountCollection(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams, periods,
                        amountForUpfrontCollection);
            } else {
                scheduleParams.reduceOutstandingBalance(amountForUpfrontCollection);
                currentPeriodParams.plusPrepaymentAmount(amountForUpfrontCollection);
            }
        }
    }

    private void addInstallmentForUpfrontAmountCollection(final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO, final MonetaryCurrency currency, LoanScheduleParams scheduleParams,
            Collection<LoanScheduleModelPeriod> periods, Money interestToBeCollected) {
        scheduleParams.reduceOutstandingBalance(interestToBeCollected);
        LoanScheduleModelPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                scheduleParams.getPeriodStartDate(), loanApplicationTerms.getExpectedDisbursementDate(), interestToBeCollected,
                scheduleParams.getOutstandingBalance(), Money.zero(currency), Money.zero(currency), Money.zero(currency),
                interestToBeCollected, true);
        periods.add(installment);
        scheduleParams.incrementInstalmentNumber();
        scheduleParams.addTotalCumulativePrincipal(interestToBeCollected);
        scheduleParams.addTotalRepaymentExpected(interestToBeCollected);
        LocalDate amountApplicableDate = installment.periodDueDate();
        if (loanApplicationTerms.isInterestRecalculationEnabled()) {
            amountApplicableDate = getNextRestScheduleDate(installment.periodDueDate().minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }
        updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), interestToBeCollected, amountApplicableDate);
    }

    private Money updateCapitalizedFee(final LoanScheduleParams scheduleParams, final LoanApplicationTerms loanApplicationTerms,
            final MonetaryCurrency currency, Money installmentCapitalizedChargeAmount,
            final ScheduleCurrentPeriodParams currentPeriodParams) {
        if(loanApplicationTerms.isPrincipalGraceApplicableForThisPeriod(scheduleParams.getInstalmentNumber())){
            return installmentCapitalizedChargeAmount.zero();
        }
        if (scheduleParams.getTotalCapitalizedChargeAmount().isGreaterThanZero()) {
            BigDecimal principalPlusCapitalizedFee = currentPeriodParams.getPrincipalForThisPeriod().getAmount();
            BigDecimal totalAmount = loanApplicationTerms.getPrincipal().getAmount()
                    .add(scheduleParams.getTotalCapitalizedChargeAmount().getAmount());
            BigDecimal installmentFee = BigDecimal
                    .valueOf(((scheduleParams.getTotalCapitalizedChargeAmount().getAmount().doubleValue() * principalPlusCapitalizedFee.doubleValue()) / totalAmount
                            .doubleValue()));
            installmentCapitalizedChargeAmount = Money.of(currency, installmentFee);
            if (loanApplicationTerms.getActualNoOfRepaymnets() == scheduleParams.getInstalmentNumber()
                    || !scheduleParams.getOutstandingBalance().minus(principalPlusCapitalizedFee).isGreaterThanZero()
                    || scheduleParams.getTotalAccountedCapitalizedCharge().plus(installmentCapitalizedChargeAmount)
                            .isGreaterThan(scheduleParams.getTotalCapitalizedChargeAmount())) {
                installmentCapitalizedChargeAmount = scheduleParams.getTotalCapitalizedChargeAmount().minus(
                        scheduleParams.getTotalAccountedCapitalizedCharge());
            } else {
                currentPeriodParams.minusPrincipalForThisPeriod(installmentCapitalizedChargeAmount);
            }
        }
        scheduleParams.addTotalAccountedCapitalizedCharge(installmentCapitalizedChargeAmount);
        return installmentCapitalizedChargeAmount;
    }

    private void updateCompoundingDetails(final Collection<LoanScheduleModelPeriod> periods, final LoanScheduleParams params,
            final LoanApplicationTerms loanApplicationTerms) {
        final Map<LocalDate, Map<LocalDate, Money>> compoundingDetails = params.getCompoundingDateVariations();
        if (compoundingDetails.isEmpty()) { return; }
        for (LoanScheduleModelPeriod loanScheduleModelPeriod : periods) {
            if (loanScheduleModelPeriod.isRepaymentPeriod() && loanScheduleModelPeriod.getLoanCompoundingDetails().isEmpty()) {
                Map<LocalDate, Money> periodCompoundingDetails = compoundingDetails.get(loanScheduleModelPeriod.periodFromDate());
                if (periodCompoundingDetails != null) {
                    for (Map.Entry<LocalDate, Money> entry : periodCompoundingDetails.entrySet()) {
                        if (entry.getValue().isGreaterThanZero() && !entry.getKey().isAfter(loanScheduleModelPeriod.periodDueDate())) {
                            LocalDate effectiveDate = entry.getKey();
                            if (loanApplicationTerms.allowCompoundingOnEod()) {
                                effectiveDate = effectiveDate.minusDays(1);
                            }
                            LoanInterestRecalcualtionAdditionalDetails additionalDetails = new LoanInterestRecalcualtionAdditionalDetails(
                                    effectiveDate, entry.getValue().getAmount());
                            loanScheduleModelPeriod.getLoanCompoundingDetails().add(additionalDetails);
                        }
                    }
                }
            }
        }
    }

    private void applyChargesForCurrentPeriod(final Set<LoanCharge> loanCharges, final MonetaryCurrency currency,
            LoanScheduleParams scheduleParams, LocalDate scheduledDueDate, ScheduleCurrentPeriodParams currentPeriodParams,
            final LoanApplicationTerms loanApplicationTerm, Money installmentCapitalizedChargeAmount, boolean considerInstallmentFee) {
        LocalDate startDate = scheduleParams.getPeriodStartDate();
        applyChargesForCurrentPeriod(loanCharges, currency, scheduleParams, scheduledDueDate, currentPeriodParams, startDate,
                loanApplicationTerm, installmentCapitalizedChargeAmount, considerInstallmentFee);
    }

    private void applyChargesForCurrentPeriod(final Set<LoanCharge> loanCharges, final MonetaryCurrency currency,
            LoanScheduleParams scheduleParams, LocalDate scheduledDueDate, ScheduleCurrentPeriodParams currentPeriodParams,
            LocalDate startDate, final LoanApplicationTerms loanApplicationTerm, Money installmentCapitalizedChargeAmount, boolean considerInstallmentFee) {
        PrincipalInterest principalInterest = new PrincipalInterest(currentPeriodParams.getPrincipalForThisPeriod(),
                currentPeriodParams.getInterestForThisPeriod(), null);
        currentPeriodParams.setFeeChargesForInstallment(
                cumulativeFeeChargesDueWithin(startDate, scheduledDueDate, loanCharges, currency,
                        principalInterest, scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativeInterest(), considerInstallmentFee,
                        loanApplicationTerm, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount));
        currentPeriodParams.setPenaltyChargesForInstallment(
                cumulativePenaltyChargesDueWithin(startDate, scheduledDueDate, loanCharges, currency,
                        principalInterest, scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativeInterest(), considerInstallmentFee,
                        loanApplicationTerm, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount));
        scheduleParams.addTotalFeeChargesCharged(currentPeriodParams.getFeeChargesForInstallment());
        scheduleParams.addTotalPenaltyChargesCharged(currentPeriodParams.getPenaltyChargesForInstallment());
    }

    private void updatePeriodsWithCharges(final MonetaryCurrency currency, LoanScheduleParams scheduleParams,
            final Collection<LoanScheduleModelPeriod> periods, final Set<LoanCharge> nonCompoundingCharges, LoanApplicationTerms loanApplicationTerms) {
        for (LoanScheduleModelPeriod loanScheduleModelPeriod : periods) {
            if (loanScheduleModelPeriod.isRepaymentPeriod()) {
            	Money installmentCapitalizedChargeAmount = null;
                PrincipalInterest principalInterest = new PrincipalInterest(Money.of(currency, loanScheduleModelPeriod.principalDue()),
                        Money.of(currency, loanScheduleModelPeriod.interestDue()), null);
                Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                        loanScheduleModelPeriod.periodDueDate(), nonCompoundingCharges, currency, principalInterest,
                        scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativeInterest(),
                        !loanScheduleModelPeriod.isRecalculatedInterestComponent(), loanApplicationTerms, loanScheduleModelPeriod.periodNumber(), installmentCapitalizedChargeAmount);
                Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(loanScheduleModelPeriod.periodFromDate(),
                        loanScheduleModelPeriod.periodDueDate(), nonCompoundingCharges, currency, principalInterest,
                        scheduleParams.getPrincipalToBeScheduled(), scheduleParams.getTotalCumulativeInterest(),
                        !loanScheduleModelPeriod.isRecalculatedInterestComponent(), loanApplicationTerms, loanScheduleModelPeriod.periodNumber(), installmentCapitalizedChargeAmount);
                scheduleParams.addTotalFeeChargesCharged(feeChargesForInstallment);
                scheduleParams.addTotalPenaltyChargesCharged(penaltyChargesForInstallment);
                scheduleParams.addTotalRepaymentExpected(feeChargesForInstallment.plus(penaltyChargesForInstallment));
                loanScheduleModelPeriod.addLoanCharges(feeChargesForInstallment.getAmount(), penaltyChargesForInstallment.getAmount());
            }
        }
    }

    private void updateAmountsWithEffectiveDate(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            LoanScheduleParams scheduleParams, LocalDate scheduledDueDate, ScheduleCurrentPeriodParams currentPeriodParams,
            LoanScheduleModelPeriod installment, LocalDate lastRestDate) {
        LocalDate amountApplicableDate = installment.periodDueDate();
        if (loanApplicationTerms.isInterestRecalculationEnabled()) {
            amountApplicableDate = getNextRestScheduleDate(installment.periodDueDate().minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }
        updateMapWithAmount(scheduleParams.getPrincipalPortionMap(),
                currentPeriodParams.getPrincipalForThisPeriod().minus(currentPeriodParams.getReducedBalance()), amountApplicableDate);
        updateCompoundingMap(loanApplicationTerms, holidayDetailDTO, scheduleParams, lastRestDate, scheduledDueDate);

        // update outstanding balance for interest calculation
        updateOutstandingBalanceAsPerRest(scheduleParams, scheduledDueDate);
    }

    private LoanScheduleModelPeriod handleRecalculationForTransactions(final MathContext mc,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO, final MonetaryCurrency currency,
            final LoanScheduleParams scheduleParams,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final Money totalInterestChargedForFullLoanTerm, final LocalDate lastRestDate, final LocalDate scheduledDueDate,
            final LocalDate periodStartDateApplicableForInterest, final Collection<RecalculationDetail> applicableTransactions,
            final ScheduleCurrentPeriodParams currentPeriodParams, final Money lastTotalOutstandingInterestPaymentDueToGrace,
            final LoanScheduleModelPeriod installment, Set<LoanCharge> loanCharges) {
        LoanScheduleModelPeriod modifiedInstallment = installment;
        if (scheduleParams.applyInterestRecalculation() && loanRepaymentScheduleTransactionProcessor != null) {
            Money principalProcessed = Money.zero(currency);
            for (RecalculationDetail detail : applicableTransactions) {
                if (!detail.isProcessed()) {
                    LocalDate transactionDate = detail.getTransactionDate();
                    List<LoanTransaction> currentTransactions = new ArrayList<>(2);
                    currentTransactions.add(detail.getTransaction());
                    // applies the transaction as per transaction strategy
                    // on scheduled installments to identify the
                    // unprocessed(early payment ) amounts
                    Money unprocessed = loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(currentTransactions, currency,
                            scheduleParams.getInstallments());
                    boolean handlePrePayment = unprocessed.isGreaterThanZero();
                    Money outstandingInCurrentInstallment = Money.zero(currency);
                    if (scheduleParams.getOutstandingBalance().isZero() && !handlePrePayment) {
                        LoanRepaymentScheduleInstallment lastInstallment = scheduleParams.getInstallments().get(
                                scheduleParams.getInstallments().size() - 1);
                        outstandingInCurrentInstallment = lastInstallment.getTotalOutstanding(currency);
                        if (outstandingInCurrentInstallment.isLessThan(lastInstallment.getInterestCharged(currency))) {
                            handlePrePayment = true;
                        }
                    }
                    if (handlePrePayment) {
                        scheduleParams.reduceOutstandingBalance(unprocessed);
                        // pre closure check and processing
                        modifiedInstallment = handlePrepaymentOfLoan(mc, loanApplicationTerms, holidayDetailDTO, scheduleParams,
                                totalInterestChargedForFullLoanTerm, scheduledDueDate, periodStartDateApplicableForInterest,
                                currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(), currentPeriodParams,
                                lastTotalOutstandingInterestPaymentDueToGrace, transactionDate, modifiedInstallment, loanCharges,
                                outstandingInCurrentInstallment);

                        Money addToPrinciapal = Money.zero(currency);
                        if (scheduleParams.getOutstandingBalance().isLessThanZero()) {
                            addToPrinciapal = addToPrinciapal.plus(scheduleParams.getOutstandingBalance());
                            scheduleParams.setOutstandingBalance(Money.zero(currency));
                        }
                        updateAmountsBasedOnEarlyPayment(loanApplicationTerms, holidayDetailDTO, scheduleParams, modifiedInstallment,
                                detail, unprocessed, addToPrinciapal);

                        scheduleParams.addReducePrincipal(unprocessed);
                        currentPeriodParams.plusPrincipalForThisPeriod(unprocessed.plus(addToPrinciapal));
                        principalProcessed = principalProcessed.plus(unprocessed.plus(addToPrinciapal));
                        BigDecimal fixedEmiAmount = loanApplicationTerms.getFixedEmiAmount();
                        scheduleParams.setReducePrincipal(applyEarlyPaymentStrategy(
                                loanApplicationTerms,
                                scheduleParams.getReducePrincipal(),
                                scheduleParams.getTotalCumulativePrincipal().plus(
                                        currentPeriodParams.getPrincipalForThisPeriod().minus(principalProcessed)),
                                scheduleParams.getPeriodNumber() + 1, mc, scheduleParams.getPrincipalToBeScheduled()));
                        if (loanApplicationTerms.getAmortizationMethod().isEqualInstallment()
                                && fixedEmiAmount.compareTo(loanApplicationTerms.getFixedEmiAmount()) != 0) {
                            currentPeriodParams.setEmiAmountChanged(true);
                        }

                    }
                    adjustCompoundedAmountWithPaidDetail(scheduleParams, lastRestDate, currentTransactions, loanApplicationTerms,
                            holidayDetailDTO);
                }
            }
            updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams.getLatePaymentMap(), scheduledDueDate,
                    scheduleParams.getInstallments(), true, lastRestDate);
            currentPeriodParams.minusPrincipalForThisPeriod(principalProcessed);
        }
        return modifiedInstallment;
    }

    private LoanScheduleModelPeriod handlePrepaymentOfLoan(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO, final LoanScheduleParams scheduleParams,
            final Money totalInterestChargedForFullLoanTerm, final LocalDate scheduledDueDate,
            LocalDate periodStartDateApplicableForInterest, final double interestCalculationGraceOnRepaymentPeriodFraction,
            final ScheduleCurrentPeriodParams currentPeriodParams, final Money lastTotalOutstandingInterestPaymentDueToGrace,
            final LocalDate transactionDate, final LoanScheduleModelPeriod installment, Set<LoanCharge> loanCharges,
            final Money outstandingInCurrentInstallment) {
        LoanScheduleModelPeriod modifiedInstallment = installment;
        Money oustanding = scheduleParams.getOutstandingBalance();
        PrincipalInterest tempPrincipalInterest = new PrincipalInterest(currentPeriodParams.getPrincipalForThisPeriod(),
                currentPeriodParams.getInterestForThisPeriod(), null);
        Money installmentCapitalizedChargeAmount = null;
        oustanding = oustanding.minus(cumulativeFeeChargesDueWithin(transactionDate,
                scheduledDueDate, loanCharges, totalInterestChargedForFullLoanTerm.getCurrency(), tempPrincipalInterest, scheduleParams.getPrincipalToBeScheduled(),
                scheduleParams.getTotalCumulativeInterest(), true, loanApplicationTerms, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount));
        oustanding = oustanding.minus(cumulativePenaltyChargesDueWithin(transactionDate,
                scheduledDueDate, loanCharges, totalInterestChargedForFullLoanTerm.getCurrency(), tempPrincipalInterest, scheduleParams.getPrincipalToBeScheduled(),
                scheduleParams.getTotalCumulativeInterest(), true, loanApplicationTerms, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount));
        
        if (!oustanding.isGreaterThan(currentPeriodParams.getInterestForThisPeriod())
                && !scheduledDueDate.equals(transactionDate)) {
            final Collection<LoanTermVariationsData> interestRates = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();
            LocalDate calculateTill = transactionDate;
            if (loanApplicationTerms.getPreClosureInterestCalculationStrategy().calculateTillRestFrequencyEnabled()) {
                calculateTill = getNextRestScheduleDate(calculateTill.minusDays(1), loanApplicationTerms, holidayDetailDTO);
            }
            if (scheduleParams.getCompoundingDateVariations().containsKey(periodStartDateApplicableForInterest)) {
                scheduleParams.getCompoundingMap().clear();
                scheduleParams.getCompoundingMap().putAll(
                        scheduleParams.getCompoundingDateVariations().get(periodStartDateApplicableForInterest));
            }
            if (currentPeriodParams.isEmiAmountChanged()) {
                updateFixedInstallmentAmount(mc, loanApplicationTerms, scheduleParams.getPeriodNumber(), loanApplicationTerms
                        .getPrincipal().minus(scheduleParams.getTotalCumulativePrincipal()));
            }
            
            scheduleParams.getCompoundingDateVariations().put(periodStartDateApplicableForInterest,
                    new TreeMap<>(scheduleParams.getCompoundingMap()));
            scheduleParams.getCompoundingMap().clear();
            populateCompoundingDatesInPeriod(periodStartDateApplicableForInterest, calculateTill, loanApplicationTerms, holidayDetailDTO,
                    scheduleParams, loanCharges, totalInterestChargedForFullLoanTerm.getCurrency());
            
            // this is to make sure we are recalculating using correct interest rate 
            // once calculation is done system will set the actual interest rate
            BigDecimal currentInterestRate = loanApplicationTerms.getAnnualNominalInterestRate();
            for(LoanTermVariationsData interestRate : interestRates){
                if(interestRate.isApplicable(periodStartDateApplicableForInterest)){
                    loanApplicationTerms.updateAnnualNominalInterestRate(interestRate.getDecimalValue());
                }
            }
            
            PrincipalInterest interestTillDate = calculatePrincipalInterestComponentsForPeriod(this.paymentPeriodsInOneYearCalculator,
                    interestCalculationGraceOnRepaymentPeriodFraction, scheduleParams.getTotalCumulativePrincipal(),
                    scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                    lastTotalOutstandingInterestPaymentDueToGrace, scheduleParams.getOutstandingBalanceAsPerRest(), loanApplicationTerms,
                    scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams), scheduleParams.getCompoundingMap(),
                    periodStartDateApplicableForInterest, calculateTill, interestRates);
            loanApplicationTerms.updateAnnualNominalInterestRate(currentInterestRate);
            
            // applies charges for the period
            final ScheduleCurrentPeriodParams tempPeriod = new ScheduleCurrentPeriodParams(
                    totalInterestChargedForFullLoanTerm.getCurrency(), interestCalculationGraceOnRepaymentPeriodFraction);
            tempPeriod.setInterestForThisPeriod(interestTillDate.interest());
            boolean considerInstallmentFee = false;
            applyChargesForCurrentPeriod(loanCharges, totalInterestChargedForFullLoanTerm.getCurrency(), scheduleParams, calculateTill,
                    tempPeriod, loanApplicationTerms, installmentCapitalizedChargeAmount, considerInstallmentFee);
            Money interestDiff = currentPeriodParams.getInterestForThisPeriod().minus(tempPeriod.getInterestForThisPeriod());
            Money chargeDiff = currentPeriodParams.getFeeChargesForInstallment().minus(tempPeriod.getFeeChargesForInstallment());
            Money penaltyDiff = currentPeriodParams.getPenaltyChargesForInstallment().minus(tempPeriod.getPenaltyChargesForInstallment());

            Money diff = interestDiff.plus(chargeDiff).plus(penaltyDiff).minus(outstandingInCurrentInstallment);
            if (scheduleParams.getOutstandingBalance().minus(diff).isGreaterThanZero()) {
                updateCompoundingDetails(scheduleParams, periodStartDateApplicableForInterest);
            } else {
                scheduleParams.reduceOutstandingBalance(diff);
                currentPeriodParams.minusInterestForThisPeriod(interestDiff);
                currentPeriodParams.minusFeeChargesForInstallment(chargeDiff);
                currentPeriodParams.minusPenaltyChargesForInstallment(penaltyDiff);
                currentPeriodParams.plusPrincipalForThisPeriod(diff);

                // create and replaces repayment period
                // from parts
                modifiedInstallment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                        scheduleParams.getPeriodStartDate(), transactionDate, currentPeriodParams.getPrincipalForThisPeriod(),
                        scheduleParams.getOutstandingBalance(), currentPeriodParams.getInterestForThisPeriod(),
                        currentPeriodParams.getFeeChargesForInstallment(), currentPeriodParams.getPenaltyChargesForInstallment(),
                        currentPeriodParams.fetchTotalAmountForPeriod(), true);
                scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(interestTillDate.interestPaymentDueToGrace());
            }
        }
        return modifiedInstallment;
    }

    private void updateAmountsBasedOnCurrentEarlyPayments(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            LoanScheduleParams scheduleParams, ScheduleCurrentPeriodParams currentPeriodParams) {
        currentPeriodParams.setReducedBalance(currentPeriodParams.getEarlyPaidAmount());
        currentPeriodParams.minusEarlyPaidAmount(currentPeriodParams.getPrincipalForThisPeriod());
        if (currentPeriodParams.getEarlyPaidAmount().isGreaterThanZero()) {
            scheduleParams.addReducePrincipal(currentPeriodParams.getEarlyPaidAmount());
            BigDecimal fixedEmiAmount = loanApplicationTerms.getFixedEmiAmount();
            scheduleParams.setReducePrincipal(applyEarlyPaymentStrategy(
                    loanApplicationTerms,
                    scheduleParams.getReducePrincipal(),
                    scheduleParams.getTotalCumulativePrincipal().plus(currentPeriodParams.getPrincipalForThisPeriod())
                            .plus(currentPeriodParams.getEarlyPaidAmount()).plus(currentPeriodParams.getPrepaymentAmount()),
                    scheduleParams.getPeriodNumber() + 1, mc, scheduleParams.getPrincipalToBeScheduled()));
            if (loanApplicationTerms.getAmortizationMethod().isEqualInstallment()
                    && fixedEmiAmount.compareTo(loanApplicationTerms.getFixedEmiAmount()) != 0) {
                currentPeriodParams.setEmiAmountChanged(true);
            }
            currentPeriodParams.plusPrincipalForThisPeriod(currentPeriodParams.getEarlyPaidAmount());
        }

        // update outstandingLoanBlance using current period
        // 'principalDue'
        scheduleParams.reduceOutstandingBalance(currentPeriodParams.getPrincipalForThisPeriod().minus(
                currentPeriodParams.getReducedBalance()));
    }

    private void updatePrincipalPortionBasedOnPreviousEarlyPayments(final MonetaryCurrency currency,
            final LoanScheduleParams scheduleParams, final ScheduleCurrentPeriodParams currentPeriodParams) {
        if (currentPeriodParams.getPrincipalForThisPeriod().isGreaterThan(scheduleParams.getReducePrincipal())) {
            currentPeriodParams.minusPrincipalForThisPeriod(scheduleParams.getReducePrincipal());
            scheduleParams.setReducePrincipal(Money.zero(currency));
        } else {
            scheduleParams.reduceReducePrincipal(currentPeriodParams.getPrincipalForThisPeriod());
            currentPeriodParams.setPrincipalForThisPeriod(Money.zero(currency));
        }
    }

    private void updateCompoundingDetails(LoanScheduleParams scheduleParams, LocalDate periodStartDateApplicableForInterest) {
        if (scheduleParams.getCompoundingDateVariations().containsKey(periodStartDateApplicableForInterest)) {
            scheduleParams.getCompoundingMap().clear();
            scheduleParams.getCompoundingMap()
                    .putAll(scheduleParams.getCompoundingDateVariations().get(periodStartDateApplicableForInterest));
            scheduleParams.getCompoundingDateVariations().remove(periodStartDateApplicableForInterest);
        }
    }

    private void handleRecalculationForNonDueDateTransactions(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO, LoanScheduleParams scheduleParams,
            final Collection<LoanScheduleModelPeriod> periods, final Money totalInterestChargedForFullLoanTerm,
            final LocalDate idealDisbursementDate, LocalDate firstRepaymentdate, final LocalDate lastRestDate,
            final LocalDate scheduledDueDate, final LocalDate periodStartDateForInterest,
            final Collection<RecalculationDetail> applicableTransactions, final ScheduleCurrentPeriodParams currentPeriodParams) {
        if (scheduleParams.applyInterestRecalculation()) {
            final MonetaryCurrency currency = scheduleParams.getCurrency();
            final Collection<LoanTermVariationsData> interestRates = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();
            boolean checkForOutstanding = true;
            List<RecalculationDetail> unprocessedTransactions = new ArrayList<>();
            List<RecalculationDetail> processTransactions = new ArrayList<>();
            LoanScheduleModelPeriod installment = null;
            LocalDate periodStartDateApplicableForInterest = periodStartDateForInterest;
            for (RecalculationDetail detail : applicableTransactions) {
                if (detail.isProcessed()) {
                    continue;
                }
                boolean updateLatePaymentMap = false;
                final LocalDate transactionDate = detail.getTransactionDate();
                boolean isPrepay = detail.getTransaction().getTransactionSubTye().isPrePayment();
                
                if (transactionDate.isBefore(scheduledDueDate)) {
                    if (isPrepay) {
                        LocalDate applicableDate = getNextRestScheduleDate(transactionDate.minusDays(1), loanApplicationTerms,
                                holidayDetailDTO);
                        Money amount = detail.getTransaction().getAmount(currency);
                        currentPeriodParams.plusPrepaymentAmount(amount);
                        applyEarlyPaymentStrategy(loanApplicationTerms, amount,
                                scheduleParams.getTotalCumulativePrincipal().plus(currentPeriodParams.getPrepaymentAmount()),
                                scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled());
                        checkForOutstanding = false;
                        scheduleParams.reduceOutstandingBalance(amount);
                        if (applicableDate.isBefore(scheduledDueDate)) {
                            updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), amount, applicableDate);

                            LoanTransaction loanTransaction = LoanTransaction.prepayment(null, amount, null, scheduledDueDate,
                                    null);
                            RecalculationDetail recalculationDetail = new RecalculationDetail(scheduledDueDate, loanTransaction);
                            unprocessedTransactions.add(recalculationDetail);
                            detail.setProcessed(true);
                            LocalDate newapplicableDate = getNextRestScheduleDate(scheduledDueDate.minusDays(1), loanApplicationTerms,
                                    holidayDetailDTO);
                            updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), amount.negated(), newapplicableDate);
                        }

                    }else  if (scheduleParams.getLoanRepaymentScheduleTransactionProcessor() != null
                            && scheduleParams.getLoanRepaymentScheduleTransactionProcessor()
                                    .isInterestFirstRepaymentScheduleTransactionProcessor()) {
                        if (detail.getTransaction().isWaiver()) {
                            processTransactions.add(detail);
                            continue;
                        }
                        List<LoanTransaction> currentTransactions = new ArrayList<>();
                        for (RecalculationDetail processDetail : processTransactions) {
                            currentTransactions.addAll(createCurrentTransactionList(processDetail));
                        }
                        processTransactions.clear();
                        currentTransactions.addAll(createCurrentTransactionList(detail));

                        if (!transactionDate.isEqual(scheduleParams.getPeriodStartDate()) || scheduleParams.getInstalmentNumber() == 1) {

                            int periodDays = Days.daysBetween(scheduleParams.getPeriodStartDate(), transactionDate).getDays();
                            // calculates period start date for interest
                            // calculation as per the configuration
                            periodStartDateApplicableForInterest = this.scheduledDateGenerator.calculateInterestStartDateForPeriod(loanApplicationTerms,
                                    scheduleParams.getPeriodStartDate(), idealDisbursementDate, firstRepaymentdate);

                            int daysInPeriodApplicable = Days.daysBetween(periodStartDateApplicableForInterest, transactionDate).getDays();
                            Money interestForThisinstallment = Money.zero(currency);
                            if (daysInPeriodApplicable > 0) {
                                // 5 determine interest till the transaction
                                // date
                                PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                                        this.paymentPeriodsInOneYearCalculator,
                                        currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(), scheduleParams
                                                .getTotalCumulativePrincipal().minus(scheduleParams.getReducePrincipal()),
                                        scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                                        scheduleParams.getTotalOutstandingInterestPaymentDueToGrace(),
                                        scheduleParams.getOutstandingBalanceAsPerRest(), loanApplicationTerms,
                                        scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams),
                                        scheduleParams.getCompoundingMap(), periodStartDateApplicableForInterest, transactionDate,
                                        interestRates);
                                interestForThisinstallment = principalInterestForThisPeriod.interest();

                                scheduleParams.setTotalOutstandingInterestPaymentDueToGrace(principalInterestForThisPeriod
                                        .interestPaymentDueToGrace());
                            }

                            Money principalForThisPeriod = Money.zero(currency);

                            // applies all the applicable charges to the
                            // newly
                            // created installment
                            PrincipalInterest principalInterest = new PrincipalInterest(principalForThisPeriod, interestForThisinstallment,
                                    null);
                            Money installmentCapitalizedChargeAmount = null;
                            Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(scheduleParams.getPeriodStartDate(),
                                    transactionDate, loanCharges, currency, principalInterest, scheduleParams.getPrincipalToBeScheduled(),
                                    scheduleParams.getTotalCumulativeInterest(), false, loanApplicationTerms, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount);
                            Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(scheduleParams.getPeriodStartDate(),
                                    transactionDate, loanCharges, currency, principalInterest, scheduleParams.getPrincipalToBeScheduled(),
                                    scheduleParams.getTotalCumulativeInterest(), false, loanApplicationTerms, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount);

                            // sum up real totalInstallmentDue from
                            // components
                            final Money totalInstallmentDue = principalForThisPeriod.plus(interestForThisinstallment)
                                    .plus(feeChargesForInstallment).plus(penaltyChargesForInstallment);
                            // create repayment period from parts
                            installment = LoanScheduleModelRepaymentPeriod.repayment(scheduleParams.getInstalmentNumber(),
                                    scheduleParams.getPeriodStartDate(), transactionDate, principalForThisPeriod,
                                    scheduleParams.getOutstandingBalance(), interestForThisinstallment, feeChargesForInstallment,
                                    penaltyChargesForInstallment, totalInstallmentDue, true);
                            periods.add(installment);
                            addLoanRepaymentScheduleInstallment(scheduleParams.getInstallments(), installment);
                            updateCompoundingMap(loanApplicationTerms, holidayDetailDTO, scheduleParams, lastRestDate, scheduledDueDate);

                            // update outstanding balance for interest
                            // calculation as per the rest
                            updateOutstandingBalanceAsPerRest(scheduleParams, transactionDate);

                            // handle cumulative fields
                            scheduleParams.addLoanTermInDays(periodDays);
                            scheduleParams.addTotalRepaymentExpected(totalInstallmentDue);
                            scheduleParams.addTotalCumulativeInterest(interestForThisinstallment);
                            scheduleParams.addTotalFeeChargesCharged(feeChargesForInstallment);
                            scheduleParams.addTotalPenaltyChargesCharged(penaltyChargesForInstallment);

                            scheduleParams.setPeriodStartDate(transactionDate);
                            periodStartDateApplicableForInterest = scheduleParams.getPeriodStartDate();
                            updateLatePaymentMap = true;
                            scheduleParams.incrementInstalmentNumber();
                            populateCompoundingDatesInPeriod(scheduleParams.getPeriodStartDate(), scheduledDueDate, loanApplicationTerms,
                                    holidayDetailDTO, scheduleParams, loanCharges, currency);
                            // creates and insert Loan repayment schedule
                            // for
                            // the period

                        } else if (installment == null) {
                            installment = ((List<LoanScheduleModelPeriod>) periods).get(periods.size() - 1);
                        }
                        // applies the transaction as per transaction
                        // strategy
                        // on scheduled installments to identify the
                        // unprocessed(early payment ) amounts
                        Money unprocessed = scheduleParams.getLoanRepaymentScheduleTransactionProcessor().handleRepaymentSchedule(
                                currentTransactions, currency, scheduleParams.getInstallments());
                        if (unprocessed.isGreaterThanZero()) {

                            if (loanApplicationTerms.getPreClosureInterestCalculationStrategy().calculateTillRestFrequencyEnabled()) {
                                LocalDate applicableDate = getNextRestScheduleDate(transactionDate.minusDays(1), loanApplicationTerms,
                                        holidayDetailDTO);
                                checkForOutstanding = transactionDate.isEqual(applicableDate);

                            }
                            // reduces actual outstanding balance
                            scheduleParams.reduceOutstandingBalance(unprocessed);
                            // if outstanding balance becomes less than zero
                            // then adjusts the princiapal
                            Money addToPrinciapal = Money.zero(currency);
                            if (!scheduleParams.getOutstandingBalance().isGreaterThanZero()) {
                                addToPrinciapal = addToPrinciapal.plus(scheduleParams.getOutstandingBalance());
                                scheduleParams.setOutstandingBalance(Money.zero(currency));
                                currentPeriodParams.setLastInstallment(installment);
                            }
                            // updates principal portion map with the early
                            // payment amounts and applicable date as per
                            // rest
                            updateAmountsBasedOnEarlyPayment(loanApplicationTerms, holidayDetailDTO, scheduleParams, installment, detail,
                                    unprocessed, addToPrinciapal);

                            // method applies early payment strategy
                            scheduleParams.addReducePrincipal(unprocessed);
                            scheduleParams.setReducePrincipal(applyEarlyPaymentStrategy(loanApplicationTerms,
                                    scheduleParams.getReducePrincipal(), scheduleParams.getTotalCumulativePrincipal(),
                                    scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled()));
                        }
                        // identify late payments and add compounding
                        // details to
                        // map for interest calculation
                        handleLatePayments(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams, lastRestDate, detail);
                        if (updateLatePaymentMap) {
                            updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams.getLatePaymentMap(),
                                    scheduledDueDate, scheduleParams.getInstallments(), true, lastRestDate);
                        }
                    } else if (scheduleParams.getLoanRepaymentScheduleTransactionProcessor() != null) {
                        LocalDate applicableDate = getNextRestScheduleDate(transactionDate.minusDays(1), loanApplicationTerms,
                                holidayDetailDTO);
                        if (applicableDate.isBefore(scheduledDueDate)) {
                            List<LoanTransaction> currentTransactions = createCurrentTransactionList(detail);
                            Money unprocessed = scheduleParams.getLoanRepaymentScheduleTransactionProcessor().handleRepaymentSchedule(
                                    currentTransactions, currency, scheduleParams.getInstallments());
                            Money arrears = fetchArrears(loanApplicationTerms, currency, detail.getTransaction());
                            if (unprocessed.isGreaterThanZero()) {
                                updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), unprocessed, applicableDate);
                                currentPeriodParams.plusEarlyPaidAmount(unprocessed);

                                // this check is to identify pre-closure and
                                // apply interest calculation as per
                                // configuration for non due date payments
                                if (!scheduleParams.getOutstandingBalance().isGreaterThan(unprocessed)
                                        && !loanApplicationTerms.getPreClosureInterestCalculationStrategy()
                                                .calculateTillRestFrequencyEnabled()) {
                                    
                                    scheduleParams.getCompoundingDateVariations().put(periodStartDateApplicableForInterest,
                                            new TreeMap<>(scheduleParams.getCompoundingMap()));
                                    LocalDate calculateTill = transactionDate;
                                    PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                                            this.paymentPeriodsInOneYearCalculator,
                                            currentPeriodParams.getInterestCalculationGraceOnRepaymentPeriodFraction(), scheduleParams
                                                    .getTotalCumulativePrincipal().minus(scheduleParams.getReducePrincipal()),
                                            scheduleParams.getTotalCumulativeInterest(), totalInterestChargedForFullLoanTerm,
                                            scheduleParams.getTotalOutstandingInterestPaymentDueToGrace(),
                                            scheduleParams.getOutstandingBalanceAsPerRest(), loanApplicationTerms,
                                            scheduleParams.getPeriodNumber(), mc, mergeVariationsToMap(scheduleParams),
                                            scheduleParams.getCompoundingMap(), periodStartDateApplicableForInterest, calculateTill,
                                            interestRates);
                                    if (!principalInterestForThisPeriod.interest()
                                            .plus(principalInterestForThisPeriod.interestPaymentDueToGrace())
                                            .plus(scheduleParams.getOutstandingBalance())
                                            .isGreaterThan(unprocessed.plus(scheduleParams.getUnProcessedAmountPostOutstandingBalance()))) {
                                        currentPeriodParams.minusEarlyPaidAmount(unprocessed);
                                        updateMapWithAmount(scheduleParams.getPrincipalPortionMap(), unprocessed.negated(), applicableDate);
                                        LoanTransaction loanTransaction = LoanTransaction.repayment(null, unprocessed, null,
                                                transactionDate, null);
                                        RecalculationDetail recalculationDetail = new RecalculationDetail(transactionDate, loanTransaction);
                                        unprocessedTransactions.add(recalculationDetail);
                                        break;
                                    }
                                }
                                LoanTransaction loanTransaction = LoanTransaction.repayment(null, unprocessed, null, scheduledDueDate,
                                        null);
                                RecalculationDetail recalculationDetail = new RecalculationDetail(scheduledDueDate, loanTransaction);
                                unprocessedTransactions.add(recalculationDetail);
                                checkForOutstanding = false;

                                scheduleParams.reduceOutstandingBalance(unprocessed);
                                // if outstanding balance becomes less than
                                // zero
                                // then adjusts the princiapal
                                Money addToPrinciapal = Money.zero(currency);
                                if (scheduleParams.getOutstandingBalance().isLessThanZero()) {
                                    addToPrinciapal = addToPrinciapal.plus(scheduleParams.getOutstandingBalance());
                                    scheduleParams.setOutstandingBalance(Money.zero(currency));
                                    currentPeriodParams.plusEarlyPaidAmount(addToPrinciapal);
                                    scheduleParams.addUnProcessedAmountPostOutstandingBalance(addToPrinciapal.negated());
                                }

                            }
                                handleLatePayments(loanApplicationTerms, holidayDetailDTO, currency, scheduleParams, lastRestDate, detail);
                        }

                    }
                }

            }
            applicableTransactions.addAll(unprocessedTransactions);
            if (checkForOutstanding && scheduleParams.getOutstandingBalance().isZero() && scheduleParams.getDisburseDetailMap().isEmpty()) {
                currentPeriodParams.setSkipCurrentLoop(true);
            }
        }
    }

    /**
     * @param loanApplicationTerms
     * @param holidayDetailDTO
     * @param currency
     * @param scheduleParams
     * @param lastRestDate
     * @param periodStartDateApplicableForInterest
     * @param detail
     */
    private void handleLatePayments(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final MonetaryCurrency currency, LoanScheduleParams scheduleParams, LocalDate lastRestDate, RecalculationDetail detail) {
        updateLatePaidAmountsToPrincipalMap(detail.getTransaction(), loanApplicationTerms, currency, holidayDetailDTO, lastRestDate,
                scheduleParams);
    }

    private void updateAmountsBasedOnEarlyPayment(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            LoanScheduleParams scheduleParams, final LoanScheduleModelPeriod installment, RecalculationDetail detail, Money unprocessed,
            Money addToPrinciapal) {
        updatePrincipalPaidPortionToMap(loanApplicationTerms, holidayDetailDTO, scheduleParams.getPrincipalPortionMap(), installment,
                detail, unprocessed.plus(addToPrinciapal), scheduleParams.getInstallments());
        scheduleParams.addTotalRepaymentExpected(unprocessed.plus(addToPrinciapal));
        scheduleParams.addTotalCumulativePrincipal(unprocessed.plus(addToPrinciapal));
    }

    private void updateOutstandingBalanceAsPerRest(final LoanScheduleParams scheduleParams, final LocalDate scheduledDueDate) {
        scheduleParams.setOutstandingBalanceAsPerRest(updateBalanceForInterestCalculation(scheduleParams.getPrincipalPortionMap(),
                scheduledDueDate, scheduleParams.getOutstandingBalanceAsPerRest(), false));
        scheduleParams.setOutstandingBalanceAsPerRest(updateBalanceForInterestCalculation(scheduleParams.getDisburseDetailMap(),
                scheduledDueDate, scheduleParams.getOutstandingBalanceAsPerRest(), true));
    }

    /**
     * Method updates outstanding balance of the loan for interest calculation
     * 
     */
    private void updateBalanceBasedOnDisbursement(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, LoanScheduleParams scheduleParams,
            final Collection<LoanScheduleModelPeriod> periods, final LocalDate scheduledDueDate) {
        if(scheduleParams.isResetEMI()){
            adjustInstallmentOrPrincipalAmount(loanApplicationTerms, scheduleParams.getTotalCumulativePrincipal(), scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled());       
            scheduleParams.setResetEMI(false);
        }
        for (Map.Entry<LocalDate, Money> disburseDetail : scheduleParams.getDisburseDetailMap().entrySet()) {
            if (disburseDetail.getKey().isAfter(scheduleParams.getPeriodStartDate()) && !disburseDetail.getKey().isAfter(scheduledDueDate)) {
                // validation check for amount not exceeds specified max
                // amount as per the configuration
                if (loanApplicationTerms.getMaxOutstandingBalance() != null
                        && scheduleParams.getOutstandingBalance().plus(disburseDetail.getValue())
                                .isGreaterThan(loanApplicationTerms.getMaxOutstandingBalance())) {
                    String errorMsg = "Outstanding balance must not exceed the amount: " + loanApplicationTerms.getMaxOutstandingBalance();
                    throw new MultiDisbursementOutstandingAmoutException(errorMsg, loanApplicationTerms.getMaxOutstandingBalance()
                            .getAmount(), disburseDetail.getValue());
                }
                LocalDate disuresementDate = disburseDetail.getKey();
                
                BigDecimal chargesDueAtTimeOfDisbursement = fetchTotalTrancheChargeForDisburesement(loanCharges, disuresementDate);
                
                // creates and add disbursement detail to the repayments
                // period
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disburseDetail.getKey(), disburseDetail.getValue(), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                // updates actual outstanding balance with new
                // disbursement detail
                scheduleParams.addOutstandingBalance(disburseDetail.getValue());
                scheduleParams.addPrincipalToBeScheduled(disburseDetail.getValue());
                loanApplicationTerms.setPrincipal(loanApplicationTerms.getPrincipal().plus(disburseDetail.getValue()));
                if(loanApplicationTerms.isEmiBasedOnDisbursements()) {
                    if(disburseDetail.getKey().isBefore(scheduledDueDate)){
                	adjustInstallmentOrPrincipalAmount(loanApplicationTerms, scheduleParams.getTotalCumulativePrincipal(), scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled());	
                    }else{
                        scheduleParams.setResetEMI(true);
                    }
                }
                
            }
        }
    }

    public BigDecimal fetchTotalTrancheChargeForDisburesement(final Set<LoanCharge> loanCharges, LocalDate disuresementDate) {
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        for (LoanCharge charge : loanCharges) {
            if (charge.isTrancheDisbursementCharge()
                    && charge.getTrancheDisbursementCharge().getloanDisbursementDetails().getDisbursementDateAsLocalDate()
                            .isEqual(disuresementDate)) {
                chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(charge.amount());
            }
        }
        return chargesDueAtTimeOfDisbursement;
    }

    /**
     * @param loanApplicationTerms
     * @param scheduleParams
     * @param previousRepaymentDate
     * @param scheduledDueDate
     * @param interestRatesForInstallments 
     * @param paymentPeriodsInOneYearCalculator  
     * @param MathContext 
     * @param priviousScheduledDueDate
     * @param scheduleDateForReversal
     * @return
     */
    private LoanTermVariationParams applyLoanTermVariations(final LoanApplicationTerms loanApplicationTerms,
            final LoanScheduleParams scheduleParams, final LocalDate previousRepaymentDate, final LocalDate scheduledDueDate,
            Collection<LoanTermVariationsData> interestRatesForInstallments, PaymentPeriodsInOneYearCalculator calculator, MathContext mc) {
     boolean skipPeriod = false;
        boolean recalculateAmounts = false;
        LocalDate modifiedScheduledDueDate = scheduledDueDate;
        ArrayList<LoanTermVariationsData> variationsData = null;

        // due date changes should be applied only for that dueDate
        if (loanApplicationTerms.getLoanTermVariations().hasDueDateVariation(scheduledDueDate)) {
            LoanTermVariationsData loanTermVariationsData = loanApplicationTerms.getLoanTermVariations().nextDueDateVariation();
            if (loanTermVariationsData.getTermApplicableFrom().isEqual(modifiedScheduledDueDate)) {
                modifiedScheduledDueDate = loanTermVariationsData.getDateValue();
                if (!loanTermVariationsData.isSpecificToInstallment()) {
                    scheduleParams.setActualRepaymentDate(modifiedScheduledDueDate);
                    loanApplicationTerms.setSeedDate(modifiedScheduledDueDate);
                }
                loanTermVariationsData.setProcessed(true);
            }
        }
        
        for (LoanTermVariationsData variation : interestRatesForInstallments) {
            if (variation.isApplicable(modifiedScheduledDueDate) && variation.getDecimalValue() != null && !variation.isProcessed()) {
                loanApplicationTerms.updateAnnualNominalInterestRate(variation.getDecimalValue());
                if (loanApplicationTerms.getInterestMethod().isDecliningBalnce()) {
                    if (loanApplicationTerms.getActualFixedEmiAmount() == null) {
                        loanApplicationTerms.setFixedEmiAmount(null);
                    }
                } else {
                    Money totalInterestDueForLoan = Money.zero(loanApplicationTerms.getCurrency());
                    loanApplicationTerms.setTotalPrincipalAccounted(scheduleParams.getTotalCumulativePrincipal());
                    loanApplicationTerms.updateTotalInterestDue(null);
                    totalInterestDueForLoan = loanApplicationTerms.calculateTotalInterestCharged(calculator, mc);
                    totalInterestDueForLoan = totalInterestDueForLoan.plus(scheduleParams.getTotalCumulativeInterest());
                    loanApplicationTerms.updateTotalInterestDue(totalInterestDueForLoan);
                    // exclude till last period in calculations
                    int extendPeriods = 0;
                    loanApplicationTerms.updateAccountedTillPeriod(scheduleParams.getPeriodNumber() - 1,
                            scheduleParams.getTotalCumulativePrincipal(), scheduleParams.getTotalCumulativeInterest(),
                            extendPeriods);
                    loanApplicationTerms.updateExcludePeriodsForCalculation(scheduleParams.getPeriodNumber() - 1);

                }
                variation.setProcessed(true);
            }
        }

        while (loanApplicationTerms.getLoanTermVariations().hasVariation(modifiedScheduledDueDate)) {
            LoanTermVariationsData loanTermVariationsData = loanApplicationTerms.getLoanTermVariations().nextVariation();
            if (loanTermVariationsData.isProcessed()) {
                continue;
            }
            switch (loanTermVariationsData.getTermVariationType()) {
                case INSERT_INSTALLMENT:
                    scheduleParams.setActualRepaymentDate(previousRepaymentDate);
                    modifiedScheduledDueDate = loanTermVariationsData.getTermApplicableFrom();
                    if (loanTermVariationsData.getDecimalValue() != null) {
                        if (loanApplicationTerms.getInterestMethod().isDecliningBalnce()
                                && loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
                            loanApplicationTerms.setCurrentPeriodFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                        } else {
                            loanApplicationTerms.setCurrentPeriodFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                        }
                        recalculateAmounts = true;
                    }
                    loanTermVariationsData.setProcessed(true);
                break;
                case DELETE_INSTALLMENT:
                    if (loanTermVariationsData.getTermApplicableFrom().isEqual(modifiedScheduledDueDate)) {
                        skipPeriod = true;
                        loanTermVariationsData.setProcessed(true);
                    }
                break;
                case EMI_AMOUNT:
                    if (loanTermVariationsData.isSpecificToInstallment()) {
                        loanApplicationTerms.setCurrentPeriodFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                        recalculateAmounts = true;
                    } else {
                        loanApplicationTerms.setFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                        loanApplicationTerms.setActualFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                    }
                    loanTermVariationsData.setProcessed(true);
                break;
                case PRINCIPAL_AMOUNT:
                    if (loanTermVariationsData.isSpecificToInstallment()) {
                        loanApplicationTerms.setCurrentPeriodFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                        recalculateAmounts = true;
                    } else {
                        loanApplicationTerms.setFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                    }
                    loanTermVariationsData.setProcessed(true);
                break;
                case EXTEND_REPAYMENT_PERIOD:
                    Integer rescheduleNumberOfRepayments = loanApplicationTerms.getNumberOfRepayments();
                    rescheduleNumberOfRepayments += loanTermVariationsData.getDecimalValue().intValue();
                    loanApplicationTerms.updateNumberOfRepayments(rescheduleNumberOfRepayments);
                    LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms, loanApplicationTerms.getHolidayDetailDTO());
                    loanApplicationTerms.updateLoanEndDate(loanEndDate);
                    loanApplicationTerms.updateAccountedTillPeriod(scheduleParams.getPeriodNumber() - 1,
                            scheduleParams.getTotalCumulativePrincipal(), scheduleParams.getTotalCumulativeInterest(),
                            loanTermVariationsData.getDecimalValue().intValue());
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, scheduleParams.getTotalCumulativePrincipal(),
                            scheduleParams.getPeriodNumber(), mc, scheduleParams.getPrincipalToBeScheduled());
                    loanTermVariationsData.setProcessed(true);
                break;
                case GRACE_ON_PRINCIPAL:
                    loanApplicationTerms.updatePrincipalGrace(loanTermVariationsData.getDecimalValue().intValue());
                    Integer interestPaymentGrace = 0;
                    loanApplicationTerms.updateInterestPaymentGrace(interestPaymentGrace);
                    loanApplicationTerms.updatePeriodNumberApplicableForPrincipalOrInterestGrace(scheduleParams.getPeriodNumber());
                    loanTermVariationsData.setProcessed(true);
                break;
                case GRACE_ON_INTEREST:
                    loanApplicationTerms.updateInterestPaymentGrace(loanTermVariationsData.getDecimalValue().intValue());
                    Integer principalGrace = 0;
                    loanApplicationTerms.updatePrincipalGrace(principalGrace);
                    loanApplicationTerms.updatePeriodNumberApplicableForPrincipalOrInterestGrace(scheduleParams.getPeriodNumber());
                    loanApplicationTerms.updateTotalInterestAccounted(scheduleParams.getTotalCumulativeInterest());
                    loanTermVariationsData.setProcessed(true);
                break;
                default:
                break;

            }
        }
        LoanTermVariationParams termVariationParams = new LoanTermVariationParams(skipPeriod, recalculateAmounts, modifiedScheduledDueDate,
                variationsData);
        return termVariationParams;
    }

    /**
     * @param loanApplicationTerms
     * @param scheduledDueDate
     * @param exceptionDataListIterator
     * @param instalmentNumber TODO
     * @param totalCumulativePrincipal 
     * @param totalCumulativeInterest 
     * @param principalToBeScheduled TODO
     * @param MathContext 
     * @return
     */
    private LoanTermVariationParams applyExceptionLoanTermVariations(final LoanApplicationTerms loanApplicationTerms,
            final LocalDate scheduledDueDate, final ListIterator<LoanTermVariationsData> exceptionDataListIterator, final int instalmentNumber,
            final Money totalCumulativePrincipal, final Money totalCumulativeInterest, final MathContext mc, Money principalToBeScheduled) {
    boolean skipPeriod = false;
        boolean recalculateAmounts = false;
        LocalDate modifiedScheduledDueDate = scheduledDueDate;
        ArrayList<LoanTermVariationsData> variationsData = new ArrayList<>();
        
        for (LoanTermVariationsData variation : loanApplicationTerms.getLoanTermVariations().getInterestRateFromInstallment()) {
            if (variation.isApplicable(modifiedScheduledDueDate) && variation.getDecimalValue() != null && !variation.isProcessed()) {
                loanApplicationTerms.updateAnnualNominalInterestRate(variation.getDecimalValue());
                if (loanApplicationTerms.getInterestMethod().isDecliningBalnce()) {
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, instalmentNumber, mc, principalToBeScheduled);
                } else {
                    loanApplicationTerms.setTotalPrincipalAccounted(totalCumulativePrincipal);
                    loanApplicationTerms.updateExcludePeriodsForCalculation(instalmentNumber - 1);
                }
                variation.setProcessed(true);
            }
        }

        while (loanApplicationTerms.getLoanTermVariations().hasExceptionVariation(modifiedScheduledDueDate, exceptionDataListIterator)) {
            LoanTermVariationsData loanTermVariationsData = exceptionDataListIterator.next();
            if (loanTermVariationsData.isProcessed()) {
                continue;
            }
            switch (loanTermVariationsData.getTermVariationType()) {
                case INSERT_INSTALLMENT:
                    modifiedScheduledDueDate = loanTermVariationsData.getTermApplicableFrom();
                    if (loanTermVariationsData.getDecimalValue() != null) {
                        recalculateAmounts = true;
                    }
                    variationsData.add(loanTermVariationsData) ;
                break;
                case DELETE_INSTALLMENT:
                    if (loanTermVariationsData.getTermApplicableFrom().isEqual(modifiedScheduledDueDate)) {
                        skipPeriod = true;
                        variationsData.add(loanTermVariationsData) ;
                    }
                    
                break;
                case EMI_AMOUNT:
                    if(loanTermVariationsData.isSpecificToInstallment()){
                        recalculateAmounts = true;
                    } else {
                        loanApplicationTerms.setFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                        loanApplicationTerms.setActualFixedEmiAmount(loanTermVariationsData.getDecimalValue());
                    }
                    variationsData.add(loanTermVariationsData);
                break;
                case PRINCIPAL_AMOUNT:
                    if(loanTermVariationsData.isSpecificToInstallment()){
                        recalculateAmounts = true;
                    } else {
                        loanApplicationTerms.setFixedPrincipalAmount(loanTermVariationsData.getDecimalValue());
                    }
                    variationsData.add(loanTermVariationsData);
                break;
                case GRACE_ON_PRINCIPAL:
                    loanApplicationTerms.updatePrincipalGrace(loanTermVariationsData.getDecimalValue().intValue());
                    Integer interestPaymentGrace = 0;
                    loanApplicationTerms.updateInterestPaymentGrace(interestPaymentGrace);
                    loanApplicationTerms.updatePeriodNumberApplicableForPrincipalOrInterestGrace(instalmentNumber);
                    variationsData.add(loanTermVariationsData);
                break;
                case GRACE_ON_INTEREST:
                    loanApplicationTerms.updateInterestPaymentGrace(loanTermVariationsData.getDecimalValue().intValue());
                    Integer principalGrace = 0;
                    loanApplicationTerms.updatePrincipalGrace(principalGrace);
                    loanApplicationTerms.updatePeriodNumberApplicableForPrincipalOrInterestGrace(instalmentNumber);
                    loanApplicationTerms.updateTotalInterestAccounted(totalCumulativeInterest);
                    variationsData.add(loanTermVariationsData);
                break;
                case EXTEND_REPAYMENT_PERIOD:
                    Integer rescheduleNumberOfRepayments = loanApplicationTerms.getNumberOfRepayments();
                    rescheduleNumberOfRepayments += loanTermVariationsData.getDecimalValue().intValue();
                    loanApplicationTerms.updateNumberOfRepayments(rescheduleNumberOfRepayments);
                    // generate list of proposed schedule due dates
                    LocalDate loanEndDate = this.scheduledDateGenerator.getLastRepaymentDate(loanApplicationTerms,
                            loanApplicationTerms.getHolidayDetailDTO());
                    loanApplicationTerms.updateLoanEndDate(loanEndDate);
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, instalmentNumber, mc, principalToBeScheduled);
                    loanTermVariationsData.setProcessed(true);
                    loanApplicationTerms.updateAccountedTillPeriod(instalmentNumber-1, totalCumulativePrincipal, totalCumulativeInterest, loanTermVariationsData.getDecimalValue().intValue());
                break;
                default:
                break;

            }
        }
        LoanTermVariationParams termVariationParams = new LoanTermVariationParams(skipPeriod, recalculateAmounts, modifiedScheduledDueDate,
                variationsData);
        return termVariationParams;
    }

    /**
     * @param loanApplicationTerms
     * @param scheduleParams
     * @param interestRates
     * @param interestRatesForInstallments TODO
     */
    private void applyLoanVariationsForPartialScheduleGenerate(final LoanApplicationTerms loanApplicationTerms,
            LoanScheduleParams scheduleParams, final Collection<LoanTermVariationsData> interestRates,
            final Collection<LoanTermVariationsData> interestRatesForInstallments) {
        // Applies loan variations
        while (loanApplicationTerms.getLoanTermVariations().hasVariation(scheduleParams.getPeriodStartDate())) {
            LoanTermVariationsData variation = loanApplicationTerms.getLoanTermVariations().nextVariation();
            if (!variation.isSpecificToInstallment()) {
                switch (variation.getTermVariationType()) {
                    case EMI_AMOUNT:
                        loanApplicationTerms.setFixedEmiAmount(variation.getDecimalValue());
                        loanApplicationTerms.setActualFixedEmiAmount(variation.getDecimalValue());
                    break;
                    case PRINCIPAL_AMOUNT:
                        loanApplicationTerms.setFixedPrincipalAmount(variation.getDecimalValue());
                    break;
                    default:
                    break;
                }
            }

            variation.setProcessed(true);
        }

        // Applies interest rate changes
        for (LoanTermVariationsData variation : interestRates) {
            if (variation.getTermVariationType().isInterestRateVariation() && variation.isApplicable(scheduleParams.getPeriodStartDate())
                    && variation.getDecimalValue() != null) {
                loanApplicationTerms.updateAnnualNominalInterestRate(variation.getDecimalValue());
            }
        }
        
        // Applies interest rate changes for installments
        for (LoanTermVariationsData variation : interestRatesForInstallments) {
            if (variation.getTermVariationType().isInterestRateFromInstallment() && variation.isApplicable(scheduleParams.getPeriodStartDate())
                    && variation.getDecimalValue() != null) {
                loanApplicationTerms.updateAnnualNominalInterestRate(variation.getDecimalValue());
                variation.setProcessed(true);
            }
        }
    }

    /**
     * this method calculates the principal amount for generating the repayment
     * schedule.
     */
    private Money getPrincipalToBeScheduled(final LoanApplicationTerms loanApplicationTerms) {
        Money principalToBeScheduled;
        if (loanApplicationTerms.isMultiDisburseLoan() && loanApplicationTerms.getApprovedPrincipal().isGreaterThanZero()
                && !loanApplicationTerms.isEmiBasedOnDisbursements()) {
            principalToBeScheduled = loanApplicationTerms.getApprovedPrincipal();
        } else {
            principalToBeScheduled = loanApplicationTerms.getPrincipal();
        }
        return principalToBeScheduled;
    }

    private boolean updateFixedInstallmentAmount(final MathContext mc, final LoanApplicationTerms loanApplicationTerms, int periodNumber,
            Money outstandingBalance) {
        boolean isAmountChanged = false;
        if (loanApplicationTerms.getActualFixedEmiAmount() == null && loanApplicationTerms.getInterestMethod().isDecliningBalnce()
                && loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
            if (periodNumber < loanApplicationTerms.getPrincipalGrace() + 1) {
                periodNumber = loanApplicationTerms.getPrincipalGrace() + 1;
            }
            Money emiAmount = loanApplicationTerms.pmtForInstallment(this.paymentPeriodsInOneYearCalculator, outstandingBalance,
                    periodNumber, mc);
            loanApplicationTerms.setFixedEmiAmount(emiAmount.getAmount());
            isAmountChanged = true;
        }
        return isAmountChanged;
    }
    
    private Money fetchArrears(final LoanApplicationTerms loanApplicationTerms, final MonetaryCurrency currency,
            final LoanTransaction transaction) {
        Money arrears = transaction.getPrincipalPortion(currency);
        arrears = arrears.plus(fetchCompoundedArrears(loanApplicationTerms, currency, transaction));
        return arrears;
    }

    private Money fetchCompoundedArrears(final LoanApplicationTerms loanApplicationTerms, final MonetaryCurrency currency,
            final LoanTransaction transaction) {
        Money arrears = Money.zero(currency);
        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isInterestCompoundingEnabled()) {
            arrears = arrears.plus(transaction.getInterestPortion(currency));
        }

        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isFeeCompoundingEnabled()) {
            arrears = arrears.plus(transaction.getFeeChargesPortion(currency)).plus(transaction.getPenaltyChargesPortion(currency));
        }
        return arrears;
    }

    /**
     * Method calculates interest on not paid outstanding principal and interest
     * (if compounding is enabled) till current date and adds new repayment
     * schedule detail
     * 
     * @param compoundingMap
     *            TODO
     * @param loanCharges
     *            TODO
     * @param principalPortioMap
     *            TODO
     * 
     */
    private Money addInterestOnlyRepaymentScheduleForCurrentdate(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO, final MonetaryCurrency currency, final Collection<LoanScheduleModelPeriod> periods,
            final LocalDate currentDate, LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final Collection<RecalculationDetail> transactions, final Set<LoanCharge> loanCharges, final LoanScheduleParams params) {
        boolean isFirstRepayment = false;
        LocalDate startDate = params.getPeriodStartDate();
        Money outstanding = params.getOutstandingBalanceAsPerRest();
        Money totalInterest = Money.zero(currency);
        Money totalCumulativeInterest = Money.zero(currency);
        double interestCalculationGraceOnRepaymentPeriodFraction = Double.valueOf(0);
        int periodNumberTemp = 1;
        LocalDate lastRestDate = getNextRestScheduleDate(currentDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
        Collection<LoanTermVariationsData> applicableVariations = loanApplicationTerms.getLoanTermVariations().getInterestRateChanges();
        params.setCompoundedInLastInstallment(params.getUnCompoundedAmount());
        do {

            params.setActualRepaymentDate(this.scheduledDateGenerator.generateNextRepaymentDate(params.getActualRepaymentDate(),
                    loanApplicationTerms, isFirstRepayment));
            if (params.getActualRepaymentDate().isAfter(currentDate)) {
                params.setActualRepaymentDate(currentDate);
            }

            Collection<RecalculationDetail> applicableTransactions = getApplicableTransactionsForPeriod(
                    params.applyInterestRecalculation(), params.getActualRepaymentDate(), transactions);

            populateCompoundingDatesInPeriod(params.getPeriodStartDate(), params.getActualRepaymentDate(), loanApplicationTerms,
                    holidayDetailDTO, params, loanCharges, currency);

            for (RecalculationDetail detail : applicableTransactions) {
                if (detail.isProcessed()) {
                    continue;
                }
                LocalDate transactionDate = detail.getTransactionDate();
                List<LoanTransaction> currentTransactions = createCurrentTransactionList(detail);

                if (!params.getPeriodStartDate().isEqual(transactionDate)) {
                    PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                            this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction,
                            totalInterest.zero(), totalInterest.zero(), totalInterest.zero(), totalInterest.zero(), outstanding,
                            loanApplicationTerms, periodNumberTemp, mc, mergeVariationsToMap(params), params.getCompoundingMap(),
                            params.getPeriodStartDate(), transactionDate, applicableVariations);

                    Money interest = principalInterestForThisPeriod.interest();
                    totalInterest = totalInterest.plus(interest);
                    ScheduleCurrentPeriodParams currentPeriodParams = new ScheduleCurrentPeriodParams(currency,interestCalculationGraceOnRepaymentPeriodFraction);
                    Money installmentCapitalizedChargeAmount = null;
                    boolean considerInstallmentFee = true;
                    applyChargesForCurrentPeriod(loanCharges, currency, params, transactionDate, currentPeriodParams, startDate,loanApplicationTerms, installmentCapitalizedChargeAmount, considerInstallmentFee);
                    LoanScheduleModelRepaymentPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(params.getInstalmentNumber(),
                            startDate, transactionDate, totalInterest.zero(), totalInterest.zero(), totalInterest,currentPeriodParams.getFeeChargesForInstallment(),
                            currentPeriodParams.getPenaltyChargesForInstallment(), totalInterest, true);
                    params.incrementInstalmentNumber();
                    periods.add(installment);
                    totalCumulativeInterest = totalCumulativeInterest.plus(totalInterest);
                    totalInterest = totalInterest.zero();
                    addLoanRepaymentScheduleInstallment(params.getInstallments(), installment);
                    updateCompoundingMap(loanApplicationTerms, holidayDetailDTO, params, lastRestDate, params.getActualRepaymentDate());
                    populateCompoundingDatesInPeriod(installment.periodDueDate(), params.getActualRepaymentDate(), loanApplicationTerms,
                            holidayDetailDTO, params, loanCharges, currency);
                    params.setCompoundedInLastInstallment(params.getUnCompoundedAmount());
                    params.setPeriodStartDate(transactionDate);
                    startDate = transactionDate;
                }
                loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(currentTransactions, currency, params.getInstallments());
                updateLatePaidAmountsToPrincipalMap(detail.getTransaction(), loanApplicationTerms, currency, holidayDetailDTO,
                        lastRestDate, params);
                updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, params.getLatePaymentMap(), currentDate,
                        params.getInstallments(), false, lastRestDate);
                if (params.getLatePaymentMap().isEmpty() && isCompleted(params.getInstallments())) {
                    outstanding = outstanding.zero();
                } else {
                    outstanding = updateBalanceForInterestCalculation(params.getPrincipalPortionMap(), params.getPeriodStartDate(),
                            outstanding, false);
                }
                if (params.getLatePaymentMap().isEmpty() && outstanding.isZero()) {
                    break;
                }
            }

            if (!outstanding.isZero() || !params.getLatePaymentMap().isEmpty()) {
                PrincipalInterest principalInterestForThisPeriod = calculatePrincipalInterestComponentsForPeriod(
                        this.paymentPeriodsInOneYearCalculator, interestCalculationGraceOnRepaymentPeriodFraction, totalInterest.zero(),
                        totalInterest.zero(), totalInterest.zero(), totalInterest.zero(), outstanding, loanApplicationTerms,
                        periodNumberTemp, mc, mergeVariationsToMap(params), params.getCompoundingMap(), params.getPeriodStartDate(),
                        params.getActualRepaymentDate(), applicableVariations);
                Money interest = principalInterestForThisPeriod.interest();
                totalInterest = totalInterest.plus(interest);

                Money uncompounded = params.getUnCompoundedAmount();
                Money compounded = uncompounded.zero();
                for (Map.Entry<LocalDate, Money> mapEntry : params.getCompoundingMap().entrySet()) {
                    if (mapEntry.getKey().isAfter(params.getPeriodStartDate())) {
                        compounded = compounded.plus(mapEntry.getValue());
                    }
                }
                Money compoundedForThisPeriod = compounded.minus(uncompounded);
                Money installmentCapitalizedChargeAmount = null;
                Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(params.getPeriodStartDate(), params.getActualRepaymentDate(),
                        loanCharges, currency, null, loanApplicationTerms.getPrincipal(), null, false, loanApplicationTerms,
                        params.getInstalmentNumber(),installmentCapitalizedChargeAmount);
                Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(params.getPeriodStartDate(),
                        params.getActualRepaymentDate(), loanCharges, currency, null, loanApplicationTerms.getPrincipal(), null, false,
                        loanApplicationTerms, params.getInstalmentNumber(),installmentCapitalizedChargeAmount);
                Money feeInPeriod = feeChargesForInstallment.plus(penaltyChargesForInstallment);
                Money uncompoundedForThisPeriod = interest.plus(feeInPeriod).minus(compoundedForThisPeriod);
                params.setUnCompoundedAmount(uncompoundedForThisPeriod);
                LocalDate compoundingDate = params.getPeriodStartDate();
                if (loanApplicationTerms.allowCompoundingOnEod()) {
                    compoundingDate = compoundingDate.minusDays(1);
                }
                compoundingDate = getNextCompoundScheduleDate(compoundingDate, loanApplicationTerms, holidayDetailDTO);
                if(compoundingDate != null && compoundingDate.isEqual(params.getActualRepaymentDate())){
                    params.getCompoundingMap().put(compoundingDate, uncompoundedForThisPeriod);
                    params.setUnCompoundedAmount(uncompoundedForThisPeriod.zero());
                }
                

            }
            params.setPeriodStartDate(params.getActualRepaymentDate());
        } while (params.getActualRepaymentDate().isBefore(currentDate) && (!outstanding.isZero() || !params.getLatePaymentMap().isEmpty()));

        if (totalInterest.isGreaterThanZero()) {
            Money installmentCapitalizedChargeAmount = null;
            boolean considerInstallmentFee = true;
            ScheduleCurrentPeriodParams currentPeriodParams = new ScheduleCurrentPeriodParams(currency, interestCalculationGraceOnRepaymentPeriodFraction);
            applyChargesForCurrentPeriod(loanCharges, currency, params, params.getActualRepaymentDate(), currentPeriodParams, startDate, loanApplicationTerms, installmentCapitalizedChargeAmount, considerInstallmentFee);
            LoanScheduleModelRepaymentPeriod installment = LoanScheduleModelRepaymentPeriod.repayment(params.getInstalmentNumber(),
                    startDate, params.getActualRepaymentDate(), totalInterest.zero(), totalInterest.zero(), totalInterest,
                    currentPeriodParams.getFeeChargesForInstallment(), currentPeriodParams.getPenaltyChargesForInstallment(), totalInterest, true);
            params.incrementInstalmentNumber();
            periods.add(installment);
            params.getCompoundingDateVariations().put(startDate, new TreeMap<>(params.getCompoundingMap()));
            totalCumulativeInterest = totalCumulativeInterest.plus(totalInterest);
        }
        return totalCumulativeInterest;
    }

    private boolean isCompleted(List<LoanRepaymentScheduleInstallment> installments) {
        boolean isCompleted = true;
        for (LoanRepaymentScheduleInstallment installment : installments) {
            if (installment.isNotFullyPaidOff()) {
                isCompleted = false;
                break;
            }
        }
        return isCompleted;
    }

    private Collection<RecalculationDetail> getApplicableTransactionsForPeriod(final boolean applyInterestRecalculation,
            LocalDate repaymentDate, final Collection<RecalculationDetail> transactions) {
        Collection<RecalculationDetail> applicableTransactions = new ArrayList<>();
        if (applyInterestRecalculation) {
            for (RecalculationDetail detail : transactions) {
                if (!detail.getTransactionDate().isAfter(repaymentDate)) {
                    applicableTransactions.add(detail);
                }
            }
            transactions.removeAll(applicableTransactions);
        }
        return applicableTransactions;
    }

    private List<LoanTransaction> createCurrentTransactionList(RecalculationDetail detail) {
        List<LoanTransaction> currentTransactions = new ArrayList<>(2);
        currentTransactions.add(detail.getTransaction());
        detail.setProcessed(true);
        return currentTransactions;

    }

    /**
     * method applies early payment strategy as per the configurations provided
     * @param principalToBeScheduled TODO
     */
    private Money applyEarlyPaymentStrategy(final LoanApplicationTerms loanApplicationTerms, Money reducePrincipal,
            final Money totalCumulativePrincipal, int periodNumber, final MathContext mc, Money principalToBeScheduled) {
        if (reducePrincipal.isGreaterThanZero()) {
            switch (loanApplicationTerms.getRescheduleStrategyMethod()) {
                case REDUCE_EMI_AMOUNT:
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, periodNumber, mc,principalToBeScheduled);
                    reducePrincipal = reducePrincipal.zero();
                break;
                case REDUCE_NUMBER_OF_INSTALLMENTS:
                    // number of installments will reduce but emi amount won't
                    // get effected
                    reducePrincipal = reducePrincipal.zero();
                break;
                case RESCHEDULE_NEXT_REPAYMENTS:
                // will reduce principal from the reduce Principal for each
                // installment(means installments will have less emi amount)
                // until this
                // amount becomes zero
                break;
                default:
                break;
            }
        }
        return reducePrincipal;
    }

    private void adjustInstallmentOrPrincipalAmount(final LoanApplicationTerms loanApplicationTerms, final Money totalCumulativePrincipal,
            int periodNumber, final MathContext mc, Money principal) {
        // in this case emi amount will be reduced but number of
        // installments won't change
        
        if(!loanApplicationTerms.isEmiBasedOnDisbursements()){
            principal = getPrincipalToBeScheduled(loanApplicationTerms);
        }
        if (!principal.minus(totalCumulativePrincipal).isGreaterThanZero()) { return; }
        if (loanApplicationTerms.getAmortizationMethod().isEqualPrincipal()) {
            loanApplicationTerms.updateFixedPrincipalAmount(mc, periodNumber, principal.minus(totalCumulativePrincipal));
        } else if (loanApplicationTerms.getActualFixedEmiAmount() == null) {
            loanApplicationTerms.setFixedEmiAmount(null);
            updateFixedInstallmentAmount(mc, loanApplicationTerms, periodNumber, principal.minus(totalCumulativePrincipal));
        }

    }

    /**
     * Identifies all the past date principal changes and apply them on
     * outstanding balance for future calculations
     */
    private Money updateBalanceForInterestCalculation(final Map<LocalDate, Money> principalPortionMap, final LocalDate scheduledDueDate,
            final Money outstandingBalanceAsPerRest, boolean addMapDetails) {
        List<LocalDate> removeFromprincipalPortionMap = new ArrayList<>();
        Money outstandingBalance = outstandingBalanceAsPerRest;
        for (Map.Entry<LocalDate, Money> principal : principalPortionMap.entrySet()) {
            if (!principal.getKey().isAfter(scheduledDueDate)) {
                if (addMapDetails) {
                    outstandingBalance = outstandingBalance.plus(principal.getValue());
                } else {
                    outstandingBalance = outstandingBalance.minus(principal.getValue());
                }
                removeFromprincipalPortionMap.add(principal.getKey());
            }
        }
        for (LocalDate date : removeFromprincipalPortionMap) {
            principalPortionMap.remove(date);
        }
        return outstandingBalance;
    }

    // this is to make sure even paid late payments(principal and compounded
    // interest/fee) should be reduced as per rest date
    private void updateLatePaidAmountsToPrincipalMap(final LoanTransaction loanTransaction, final LoanApplicationTerms applicationTerms,
            final MonetaryCurrency currency, final HolidayDetailDTO holidayDetailDTO, final LocalDate lastRestDate,
            final LoanScheduleParams params) {
        LocalDate applicableDate = getNextRestScheduleDate(loanTransaction.getTransactionDate().minusDays(1), applicationTerms,
                holidayDetailDTO);

        Money principalPortion = loanTransaction.getPrincipalPortion(currency);

        updateLatePaymentCompoundingAmount(params.getPrincipalPortionMap(), params.getLatePaymentMap(), currency, lastRestDate,
                principalPortion, applicableDate);
        adjustCompoundedAmountWithPaidDetail(params, lastRestDate, applicableDate, loanTransaction, applicationTerms);
    }

    private void updateLatePaymentCompoundingAmount(final Map<LocalDate, Money> principalVariationMap,
            final Map<LocalDate, Money> latePaymentCompoundingMap, final MonetaryCurrency currency, final LocalDate lastRestDate,
            Money compoundedPortion, final LocalDate applicableDate) {
        Money appliedOnPrincipalVariationMap = Money.zero(currency);
        Map<LocalDate, Money> temp = new HashMap<>();
        for (LocalDate date : latePaymentCompoundingMap.keySet()) {
            if (date.isBefore(lastRestDate)) {
                Money money = latePaymentCompoundingMap.get(date);
                appliedOnPrincipalVariationMap = appliedOnPrincipalVariationMap.plus(money);
                if (appliedOnPrincipalVariationMap.isLessThan(compoundedPortion)) {
                    if (date.isBefore(applicableDate)) {
                        updateMapWithAmount(principalVariationMap, money.negated(), date);
                        updateMapWithAmount(principalVariationMap, money, applicableDate);
                    }
                } else if (temp.isEmpty()) {
                    Money diff = money.minus(appliedOnPrincipalVariationMap.minus(compoundedPortion));
                    updateMapWithAmount(principalVariationMap, diff.negated(), date);
                    updateMapWithAmount(principalVariationMap, diff, applicableDate);
                    updateMapWithAmount(temp, money.minus(diff), date);
                    updateMapWithAmount(temp, money.minus(diff).negated(), lastRestDate);
                } else {
                    updateMapWithAmount(temp, money, date);
                    updateMapWithAmount(temp, money.negated(), lastRestDate);
                }
            }
        }
        latePaymentCompoundingMap.clear();
        latePaymentCompoundingMap.putAll(temp);
    }

    /**
     * this Method updates late/ not paid installment components to Map with
     * effective date as per REST(for principal portion ) and compounding
     * (interest or fee or interest and fee portions) frequency
     * 
     */
    private void updateLatePaymentsToMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final MonetaryCurrency currency, final Map<LocalDate, Money> latePaymentMap, final LocalDate scheduledDueDate,
            List<LoanRepaymentScheduleInstallment> installments, boolean applyRestFrequencyForPrincipal, final LocalDate lastRestDate) {
        latePaymentMap.clear();
        LocalDate currentDate = DateUtils.getLocalDateOfTenant();

        Money totalCompoundingAmount = Money.zero(currency);
        for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : installments) {
            if (loanRepaymentScheduleInstallment.isNotFullyPaidOff()
                    && !loanRepaymentScheduleInstallment.getDueDate().isAfter(scheduledDueDate)
                    && !loanRepaymentScheduleInstallment.isRecalculatedInterestComponent()) {
                LocalDate principalEffectiveDate = loanRepaymentScheduleInstallment.getDueDate();
                if (applyRestFrequencyForPrincipal) {
                    principalEffectiveDate = getNextRestScheduleDate(loanRepaymentScheduleInstallment.getDueDate().minusDays(1),
                            loanApplicationTerms, holidayDetailDTO);
                }
                if (principalEffectiveDate.isBefore(currentDate)) {
                    updateMapWithAmount(latePaymentMap, loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency),
                            principalEffectiveDate);
                    totalCompoundingAmount = totalCompoundingAmount
                            .plus(loanRepaymentScheduleInstallment.getPrincipalOutstanding(currency));
                }

            }
        }
        if (totalCompoundingAmount.isGreaterThanZero()) {
            updateMapWithAmount(latePaymentMap, totalCompoundingAmount.negated(), lastRestDate);
        }
    }

    private void updateCompoundingMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final LoanScheduleParams params, final LocalDate lastRestDate, final LocalDate scheduledDueDate) {
        if (loanApplicationTerms.isInterestRecalculationEnabled()
                && loanApplicationTerms.getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) {
            final MonetaryCurrency currency = params.getCurrency();
            Money totalCompoundedAmount = Money.zero(currency);
            for (LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment : params.getInstallments()) {
                if (!params.getCompoundingDateVariations().containsKey(loanRepaymentScheduleInstallment.getFromDate())) {
                    final boolean isPastDate = params.applyInterestRecalculation()
                            && loanRepaymentScheduleInstallment.getDueDate().isBefore(DateUtils.getLocalDateOfTenant());
                    boolean periodHasCompoundingDate = false;
                    Money amountCharged = getIncomeForCompounding(loanApplicationTerms, currency, loanRepaymentScheduleInstallment);
                    final Map<LocalDate, Money> compoundingMap = params.getCompoundingMap();
                    LocalDate effectiveStartDate = loanRepaymentScheduleInstallment.getFromDate();
                    if (loanApplicationTerms.allowCompoundingOnEod()) {
                        effectiveStartDate = loanRepaymentScheduleInstallment.getFromDate().minusDays(1);
                    }
                    LocalDate compoundingEffectiveDate = getNextCompoundScheduleDate(effectiveStartDate, loanApplicationTerms,
                            holidayDetailDTO);
                    final LocalDate restDate = getNextRestScheduleDate(loanRepaymentScheduleInstallment.getDueDate().minusDays(1), loanApplicationTerms,
                            holidayDetailDTO);
                    if (!compoundingEffectiveDate.isAfter(loanRepaymentScheduleInstallment.getDueDate())) {
                        Money amountCompoundedFromLastPeriod = params.getCompoundedInLastInstallment();
                        if (amountCompoundedFromLastPeriod.isZero()) {
                            amountCompoundedFromLastPeriod = params.getUnCompoundedAmount();
                        }
                        totalCompoundedAmount = totalCompoundedAmount.minus(amountCompoundedFromLastPeriod);
                        periodHasCompoundingDate = true;
                    }
                    while (!compoundingEffectiveDate.isAfter(loanRepaymentScheduleInstallment.getDueDate())) {
						if (compoundingEffectiveDate.isEqual(loanRepaymentScheduleInstallment.getDueDate())
								&& compoundingEffectiveDate.isEqual(scheduledDueDate)) {
                            Money amountToBeCompounding = amountCharged.minus(totalCompoundedAmount);
                            updateMapWithAmount(compoundingMap, amountToBeCompounding, compoundingEffectiveDate);
                            totalCompoundedAmount = totalCompoundedAmount.plus(amountToBeCompounding);

                        } else if (compoundingMap.containsKey(compoundingEffectiveDate)) {
                            Money compounedAmount = compoundingMap.get(compoundingEffectiveDate);
                            totalCompoundedAmount = totalCompoundedAmount.plus(compounedAmount);
                        }

                        if (!loanApplicationTerms.allowCompoundingOnEod()) {
                            compoundingEffectiveDate = compoundingEffectiveDate.plusDays(1);
                        }
                        compoundingEffectiveDate = getNextCompoundScheduleDate(compoundingEffectiveDate, loanApplicationTerms,
                                holidayDetailDTO);
                    }
                    if (periodHasCompoundingDate) {
                        if (isPastDate) {
                            updateMapWithAmount(params.getPrincipalPortionMap(), totalCompoundedAmount, lastRestDate);
                        } 
                    }
                    Money uncompoundedInThisPeriod = Money.zero(currency);
                    if (totalCompoundedAmount.isGreaterThanZero()) {
                        params.getCompoundingDateVariations().put(loanRepaymentScheduleInstallment.getFromDate(),
                                new TreeMap<>(params.getCompoundingMap()));
                        for (Map.Entry<LocalDate, Money> mapEntry : params.getCompoundingMap().entrySet()) {
                            if (!mapEntry.getKey().isAfter(loanRepaymentScheduleInstallment.getDueDate())) {
                                updateMapWithAmount(params.getPrincipalPortionMap(), mapEntry.getValue().negated(), mapEntry.getKey());
                            }
                        }
                        params.minusUnCompoundedAmount(params.getUnCompoundedAmount());
                        params.getCompoundingMap().clear();
                        params.addUnCompoundedAmount(amountCharged.minus(totalCompoundedAmount));
                        uncompoundedInThisPeriod = params.getUnCompoundedAmount();
                    } else {
                        params.getCompoundingMap().clear();
                        params.getCompoundingDateVariations().put(loanRepaymentScheduleInstallment.getFromDate(),
                                new TreeMap<>(params.getCompoundingMap()));
                        if(params.getOutstandingBalance().isZero() && loanRepaymentScheduleInstallment.isRecalculatedInterestComponent()){
                            params.addUnCompoundedAmount(amountCharged.minus(params.getUnCompoundedAmount().minus(params.getCompoundedInLastInstallment())));
                        }else{
                            params.addUnCompoundedAmount(amountCharged);
                        }
                        uncompoundedInThisPeriod = amountCharged;
                    }
                    if(!isPastDate && amountCharged.isGreaterThanZero()){
                        updateMapWithAmount(params.getPrincipalPortionMap(), amountCharged, restDate);
                    }
                    params.setCompoundedInLastInstallment(amountCharged.zero());
                    if (uncompoundedInThisPeriod.isGreaterThanZero()) {
                        if (isPastDate) {
                            updateMapWithAmount(params.getPrincipalPortionMap(), uncompoundedInThisPeriod, lastRestDate);
                        }
                    }
                }

            }
        }

    }

    private Money getIncomeForCompounding(final LoanApplicationTerms loanApplicationTerms, final MonetaryCurrency currency,
            LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment) {
        Money interestCharged = Money.zero(currency);
        Money feeCharged = Money.zero(currency);
        Money penaltyCharged = Money.zero(currency);
        Money amountCharged = Money.zero(currency);
        switch (loanApplicationTerms.getInterestRecalculationCompoundingMethod()) {
            case INTEREST:
                interestCharged = interestCharged.plus(loanRepaymentScheduleInstallment.getInterestCharged(currency));
            break;
            case FEE:
                feeCharged = feeCharged.plus(loanRepaymentScheduleInstallment.getFeeChargesCharged(currency));
                penaltyCharged = penaltyCharged.plus(loanRepaymentScheduleInstallment.getPenaltyChargesCharged(currency));
            break;
            case INTEREST_AND_FEE:
                interestCharged = interestCharged.plus(loanRepaymentScheduleInstallment.getInterestCharged(currency));
                feeCharged = feeCharged.plus(loanRepaymentScheduleInstallment.getFeeChargesCharged(currency));
                penaltyCharged = penaltyCharged.plus(loanRepaymentScheduleInstallment.getPenaltyChargesCharged(currency));
            break;
            default:
            break;
        }
        amountCharged = interestCharged.plus(feeCharged).plus(penaltyCharged);
        return amountCharged;
    }

    private void adjustCompoundedAmountWithPaidDetail(final LoanScheduleParams params, final LocalDate lastRestDate,
            final Collection<LoanTransaction> transactions, final LoanApplicationTerms loanApplicationTerms,
            HolidayDetailDTO holidayDetailDTO) {
        for (LoanTransaction loanTransaction : transactions) {
            final LocalDate amountApplicableDate = getNextRestScheduleDate(loanTransaction.getTransactionDate().minusDays(1),
                    loanApplicationTerms, holidayDetailDTO);
            adjustCompoundedAmountWithPaidDetail(params, lastRestDate, amountApplicableDate, loanTransaction, loanApplicationTerms);
        }
    }

    private void adjustCompoundedAmountWithPaidDetail(final LoanScheduleParams params, final LocalDate lastRestDate,
            final LocalDate amountApplicableDate, final LoanTransaction transaction, final LoanApplicationTerms loanApplicationTerms) {
        Set<LocalDate> compoundingDates = params.getCompoundingMap().keySet();
        LocalDate nextCompoundingDate = null;
        for (LocalDate date : compoundingDates) {
            if ((nextCompoundingDate == null || nextCompoundingDate.isAfter(date)) && date.isAfter(amountApplicableDate)) {
                nextCompoundingDate = date;
            }
        }
        adjustCompoundedAmountWithPaidDetail(params.getPrincipalPortionMap(), lastRestDate, amountApplicableDate, transaction,
                loanApplicationTerms, params.getCurrency());
    }

    private Money adjustCompoundedAmountWithPaidDetail(final Map<LocalDate, Money> principalPortionMap, final LocalDate lastRestDate,
            final LocalDate amountApplicableDate, final LoanTransaction transaction, final LoanApplicationTerms loanApplicationTerms,
            final MonetaryCurrency currency) {
        Money uncompoundedIncomePayment = Money.zero(currency);
        Money compoundedIncome = fetchCompoundedArrears(loanApplicationTerms, currency, transaction);
        if (amountApplicableDate.isBefore(lastRestDate)) {
            updateMapWithAmount(principalPortionMap, compoundedIncome, amountApplicableDate);
            updateMapWithAmount(principalPortionMap, compoundedIncome.negated(), lastRestDate);
        } else if (amountApplicableDate.isAfter(lastRestDate)) {
            updateMapWithAmount(principalPortionMap, compoundedIncome.negated(), amountApplicableDate);
        }
        return uncompoundedIncomePayment;
    }

    private void populateCompoundingDatesInPeriod(final LocalDate startDate, final LocalDate endDate,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final LoanScheduleParams scheduleParams, final Set<LoanCharge> charges, MonetaryCurrency currency) {
        if (loanApplicationTerms.getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) {
            final Map<LocalDate, Money> compoundingMap = scheduleParams.getCompoundingMap();
            LocalDate lastCompoundingDate = startDate;
            LocalDate compoundingDate = startDate;
            boolean addUncompounded = true;
            while (compoundingDate.isBefore(endDate)) {
                if (loanApplicationTerms.allowCompoundingOnEod()) {
                    compoundingDate = compoundingDate.minusDays(1);
                }
                compoundingDate = getNextCompoundScheduleDate(compoundingDate, loanApplicationTerms, holidayDetailDTO);

                if (compoundingDate.isBefore(endDate)) {
                    LocalDate feeCompoundStartDate = lastCompoundingDate;
                    LocalDate feeCompoundEndDate = compoundingDate;
                    if (loanApplicationTerms.allowCompoundingOnEod()) {
                        if (!addUncompounded) {
                        feeCompoundStartDate = lastCompoundingDate.minusDays(1);
                        }
                        feeCompoundEndDate = compoundingDate.minusDays(1);
                    }
                	Money installmentCapitalizedChargeAmount = null;
                    Money feeChargesForInstallment = cumulativeFeeChargesDueWithin(feeCompoundStartDate, feeCompoundEndDate, charges, currency,
                            null, loanApplicationTerms.getPrincipal(), null, false, loanApplicationTerms, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount);
                    Money penaltyChargesForInstallment = cumulativePenaltyChargesDueWithin(feeCompoundStartDate, feeCompoundEndDate, charges,
                            currency, null, loanApplicationTerms.getPrincipal(), null, false, loanApplicationTerms, scheduleParams.getInstalmentNumber(), installmentCapitalizedChargeAmount);
                    Money compoundAmount = feeChargesForInstallment.plus(penaltyChargesForInstallment);
                    if (addUncompounded) {
                        compoundAmount = compoundAmount.plus(scheduleParams.getUnCompoundedAmount());
                        addUncompounded = false;
                    }
                    updateMapWithAmount(compoundingMap, compoundAmount, compoundingDate);
                }

                lastCompoundingDate = compoundingDate;
            }
        }
    }

    /**
     * This Method updates principal paid component to map with effective date
     * as per the REST
     * 
     */
    private void updatePrincipalPaidPortionToMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            Map<LocalDate, Money> principalPortionMap, final LoanScheduleModelPeriod installment, final RecalculationDetail detail,
            final Money unprocessed, final List<LoanRepaymentScheduleInstallment> installments) {
        LocalDate applicableDate = getNextRestScheduleDate(detail.getTransactionDate().minusDays(1), loanApplicationTerms, holidayDetailDTO);
        updateMapWithAmount(principalPortionMap, unprocessed, applicableDate);
        installment.addPrincipalAmount(unprocessed);
        LoanRepaymentScheduleInstallment lastInstallment = installments.get(installments.size() - 1);
        lastInstallment.updatePrincipal(lastInstallment.getPrincipal(unprocessed.getCurrency()).plus(unprocessed).getAmount());
        lastInstallment.payPrincipalComponent(detail.getTransactionDate(), unprocessed);
    }

    /**
     * merges all the applicable amounts(compounding dates, disbursements, late
     * payment compounding and principal change as per rest) changes to single
     * map for interest calculation
     * 
     */
    private TreeMap<LocalDate, Money> mergeVariationsToMap(final LoanScheduleParams params) {
        TreeMap<LocalDate, Money> map = new TreeMap<>();
        map.putAll(params.getLatePaymentMap());
        for (Map.Entry<LocalDate, Money> mapEntry : params.getDisburseDetailMap().entrySet()) {
            Money value = mapEntry.getValue();
            if (map.containsKey(mapEntry.getKey())) {
                value = value.plus(map.get(mapEntry.getKey()));
            }
            if(value.isZero()){
                map.remove(mapEntry.getKey());
            }else{
                map.put(mapEntry.getKey(), value);
            }
        }

        for (Map.Entry<LocalDate, Money> mapEntry : params.getPrincipalPortionMap().entrySet()) {
            Money value = mapEntry.getValue().negated();
            if (map.containsKey(mapEntry.getKey())) {
                value = value.plus(map.get(mapEntry.getKey()));
            }
            if(value.isZero()){
                map.remove(mapEntry.getKey());
            }else{
                map.put(mapEntry.getKey(), value);
            }
        }

        for (Map.Entry<LocalDate, Money> mapEntry : params.getCompoundingMap().entrySet()) {
            Money value = mapEntry.getValue();
            if (!map.containsKey(mapEntry.getKey())) {
                map.put(mapEntry.getKey(), value.zero());
            }
        }

        return map;
    }

    private void updateMapWithAmount(final Map<LocalDate, Money> map, final Money amount, final LocalDate amountApplicableDate) {
        Money principalPaid = amount;
        if (map.containsKey(amountApplicableDate)) {
            principalPaid = map.get(amountApplicableDate).plus(principalPaid);
        }
        map.put(amountApplicableDate, principalPaid);

    }
    
    public abstract PrincipalInterest calculatePrincipalInterestComponentsForPeriod(PaymentPeriodsInOneYearCalculator calculator,
            double interestCalculationGraceOnRepaymentPeriodFraction, Money totalCumulativePrincipal, Money totalCumulativeInterest,
            Money totalInterestDueForLoan, Money cumulatingInterestPaymentDueToGrace, Money outstandingBalance,
            LoanApplicationTerms loanApplicationTerms, int periodNumber, MathContext mc, TreeMap<LocalDate, Money> principalVariation,
            Map<LocalDate, Money> compoundingMap, LocalDate periodStartDate, LocalDate periodEndDate,
            Collection<LoanTermVariationsData> termVariations);

    protected final boolean isLastRepaymentPeriod(final int numberOfRepayments, final int periodNumber) {
        return periodNumber == numberOfRepayments;
    }

    private BigDecimal deriveTotalChargesDueAtTimeOfDisbursement(final Set<LoanCharge> loanCharges,
            final LoanApplicationTerms loanApplicationTerms) {
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isTrancheDisbursementCharge()) {
                    LoanDisbursementDetails disbursementData = loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails();
                    if (loanApplicationTerms.isConsiderAllDisbursmentsInSchedule()
                            || disbursementData.isDisbursed()
                            || (loanApplicationTerms.isConsiderFutureDisbursmentsInSchedule() && !disbursementData
                                    .getDisbursementDateAsLocalDate().isBefore(DateUtils.getLocalDateOfTenant()))) {
                        chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
                    }
                } else {
                    chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
                }
            }
        }
        return chargesDueAtTimeOfDisbursement;
    }

    private BigDecimal getDisbursementAmount(final LoanApplicationTerms loanApplicationTerms, LocalDate disbursementDate,
            final Collection<LoanScheduleModelPeriod> periods, final Map<LocalDate, Money> disurseDetail, final Set<LoanCharge> loanCharges) {
        BigDecimal principal = BigDecimal.ZERO;
        MonetaryCurrency currency = loanApplicationTerms.getPrincipal().getCurrency();
        
        BigDecimal chargesDueAtTimeOfDisbursement = BigDecimal.ZERO;
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isTrancheDisbursementCharge()) {
                    if (loanCharge.getTrancheDisbursementCharge().getloanDisbursementDetails().getDisbursementDateAsLocalDate()
                            .isEqual(loanApplicationTerms.getExpectedDisbursementDate())) {
                        chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
                    }
                } else {
                    chargesDueAtTimeOfDisbursement = chargesDueAtTimeOfDisbursement.add(loanCharge.amount());
                }
            }
        }
        
        for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
            if (disbursementData.disbursementDate().equals(disbursementDate)) {
                final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                        disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()), chargesDueAtTimeOfDisbursement);
                periods.add(disbursementPeriod);
                principal = principal.add(disbursementData.amount());
            } else if (loanApplicationTerms.isConsiderAllDisbursmentsInSchedule()
                    || disbursementData.isDisbursed() || (loanApplicationTerms.isConsiderFutureDisbursmentsInSchedule() && !disbursementData.disbursementDate().isBefore(
                            DateUtils.getLocalDateOfTenant()))) {
                disurseDetail.put(disbursementData.disbursementDate(), Money.of(currency, disbursementData.amount()));
            }
        }
        return principal;
    }

    private Collection<LoanScheduleModelPeriod> createNewLoanScheduleListWithDisbursementDetails(final int numberOfRepayments,
            final LoanApplicationTerms loanApplicationTerms, final BigDecimal chargesDueAtTimeOfDisbursement) {

        Collection<LoanScheduleModelPeriod> periods = null;
        if (loanApplicationTerms.isMultiDisburseLoan()) {
            periods = new ArrayList<>(numberOfRepayments + loanApplicationTerms.getDisbursementDatas().size());
        } else {
            periods = new ArrayList<>(numberOfRepayments + 1);
            final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                    loanApplicationTerms, chargesDueAtTimeOfDisbursement);
            periods.add(disbursementPeriod);
        }

        return periods;
    }

    private Set<LoanCharge> seperateTotalCompoundingPercentageCharges(final Set<LoanCharge> loanCharges) {
        Set<LoanCharge> interestCharges = new HashSet<>();
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isSpecifiedDueDate()
                    && (loanCharge.getChargeCalculation().isPercentageOfInterest() || loanCharge.getChargeCalculation()
                            .isPercentageOfAmountAndInterest())) {
                interestCharges.add(loanCharge);
            }
        }
        loanCharges.removeAll(interestCharges);
        return interestCharges;
    }

    private Money cumulativeFeeChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd, final Set<LoanCharge> loanCharges,
            final MonetaryCurrency monetaryCurrency, final PrincipalInterest principalInterestForThisPeriod,
            final Money principalDisbursed, final Money totalInterestChargedForFullLoanTerm, boolean isInstallmentChargeApplicable, 
            final LoanApplicationTerms loanApplicationTerm, int installmentNumber, Money installmentCapitalizedChargeAmount) {

        Money cumulative = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (!loanCharge.isDueAtDisbursement() && loanCharge.isFeeCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {                    
                    if(loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()){
                    	
                    	cumulative = cumulative.plus(calculateInstallmentCharge(principalInterestForThisPeriod, cumulative, loanCharge, loanApplicationTerm, installmentNumber, installmentCapitalizedChargeAmount));
                    }else{
                    	cumulative = calculateInstallmentCharge(principalInterestForThisPeriod, cumulative, loanCharge, loanApplicationTerm, installmentNumber, installmentCapitalizedChargeAmount);
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = calculateChargeWithPercentage(principalDisbursed, totalInterestChargedForFullLoanTerm,
                            cumulative, loanCharge);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                } else if (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        cumulative = calculateChargeWithPercentage(principalDisbursed, totalInterestChargedForFullLoanTerm, cumulative,
                                loanCharge);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount());
                    }
                }
            }
        }
        return cumulative;
    }

    private Money calculateChargeWithPercentage(final Money principalDisbursed,
            final Money totalInterestChargedForFullLoanTerm, Money cumulative, final LoanCharge loanCharge) {
        BigDecimal amount = BigDecimal.ZERO;
        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
            amount = amount.add(principalDisbursed.getAmount()).add(totalInterestChargedForFullLoanTerm.getAmount());
        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
            amount = amount.add(totalInterestChargedForFullLoanTerm.getAmount());
        } else {
            amount = amount.add(principalDisbursed.getAmount());
        }
        BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
        cumulative = cumulative.plus(loanChargeAmt);
        return cumulative;
    }

    private Money calculateInstallmentCharge(final PrincipalInterest principalInterestForThisPeriod, Money cumulative,
            final LoanCharge loanCharge, final LoanApplicationTerms loanApplicationTerm, int periodNumber, Money installmentCapitalizedChargeAmount) {
        if (loanCharge.getChargeCalculation().isPercentageBased()) {
            BigDecimal amount = BigDecimal.ZERO;
            if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                amount = amount.add(principalInterestForThisPeriod.principal().getAmount()).add(
                        principalInterestForThisPeriod.interest().getAmount());
            } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                amount = amount.add(principalInterestForThisPeriod.interest().getAmount());
            } else if (loanCharge.isInstalmentFee() && loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()) {
                Set<GroupLoanIndividualMonitoring> glimMembers = loanApplicationTerm.getGroupLoanIndividualMonitoring();
                BigDecimal chargeAmountPerInstallment = BigDecimal.ZERO;
                for(GroupLoanIndividualMonitoring glimMember : glimMembers) {
                    Set<GroupLoanIndividualMonitoringCharge> glimCharges = glimMember.getGroupLoanIndividualMonitoringCharges();
                    for(GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                        if(glimCharge.getCharge().getId().equals(loanCharge.getCharge().getId())) {
                            BigDecimal chargeAmount  = MathUtility.isNull(glimCharge.getRevisedFeeAmount())?glimCharge.getFeeAmount():glimCharge.getRevisedFeeAmount();
                            BigDecimal charge = Money.of(cumulative.getCurrency(), BigDecimal.valueOf(chargeAmount.doubleValue()/loanApplicationTerm.getNumberOfRepayments().doubleValue())).getAmount();
                            if(loanApplicationTerm.getNumberOfRepayments() != periodNumber){
                                chargeAmountPerInstallment = MathUtility.add(chargeAmountPerInstallment,charge);
                            }else{
                                BigDecimal chargeBeforeLastInstallment =  charge.multiply(BigDecimal.valueOf(loanApplicationTerm.getNumberOfRepayments()-1));
                                chargeAmountPerInstallment = chargeAmountPerInstallment.add(chargeAmount.subtract(chargeBeforeLastInstallment));
                            }
                        }
                    }
                }
                return Money.of(cumulative.getCurrency(), chargeAmountPerInstallment);
            } else {
                amount = amount.add(principalInterestForThisPeriod.principal().getAmount());
            }
            BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
            cumulative = cumulative.plus(loanChargeAmt);
        } else if (loanCharge.isInstalmentFee() && loanCharge.getChargeCalculation().isSlabBased()) {
        	if(loanCharge.isCapitalized()){
        		cumulative = cumulative.plus(installmentCapitalizedChargeAmount);
        	}else{
        		cumulative = cumulative.plus(MathUtility.getInstallmentAmount(loanCharge.amountOrPercentage(), loanApplicationTerm.getNumberOfRepayments(), cumulative.getCurrency(), periodNumber));
        	}
            
        } else {
            cumulative = cumulative.plus(loanCharge.amountOrPercentage());
        }
        return cumulative;
    }

    private Money cumulativePenaltyChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency monetaryCurrency,
            final PrincipalInterest principalInterestForThisPeriod, final Money principalDisbursed,
            final Money totalInterestChargedForFullLoanTerm, boolean isInstallmentChargeApplicable, LoanApplicationTerms loanApplicationTerm, 
            int installmentNumber, Money installmentCapitalizedChargeAmount) {

        Money cumulative = Money.zero(monetaryCurrency);
        Money totalLoanCharges = Money.zero(monetaryCurrency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                	if(loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()){
                    	cumulative = cumulative.plus(calculateInstallmentCharge(principalInterestForThisPeriod, cumulative, loanCharge, loanApplicationTerm, installmentNumber, installmentCapitalizedChargeAmount));
                    }else{
                    	cumulative = calculateInstallmentCharge(principalInterestForThisPeriod, cumulative, loanCharge, loanApplicationTerm, installmentNumber, installmentCapitalizedChargeAmount);
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = calculateChargeWithPercentage(principalDisbursed, totalInterestChargedForFullLoanTerm,
                            cumulative, loanCharge);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                } else if (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        cumulative = calculateChargeWithPercentage(principalDisbursed, totalInterestChargedForFullLoanTerm, cumulative,
                                loanCharge);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount());
                    }
                }
            }
        }
        
        if (isLastRepaymentPeriod(loanApplicationTerm.getNumberOfRepayments(), installmentNumber) && totalLoanCharges.compareTo(Money.zero(cumulative.getCurrency())) == 1) {
            Money totalGlimCharges = cumulative.multipliedBy(BigDecimal.valueOf(loanApplicationTerm.getNumberOfRepayments().doubleValue()));
            if (totalGlimCharges.compareTo(totalLoanCharges) != 0) {
                cumulative = cumulative.minus((totalGlimCharges.minus(totalLoanCharges)));
            }
        }

        return cumulative;
    }

    /**
     * Method preprocess the installments and transactions and sets the required
     * fields to generate the schedule
     */
    @Override
    public LoanScheduleDTO rescheduleNextInstallments(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Loan loan, final HolidayDetailDTO holidayDetailDTO, final List<LoanTransaction> transactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final LocalDate rescheduleFrom) {

        // Fixed schedule End Date for generating schedule
        final LocalDate scheduleTillDate = null;
        return rescheduleNextInstallments(mc, loanApplicationTerms, loan, holidayDetailDTO, transactions,
                loanRepaymentScheduleTransactionProcessor, rescheduleFrom, scheduleTillDate);

    }

    private LoanScheduleDTO rescheduleNextInstallments(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Loan loan, final HolidayDetailDTO holidayDetailDTO, final List<LoanTransaction> transactions,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor, final LocalDate rescheduleFrom,
            final LocalDate scheduleTillDate) {
        // Loan transactions to process and find the variation on payments
        Collection<RecalculationDetail> recalculationDetails = new ArrayList<>();
        
        for (LoanTransaction loanTransaction : transactions) {
            if (loanTransaction.isPaymentTransaction()) {
                recalculationDetails.add(new RecalculationDetail(loanTransaction.getTransactionDate(),
                        LoanTransaction.copyTransactionProperties(loanTransaction)));
            }
        }
        
        Set<LoanCharge> charges = loan.chargesCopy();
        
        final boolean applyInterestRecalculation = loanApplicationTerms.isInterestRecalculationEnabled();

        LoanScheduleParams loanScheduleParams = null;
        Collection<LoanScheduleModelPeriod> periods = new ArrayList<>();
        final List<LoanRepaymentScheduleInstallment> retainedInstallments = new ArrayList<>();

        // this block is to retain the schedule installments prior to the
        // provided date and creates late and early payment details for further
        // calculations
        if (rescheduleFrom != null) {
            Money principalToBeScheduled = getPrincipalToBeScheduled(loanApplicationTerms);
            // actual outstanding balance for interest calculation
            Money outstandingBalance = principalToBeScheduled;
            // total outstanding balance as per rest for interest calculation.
            Money outstandingBalanceAsPerRest = outstandingBalance;

            // this is required to update total fee amounts in the
            // LoanScheduleModel
            final BigDecimal chargesDueAtTimeOfDisbursement = deriveTotalChargesDueAtTimeOfDisbursement(charges,loanApplicationTerms);
            periods = createNewLoanScheduleListWithDisbursementDetails(loanApplicationTerms.fetchNumberOfRepaymentsAfterExceptions(),
                    loanApplicationTerms, chargesDueAtTimeOfDisbursement);
            MonetaryCurrency currency = outstandingBalance.getCurrency();

            // early payments will be added here and as per the selected
            // strategy
            // action will be performed on this value
            Money reducePrincipal = outstandingBalanceAsPerRest.zero();

            Money uncompoundedAmount = outstandingBalanceAsPerRest.zero();
            // principal changes will be added along with date(after applying
            // rest)
            // from when these amounts will effect the outstanding balance for
            // interest calculation
            final Map<LocalDate, Money> principalPortionMap = new HashMap<>();
            // compounding(principal) amounts will be added along with
            // date(after applying compounding frequency)
            // from when these amounts will effect the outstanding balance for
            // interest calculation
            final Map<LocalDate, Money> latePaymentMap = new HashMap<>();

            // compounding(interest/Fee) amounts will be added along with
            // date(after applying compounding frequency)
            // from when these amounts will effect the outstanding balance for
            // interest calculation
            final TreeMap<LocalDate, Money> compoundingMap = new TreeMap<>();
            final Map<LocalDate, Map<LocalDate, Money>> compoundingDateVariations = new HashMap<>();
            LocalDate currentDate = DateUtils.getLocalDateOfTenant();
            LocalDate lastRestDate = currentDate;
            if (loanApplicationTerms.getRestCalendarInstance() != null) {
                lastRestDate = getNextRestScheduleDate(currentDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
            }
            LocalDate actualRepaymentDate = loanApplicationTerms.getExpectedDisbursementDate();
            boolean isFirstRepayment = true;

            // cumulative fields
            Money totalCumulativePrincipal = principalToBeScheduled.zero();
            Money totalCumulativeInterest = principalToBeScheduled.zero();
            Money totalFeeChargesCharged = principalToBeScheduled.zero().plus(chargesDueAtTimeOfDisbursement);
            Money totalPenaltyChargesCharged = principalToBeScheduled.zero();
            Money totalRepaymentExpected = principalToBeScheduled.zero();

            // Actual period Number as per the schedule
            int periodNumber = 1;
            // Actual period Number plus interest only repayments
            int instalmentNumber = 1;
            LocalDate lastInstallmentDate = actualRepaymentDate;
            LocalDate periodStartDate = loanApplicationTerms.getExpectedDisbursementDate();
            // Set fixed Amortization Amounts(either EMI or Principal )
            Money totalCapitalizedChargeAmount = LoanUtilService.getTotalCapitalizedCharge(loanApplicationTerms);
            updateAmortization(mc, loanApplicationTerms, periodNumber,
                    outstandingBalance.plus(totalCapitalizedChargeAmount).minus(loanApplicationTerms.getAmountForUpfrontCollection()));

            final Map<LocalDate, Money> disburseDetailMap = new HashMap<>();
            boolean resetEMI = false;
            if (loanApplicationTerms.isMultiDisburseLoan()) {
                // fetches the first tranche amount and also updates other
                // tranche
                // details to map
                BigDecimal disburseAmt = getDisbursementAmount(loanApplicationTerms, loanApplicationTerms.getExpectedDisbursementDate(),
                        periods, disburseDetailMap, charges);
                outstandingBalance = outstandingBalance.zero().plus(disburseAmt);
                outstandingBalanceAsPerRest = outstandingBalance;
                principalToBeScheduled = principalToBeScheduled.zero().plus(disburseAmt);
                if(loanApplicationTerms.isEmiBasedOnDisbursements()) {
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms,
                            Money.of(loanApplicationTerms.getCurrency(), loanApplicationTerms.getAmountForUpfrontCollection()),
                            periodNumber, mc, principalToBeScheduled);	
                }
            }
            int loanTermInDays = 0;

            List<LoanTermVariationsData> exceptionDataList = loanApplicationTerms.getLoanTermVariations().getExceptionData();
            final ListIterator<LoanTermVariationsData> exceptionDataListIterator = exceptionDataList.listIterator();
            LoanTermVariationParams loanTermVariationParams = null;
            
            final List<LoanRepaymentScheduleInstallment> processInstallmentsInstallments = fetchRetainedInstallments(
                     loan.getRepaymentScheduleInstallments(), rescheduleFrom, currency);
            final List<LoanRepaymentScheduleInstallment> newRepaymentScheduleInstallments = new ArrayList<>();

            // Block process the installment and creates the period if it falls
            // before reschedule from date
            // This will create the recalculation details by applying the
            // transactions
            for (LoanRepaymentScheduleInstallment installment : processInstallmentsInstallments) {
                // this will generate the next schedule due date and allows to
                // process the installment only if recalculate from date is
                // greater than due date
                if (installment.getDueDate().isAfter(lastInstallmentDate) || isFirstRepayment) {
             //       if (totalCumulativePrincipal.isGreaterThanOrEqualTo(loanApplicationTerms.getTotalDisbursedAmount())) {
               //         break;
                //    }
                    LocalDate previousRepaymentDate = actualRepaymentDate;
                    ArrayList<LoanTermVariationsData> dueDateVariationsDataList = new ArrayList<>();
                    actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(actualRepaymentDate,
                            loanApplicationTerms, isFirstRepayment);
                    isFirstRepayment = false;
                    AdjustedDateDetailsDTO adjustedDateDetailsDTO = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                            holidayDetailDTO);
                    lastInstallmentDate = adjustedDateDetailsDTO.getChangedScheduleDate();
                    actualRepaymentDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();

                 // check for date changes
                    while (loanApplicationTerms.getLoanTermVariations().hasDueDateVariation(lastInstallmentDate)) {
                        LoanTermVariationsData variation = loanApplicationTerms.getLoanTermVariations().nextDueDateVariation();
                        if (!variation.isSpecificToInstallment()) {
                            actualRepaymentDate = variation.getDateValue();
                            loanApplicationTerms.setSeedDate(actualRepaymentDate);
                        }
                        lastInstallmentDate = variation.getDateValue();
                        dueDateVariationsDataList.add(variation);
                    }
                    
                    loanTermVariationParams = applyExceptionLoanTermVariations(loanApplicationTerms, lastInstallmentDate,
                            exceptionDataListIterator, instalmentNumber, totalCumulativePrincipal, totalCumulativeInterest, mc, principalToBeScheduled);

                    while (loanTermVariationParams != null && loanTermVariationParams.isSkipPeriod()) {
                        actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(actualRepaymentDate,
                                loanApplicationTerms, isFirstRepayment);
                        isFirstRepayment = false;
                        lastInstallmentDate = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                                holidayDetailDTO).getChangedScheduleDate();
                        loanTermVariationParams = applyExceptionLoanTermVariations(loanApplicationTerms, lastInstallmentDate,
                                exceptionDataListIterator, instalmentNumber, totalCumulativePrincipal, totalCumulativeInterest, mc, principalToBeScheduled);
                    }

                    if (!lastInstallmentDate.isBefore(rescheduleFrom)) {
                        actualRepaymentDate = previousRepaymentDate;
                        int dueDateVariationsProcessed = dueDateVariationsDataList.size();
                        while (dueDateVariationsProcessed > 0) {
                            loanApplicationTerms.getLoanTermVariations().previousDueDateVariation();
                            dueDateVariationsProcessed--;
                        }
                        break;
                    }
                    periodNumber++;

                    for (LoanTermVariationsData dueDateVariation : dueDateVariationsDataList) {
                        dueDateVariation.setProcessed(true);
                    }

                    if (loanTermVariationParams != null && loanTermVariationParams.isSkipPeriod()) {
                        ArrayList<LoanTermVariationsData> variationsDataList = loanTermVariationParams.getVariationsDataList();
                        for (LoanTermVariationsData variationsData : variationsDataList) {
                            variationsData.setProcessed(true);
                        }
                    }
                }

                if(loanApplicationTerms.isMultiDisburseLoan() && resetEMI){
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, periodNumber-1, mc, principalToBeScheduled);
                    resetEMI = false;
                }
                for (Map.Entry<LocalDate, Money> disburseDetail : disburseDetailMap.entrySet()) {
                    if (disburseDetail.getKey().isAfter(installment.getFromDate())
                            && !disburseDetail.getKey().isAfter(installment.getDueDate())) {
                        // creates and add disbursement detail to the repayments
                        // period
                        BigDecimal chargesDueAtTimeOfDisbursementForPeriod = fetchTotalTrancheChargeForDisburesement(charges, disburseDetail.getKey());
                        final LoanScheduleModelDisbursementPeriod disbursementPeriod = LoanScheduleModelDisbursementPeriod.disbursement(
                                disburseDetail.getKey(), disburseDetail.getValue(), chargesDueAtTimeOfDisbursementForPeriod);
                        periods.add(disbursementPeriod);
                        // updates actual outstanding balance with new
                        // disbursement detail
                        outstandingBalance = outstandingBalance.plus(disburseDetail.getValue());
                        principalToBeScheduled = principalToBeScheduled.plus(disburseDetail.getValue());
                        if(loanApplicationTerms.isEmiBasedOnDisbursements()) {
                            if(disburseDetail.getKey().isBefore(installment.getDueDate())){
                        	adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, periodNumber-1, mc, principalToBeScheduled);
                            }else{
                                resetEMI = true;
                            }
                        }
                    }
                }

                if(instalmentNumber == 1 && loanApplicationTerms.isFirstInstallmentCalculationRequired()){
                    loanApplicationTerms.updateTotalInterestAccounted(installment.getInterestCharged(currency));
                }
                
                // calculation of basic fields to start the schedule generation
                // from the middle
                periodStartDate = installment.getDueDate();
                installment.resetDerivedComponents();
                newRepaymentScheduleInstallments.add(installment);
                outstandingBalance = outstandingBalance.minus(installment.getPrincipal(currency));
                final LoanScheduleModelPeriod loanScheduleModelPeriod = createLoanScheduleModelPeriod(installment, outstandingBalance);
                periods.add(loanScheduleModelPeriod);
                totalCumulativePrincipal = totalCumulativePrincipal.plus(installment.getPrincipal(currency));
                totalCumulativeInterest = totalCumulativeInterest.plus(installment.getInterestCharged(currency));
                totalFeeChargesCharged = totalFeeChargesCharged.plus(installment.getFeeChargesCharged(currency));
                totalPenaltyChargesCharged = totalPenaltyChargesCharged.plus(installment.getPenaltyChargesCharged(currency));
                instalmentNumber++;
                loanTermInDays = Days.daysBetween(installment.getFromDate(), installment.getDueDate()).getDays();
                if (loanApplicationTerms.isInterestRecalculationEnabled()) {
                    // populates the collection with transactions till the due
                    // date
                    // of
                    // the period for interest recalculation enabled loans
                    Collection<RecalculationDetail> applicableTransactions = getApplicableTransactionsForPeriod(applyInterestRecalculation,
                            installment.getDueDate(), recalculationDetails);

                    if (loanApplicationTerms.isSubsidyApplicable()) {
                        processSubsidyTransactions(principalPortionMap, applicableTransactions, loanApplicationTerms, holidayDetailDTO);
                    }

                    // calculates the expected principal value for this
                    // repayment
                    // schedule
                    Money principalPortionCalculated = principalToBeScheduled.zero();
                    if (!installment.isRecalculatedInterestComponent() && !loanApplicationTerms.isPrincipalGraceApplicableForThisPeriod(periodNumber-1)) {
                        principalPortionCalculated = calculateExpectedPrincipalPortion(installment.getInterestCharged(currency),
                                loanApplicationTerms);
                    }

                    // expected principal considering the previously paid excess
                    // amount
                    Money actualPrincipalPortion = principalPortionCalculated.minus(reducePrincipal);
                    if (actualPrincipalPortion.isLessThanZero()) {
                        actualPrincipalPortion = principalPortionCalculated.zero();
                    }

                    Money unprocessed = updateEarlyPaidAmountsToMap(loanApplicationTerms, holidayDetailDTO,
                            loanRepaymentScheduleTransactionProcessor, newRepaymentScheduleInstallments, currency, principalPortionMap,
                            installment, applicableTransactions, actualPrincipalPortion);

                    // this block is to adjust the period number based on the
                    // actual
                    // schedule due date and installment due date
                    // recalculatedInterestComponent installment shouldn't be
                    // considered while calculating fixed EMI amounts
                    int period = periodNumber;
                    if (!lastInstallmentDate.isEqual(installment.getDueDate())) {
                        period--;
                    }
                    reducePrincipal = fetchEarlyPaidAmount(installment.getPrincipal(currency), principalPortionCalculated, reducePrincipal,
                            loanApplicationTerms, totalCumulativePrincipal, period, mc, principalToBeScheduled);
                    // Updates principal paid map with efective date for
                    // reducing
                    // the amount from outstanding balance(interest calculation)
                    LocalDate amountApplicableDate = getNextRestScheduleDate(installment.getDueDate().minusDays(1), loanApplicationTerms,
                            holidayDetailDTO);
                    // updates map with the installment principal amount
                    // excluding
                    // unprocessed amount since this amount is already
                    // accounted.
                    updateMapWithAmount(principalPortionMap, installment.getPrincipal(currency).minus(unprocessed), amountApplicableDate);
                    uncompoundedAmount = updateCompoundingDetailsForPartialScheduleGeneration(installment, loanApplicationTerms,
                            principalPortionMap, compoundingDateVariations, uncompoundedAmount, applicableTransactions, lastRestDate,
                            holidayDetailDTO);
                    // update outstanding balance for interest calculation
                    outstandingBalanceAsPerRest = updateBalanceForInterestCalculation(principalPortionMap, installment.getDueDate(),
                            outstandingBalanceAsPerRest, false);
                    outstandingBalanceAsPerRest = updateBalanceForInterestCalculation(disburseDetailMap, installment.getDueDate(),
                            outstandingBalanceAsPerRest, true);
                    // updates the map with over due amounts
                    updateLatePaymentsToMap(loanApplicationTerms, holidayDetailDTO, currency, latePaymentMap, lastInstallmentDate,
                            newRepaymentScheduleInstallments, true, lastRestDate);
                } else {
                    outstandingBalanceAsPerRest = outstandingBalance;
                }
                totalRepaymentExpected = totalCumulativePrincipal.plus(totalCumulativeInterest).plus(totalFeeChargesCharged)
                        .plus(totalPenaltyChargesCharged);
                if(loanTermVariationParams!= null && loanTermVariationParams.recalculateAmounts){
                    adjustInstallmentOrPrincipalAmount(loanApplicationTerms, totalCumulativePrincipal, periodNumber, mc, principalToBeScheduled);
                }
           
            }

            // for partial schedule generation
            if (!newRepaymentScheduleInstallments.isEmpty() && totalCumulativeInterest.isGreaterThanZero()) {
                Money totalOutstandingInterestPaymentDueToGrace = Money.zero(currency);
                loanScheduleParams = LoanScheduleParams.createLoanScheduleParamsForPartialUpdate(periodNumber, instalmentNumber,
                        loanTermInDays, periodStartDate, actualRepaymentDate, totalCumulativePrincipal, totalCumulativeInterest,
                        totalFeeChargesCharged, totalPenaltyChargesCharged, totalRepaymentExpected,
                        totalOutstandingInterestPaymentDueToGrace, reducePrincipal, principalPortionMap, latePaymentMap, compoundingMap,
                        uncompoundedAmount, disburseDetailMap, principalToBeScheduled, outstandingBalance, outstandingBalanceAsPerRest,
                        newRepaymentScheduleInstallments, recalculationDetails, loanRepaymentScheduleTransactionProcessor,
                        scheduleTillDate, currency, applyInterestRecalculation);
                loanScheduleParams.setResetEMI(resetEMI);
                retainedInstallments.addAll(newRepaymentScheduleInstallments);
                loanScheduleParams.getCompoundingDateVariations().putAll(compoundingDateVariations);
                loanApplicationTerms.updateTotalInterestDue(Money.of(currency, loan.getLoanSummary().getTotalInterestCharged()));
                if (loanApplicationTerms.isInterestRecalculationEnabled()) {
                    loanApplicationTerms.setAdjustLastInstallmentInterestForRounding(false);
                }else {
                    loanApplicationTerms.setAdjustLastInstallmentInterestForRounding(loanApplicationTerms.isAdjustInterestForRounding());
                }
            } else {
                loanApplicationTerms.getLoanTermVariations().resetVariations();
            }

        }
        // for complete schedule generation
        if (loanScheduleParams == null) {
            loanScheduleParams = LoanScheduleParams.createLoanScheduleParamsForCompleteUpdate(recalculationDetails,
                    loanRepaymentScheduleTransactionProcessor, scheduleTillDate, applyInterestRecalculation);
            if (loanApplicationTerms.isAdjustFirstEMIAmount()) {
                boolean isFirstRepayment = true;
                LocalDate actualRepaymentDate = this.scheduledDateGenerator.generateNextRepaymentDate(
                        loanApplicationTerms.getExpectedDisbursementDate(), loanApplicationTerms, isFirstRepayment);

                LocalDate scheduleDate = this.scheduledDateGenerator.adjustRepaymentDate(actualRepaymentDate, loanApplicationTerms,
                        holidayDetailDTO).getChangedScheduleDate();
                for (LoanTermVariationsData loanTermVariationsData : loanApplicationTerms.getLoanTermVariations().getDueDateVariation()) {
                    if (!loanTermVariationsData.getTermApplicableFrom().isAfter(scheduleDate)) {
                        scheduleDate = loanTermVariationsData.getDateValue();
                    }
                }
            }
            periods.clear();
        }
        EmiDetails emiDetails  = new EmiDetails();
        LoanScheduleModel loanScheduleModel = generate(mc, loanApplicationTerms, charges, holidayDetailDTO, loanScheduleParams, emiDetails);
        for (LoanScheduleModelPeriod loanScheduleModelPeriod : loanScheduleModel.getPeriods()) {
            if (loanScheduleModelPeriod.isRepaymentPeriod()) {
                // adding newly created repayment periods to installments
                addLoanRepaymentScheduleInstallment(retainedInstallments, loanScheduleModelPeriod);
            }
        }
        periods.addAll(loanScheduleModel.getPeriods());
        LoanScheduleModel loanScheduleModelwithPeriodChanges = LoanScheduleModel.withLoanScheduleModelPeriods(periods, loanScheduleModel);
        return LoanScheduleDTO.from(retainedInstallments, loanScheduleModelwithPeriodChanges);
    }
    
    /**
     * set capitalized charges detailes to loan schedule params
     * 
     * @param loanScheduleParams
     * @param loanApplicationTerms
     */
    private void setCapitalizedChargesDetailesToLoanScheduleParams(final LoanScheduleParams loanScheduleParams,
            final LoanApplicationTerms loanApplicationTerms) {
        /**
         * Set the total capitalized charge amount.
         */
        loanScheduleParams.setTotalCapitalizedChargeAmount(LoanUtilService.getTotalCapitalizedCharge(loanApplicationTerms));
        /**
         * Set the total accounted capitalized charge amount from the retained
         * installments
         */
        if (!CollectionUtils.isEmpty(loanScheduleParams.getInstallments())) {
            Money totalAccountedCapitalizedCharge = Money.zero(loanApplicationTerms.getCurrency());
            if (!CollectionUtils.isEmpty(loanApplicationTerms.getCapitalizedCharges())) {
                for (final LoanRepaymentScheduleInstallment installment : loanScheduleParams.getInstallments()) {
                    if (!CollectionUtils.isEmpty(installment.getLoan().getLoanCharges())) {
                        for (final LoanCharge loanCharge : installment.getLoan().getLoanCharges()) {
                            if (loanCharge.isCapitalized()) {
                                if (loanCharge.getInstallmentLoanCharge(installment.getInstallmentNumber()) != null) {
                                    totalAccountedCapitalizedCharge = totalAccountedCapitalizedCharge.plus(loanCharge
                                            .getInstallmentLoanCharge(installment.getInstallmentNumber()).getAmount(
                                                    loanApplicationTerms.getCurrency()));
                                }
                            }
                        }
                    }
                }
            }
            loanScheduleParams.setTotalAccountedCapitalizedCharge(totalAccountedCapitalizedCharge);
        }
    }

    public List<LoanRepaymentScheduleInstallment> fetchRetainedInstallments(
            final List<LoanRepaymentScheduleInstallment> repaymentScheduleInstallments, final LocalDate rescheduleFrom,
            MonetaryCurrency currency) {
        List<LoanRepaymentScheduleInstallment> newRepaymentScheduleInstallments = new ArrayList<>();
        List<LoanRepaymentScheduleInstallment> interestRecalcualtedPeriods = new ArrayList<>();
        int lastInterestAvilablePeriod = 0;
        int processedPeriod = 0;
        for (LoanRepaymentScheduleInstallment installment : repaymentScheduleInstallments) {
            if (installment.getDueDate().isBefore(rescheduleFrom)) {
                if(installment.isRecalculatedInterestComponent()){
                    interestRecalcualtedPeriods.add(installment);
                }else{
                    newRepaymentScheduleInstallments.addAll(interestRecalcualtedPeriods);
                    interestRecalcualtedPeriods.clear();
                    newRepaymentScheduleInstallments.add(installment);
                }
               
                if(installment.getInterestCharged(currency).isGreaterThanZero()){
                    lastInterestAvilablePeriod = installment.getInstallmentNumber();
                }
                processedPeriod = installment.getInstallmentNumber();
            } else {
                break;
            }
        }
       
        // this block is to remove the periods till last interest available
        // period.
        // if the last retained period is interest grace period then we
        // can't get the interest of last period without calculating again
        // to fix this adjusting retained periods
        if(lastInterestAvilablePeriod != processedPeriod){
            final List<LoanRepaymentScheduleInstallment> retainRepaymentScheduleInstallments = new ArrayList<>();
            for (LoanRepaymentScheduleInstallment installment : newRepaymentScheduleInstallments) {
                if(installment.getInstallmentNumber() <= lastInterestAvilablePeriod){
                    retainRepaymentScheduleInstallments.add(installment);
                }
            }
            newRepaymentScheduleInstallments.retainAll(retainRepaymentScheduleInstallments);
        }
        return newRepaymentScheduleInstallments;
    }
    /**
     * Method identifies the early paid amounts for a installment and update the
     * principal map for further calculations
     */
    private Money updateEarlyPaidAmountsToMap(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor,
            final List<LoanRepaymentScheduleInstallment> newRepaymentScheduleInstallments, MonetaryCurrency currency,
            final Map<LocalDate, Money> principalPortionMap, LoanRepaymentScheduleInstallment installment,
            Collection<RecalculationDetail> applicableTransactions, Money actualPrincipalPortion) {
        Money unprocessed = Money.zero(currency);
        Money totalUnprocessed = Money.zero(currency);
        for (RecalculationDetail detail : applicableTransactions) {
            if (!detail.isProcessed()) {
                Money principalProcessed = installment.getPrincipalCompleted(currency);
                List<LoanTransaction> currentTransactions = new ArrayList<>(2);
                currentTransactions.add(detail.getTransaction());
                // applies the transaction as per transaction strategy
                // on scheduled installments to identify the
                // unprocessed(early payment ) amounts
                loanRepaymentScheduleTransactionProcessor.handleRepaymentSchedule(currentTransactions, currency,
                        newRepaymentScheduleInstallments);

                // Identifies totalEarlyPayment and early paid amount with this
                // transaction
                Money principalPaidWithTransaction = installment.getPrincipalCompleted(currency).minus(principalProcessed);
                Money totalEarlyPayment = installment.getPrincipalCompleted(currency).minus(actualPrincipalPortion);

                if (totalEarlyPayment.isGreaterThanZero()) {
                    unprocessed = principalPaidWithTransaction;
                    // will execute this block if partial amount paid as
                    // early
                    if (principalPaidWithTransaction.isGreaterThan(totalEarlyPayment)) {
                        unprocessed = totalEarlyPayment;
                    }
                }
                // updates principal portion map with the early
                // payment amounts and applicable date as per rest
                LocalDate applicableDate = getNextRestScheduleDate(detail.getTransactionDate().minusDays(1), loanApplicationTerms,
                        holidayDetailDTO);
                updateMapWithAmount(principalPortionMap, unprocessed, applicableDate);
                totalUnprocessed = totalUnprocessed.plus(unprocessed);
            }
        }
        return totalUnprocessed;
    }

    private Money updateCompoundingDetailsForPartialScheduleGeneration(final LoanRepaymentScheduleInstallment installment,
            LoanApplicationTerms loanApplicationTerms, Map<LocalDate, Money> principalMap,
            final Map<LocalDate, Map<LocalDate, Money>> compoundingDateVariations, final Money uncompoundedAmount,
            final Collection<RecalculationDetail> applicableTransactions, LocalDate lastRestDate, HolidayDetailDTO holidayDetailDTO) {
        Money uncompounded = uncompoundedAmount;
        Money uncompoundedInThisPeriod = uncompounded.zero();
        MonetaryCurrency currency = uncompoundedAmount.getCurrency();
        Money amountCharged = getIncomeForCompounding(loanApplicationTerms, currency, installment);
        final List<LoanInterestRecalcualtionAdditionalDetails> details = installment.getLoanCompoundingDetails();
        Money totalCompounded = Money.zero(currency);
        Map<LocalDate, Money> compoundingMap = new TreeMap<>();
        for (LoanInterestRecalcualtionAdditionalDetails additionalDetails : details) {
            LocalDate effectiveDate = additionalDetails.getEffectiveDate();
            if (loanApplicationTerms.allowCompoundingOnEod()) {
                effectiveDate = effectiveDate.plusDays(1);
            }
            compoundingMap.put(effectiveDate, Money.of(currency, additionalDetails.getAmount()));
            totalCompounded = totalCompounded.plus(additionalDetails.getAmount());
            updateMapWithAmount(principalMap, Money.of(currency, additionalDetails.getAmount()).negated(), effectiveDate);
        }
        compoundingDateVariations.put(installment.getFromDate(), compoundingMap);
        final LocalDate restDate = getNextRestScheduleDate(installment.getDueDate().minusDays(1), loanApplicationTerms, holidayDetailDTO);
        final boolean isPastDate = installment.getDueDate().isBefore(DateUtils.getLocalDateOfTenant());
        if (totalCompounded.isGreaterThanZero()) {
            if (isPastDate) {
                updateMapWithAmount(principalMap, totalCompounded.minus(uncompounded), lastRestDate);
            } else {
                updateMapWithAmount(principalMap, totalCompounded.minus(uncompounded), restDate);
            }
            uncompounded = amountCharged.plus(uncompounded).minus(totalCompounded);
            uncompoundedInThisPeriod = uncompounded;
        } else {
            uncompounded = uncompounded.plus(amountCharged);
            uncompoundedInThisPeriod = amountCharged;
        }
        if (uncompoundedInThisPeriod.isGreaterThanZero()) {
            if (isPastDate) {
                updateMapWithAmount(principalMap, uncompoundedInThisPeriod, lastRestDate);
            } else {
                updateMapWithAmount(principalMap, uncompoundedInThisPeriod, restDate);
            }
        }
        for (RecalculationDetail detail : applicableTransactions) {
            LocalDate applicableDate = getNextRestScheduleDate(detail.getTransactionDate().minusDays(1), loanApplicationTerms,
                    holidayDetailDTO);
            adjustCompoundedAmountWithPaidDetail(principalMap, lastRestDate, applicableDate, detail.getTransaction(), loanApplicationTerms,
                    currency);
        }
        return uncompounded;
    }

    private void updateAmortization(final MathContext mc, final LoanApplicationTerms loanApplicationTerms, int periodNumber,
            Money outstandingBalance) {
        if (loanApplicationTerms.getAmortizationMethod().isEqualInstallment()) {
            updateFixedInstallmentAmount(mc, loanApplicationTerms, periodNumber, outstandingBalance);
        } else {
            loanApplicationTerms.updateFixedPrincipalAmount(mc, periodNumber, outstandingBalance);
        }
    }

    /**
     * Method identifies early paid amount and applies the early payment
     * strategy
     * @param principalToBeScheduled TODO
     */
    private Money fetchEarlyPaidAmount(final Money principalPortion, final Money principalPortionCalculated, final Money reducePrincipal,
            final LoanApplicationTerms applicationTerms, final Money totalCumulativePrincipal, int periodNumber, final MathContext mc,
            Money principalToBeScheduled) {
        Money existingEarlyPayment = reducePrincipal.minus(principalPortionCalculated);
        Money earlyPaidAmount = principalPortion.plus(existingEarlyPayment);
        if (existingEarlyPayment.isLessThanZero()) {
            existingEarlyPayment = existingEarlyPayment.zero();
        }
        boolean isEarlyPaid = earlyPaidAmount.isGreaterThan(existingEarlyPayment);

        if (earlyPaidAmount.isLessThanZero()) {
            earlyPaidAmount = earlyPaidAmount.zero();
        }

        if (isEarlyPaid  && applicationTerms.getRescheduleStrategyMethod() != null) {
            switch (applicationTerms.getRescheduleStrategyMethod()) {
                case REDUCE_EMI_AMOUNT:
                    adjustInstallmentOrPrincipalAmount(applicationTerms, totalCumulativePrincipal, periodNumber, mc, principalToBeScheduled);
                    earlyPaidAmount = earlyPaidAmount.zero();
                break;
                case REDUCE_NUMBER_OF_INSTALLMENTS:
                    // number of installments will reduce but emi amount won't
                    // get effected
                    earlyPaidAmount = earlyPaidAmount.zero();
                break;
                case RESCHEDULE_NEXT_REPAYMENTS:
                // will reduce principal from the reduce Principal for each
                // installment(means installments will have less emi amount)
                // until this
                // amount becomes zero
                break;
                default:
                break;
            }
        }

        return earlyPaidAmount;
    }

    private Money calculateExpectedPrincipalPortion(final Money interestPortion, final LoanApplicationTerms applicationTerms) {
        Money principalPortionCalculated = interestPortion.zero();
        if (applicationTerms.getAmortizationMethod().isEqualInstallment()) {
            principalPortionCalculated = principalPortionCalculated.plus(applicationTerms.getFixedEmiAmount()).minus(interestPortion);
        } else {
            principalPortionCalculated = principalPortionCalculated.plus(applicationTerms.getFixedPrincipalAmount());
        }
        return principalPortionCalculated;
    }

    private LoanRepaymentScheduleInstallment addLoanRepaymentScheduleInstallment(final List<LoanRepaymentScheduleInstallment> installments,
            final LoanScheduleModelPeriod scheduledLoanInstallment) {
        LoanRepaymentScheduleInstallment installment = null;
        if (scheduledLoanInstallment.isRepaymentPeriod()) {
            installment = new LoanRepaymentScheduleInstallment(null, scheduledLoanInstallment.periodNumber(),
                    scheduledLoanInstallment.periodFromDate(), scheduledLoanInstallment.periodDueDate(),
                    scheduledLoanInstallment.principalDue(), scheduledLoanInstallment.interestDue(),
                    scheduledLoanInstallment.feeChargesDue(), scheduledLoanInstallment.penaltyChargesDue(),
                    scheduledLoanInstallment.isRecalculatedInterestComponent(), scheduledLoanInstallment.getLoanCompoundingDetails(),
                    scheduledLoanInstallment.advancePayment(), scheduledLoanInstallment.capitalChargeDue());
            installments.add(installment);
        }
        return installment;
    }

    private LoanScheduleModelPeriod createLoanScheduleModelPeriod(final LoanRepaymentScheduleInstallment installment,
            final Money outstandingPrincipal) {
        final MonetaryCurrency currency = outstandingPrincipal.getCurrency();
        LoanScheduleModelPeriod scheduledLoanInstallment = LoanScheduleModelRepaymentPeriod.repayment(installment.getInstallmentNumber(),
                installment.getFromDate(), installment.getDueDate(), installment.getPrincipal(currency), outstandingPrincipal,
                installment.getInterestCharged(currency), installment.getFeeChargesCharged(currency),
                installment.getPenaltyChargesCharged(currency), installment.getDue(currency),
                installment.isRecalculatedInterestComponent(), installment.getAdvancePaymentAmount(currency));
        return scheduledLoanInstallment;
    }

    private LocalDate getNextRestScheduleDate(LocalDate startDate, LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO) {
        LocalDate nextScheduleDate = null;
        if (loanApplicationTerms.getRecalculationFrequencyType().isSameAsRepayment()) {
            nextScheduleDate = this.scheduledDateGenerator.generateNextScheduleDateStartingFromDisburseDate(startDate,
                    loanApplicationTerms, holidayDetailDTO);
        } else {
            CalendarInstance calendarInstance = loanApplicationTerms.getRestCalendarInstance();
            nextScheduleDate = CalendarUtils.getNextScheduleDate(calendarInstance.getCalendar(), startDate);
        }

        return nextScheduleDate;
    }

    private LocalDate getNextCompoundScheduleDate(LocalDate startDate, LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO) {
        LocalDate nextScheduleDate = null;
        if (!loanApplicationTerms.getInterestRecalculationCompoundingMethod().isCompoundingEnabled()) { return null; }
        if (loanApplicationTerms.getCompoundingFrequencyType().isSameAsRepayment()) {
            nextScheduleDate = this.scheduledDateGenerator.generateNextScheduleDateStartingFromDisburseDate(startDate,
                    loanApplicationTerms, holidayDetailDTO);
        } else {
            CalendarInstance calendarInstance = loanApplicationTerms.getCompoundingCalendarInstance();
            nextScheduleDate = CalendarUtils.getNextScheduleDate(calendarInstance.getCalendar(), startDate);
            if (loanApplicationTerms.allowCompoundingOnEod()) {
                nextScheduleDate = nextScheduleDate.plusDays(1);
            }
        }

        return nextScheduleDate;
    }

    /**
     * Method returns the amount payable to close the loan account as of today.
     */
    @Override
    public LoanRepaymentScheduleInstallment calculatePrepaymentAmount(final MonetaryCurrency currency, final LocalDate onDate,
            final LoanApplicationTerms loanApplicationTerms, final MathContext mc, Loan loan, final HolidayDetailDTO holidayDetailDTO,
            final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor) {

        LocalDate calculateTill = onDate;
        if (loanApplicationTerms.getPreClosureInterestCalculationStrategy().calculateTillRestFrequencyEnabled()) {
            calculateTill = getNextRestScheduleDate(onDate.minusDays(1), loanApplicationTerms, holidayDetailDTO);
        }
        List<LoanTransaction> loanTransactions = loan.retreiveCopyOfTransactionsPostDisbursementExcludeAccruals();
        
        LoanScheduleDTO loanScheduleDTO = rescheduleNextInstallments(mc, loanApplicationTerms, loan, holidayDetailDTO, loanTransactions,
                loanRepaymentScheduleTransactionProcessor, onDate, calculateTill);

        loanRepaymentScheduleTransactionProcessor.handleTransaction(loanApplicationTerms.getExpectedDisbursementDate(), loanTransactions,
                currency, loanScheduleDTO.getInstallments(), loan.chargesCopy(), loan.getDisbursementDetails(),
                loan.isPeriodicAccrualAccountingEnabledOnLoanProduct());
        Money feeCharges = Money.zero(currency);
        Money penaltyCharges = Money.zero(currency);
        Money totalPrincipal = Money.zero(currency);
        Money totalInterest = Money.zero(currency);
        for (final LoanRepaymentScheduleInstallment currentInstallment : loanScheduleDTO.getInstallments()) {
            if (currentInstallment.isNotFullyPaidOff()) {
                totalPrincipal = totalPrincipal.plus(currentInstallment.getPrincipalOutstanding(currency));
                totalInterest = totalInterest.plus(currentInstallment.getInterestOutstanding(currency));
                feeCharges = feeCharges.plus(currentInstallment.getFeeChargesOutstanding(currency));
                penaltyCharges = penaltyCharges.plus(currentInstallment.getPenaltyChargesOutstanding(currency));
            }
        }
        final List<LoanInterestRecalcualtionAdditionalDetails> compoundingDetails = null;
        return new LoanRepaymentScheduleInstallment(null, 0, onDate, onDate, totalPrincipal.getAmount(), totalInterest.getAmount(),
                feeCharges.getAmount(), penaltyCharges.getAmount(), false, compoundingDetails);
    }
    
    /**
     * Method calculates first Installment amount if the configuration to adjust first Installment amount enabled
     */
    @Override
    public BigDecimal calculateFirstInstallmentAmount(final MathContext mc, final LoanApplicationTerms loanApplicationTerms,
            final Set<LoanCharge> loanCharges, final HolidayDetailDTO holidayDetailDTO) {
        BigDecimal firstEmiAmount = null;
        if (loanApplicationTerms.adjustFirstEMIAmount()) {
            final boolean adjustInterestForRounding = loanApplicationTerms.isAdjustInterestForRounding();
            loanApplicationTerms.setAdjustInterestForRounding(false);
            final LoanScheduleParams loanScheduleRecalculationDTO = null;
            EmiDetails emiDetails = new EmiDetails();
            BigDecimal lastDiffAmount = BigDecimal.ZERO;
            BigDecimal extraInterest = BigDecimal.ZERO;
            boolean isFirstRepayment = true;
            LocalDate firstRepaymentdate = this.scheduledDateGenerator.generateNextRepaymentDate(
                    loanApplicationTerms.getExpectedDisbursementDate(), loanApplicationTerms, isFirstRepayment);
            final LocalDate idealDisbursementDate = this.scheduledDateGenerator.idealDisbursementDateBasedOnFirstRepaymentDate(
                    loanApplicationTerms.getLoanTermPeriodFrequencyType(), loanApplicationTerms.getRepaymentEvery(), firstRepaymentdate,
                    loanApplicationTerms.getLoanCalendar(), loanApplicationTerms.getHolidayDetailDTO(), loanApplicationTerms);
            LocalDate periodStartDateApplicableForInterest = this.scheduledDateGenerator.calculateInterestStartDateForPeriod(
                    loanApplicationTerms, loanApplicationTerms.getExpectedDisbursementDate(), idealDisbursementDate, firstRepaymentdate);
            int totalInterestDays = Days.daysBetween(periodStartDateApplicableForInterest, firstRepaymentdate).getDays() - 30;
            if (totalInterestDays > 0) {

                Money outstandingBalance = loanApplicationTerms.getPrincipal();
                if (loanApplicationTerms.isMultiDisburseLoan()) {
                    outstandingBalance = outstandingBalance.zero();
                    for (DisbursementData disbursementData : loanApplicationTerms.getDisbursementDatas()) {
                        if (disbursementData.disbursementDate().equals(loanApplicationTerms.getExpectedDisbursementDate())) {
                            outstandingBalance = outstandingBalance.plus(disbursementData.amount());
                        }
                    }
                }

                Money actualInterest = loanApplicationTerms
                        .calculateDecliningInterestDueForInstallmentBeforeApplyingGrace(paymentPeriodsInOneYearCalculator, mc,
                                outstandingBalance, periodStartDateApplicableForInterest, firstRepaymentdate);

                final LocalDate periodStartDate = loanApplicationTerms.getExpectedDisbursementDate().withDayOfMonth(1);
                final LocalDate periodEndDate = loanApplicationTerms.getPeriodEndDate(periodStartDate);

                Money totalInterestPerPeriod = loanApplicationTerms.calculateDecliningInterestDueForInstallmentBeforeApplyingGrace(
                        paymentPeriodsInOneYearCalculator, mc, outstandingBalance, periodStartDate, periodEndDate);
                extraInterest = extraInterest.add(actualInterest.minus(totalInterestPerPeriod).getAmount());
            }
            BigDecimal addDiff = BigDecimal.ONE;
            loanApplicationTerms.setFirstEmiAmount(null);
            LoanScheduleModel model = null;
            while (true) {
                model = generate(mc, loanApplicationTerms, loanCharges, holidayDetailDTO, loanScheduleRecalculationDTO, emiDetails);
                if (firstEmiAmount == null) {
                    firstEmiAmount = emiDetails.getEmiAmount();
                    int totalPeriodsSize = model.getPeriods().size();
                    int disbursements = 1;
                    if(loanApplicationTerms.isMultiDisburseLoan()){
                        disbursements = loanApplicationTerms.getDisbursementDatas().size();
                    }
                    loanApplicationTerms.setFixedNumberOfRepayments(totalPeriodsSize - disbursements);
                }
                BigDecimal amountLeft = emiDetails.lastEmiAmount.subtract(emiDetails.getEmiAmount());
                if (amountLeft.compareTo(BigDecimal.ZERO) == 0
                        || (addDiff.compareTo(BigDecimal.valueOf(.01)) == 0 && ((lastDiffAmount.compareTo(BigDecimal.ZERO) == 1 && amountLeft
                                .compareTo(BigDecimal.ZERO) == -1) || (lastDiffAmount.compareTo(BigDecimal.ZERO) == -1 && amountLeft
                                .compareTo(BigDecimal.ZERO) == 1)))) {

                    firstEmiAmount = firstEmiAmount.add(amountLeft).setScale(MoneyHelper.getScaleForInternalCalculations(),
                            MoneyHelper.getRoundingModeForInternalCalculations());
                    loanApplicationTerms.setFirstEmiAmount(firstEmiAmount);
                    break;
                }
                if (lastDiffAmount.compareTo(BigDecimal.ZERO) == 0 && extraInterest.compareTo(BigDecimal.ZERO) == 0) {
                    lastDiffAmount = amountLeft;
                    addDiff = BigDecimal.ONE;
                    if (lastDiffAmount.compareTo(BigDecimal.ZERO) == -1) {
                        addDiff = addDiff.negate();
                    }
                } else if (lastDiffAmount.compareTo(BigDecimal.ZERO) == 1 && amountLeft.compareTo(BigDecimal.ZERO) == -1) {
                    lastDiffAmount = amountLeft;
                    addDiff = BigDecimal.valueOf(.01).negate();

                } else if (lastDiffAmount.compareTo(BigDecimal.ZERO) == -1 && amountLeft.compareTo(BigDecimal.ZERO) == 1) {
                    lastDiffAmount = amountLeft;
                    addDiff = BigDecimal.valueOf(.01);
                }

                if (extraInterest.compareTo(BigDecimal.ZERO) == 1) {
                    firstEmiAmount = firstEmiAmount.add(extraInterest).setScale(MoneyHelper.getScaleForInternalCalculations(),
                            MoneyHelper.getRoundingModeForInternalCalculations());
                    extraInterest = BigDecimal.ZERO;
                } else {
                    firstEmiAmount = firstEmiAmount.add(addDiff).setScale(MoneyHelper.getScaleForInternalCalculations(),
                            MoneyHelper.getRoundingModeForInternalCalculations());
                }
                loanApplicationTerms.setFirstEmiAmount(firstEmiAmount);
            }
            loanApplicationTerms.setAdjustInterestForRounding(adjustInterestForRounding);
        }
        return firstEmiAmount;

    }

    private final class LoanTermVariationParams {

        private final boolean skipPeriod;
        private final boolean recalculateAmounts;
        private final LocalDate scheduledDueDate;
        private final ArrayList<LoanTermVariationsData> variationsData;

        public LoanTermVariationParams(final boolean skipPeriod, final boolean recalculateAmounts, final LocalDate scheduledDueDate,
                final ArrayList<LoanTermVariationsData> variationsData) {
            this.skipPeriod = skipPeriod;
            this.recalculateAmounts = recalculateAmounts;
            this.scheduledDueDate = scheduledDueDate;
            this.variationsData = variationsData;
        }

        public boolean isSkipPeriod() {
            return this.skipPeriod;
        }

        public boolean isRecalculateAmounts() {
            return this.recalculateAmounts;
        }

        public LocalDate getScheduledDueDate() {
            return this.scheduledDueDate;
        }

        public ArrayList<LoanTermVariationsData> getVariationsDataList() {
            return this.variationsData;
        }

    }

    private final class ScheduleCurrentPeriodParams {

        Money earlyPaidAmount;
        LoanScheduleModelPeriod lastInstallment;
        boolean skipCurrentLoop;
        Money interestForThisPeriod;
        Money principalForThisPeriod;
        Money feeChargesForInstallment;
        Money penaltyChargesForInstallment;
        // for adjusting outstandingBalances
        Money reducedBalance;
        boolean isEmiAmountChanged;
        double interestCalculationGraceOnRepaymentPeriodFraction;
        Money prepaymentAmount;

        public ScheduleCurrentPeriodParams(final MonetaryCurrency currency, double interestCalculationGraceOnRepaymentPeriodFraction) {
            this.earlyPaidAmount = Money.zero(currency);
            this.lastInstallment = null;
            this.skipCurrentLoop = false;
            this.interestForThisPeriod = Money.zero(currency);
            this.principalForThisPeriod = Money.zero(currency);
            this.reducedBalance = Money.zero(currency);
            this.feeChargesForInstallment = Money.zero(currency);
            this.penaltyChargesForInstallment = Money.zero(currency);
            this.isEmiAmountChanged = false;
            this.interestCalculationGraceOnRepaymentPeriodFraction = interestCalculationGraceOnRepaymentPeriodFraction;
            this.prepaymentAmount = Money.zero(currency);
        }

        public Money getEarlyPaidAmount() {
            return this.earlyPaidAmount;
        }

        public void plusEarlyPaidAmount(Money earlyPaidAmount) {
            this.earlyPaidAmount = this.earlyPaidAmount.plus(earlyPaidAmount);
        }

        public void minusEarlyPaidAmount(Money earlyPaidAmount) {
            this.earlyPaidAmount = this.earlyPaidAmount.minus(earlyPaidAmount);
        }

        public LoanScheduleModelPeriod getLastInstallment() {
            return this.lastInstallment;
        }

        public void setLastInstallment(LoanScheduleModelPeriod lastInstallment) {
            this.lastInstallment = lastInstallment;
        }

        public boolean isSkipCurrentLoop() {
            return this.skipCurrentLoop;
        }

        public void setSkipCurrentLoop(boolean skipCurrentLoop) {
            this.skipCurrentLoop = skipCurrentLoop;
        }

        public Money getInterestForThisPeriod() {
            return this.interestForThisPeriod;
        }

        public void setInterestForThisPeriod(Money interestForThisPeriod) {
            this.interestForThisPeriod = interestForThisPeriod;
        }

        public void minusInterestForThisPeriod(Money interestForThisPeriod) {
            this.interestForThisPeriod = this.interestForThisPeriod.minus(interestForThisPeriod);
        }
        
        public void plusInterestForThisPeriod(Money interestForThisPeriod) {
            this.interestForThisPeriod = this.interestForThisPeriod.plus(interestForThisPeriod);
        }

        public Money getPrincipalForThisPeriod() {
            return this.principalForThisPeriod;
        }

        public void setPrincipalForThisPeriod(Money principalForThisPeriod) {
            this.principalForThisPeriod = principalForThisPeriod;
        }

        public void plusPrincipalForThisPeriod(Money principalForThisPeriod) {
            this.principalForThisPeriod = this.principalForThisPeriod.plus(principalForThisPeriod);
        }

        public void minusPrincipalForThisPeriod(Money principalForThisPeriod) {
            this.principalForThisPeriod = this.principalForThisPeriod.minus(principalForThisPeriod);
        }

        public Money getReducedBalance() {
            return this.reducedBalance;
        }

        public void setReducedBalance(Money reducedBalance) {
            this.reducedBalance = reducedBalance;
        }

        public Money getFeeChargesForInstallment() {
            return this.feeChargesForInstallment;
        }

        public void setFeeChargesForInstallment(Money feeChargesForInstallment) {
            this.feeChargesForInstallment = feeChargesForInstallment;
        }

        public void minusFeeChargesForInstallment(Money feeChargesForInstallment) {
            this.feeChargesForInstallment = this.feeChargesForInstallment.minus(feeChargesForInstallment);
        }

        public Money getPenaltyChargesForInstallment() {
            return this.penaltyChargesForInstallment;
        }

        public void setPenaltyChargesForInstallment(Money penaltyChargesForInstallment) {
            this.penaltyChargesForInstallment = penaltyChargesForInstallment;
        }

        public void minusPenaltyChargesForInstallment(Money penaltyChargesForInstallment) {
            this.penaltyChargesForInstallment = this.penaltyChargesForInstallment.minus(penaltyChargesForInstallment);
        }

        public Money fetchTotalAmountForPeriod() {
            return this.principalForThisPeriod.plus(interestForThisPeriod).plus(feeChargesForInstallment)
                    .plus(penaltyChargesForInstallment);
        }

        public boolean isEmiAmountChanged() {
            return this.isEmiAmountChanged;
        }

        public void setEmiAmountChanged(boolean isEmiAmountChanged) {
            this.isEmiAmountChanged = isEmiAmountChanged;
        }

        public double getInterestCalculationGraceOnRepaymentPeriodFraction() {
            return this.interestCalculationGraceOnRepaymentPeriodFraction;
        }
        
        public Money getPrepaymentAmount() {
            return this.prepaymentAmount;
        }

        public void plusPrepaymentAmount(Money prepaymentAmount) {
            this.prepaymentAmount = this.prepaymentAmount.plus(prepaymentAmount);
        }

        /*public void minusPrepaymentAmount(Money prepaymentAmount) {
            this.prepaymentAmount = this.prepaymentAmount.minus(earlyPaidAmount);
        }*/


    }
    
    /**
     * Method will adjust first and last interest amounts for rounding EMI
     * @param currentPeriodParams TODO
     */
    private void adjustInterestForRoundingEMIAmount(final LoanApplicationTerms loanApplicationTerms, final EmiDetails emiDetails,
            LoanScheduleParams scheduleParams, LoanScheduleModelPeriod installment, final ScheduleCurrentPeriodParams currentPeriodParams) {
        if ((loanApplicationTerms.isAdjustFirstEMIAmount()
        		|| loanApplicationTerms.isAdjustLastInstallmentInterestForRounding())) {
            final MonetaryCurrency currency = scheduleParams.getCurrency();
            if (scheduleParams.getOutstandingBalance().isZero()) {
                if (loanApplicationTerms.isAdjustInterestForRounding() && currentPeriodParams.getPrincipalForThisPeriod().isGreaterThanZero()) {
                    Money amountForAdjust = Money.zero(currency);
                    if (loanApplicationTerms.getAdjustedInstallmentInMultiplesOf() != null) {
                        BigDecimal roundedEMI = loanApplicationTerms.roundAdjustedEmiAmount(emiDetails.getLastEmiAmount());
                        amountForAdjust = Money.of(currency, roundedEMI.subtract(emiDetails.getLastEmiAmount()));
                    } else {
                        amountForAdjust = Money.of(currency, emiDetails.getEmiAmount().subtract(emiDetails.getLastEmiAmount()));
                    }
                    
                    if (!amountForAdjust.plus(installment.interestDue()).isLessThanZero()) {
                        installment.addInterestAmount(amountForAdjust);
                        if (loanApplicationTerms.getInterestMethod().isFlat()) {
                            loanApplicationTerms.updateTotalInterestDue(loanApplicationTerms.getTotalInterestDue().plus(amountForAdjust));
                        }
                        currentPeriodParams.plusInterestForThisPeriod(amountForAdjust);
                        scheduleParams.addTotalRepaymentExpected(amountForAdjust);
                    }
                }
            } else if (scheduleParams.getPeriodNumber() == 1 && loanApplicationTerms.getFirstEmiAmount() != null) {
                BigDecimal roundedFirstEmiAmount = loanApplicationTerms.roundAdjustedEmiAmount(loanApplicationTerms.getFirstEmiAmount());
                if (roundedFirstEmiAmount.compareTo(loanApplicationTerms.getFirstEmiAmount()) != 0) {
                    installment.addInterestAmount(Money.of(currency,
                            roundedFirstEmiAmount.subtract(loanApplicationTerms.getFirstEmiAmount())));
                }
            }
        }
    }   

    private void processCapitalizedTransactions(final Map<LocalDate, Money> principalPortionMap,
            final Money amount,LocalDate applicableDate) {  
    	updateMapWithAmount(principalPortionMap, amount, applicableDate);                
    }
    
    private class EmiDetails {
        private BigDecimal emiAmount;
        private BigDecimal lastEmiAmount;

        public BigDecimal getEmiAmount() {
            return this.emiAmount;
        }

        public void setEmiAmount(BigDecimal emiAmount) {
            if (this.emiAmount == null) {
                this.emiAmount = emiAmount;
            }
        }

        public BigDecimal getLastEmiAmount() {
            return this.lastEmiAmount;
        }

        public void setLastEmiAmount(BigDecimal lastEmiAmount) {
            this.lastEmiAmount = lastEmiAmount;
        }
    }
}