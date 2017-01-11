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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.accountnumberformat.domain.EntityAccountType;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.domain.HolidayRepository;
import org.apache.fineract.organisation.holiday.domain.HolidayStatusType;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrency;
import org.apache.fineract.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.organisation.workingdays.domain.WorkingDaysRepositoryWrapper;
import org.apache.fineract.organisation.workingdays.service.WorkingDayExemptionsReadPlatformService;
import org.apache.fineract.portfolio.calendar.data.CalendarHistoryDataWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarEntityType;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstance;
import org.apache.fineract.portfolio.calendar.domain.CalendarInstanceRepository;
import org.apache.fineract.portfolio.calendar.service.CalendarReadPlatformService;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.floatingrates.data.FloatingRateDTO;
import org.apache.fineract.portfolio.floatingrates.data.FloatingRatePeriodData;
import org.apache.fineract.portfolio.floatingrates.exception.FloatingRateNotFoundException;
import org.apache.fineract.portfolio.floatingrates.service.FloatingRatesReadPlatformService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.apache.fineract.portfolio.loanaccount.data.ScheduleGeneratorDTO;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultScheduledDateGenerator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanScheduleGeneratorFactory;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.ScheduledDateGenerator;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProductRelatedDetail;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Component
public class LoanUtilService {

    private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
    private final CalendarInstanceRepository calendarInstanceRepository;
    private final ConfigurationDomainService configurationDomainService;
    private final HolidayRepository holidayRepository;
    private final WorkingDaysRepositoryWrapper workingDaysRepository;
    private final LoanScheduleGeneratorFactory loanScheduleFactory;
    private final FloatingRatesReadPlatformService floatingRatesReadPlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final CalendarReadPlatformService calendarReadPlatformService;
    private final WorkingDayExemptionsReadPlatformService workingDayExcumptionsReadPlatformService;
    

    @Autowired
    public LoanUtilService(final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
            final CalendarInstanceRepository calendarInstanceRepository, final ConfigurationDomainService configurationDomainService,
            final HolidayRepository holidayRepository, final WorkingDaysRepositoryWrapper workingDaysRepository,
            final LoanScheduleGeneratorFactory loanScheduleFactory, final FloatingRatesReadPlatformService floatingRatesReadPlatformService,
            final FromJsonHelper fromApiJsonHelper, final CalendarReadPlatformService calendarReadPlatformService,
            final WorkingDayExemptionsReadPlatformService workingDayExcumptionsReadPlatformService) {
        this.applicationCurrencyRepository = applicationCurrencyRepository;
        this.calendarInstanceRepository = calendarInstanceRepository;
        this.configurationDomainService = configurationDomainService;
        this.holidayRepository = holidayRepository;
        this.workingDaysRepository = workingDaysRepository;
        this.loanScheduleFactory = loanScheduleFactory;
        this.floatingRatesReadPlatformService = floatingRatesReadPlatformService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.calendarReadPlatformService = calendarReadPlatformService;
        this.workingDayExcumptionsReadPlatformService = workingDayExcumptionsReadPlatformService;
    }

    public ScheduleGeneratorDTO buildScheduleGeneratorDTO(final Loan loan, final LocalDate recalculateFrom) {
        final HolidayDetailDTO holidayDetailDTO = null;
        return buildScheduleGeneratorDTO(loan, recalculateFrom, holidayDetailDTO);
    }
    
    public ScheduleGeneratorDTO buildScheduleGeneratorDTO(final Loan loan, final LocalDate recalculateFrom,
            final boolean considerFutureDisbursmentsInSchedule, final boolean considerAllDisbursmentsInSchedule) {
        final HolidayDetailDTO holidayDetailDTO = null;
        return buildScheduleGeneratorDTO(loan, recalculateFrom, holidayDetailDTO, considerFutureDisbursmentsInSchedule,
                considerAllDisbursmentsInSchedule);
    }

