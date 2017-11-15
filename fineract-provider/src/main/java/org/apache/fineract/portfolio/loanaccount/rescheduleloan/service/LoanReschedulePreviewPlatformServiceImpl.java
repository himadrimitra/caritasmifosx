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
package org.apache.fineract.portfolio.loanaccount.rescheduleloan.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.charge.domain.ChargePaymentMode;
import org.apache.fineract.portfolio.charge.domain.ChargeRepositoryWrapper;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanLifecycleStateMachine;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleTransactionProcessorFactory;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRescheduleRequestToTermVariationMapping;
import org.apache.fineract.portfolio.loanaccount.domain.LoanSummaryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariations;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.loanschedule.data.LoanScheduleDTO;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleGeneratorFactory;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleModel;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequest;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.domain.LoanRescheduleRequestRepository;
import org.apache.fineract.portfolio.loanaccount.rescheduleloan.exception.LoanRescheduleRequestNotFoundException;
import org.apache.fineract.portfolio.loanaccount.service.LoanOverdueChargeService;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanReschedulePreviewPlatformServiceImpl implements LoanReschedulePreviewPlatformService {

    private final LoanRescheduleRequestRepository loanRescheduleRequestRepository;
    private final LoanUtilService loanUtilService;
    private final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory;
    private final LoanScheduleGeneratorFactory loanScheduleFactory;
    private final LoanSummaryWrapper loanSummaryWrapper;
    private final DefaultScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
    private final GlimLoanRescheduleService glimLoanRescheduleService;
    private final LoanOverdueChargeService loanOverdueChargeService;
    private final ChargeRepositoryWrapper chargeRepository;

    @Autowired
    public LoanReschedulePreviewPlatformServiceImpl(final LoanRescheduleRequestRepository loanRescheduleRequestRepository,
            final LoanUtilService loanUtilService,
            final LoanRepaymentScheduleTransactionProcessorFactory loanRepaymentScheduleTransactionProcessorFactory,
            final LoanScheduleGeneratorFactory loanScheduleFactory, final LoanSummaryWrapper loanSummaryWrapper,
            final GlimLoanRescheduleService glimLoanRescheduleService,final LoanOverdueChargeService loanOverdueChargeService,
            final ChargeRepositoryWrapper chargeRepository) {
        this.loanRescheduleRequestRepository = loanRescheduleRequestRepository;
        this.loanUtilService = loanUtilService;
        this.loanRepaymentScheduleTransactionProcessorFactory = loanRepaymentScheduleTransactionProcessorFactory;
        this.loanScheduleFactory = loanScheduleFactory;
        this.loanSummaryWrapper = loanSummaryWrapper;
        this.glimLoanRescheduleService = glimLoanRescheduleService;
        this.loanOverdueChargeService = loanOverdueChargeService;
        this.chargeRepository = chargeRepository;
    }

    @Override
    public LoanScheduleModel previewLoanReschedule(final Long requestId) {
        final LoanRescheduleRequest loanRescheduleRequest = this.loanRescheduleRequestRepository.findOne(requestId);

        if (loanRescheduleRequest == null) { throw new LoanRescheduleRequestNotFoundException(requestId); }

        final Loan loan = loanRescheduleRequest.getLoan();

        final ScheduleGeneratorDTO scheduleGeneratorDTO = this.loanUtilService.buildScheduleGeneratorDTO(loan,
                loanRescheduleRequest.getRescheduleFromDate());
        LocalDate rescheduleFromDate = null;
        LocalDate previouslyAdjustedDate = null;
        final List<LoanTermVariationsData> removeLoanTermVariationsData = new ArrayList<>();
        final LoanApplicationTerms loanApplicationTerms = loan.constructLoanApplicationTerms(scheduleGeneratorDTO);
        final LoanTermVariations dueDateVariationInCurrentRequest = loanRescheduleRequest.getDueDateTermVariationIfExists();
        if (dueDateVariationInCurrentRequest != null) {
            for (final LoanTermVariationsData loanTermVariation : loanApplicationTerms.getLoanTermVariations().getDueDateVariation()) {
                if (loanTermVariation.getDateValue().equals(dueDateVariationInCurrentRequest.fetchTermApplicaDate())) {
                    rescheduleFromDate = loanTermVariation.getTermApplicableFrom();
                    previouslyAdjustedDate = loanTermVariation.getDateValue();
                    removeLoanTermVariationsData.add(loanTermVariation);
                }
            }
            if (!(loanApplicationTerms.getNthDay() == null || loanApplicationTerms.getWeekDayType() == null
                    || loanApplicationTerms.getWeekDayType() == DayOfWeekType.INVALID)
                    && loanApplicationTerms.getRepaymentPeriodFrequencyType().isMonthly()) {
                final Calendar loanCalendar = loanApplicationTerms.getLoanCalendar();
                final CalendarHistory calendarHistory = new CalendarHistory(loanCalendar, loanCalendar.getStartDate());
                final Date endDate = dueDateVariationInCurrentRequest.fetchDateValue().minusDays(1).toDate();
                calendarHistory.updateEndDate(endDate);
                loanApplicationTerms.getCalendarHistoryDataWrapper().getCalendarHistoryList().add(calendarHistory);
                final boolean isMeetingAttached = this.loanUtilService.isMeetingAttached(loan);
                if (loan.isGLIMLoan()) {
                    loanCalendar.updateStartDateAndNthDayAndDayOfWeek(dueDateVariationInCurrentRequest.fetchDateValue(), isMeetingAttached);
                } else {
                    loanCalendar.updateStartDateAndNthDayAndDayOfWeekType(dueDateVariationInCurrentRequest.fetchDateValue(),
                            isMeetingAttached);
                }

            }
        }
        loanApplicationTerms.getLoanTermVariations().getDueDateVariation().removeAll(removeLoanTermVariationsData);
        if (rescheduleFromDate == null) {
            rescheduleFromDate = loanRescheduleRequest.getRescheduleFromDate();
        }
        final List<LoanTermVariationsData> loanTermVariationsData = new ArrayList<>();
        LocalDate adjustedApplicableDate = null;
        final Set<LoanRescheduleRequestToTermVariationMapping> loanRescheduleRequestToTermVariationMappings = loanRescheduleRequest
                .getLoanRescheduleRequestToTermVariationMappings();
        if (!loanRescheduleRequestToTermVariationMappings.isEmpty()) {
            for (final LoanRescheduleRequestToTermVariationMapping loanRescheduleRequestToTermVariationMapping : loanRescheduleRequestToTermVariationMappings) {
                if (loanRescheduleRequestToTermVariationMapping.getLoanTermVariations().getTermType().isDueDateVariation()
                        && rescheduleFromDate != null) {
                    adjustedApplicableDate = loanRescheduleRequestToTermVariationMapping.getLoanTermVariations().fetchDateValue();
                    loanRescheduleRequestToTermVariationMapping.getLoanTermVariations().setTermApplicableFrom(rescheduleFromDate.toDate());
                }
                loanTermVariationsData.add(loanRescheduleRequestToTermVariationMapping.getLoanTermVariations().toData());
            }
        }

        /**
         * Validate adjusted applicable date is not a holiday or non working day
         */
        if (adjustedApplicableDate != null) {
            final HolidayDetailDTO holidayDetailDTO = scheduleGeneratorDTO.getHolidayDetailDTO();
            loan.validateRepaymentDateIsOnHoliday(adjustedApplicableDate, holidayDetailDTO.isAllowTransactionsOnHoliday(),
                    holidayDetailDTO.getHolidays());
            loan.validateRepaymentDateIsOnNonWorkingDay(adjustedApplicableDate, holidayDetailDTO.getWorkingDays(),
                    holidayDetailDTO.isAllowTransactionsOnNonWorkingDay());
        }

        for (final LoanTermVariationsData loanTermVariation : loanApplicationTerms.getLoanTermVariations().getDueDateVariation()) {
            if (rescheduleFromDate.isBefore(loanTermVariation.getTermApplicableFrom())) {
                final LocalDate applicableDate = this.scheduledDateGenerator.generateNextRepaymentDate(rescheduleFromDate,
                        loanApplicationTerms, false);
                if (loanTermVariation.getTermApplicableFrom().equals(applicableDate)) {
                    final LocalDate adjustedDate = this.scheduledDateGenerator.generateNextRepaymentDate(adjustedApplicableDate,
                            loanApplicationTerms, false);
                    loanTermVariation.setApplicableFromDate(adjustedDate);
                    loanTermVariationsData.add(loanTermVariation);
                }
            }
        }

        if (previouslyAdjustedDate != null) {
            rescheduleFromDate = rescheduleFromDate.isAfter(previouslyAdjustedDate) ? previouslyAdjustedDate : rescheduleFromDate;
        }

        loanApplicationTerms.getLoanTermVariations().updateLoanTermVariationsData(loanTermVariationsData);
        final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
        final MathContext mathContext = new MathContext(8, roundingMode);
        final LoanRepaymentScheduleTransactionProcessor loanRepaymentScheduleTransactionProcessor = this.loanRepaymentScheduleTransactionProcessorFactory
                .determineProcessor(loan.transactionProcessingStrategy());
        final LoanScheduleGenerator loanScheduleGenerator = this.loanScheduleFactory.create(loanApplicationTerms.getInterestMethod());
        final LoanLifecycleStateMachine loanLifecycleStateMachine = null;
        loan.setHelpers(loanLifecycleStateMachine, this.loanSummaryWrapper, this.loanRepaymentScheduleTransactionProcessorFactory);
        if (loan.isGLIMLoan()) { return this.glimLoanRescheduleService.getGlimLoanScheduleModels(loan, scheduleGeneratorDTO,
                loanApplicationTerms, rescheduleFromDate); }
        Set<LoanCharge> charges = loan.chargesCopy();
        if (!loan.getLoanRecurringCharges().isEmpty() && rescheduleFromDate.isBefore(DateUtils.getLocalDateOfTenant())) {
            charges = deleteOverdueChargesAfter(rescheduleFromDate, charges);
            LoanOverdueChargeData overdueChargeData = this.loanOverdueChargeService.calculateOverdueChargesAsOnDate(loan,
                    rescheduleFromDate, rescheduleFromDate);
            BigDecimal chargeAmount = overdueChargeData.getPenaltyToBePostedAsOnDate();
            if (MathUtility.isGreaterThanZero(chargeAmount)) {
                final Charge charge = this.chargeRepository.findOneWithNotFoundDetection(loan.getLoanRecurringCharges().get(0)
                        .getChargeId());
                final BigDecimal percentage = null;
                final boolean penalty = true;
                LoanCharge loanCharge = new LoanCharge(loan, charge, chargeAmount, percentage, ChargeTimeType.OVERDUE_INSTALLMENT,
                        ChargeCalculationType.FLAT, rescheduleFromDate, ChargePaymentMode.REGULAR, penalty, rescheduleFromDate);
                charges.add(loanCharge);
            }
        }
        
        final LoanScheduleDTO loanSchedule = loanScheduleGenerator.rescheduleNextInstallments(mathContext, loanApplicationTerms,
                loan, loanApplicationTerms.getHolidayDetailDTO(),loan.getLoanTransactions(),
                loanRepaymentScheduleTransactionProcessor, rescheduleFromDate, charges);
        final LoanScheduleModel loanScheduleModel = loanSchedule.getLoanScheduleModel();
        final LoanScheduleModel loanScheduleModels = LoanScheduleModel.withLoanScheduleModelPeriods(loanScheduleModel.getPeriods(),
                loanScheduleModel);
        return loanScheduleModels;
    }
    
    public Set<LoanCharge> deleteOverdueChargesAfter(final LocalDate date, final Set<LoanCharge> charges) {
        final Set<LoanCharge> activeCharges = new HashSet<>(charges.size());
        for (LoanCharge loanCharge : charges) {
            if (loanCharge.isActive() && loanCharge.isOverdueInstallmentCharge()) {
                if (loanCharge.getDueLocalDate().isAfter(date) && loanCharge.isChargePending()) {
                    loanCharge.setActive(false);
                }else{
                    activeCharges.add(loanCharge);
                }
            }
        }
        return activeCharges;
    }
   
}