    public ScheduleGeneratorDTO buildScheduleGeneratorDTO(final Loan loan, final LocalDate recalculateFrom,
            final HolidayDetailDTO holidayDetailDTO) {
        boolean considerFutureDisbursmentsInSchedule = true;
        boolean considerAllDisbursmentsInSchedule =true;
        
        if(loan.isOpen()){
            considerFutureDisbursmentsInSchedule = false;
            considerAllDisbursmentsInSchedule = false;
        }
        
        return buildScheduleGeneratorDTO(loan, recalculateFrom, holidayDetailDTO, considerFutureDisbursmentsInSchedule,
                considerAllDisbursmentsInSchedule);
    }

    private ScheduleGeneratorDTO buildScheduleGeneratorDTO(final Loan loan, final LocalDate recalculateFrom,
            final HolidayDetailDTO holidayDetailDTO, final boolean considerFutureDisbursmentsInSchedule,
            final boolean considerAllDisbursmentsInSchedule) {
        HolidayDetailDTO holidayDetails = holidayDetailDTO;
        if (holidayDetailDTO == null) {
            holidayDetails = constructHolidayDTO(loan);
        }
        final MonetaryCurrency currency = loan.getCurrency();
        ApplicationCurrency applicationCurrency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currency);
        final CalendarInstance calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(loan.getId(),
                CalendarEntityType.LOANS.getValue());
        Calendar calendar = null;
        CalendarHistoryDataWrapper calendarHistoryDataWrapper = null;
        if (calendarInstance != null) {
            calendar = calendarInstance.getCalendar();
            Set<CalendarHistory> calendarHistory = calendar.getActiveCalendarHistory();
            calendarHistoryDataWrapper = new CalendarHistoryDataWrapper(calendarHistory);
        }
        LocalDate calculatedRepaymentsStartingFromDate = this.getCalculatedRepaymentsStartingFromDate(loan.getDisbursementDate(), loan,
                calendarInstance, calendarHistoryDataWrapper);
        CalendarInstance restCalendarInstance = null;
        CalendarInstance compoundingCalendarInstance = null;
        Long overdurPenaltyWaitPeriod = null;
        if (loan.repaymentScheduleDetail().isInterestRecalculationEnabled()) {
            restCalendarInstance = calendarInstanceRepository.findCalendarInstaneByEntityId(loan.loanInterestRecalculationDetailId(),
                    CalendarEntityType.LOAN_RECALCULATION_REST_DETAIL.getValue());
            compoundingCalendarInstance = calendarInstanceRepository.findCalendarInstaneByEntityId(
                    loan.loanInterestRecalculationDetailId(), CalendarEntityType.LOAN_RECALCULATION_COMPOUNDING_DETAIL.getValue());
            overdurPenaltyWaitPeriod = this.configurationDomainService.retrievePenaltyWaitPeriod();
        }
        final Boolean isInterestChargedFromDateAsDisbursementDateEnabled = this.configurationDomainService.isInterestChargedFromDateSameAsDisbursementDate();
        FloatingRateDTO floatingRateDTO = constructFloatingRateDTO(loan);
        Boolean isSkipRepaymentOnFirstMonth = false;
        Integer numberOfDays = 0;
        boolean isSkipRepaymentOnFirstMonthEnabled = configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
        if(isSkipRepaymentOnFirstMonthEnabled){
            isSkipRepaymentOnFirstMonth = isLoanRepaymentsSyncWithMeeting(loan.group(), calendar);
            if(isSkipRepaymentOnFirstMonth) { numberOfDays = configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue(); } 
        }
        final Boolean isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled = this.configurationDomainService.isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled();
        
        ScheduleGeneratorDTO scheduleGeneratorDTO = new ScheduleGeneratorDTO(loanScheduleFactory, applicationCurrency,
                calculatedRepaymentsStartingFromDate, holidayDetails, restCalendarInstance, compoundingCalendarInstance, recalculateFrom,
                overdurPenaltyWaitPeriod, floatingRateDTO, calendar, calendarHistoryDataWrapper, isInterestChargedFromDateAsDisbursementDateEnabled,
                numberOfDays, isSkipRepaymentOnFirstMonth, isChangeEmiIfRepaymentDateSameAsDisbursementDateEnabled, considerFutureDisbursmentsInSchedule, considerAllDisbursmentsInSchedule);

        return scheduleGeneratorDTO;
    }
    
    public Boolean isLoanRepaymentsSyncWithMeeting(final Group group, final Calendar calendar) {
        Boolean isSkipRepaymentOnFirstMonth = false;
        Long entityId = null;
        Long entityTypeId = null;

        if (group != null) {
            if (group.getParent() != null) {
                entityId = group.getParent().getId();
                entityTypeId = CalendarEntityType.CENTERS.getValue().longValue();
            } else {
                entityId = group.getId();
                entityTypeId = CalendarEntityType.GROUPS.getValue().longValue();
            }
        }

        if (entityId == null || calendar == null) { return isSkipRepaymentOnFirstMonth; }
        isSkipRepaymentOnFirstMonth = this.calendarReadPlatformService.isCalendarAssociatedWithEntity(entityId, calendar.getId(),
                entityTypeId);
        return isSkipRepaymentOnFirstMonth;
    }

    public LocalDate getCalculatedRepaymentsStartingFromDate(final Loan loan) {
        final CalendarInstance calendarInstance = this.calendarInstanceRepository.findCalendarInstaneByEntityId(loan.getId(),
                CalendarEntityType.LOANS.getValue());
        final CalendarHistoryDataWrapper calendarHistoryDataWrapper = null;
        return this.getCalculatedRepaymentsStartingFromDate(loan.getDisbursementDate(), loan, calendarInstance, calendarHistoryDataWrapper);
    }

    public HolidayDetailDTO constructHolidayDTO(final Loan loan) {
        final List<Holiday> holidays = this.holidayRepository.findByOfficeIdAndGreaterThanDate(loan.getOfficeId(), loan
                .getDisbursementDate().toDate(), HolidayStatusType.ACTIVE.getValue());
        HolidayDetailDTO holidayDetailDTO = constructHolidayDTO(holidays);
        return holidayDetailDTO;
    }

    public HolidayDetailDTO constructHolidayDTO(final List<Holiday> holidays) {
        final boolean isHolidayEnabled = this.configurationDomainService.isRescheduleRepaymentsOnHolidaysEnabled();
        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final boolean allowTransactionsOnHoliday = this.configurationDomainService.allowTransactionsOnHolidayEnabled();
        final boolean allowTransactionsOnNonWorkingDay = this.configurationDomainService.allowTransactionsOnNonWorkingDayEnabled();
        final List<WorkingDayExemptionsData> workingDayExemptions = this.workingDayExcumptionsReadPlatformService.getWorkingDayExemptionsForEntityType(EntityAccountType.LOAN.getValue());
        
        HolidayDetailDTO holidayDetailDTO = new HolidayDetailDTO(isHolidayEnabled, holidays, workingDays, allowTransactionsOnHoliday,
                allowTransactionsOnNonWorkingDay, workingDayExemptions);
        return holidayDetailDTO;
    }

    private FloatingRateDTO constructFloatingRateDTO(final Loan loan) {
        FloatingRateDTO floatingRateDTO = null;
        if (loan.loanProduct().isLinkedToFloatingInterestRate()) {
            boolean isFloatingInterestRate = loan.getIsFloatingInterestRate();
            BigDecimal interestRateDiff = loan.getInterestRateDifferential();
            List<FloatingRatePeriodData> baseLendingRatePeriods = null;
            try {
                baseLendingRatePeriods = this.floatingRatesReadPlatformService.retrieveBaseLendingRate().getRatePeriods();
            } catch (final FloatingRateNotFoundException ex) {
                // Do not do anything
            }

            floatingRateDTO = new FloatingRateDTO(isFloatingInterestRate, loan.getDisbursementDate(), interestRateDiff,
                    baseLendingRatePeriods);
        }
        return floatingRateDTO;
    }

    public LocalDate getCalculatedRepaymentsStartingFromDate(final LocalDate actualDisbursementDate, final Loan loan,
            final CalendarInstance calendarInstance, final CalendarHistoryDataWrapper calendarHistoryDataWrapper) {
        final Calendar calendar = calendarInstance == null ? null : calendarInstance.getCalendar();
        return calculateRepaymentStartingFromDate(actualDisbursementDate, loan, calendar, calendarHistoryDataWrapper);
    }
    
    private LocalDate calculateRepaymentStartingFromDate(final LocalDate actualDisbursementDate,
            final Integer repayEvery, Integer minimumDaysBetweenDisbursalAndFirstRepayment,final PeriodFrequencyType repaymentPeriodFrequencyType){
        final LocalDate dateBasedOnMinimumDaysBetweenDisbursalAndFirstRepayment = actualDisbursementDate
                .plusDays(minimumDaysBetweenDisbursalAndFirstRepayment);
        LocalDate dateBasedOnRepaymentFrequency;
        // Derive the first repayment date as greater date among
        // (disbursement date + plus frequency) or
        // (disbursement date + minimum between disbursal and first
        // repayment )
        if (repaymentPeriodFrequencyType.isDaily()) {
            dateBasedOnRepaymentFrequency = actualDisbursementDate.plusDays(repayEvery);
        } else if (repaymentPeriodFrequencyType.isWeekly()) {
            dateBasedOnRepaymentFrequency = actualDisbursementDate.plusWeeks(repayEvery);
        } else if (repaymentPeriodFrequencyType.isMonthly()) {
            dateBasedOnRepaymentFrequency = actualDisbursementDate.plusMonths(repayEvery);
        }/** yearly loan **/
        else {
            dateBasedOnRepaymentFrequency = actualDisbursementDate.plusYears(repayEvery);
        }
        return dateBasedOnRepaymentFrequency.isAfter(dateBasedOnMinimumDaysBetweenDisbursalAndFirstRepayment) ? dateBasedOnRepaymentFrequency
                : dateBasedOnMinimumDaysBetweenDisbursalAndFirstRepayment;
    }

    public LocalDate getCalculatedRepaymentsStartingFromDate(final Integer repaymentEvery, final LocalDate expectedDisbursementDate,
            final PeriodFrequencyType repaymentPeriodFrequencyType, final Integer minimumDaysBetweenDisbursalAndFirstRepayment,
            final Calendar calendar, final Group group) {
        
        LocalDate calculatedRepaymentsStartingFromDate = null;
        if(calendar != null){
            final LocalDate refernceDateForCalculatingFirstRepaymentDate = null;
            Set<CalendarHistory> calendarHistory = calendar.getActiveCalendarHistory();
            final CalendarHistoryDataWrapper calendarHistoryDataWrapper = new CalendarHistoryDataWrapper(calendarHistory);
            calculatedRepaymentsStartingFromDate =  calculateRepaymentStartingFromDate(expectedDisbursementDate, calendar, calendarHistoryDataWrapper,
                    refernceDateForCalculatingFirstRepaymentDate, repaymentEvery, minimumDaysBetweenDisbursalAndFirstRepayment,
                    repaymentPeriodFrequencyType, group);
        } else {

            calculatedRepaymentsStartingFromDate = calculateRepaymentStartingFromDate(expectedDisbursementDate, repaymentEvery,
                    minimumDaysBetweenDisbursalAndFirstRepayment, repaymentPeriodFrequencyType);
        }
        
            return calculatedRepaymentsStartingFromDate;
    }

    private LocalDate calculateRepaymentStartingFromDate(final LocalDate actualDisbursementDate, final Calendar calendar,
            final CalendarHistoryDataWrapper calendarHistoryDataWrapper, LocalDate calculatedRepaymentsStartingFromDate,
            final Integer repayEvery, Integer minimumDaysBetweenDisbursalAndFirstRepayment,
            final PeriodFrequencyType repaymentPeriodFrequencyType, Group group) {
        
        if (calculatedRepaymentsStartingFromDate == null) {

            if (calendar != null) {// sync repayments

                if (!calendar.getCalendarHistory().isEmpty() && calendarHistoryDataWrapper != null) {
                    // generate the first repayment date based on calendar
                    // history
                    List<CalendarHistory> historyList = calendarHistoryDataWrapper.getCalendarHistoryList();
                    if (historyList != null && historyList.size() > 0) {
                        calculatedRepaymentsStartingFromDate = generateCalculatedRepaymentStartDate(calendarHistoryDataWrapper,
                                actualDisbursementDate, group, minimumDaysBetweenDisbursalAndFirstRepayment, repayEvery,
                                repaymentPeriodFrequencyType);
                    }
                }

                // TODO: AA - user provided first repayment date takes
                // precedence
                // over recalculated meeting date
                if (calculatedRepaymentsStartingFromDate == null) {
                    // FIXME: AA - Possibility of having next meeting date
                    // immediately after disbursement date,
                    // need to have minimum number of days gap between
                    // disbursement
                    // and first repayment date.
                    calculatedRepaymentsStartingFromDate = this.deriveFirstRepaymentDateForLoans(repayEvery, actualDisbursementDate,
                            actualDisbursementDate, repaymentPeriodFrequencyType, minimumDaysBetweenDisbursalAndFirstRepayment, calendar);
                }
            } else {

                calculatedRepaymentsStartingFromDate = calculateRepaymentStartingFromDate(actualDisbursementDate, repayEvery,
                        minimumDaysBetweenDisbursalAndFirstRepayment, repaymentPeriodFrequencyType);
            }
        }
        return calculatedRepaymentsStartingFromDate;
    }

    private LocalDate calculateRepaymentStartingFromDate(final LocalDate actualDisbursementDate, final Loan loan, final Calendar calendar,
            final CalendarHistoryDataWrapper calendarHistoryDataWrapper) {
        LocalDate calculatedRepaymentsStartingFromDate = loan.getExpectedFirstRepaymentOnDate();
        final LoanProductRelatedDetail repaymentScheduleDetails = loan.repaymentScheduleDetail();
        if (repaymentScheduleDetails != null) {
            final Integer repayEvery = repaymentScheduleDetails.getRepayEvery();
            Integer minimumDaysBetweenDisbursalAndFirstRepayment = calculateMinimumDaysBetweenDisbursalAndFirstRepayment(
                    actualDisbursementDate, loan.getLoanProduct(), loan.getLoanRepaymentScheduleDetail().getRepaymentPeriodFrequencyType(),
                    repayEvery);
            final PeriodFrequencyType repaymentPeriodFrequencyType = repaymentScheduleDetails.getRepaymentPeriodFrequencyType();
            Group group = loan.group();
            calculatedRepaymentsStartingFromDate = calculateRepaymentStartingFromDate(actualDisbursementDate, calendar,
                    calendarHistoryDataWrapper, calculatedRepaymentsStartingFromDate, repayEvery,
                    minimumDaysBetweenDisbursalAndFirstRepayment, repaymentPeriodFrequencyType, group);
        }

        return calculatedRepaymentsStartingFromDate;
    }

    private LocalDate generateCalculatedRepaymentStartDate(CalendarHistoryDataWrapper calendarHistoryDataWrapper, LocalDate actualDisbursementDate,
            Group group, final Integer minimumDaysBetweenDisbursalAndFirstRepayment, final Integer repayEvery, final PeriodFrequencyType repaymentPeriodFrequencyType) {
        LocalDate calculatedRepaymentsStartingFromDate;
        final WorkingDays workingDays = this.workingDaysRepository.findOne();
        final String frequency = CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(repaymentPeriodFrequencyType);
        Boolean isSkipRepaymentOnFirstMonth = false;
        Integer numberOfDays = 0;
        boolean isSkipRepaymentOnFirstMonthEnabled = this.configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
        if (isSkipRepaymentOnFirstMonthEnabled) {
            numberOfDays = configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
            isSkipRepaymentOnFirstMonth = isLoanRepaymentsSyncWithMeeting(group, calendarHistoryDataWrapper.getCalendarHistoryList().get(0).getCalendar());
        }
         
        calculatedRepaymentsStartingFromDate = this.deriveFirstRepaymentDateForLoans(actualDisbursementDate,
                minimumDaysBetweenDisbursalAndFirstRepayment, calendarHistoryDataWrapper, workingDays, isSkipRepaymentOnFirstMonth, numberOfDays,
                repayEvery, frequency);
        return calculatedRepaymentsStartingFromDate;
    }
    
    private LocalDate deriveFirstRepaymentDateForLoans(final Integer repaymentEvery, final LocalDate expectedDisbursementDate,
            final LocalDate refernceDateForCalculatingFirstRepaymentDate, final PeriodFrequencyType repaymentPeriodFrequencyType,
            final Integer minimumDaysBetweenDisbursalAndFirstRepayment, final Calendar calendar) {
        boolean isMeetingSkipOnFirstDayOfMonth = configurationDomainService.isSkippingMeetingOnFirstDayOfMonthEnabled();
        int numberOfDays = configurationDomainService.retreivePeroidInNumberOfDaysForSkipMeetingDate().intValue();
        final String frequency = CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(repaymentPeriodFrequencyType);
        final LocalDate derivedFirstRepayment = CalendarUtils.getFirstRepaymentMeetingDate(calendar,
                refernceDateForCalculatingFirstRepaymentDate, repaymentEvery, frequency, isMeetingSkipOnFirstDayOfMonth, numberOfDays);
        final LocalDate minimumFirstRepaymentDate = expectedDisbursementDate.plusDays(minimumDaysBetweenDisbursalAndFirstRepayment);
        return minimumFirstRepaymentDate.isBefore(derivedFirstRepayment) ? derivedFirstRepayment : deriveFirstRepaymentDateForLoans(
                repaymentEvery, expectedDisbursementDate, derivedFirstRepayment, repaymentPeriodFrequencyType,
                minimumDaysBetweenDisbursalAndFirstRepayment, calendar);
    }
    
    private LocalDate deriveFirstRepaymentDateForLoans(final LocalDate actualDisbursementDate,
            final Integer minimumDaysBetweenDisbursalAndFirstRepayment, CalendarHistoryDataWrapper calendarHistoryDataWrapper, final WorkingDays workingDays,
            final boolean isMeetingSkipOnFirstDayOfMonth, int numberOfDays, final Integer repayEvery, final String frequency) {
        boolean useNextHistoryItem = false;
        int matchHistory  = 1;
        LocalDate derivedFirstRepayment = null;
        do {
            CalendarHistory history = calendarHistoryDataWrapper.getCalendarHistory(actualDisbursementDate, matchHistory);
            useNextHistoryItem = false;
            if (history == null) { return null; }
            derivedFirstRepayment = CalendarUtils.getNextRepaymentMeetingDate(history.getRecurrence(), history.getStartDateLocalDate(),
                    actualDisbursementDate, repayEvery, frequency, workingDays, isMeetingSkipOnFirstDayOfMonth, numberOfDays);
            if (history.getEndDateLocalDate().isBefore(derivedFirstRepayment)) {
                useNextHistoryItem = true;
                matchHistory++;
            }
        } while (useNextHistoryItem);

        final LocalDate minimumFirstRepaymentDate = actualDisbursementDate.plusDays(minimumDaysBetweenDisbursalAndFirstRepayment);
        return minimumFirstRepaymentDate.isBefore(derivedFirstRepayment) ? derivedFirstRepayment : deriveFirstRepaymentDateForLoans(
                derivedFirstRepayment, minimumDaysBetweenDisbursalAndFirstRepayment, calendarHistoryDataWrapper, workingDays,
                isMeetingSkipOnFirstDayOfMonth, numberOfDays, repayEvery, frequency);

    }
    
    public Set<LoanDisbursementDetails> fetchDisbursementData(final JsonObject command) {
        final Locale locale = this.fromApiJsonHelper.extractLocaleParameter(command);
        final String dateFormat = this.fromApiJsonHelper.extractDateFormatParameter(command);
        Set<LoanDisbursementDetails> disbursementDatas = new HashSet<>();
        if (command.has(LoanApiConstants.disbursementDataParameterName)) {
            final JsonArray disbursementDataArray = command.getAsJsonArray(LoanApiConstants.disbursementDataParameterName);
            if (disbursementDataArray != null && disbursementDataArray.size() > 0) {
                int i = 0;
                do {
                    final JsonObject jsonObject = disbursementDataArray.get(i).getAsJsonObject();
                    Date expectedDisbursementDate = null;
                    Date actualDisbursementDate = null;
                    BigDecimal principal = null;

                    if (jsonObject.has(LoanApiConstants.disbursementDateParameterName)) {
                        LocalDate date = this.fromApiJsonHelper.extractLocalDateNamed(LoanApiConstants.disbursementDateParameterName,
                                jsonObject, dateFormat, locale);
                        if (date != null) {
                            expectedDisbursementDate = date.toDate();
                        }
                    }
                    if (jsonObject.has(LoanApiConstants.disbursementPrincipalParameterName)
                            && jsonObject.get(LoanApiConstants.disbursementPrincipalParameterName).isJsonPrimitive()
                            && StringUtils.isNotBlank((jsonObject.get(LoanApiConstants.disbursementPrincipalParameterName).getAsString()))) {
                        principal = jsonObject.getAsJsonPrimitive(LoanApiConstants.disbursementPrincipalParameterName).getAsBigDecimal();
                    }

                    disbursementDatas.add(new LoanDisbursementDetails(expectedDisbursementDate, actualDisbursementDate, principal));
                    i++;
                } while (i < disbursementDataArray.size());
            }
        }
        return disbursementDatas;
    }

    public static Money deductGivenComponent(final Money transactionAmountRemaining, final Money portionOfTransaction) {
        if (transactionAmountRemaining.isGreaterThanOrEqualTo(portionOfTransaction)) { return portionOfTransaction; }
        return transactionAmountRemaining;
    }
    
	public static Money getCapitalizedChargeBalance(final LoanApplicationTerms loanApplicationTerms, int periodNumber) {
	    BigDecimal capitalizedAmount = BigDecimal.ZERO;
	    MonetaryCurrency currency = loanApplicationTerms.getCurrency();
	    Integer loanTerm = loanApplicationTerms.getNumberOfRepayments();
	    if (loanApplicationTerms.getCapitalizedCharges() != null && loanApplicationTerms.getCapitalizedCharges().size() > 0 && periodNumber != loanTerm) {        	
	        for (LoanCharge loanCharge : loanApplicationTerms.getCapitalizedCharges()) {
	            BigDecimal totalAmount = capitalizedAmount.add(loanCharge.getAmount(currency).getAmount());
	            BigDecimal defaultAmount = MathUtility.getInstallmentAmount(totalAmount, loanTerm, currency, 1);
	            if (periodNumber == 0) {
	            	capitalizedAmount = capitalizedAmount.add(totalAmount);
	            }else if(periodNumber != loanTerm-1){
	            	capitalizedAmount = capitalizedAmount.add(defaultAmount);
	            }else{
	            	capitalizedAmount = capitalizedAmount.add(MathUtility.getInstallmentAmount(totalAmount, loanTerm, currency, loanTerm));
	            }
	        }
	        
	    }
	    return Money.of(currency, capitalizedAmount);
	}
    
    public static BigDecimal getCapitalizedChargeAmount(Collection<LoanCharge> loanCharges){
    	BigDecimal totalCapitalizedCharge = BigDecimal.ZERO;
        for(LoanCharge loanCharge:loanCharges){
            if(loanCharge.isCapitalized()){
                totalCapitalizedCharge = MathUtility.add(totalCapitalizedCharge, loanCharge.getAmount(loanCharge.getLoan().getCurrency()).getAmount());
            }
        }
        return totalCapitalizedCharge;
    }
    
    public static List<LoanCharge> getCapitalizedCharges(Collection<LoanCharge> loanCharges){
    	List<LoanCharge> capitalizedCharges = new ArrayList<>();
        for(LoanCharge loanCharge:loanCharges){
            if(loanCharge.isCapitalized()){
                capitalizedCharges.add(loanCharge);                
            }
        }
        return capitalizedCharges;
    }
    
    public Integer calculateMinimumDaysBetweenDisbursalAndFirstRepayment(final LocalDate disbursalDate, final LoanProduct loanProduct,
            final PeriodFrequencyType loanTermPeriodFrequencyType, final Integer repaymentEvery) {

        final Integer minimumDaysBetweenDisbursalAndFirstRepayment = loanProduct.getMinimumDaysBetweenDisbursalAndFirstRepayment();
        Integer minumumPeriodsBetweenDisbursalAndFirstrepaymentdate = loanProduct.getMinimumPeriodsBetweenDisbursalAndFirstRepayment();
        final Integer minumumDaysBetweenDisbursalAndFirstrepaymentdateFromPeriod = calculateNumberOfDaysBetweenDisbursalRepaymentDateWithRepaymentPeriod(
                disbursalDate, loanTermPeriodFrequencyType, repaymentEvery, minumumPeriodsBetweenDisbursalAndFirstrepaymentdate);
        final Integer calculatedminimumDaysBetweenDisbursalAndFirstRepayment = minimumDaysBetweenDisbursalAndFirstRepayment >= minumumDaysBetweenDisbursalAndFirstrepaymentdateFromPeriod ? minimumDaysBetweenDisbursalAndFirstRepayment
                : minumumDaysBetweenDisbursalAndFirstrepaymentdateFromPeriod;
        return calculatedminimumDaysBetweenDisbursalAndFirstRepayment;
    }
    
    public Integer calculateMinimumDaysBetweenDisbursalAndFirstRepayment(final LocalDate disbursalDate, final Loan loan) {
        LoanProduct loanProduct = loan.getLoanProduct();
        PeriodFrequencyType loanTermPeriodFrequencyType = loan.getLoanProductRelatedDetail().getRepaymentPeriodFrequencyType();
        Integer repaymentEvery = loan.getLoanProductRelatedDetail().getRepayEvery();
        final Integer minimumDaysBetweenDisbursalAndFirstRepayment = loanProduct.getMinimumDaysBetweenDisbursalAndFirstRepayment();
        Integer minumumPeriodsBetweenDisbursalAndFirstrepaymentdate = loanProduct.getMinimumPeriodsBetweenDisbursalAndFirstRepayment();
        final Integer minumumDaysBetweenDisbursalAndFirstrepaymentdateFromPeriod = calculateNumberOfDaysBetweenDisbursalRepaymentDateWithRepaymentPeriod(
                disbursalDate, loanTermPeriodFrequencyType, repaymentEvery, minumumPeriodsBetweenDisbursalAndFirstrepaymentdate);
        final Integer calculatedminimumDaysBetweenDisbursalAndFirstRepayment = minimumDaysBetweenDisbursalAndFirstRepayment >= minumumDaysBetweenDisbursalAndFirstrepaymentdateFromPeriod ? minimumDaysBetweenDisbursalAndFirstRepayment
                : minumumDaysBetweenDisbursalAndFirstrepaymentdateFromPeriod;
        return calculatedminimumDaysBetweenDisbursalAndFirstRepayment;
    }
    
    public Integer calculateNumberOfDaysBetweenDisbursalRepaymentDateWithRepaymentPeriod(final LocalDate disbursalDate,
            final PeriodFrequencyType loanTermPeriodFrequencyType, final Integer repaymentEvery, Integer numberOfRepaymentPeriods) {

        LocalDate calculatedRepaymentdateFromNumberOfRepaymentPeriods = calculateRepaymentDateFromNumberOfRepaymentsPeriods(disbursalDate,
                loanTermPeriodFrequencyType, repaymentEvery, numberOfRepaymentPeriods);
        return Days.daysBetween(disbursalDate, calculatedRepaymentdateFromNumberOfRepaymentPeriods).getDays();
    }

    private LocalDate calculateRepaymentDateFromNumberOfRepaymentsPeriods(final LocalDate disbursalDate,
            final PeriodFrequencyType loanTermPeriodFrequencyType, final Integer repaymentEvery, Integer numberOfRepaymentPeriods) {

        // for calculating nth re payment date according loanFreequnncy of the
        // loan
        // between
        // disbursal date and nth re payment date taking repaid every as
        // loan
        // repaidEvery*loanFreequnncy
        final ScheduledDateGenerator scheduledDateGenerator = new DefaultScheduledDateGenerator();
        Integer repaidEvery = repaymentEvery * numberOfRepaymentPeriods;
        return scheduledDateGenerator.getRepaymentPeriodDate(loanTermPeriodFrequencyType, repaidEvery, disbursalDate);
    }
    

}
