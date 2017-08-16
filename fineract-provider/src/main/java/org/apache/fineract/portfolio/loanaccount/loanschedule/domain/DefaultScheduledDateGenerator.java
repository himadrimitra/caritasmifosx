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


import java.util.ArrayList;
import java.util.Date;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.service.HolidayUtil;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.service.ExpressionUtilsServiceImpl;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.calendar.data.CalendarHistoryDataWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

public class DefaultScheduledDateGenerator implements ScheduledDateGenerator {
    
    @Override
    public LocalDate getLastRepaymentDate(final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO) {

        final int numberOfRepayments = loanApplicationTerms.getNumberOfRepayments();

        LocalDate lastRepaymentDate = loanApplicationTerms.getExpectedDisbursementDate();
        boolean isFirstRepayment = true;
        for (int repaymentPeriod = 1; repaymentPeriod <= numberOfRepayments; repaymentPeriod++) {
            lastRepaymentDate = generateNextRepaymentDate(lastRepaymentDate, loanApplicationTerms, isFirstRepayment);
            isFirstRepayment = false;
        }
        return adjustRepaymentDate(lastRepaymentDate, loanApplicationTerms, holidayDetailDTO).getChangedScheduleDate();
    }

    @Override
    public LocalDate generateNextRepaymentDate(final LocalDate lastRepaymentDate, final LoanApplicationTerms loanApplicationTerms,
            boolean isFirstRepayment) {
        final LocalDate firstRepaymentPeriodDate = loanApplicationTerms.getCalculatedRepaymentsStartingFromLocalDate();
        LocalDate dueRepaymentPeriodDate = null;
        if (isFirstRepayment && firstRepaymentPeriodDate != null) {
            dueRepaymentPeriodDate = firstRepaymentPeriodDate;
        } else {
        	 LocalDate seedDate = null;
             String reccuringString = null;
            Calendar currentCalendar = loanApplicationTerms.getLoanCalendar();
            dueRepaymentPeriodDate = getRepaymentPeriodDate(loanApplicationTerms.getRepaymentPeriodFrequencyType(),
                    loanApplicationTerms.getRepaymentEvery(), lastRepaymentDate);
            dueRepaymentPeriodDate = CalendarUtils.adjustDate(dueRepaymentPeriodDate, loanApplicationTerms.getSeedDate(),
                    loanApplicationTerms.getRepaymentPeriodFrequencyType());
            if (currentCalendar != null && useCalendar(lastRepaymentDate, loanApplicationTerms, currentCalendar)) {
                // If we have currentCalendar object, this means there is a
                // calendar associated with
                // the loan, and we should use it in order to calculate next
                // repayment
                
                CalendarHistory calendarHistory = null;
                CalendarHistoryDataWrapper calendarHistoryDataWrapper = loanApplicationTerms.getCalendarHistoryDataWrapper();
                if(calendarHistoryDataWrapper != null){
                    calendarHistory = loanApplicationTerms.getCalendarHistoryDataWrapper().getCalendarHistory(dueRepaymentPeriodDate);
                }
 
                // get the start date from the calendar history
                if (calendarHistory == null) {
                    seedDate = currentCalendar.getStartDateLocalDate();
                    reccuringString = currentCalendar.getRecurrence();
                } else {
                    seedDate = calendarHistory.getStartDateLocalDate();
                    reccuringString = calendarHistory.getRecurrence();
                }

                dueRepaymentPeriodDate = CalendarUtils.getNextRepaymentMeetingDate(reccuringString, seedDate, lastRepaymentDate,
                        loanApplicationTerms.getRepaymentEvery(),
                        CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(loanApplicationTerms.getLoanTermPeriodFrequencyType()),
                        loanApplicationTerms.isSkipRepaymentOnFirstDayofMonth(),
                        loanApplicationTerms.getNumberOfdays());
            }
        }
        
        return dueRepaymentPeriodDate;
    }
    
    private boolean useCalendar(final LocalDate lastRepaymentDate, final LoanApplicationTerms loanApplicationTerms,
            final Calendar currentCalendar) {
        LocalDate calendarCreationDate = DateUtils.getLocalDateOfTenant().withDayOfMonth(1).withYear(2017).withMonthOfYear(8);
        Date calendarStartDate = currentCalendar.getCreatedDate() == null ? DateUtils.getLocalDateOfTenant().toDate() : currentCalendar.getCreatedDate().toDate();
        if(calendarStartDate.after(calendarCreationDate.toDate())){
            return true;
        }
        LocalDate newCodeMigrationDate = DateUtils.getLocalDateOfTenant().withDayOfMonth(8).withYear(2016).withMonthOfYear(4);
        boolean useCalendar = lastRepaymentDate.isAfter(newCodeMigrationDate);
        if (useCalendar) {
            if(loanApplicationTerms.getExpectedDisbursementDate().isBefore(newCodeMigrationDate)){
                useCalendar = currentCalendar.getStartDateLocalDate().isAfter(newCodeMigrationDate);
            }
        }

        return useCalendar;
    }

    @Override
    public AdjustedDateDetailsDTO adjustRepaymentDate(final LocalDate dueRepaymentPeriodDate,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO) {
        final LocalDate adjustedDate = dueRepaymentPeriodDate;
        return getAdjustedDateDetailsDTO(dueRepaymentPeriodDate, loanApplicationTerms, holidayDetailDTO, adjustedDate);
    }

    private AdjustedDateDetailsDTO getAdjustedDateDetailsDTO(final LocalDate dueRepaymentPeriodDate,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO, final LocalDate adjustedDate) {
        final boolean isFirstRepayment = false;
        final LocalDate nextRepaymentPeriodDueDate = generateNextRepaymentDate(adjustedDate, loanApplicationTerms, isFirstRepayment);
        final AdjustedDateDetailsDTO newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(adjustedDate, dueRepaymentPeriodDate,
                nextRepaymentPeriodDueDate);
        return recursivelyCheckNonWorkingDaysAndHolidaysAndWorkingDaysExemptionToGenerateNextRepaymentPeriodDate(newAdjustedDateDetailsDTO,
                loanApplicationTerms, holidayDetailDTO, isFirstRepayment);
    }

    /**
     * Recursively checking non working days and holidays and working days
     * exemption to generate next repayment period date Base on the
     * configuration
     * 
     * @param adjustedDateDetailsDTO
     * @param loanApplicationTerms
     * @param holidayDetailDTO
     * @param nextRepaymentPeriodDueDate
     * @param rescheduleType
     * @param isFirstRepayment
     * @return
     */
    private AdjustedDateDetailsDTO recursivelyCheckNonWorkingDaysAndHolidaysAndWorkingDaysExemptionToGenerateNextRepaymentPeriodDate(
            final AdjustedDateDetailsDTO adjustedDateDetailsDTO, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO, final boolean isFirstRepayment) {
        
        checkAndUpdateWorkingDayIfRepaymentDateIsNonWorkingDay(adjustedDateDetailsDTO, holidayDetailDTO, loanApplicationTerms,
                isFirstRepayment);
        
        checkAndUpdateWorkingDayIfRepaymentDateIsHolidayDay(adjustedDateDetailsDTO, holidayDetailDTO, loanApplicationTerms,
                isFirstRepayment);
                
        if (loanApplicationTerms.getLoanCalendar() != null) {
            adjustNextRepaymentDateBasedOnWorkingDaysExemption(adjustedDateDetailsDTO, holidayDetailDTO, loanApplicationTerms);
        }

        /**
         * Check Changed Schedule Date is holiday or is not a working day Then
         * re-call this method to get the non holiday and working day
         */
        if ((holidayDetailDTO.isHolidayEnabled() && HolidayUtil.getApplicableHoliday(adjustedDateDetailsDTO.getChangedScheduleDate(),
                holidayDetailDTO.getHolidays()) != null)
                || WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), adjustedDateDetailsDTO.getChangedScheduleDate())) {
            recursivelyCheckNonWorkingDaysAndHolidaysAndWorkingDaysExemptionToGenerateNextRepaymentPeriodDate(adjustedDateDetailsDTO,
                    loanApplicationTerms, holidayDetailDTO, isFirstRepayment);
        }
        return adjustedDateDetailsDTO;
    }

    /**
     * This method to check and update the working day if repayment date is
     * holiday
     * 
     * @param adjustedDateDetailsDTO
     * @param holidayDetailDTO
     * @param loanApplicationTerms
     * @param isFirstRepayment
     */
    private void checkAndUpdateWorkingDayIfRepaymentDateIsHolidayDay(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final LoanApplicationTerms loanApplicationTerms, final boolean isFirstRepayment) {
        if (holidayDetailDTO.isHolidayEnabled()) {
            Holiday applicableHolidayForNewAdjustedDate = null;
            while ((applicableHolidayForNewAdjustedDate = HolidayUtil.getApplicableHoliday(adjustedDateDetailsDTO.getChangedScheduleDate(),
                    holidayDetailDTO.getHolidays())) != null) {
                if (applicableHolidayForNewAdjustedDate.getReScheduleType().isResheduleToNextRepaymentDate()) {
                    LocalDate nextRepaymentPeriodDueDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
                    while (!nextRepaymentPeriodDueDate.isAfter(adjustedDateDetailsDTO.getChangedScheduleDate())) {
                        nextRepaymentPeriodDueDate = generateNextRepaymentDate(nextRepaymentPeriodDueDate, loanApplicationTerms,
                                isFirstRepayment);
                    }
                    adjustedDateDetailsDTO.setChangedScheduleDate(nextRepaymentPeriodDueDate);
                    adjustedDateDetailsDTO.setNextRepaymentPeriodDueDate(nextRepaymentPeriodDueDate);
                    if (applicableHolidayForNewAdjustedDate.isExtendRepaymentReschedule()) {
                        adjustedDateDetailsDTO.setChangedActualRepaymentDate(adjustedDateDetailsDTO.getChangedScheduleDate());
                    }
                } else {
                    HolidayUtil.updateRepaymentRescheduleDateToWorkingDayIfItIsHoliday(adjustedDateDetailsDTO,
                            applicableHolidayForNewAdjustedDate);
                }
            }
        }
    }

    /**
     * This method to check and update the working day if repayment date is non
     * working day
     * 
     * @param adjustedDateDetailsDTO
     * @param holidayDetailDTO
     * @param isFirstRepayment 
     * @param loanApplicationTerms 
     */
    private void checkAndUpdateWorkingDayIfRepaymentDateIsNonWorkingDay(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final LoanApplicationTerms loanApplicationTerms, final boolean isFirstRepayment) {
        while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), adjustedDateDetailsDTO.getChangedScheduleDate())) {
            if (WorkingDaysUtil.getRepaymentRescheduleType(holidayDetailDTO.getWorkingDays(),
                    adjustedDateDetailsDTO.getChangedScheduleDate()).isMoveToNextRepaymentDay()) {
                while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(),
                        adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate())
                        || adjustedDateDetailsDTO.getChangedScheduleDate().isAfter(adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate())) {
                    final LocalDate nextRepaymentPeriodDueDate = generateNextRepaymentDate(
                            adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate(), loanApplicationTerms, isFirstRepayment);
                    adjustedDateDetailsDTO.setNextRepaymentPeriodDueDate(nextRepaymentPeriodDueDate);
                }
            }
            WorkingDaysUtil.updateWorkingDayIfRepaymentDateIsNonWorkingDay(adjustedDateDetailsDTO, holidayDetailDTO.getWorkingDays());
        }
    }

    @Override
    public LocalDate getRepaymentPeriodDate(final PeriodFrequencyType frequency, final int repaidEvery, final LocalDate startDate) {
        LocalDate dueRepaymentPeriodDate = startDate;
        switch (frequency) {
            case DAYS:
                dueRepaymentPeriodDate = startDate.plusDays(repaidEvery);
            break;
            case WEEKS:
                dueRepaymentPeriodDate = startDate.plusWeeks(repaidEvery);
            break;
            case MONTHS:
                dueRepaymentPeriodDate = startDate.plusMonths(repaidEvery);
            break;
            case YEARS:
                dueRepaymentPeriodDate = startDate.plusYears(repaidEvery);
            break;
            case INVALID:
            break;
        }
        return dueRepaymentPeriodDate;
    }

    @Override
    public Boolean isDateFallsInSchedule(final PeriodFrequencyType frequency, final int repaidEvery, final LocalDate startDate,
            final LocalDate date) {
        boolean isScheduledDate = false;
        switch (frequency) {
            case DAYS:
                int diff = Days.daysBetween(startDate, date).getDays();
                isScheduledDate = (diff % repaidEvery) == 0;
            break;
            case WEEKS:
                int weekDiff = Weeks.weeksBetween(startDate, date).getWeeks();
                isScheduledDate = (weekDiff % repaidEvery) == 0;
                if (isScheduledDate) {
                    LocalDate modifiedDate = startDate.plusWeeks(weekDiff);
                    isScheduledDate = modifiedDate.isEqual(date);
                }
            break;
            case MONTHS:
                int monthDiff = Months.monthsBetween(startDate, date).getMonths();
                isScheduledDate = (monthDiff % repaidEvery) == 0;
                if (isScheduledDate) {
                    LocalDate modifiedDate = startDate.plusMonths(monthDiff);
                    isScheduledDate = modifiedDate.isEqual(date);
                }
            break;
            case YEARS:
                int yearDiff = Years.yearsBetween(startDate, date).getYears();
                isScheduledDate = (yearDiff % repaidEvery) == 0;
                if (isScheduledDate) {
                    LocalDate modifiedDate = startDate.plusYears(yearDiff);
                    isScheduledDate = modifiedDate.isEqual(date);
                }
            break;
            case INVALID:
            break;
        }
        return isScheduledDate;
    }

    @Override
    public LocalDate idealDisbursementDateBasedOnFirstRepaymentDate(final PeriodFrequencyType repaymentPeriodFrequencyType,
            final int repaidEvery, final LocalDate firstRepaymentDate, final Calendar loanCalendar, final HolidayDetailDTO holidayDetailDTO, 
            final LoanApplicationTerms loanApplicationTerms) {

        LocalDate idealDisbursementDate = null;

        switch (repaymentPeriodFrequencyType) {
            case DAYS:
                idealDisbursementDate = firstRepaymentDate.minusDays(repaidEvery);
            break;
            case WEEKS:
                idealDisbursementDate = firstRepaymentDate.minusWeeks(repaidEvery);
            break;
            case MONTHS:
                if (loanCalendar == null) {
                    idealDisbursementDate = firstRepaymentDate.minusMonths(repaidEvery);
                } else {
                    LocalDate meetingStartDate = loanCalendar.getStartDateLocalDate(); 
                    String recurrence = loanCalendar.getRecurrence();
                    CalendarHistory calendarHistory = null;
                    CalendarHistoryDataWrapper calendarHistoryDataWrapper = loanApplicationTerms.getCalendarHistoryDataWrapper();
                    if(calendarHistoryDataWrapper != null){
                        calendarHistory = loanApplicationTerms.getCalendarHistoryDataWrapper().getCalendarHistory(firstRepaymentDate);
                    }
     
                    // get the start date from the calendar history
                    if (calendarHistory != null) {
                        meetingStartDate = calendarHistory.getStartDateLocalDate();
                        recurrence = calendarHistory.getRecurrence();
                    }
                    
                   
                    while (!meetingStartDate.isBefore(loanApplicationTerms.getExpectedDisbursementDate())
                            || meetingStartDate.getMonthOfYear() == loanApplicationTerms.getExpectedDisbursementDate().getMonthOfYear()) {
                        meetingStartDate = meetingStartDate.minusMonths(repaidEvery);
                    }
                    LocalDate idealDisbursementDateTemp = meetingStartDate;
                    while (idealDisbursementDateTemp.isBefore(firstRepaymentDate)) {
                        idealDisbursementDate = idealDisbursementDateTemp;
                        idealDisbursementDateTemp = CalendarUtils.getNewRepaymentMeetingDate(recurrence,
                                meetingStartDate, idealDisbursementDateTemp.plusDays(1), repaidEvery,
                                CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(repaymentPeriodFrequencyType),
                                holidayDetailDTO.getWorkingDays(), loanApplicationTerms.isSkipRepaymentOnFirstDayofMonth(),
                                loanApplicationTerms.getNumberOfdays());
                    }
                    
                   
                }
            break;
            case YEARS:
                idealDisbursementDate = firstRepaymentDate.minusYears(repaidEvery);
            break;
            case INVALID:
            break;
        }

        return idealDisbursementDate;
    }

    @Override
    public LocalDate generateNextScheduleDateStartingFromDisburseDate(final LocalDate lastRepaymentDate,
            final LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO) {
        LocalDate generatedDate = loanApplicationTerms.getExpectedDisbursementDate();
        boolean isFirstRepayment = true;
        while (!generatedDate.isAfter(lastRepaymentDate)) {
            generatedDate = generateNextRepaymentDate(generatedDate, loanApplicationTerms, isFirstRepayment);
            isFirstRepayment = false;
        }
        return adjustRepaymentDate(generatedDate, loanApplicationTerms, holidayDetailDTO).getChangedScheduleDate();
    }
    
    /**
     * It will validate working days exemption and generate the repayment date based on the config
     * @param adjustedDateDetailsDTO
     * @param holidayDetailDTO
     * @param loanApplicationTerms
     * @param dueRepaymentPeriodDate
     */
    private void adjustNextRepaymentDateBasedOnWorkingDaysExemption(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final LoanApplicationTerms loanApplicationTerms) {
        final LocalDate originalAdjustDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
        LocalDate seedDate = null;
        String reccuringString = null;
        CalendarHistory calendarHistory = null;
        final Calendar currentCalendar = loanApplicationTerms.getLoanCalendar();
        final CalendarHistoryDataWrapper calendarHistoryDataWrapper = loanApplicationTerms.getCalendarHistoryDataWrapper();
        if (calendarHistoryDataWrapper != null) {
            calendarHistory = loanApplicationTerms.getCalendarHistoryDataWrapper().getCalendarHistory(originalAdjustDate);
        }
        // get the start date from the calendar history
        if (calendarHistory == null) {
            seedDate = currentCalendar.getStartDateLocalDate();
            reccuringString = currentCalendar.getRecurrence();
        } else {
            seedDate = calendarHistory.getStartDateLocalDate();
            reccuringString = calendarHistory.getRecurrence();
        }
        LocalDate scheduleDate = adjustedDateDetailsDTO.getChangedScheduleDate();
        if (seedDate != null) {
            final ArrayList<WorkingDayExemptionsData> workingDayExemptions = (ArrayList<WorkingDayExemptionsData>) holidayDetailDTO
                    .getWorkingDayExemptions();
            for (final WorkingDayExemptionsData workingDayExcemption : workingDayExemptions) {
                if (ExpressionUtilsServiceImpl.isGivenDateisWorkingDayExemption(adjustedDateDetailsDTO.getChangedScheduleDate(),
                        workingDayExcemption.getExpression())) {
                    do {
                        if (workingDayExcemption.getApplicableProperty().isInstallmentDate()) {
                            switch (workingDayExcemption.getRepaymentRescheduleType()) {
                                case MOVE_TO_NEXT_REPAYMENT_DAY:
                                    scheduleDate = generateNextRepaymentDate(scheduleDate, loanApplicationTerms, false);
                                break;
                                case MOVE_TO_NEXT_MEETING_DAY:
                                    scheduleDate = CalendarUtils.getNextRecurringDate(reccuringString, seedDate, scheduleDate);
                                break;
                                default:
                                break;
                            }
                        }
                    } while (ExpressionUtilsServiceImpl
                            .isGivenDateisWorkingDayExemption(scheduleDate, workingDayExcemption.getExpression()));
                    if (workingDayExcemption.getRepaymentScheduleUpdationType().isExtendRepaymentSchedule()) {
                        adjustedDateDetailsDTO.setChangedScheduleDate(scheduleDate);
                        /**
                         * Due to Extend Repayment Schedule setting we are changing
                         * the Actual Repayment Date to generate next repayments
                         * date should extend accordingly
                         */
                        adjustedDateDetailsDTO.setChangedActualRepaymentDate(scheduleDate);
                    } else {
                        adjustedDateDetailsDTO.setChangedScheduleDate(scheduleDate);
                    }
                }
            }
        }
    }
    
    /**
     * calculates Interest stating date as per the settings
     * 
     * @param firstRepaymentdate
     *            TODO
     * @param boolean1
     * @param localDate
     */
  
    @Override
    public LocalDate calculateInterestStartDateForPeriod(final LoanApplicationTerms loanApplicationTerms, LocalDate periodStartDate,
            final LocalDate idealDisbursementDate, final LocalDate firstRepaymentdate) {
        final Boolean isInterestChargedFromDateSameAsDisbursalDateEnabled = loanApplicationTerms
                .isInterestChargedFromDateSameAsDisbursalDateEnabled();
        final LocalDate expectedDisbursementDate = loanApplicationTerms.getExpectedDisbursementDate();
        LocalDate periodStartDateApplicableForInterest = periodStartDate;
        if (periodStartDate.isBefore(idealDisbursementDate) || firstRepaymentdate.isAfter(periodStartDate)) {
            if (loanApplicationTerms.getInterestChargedFromLocalDate() != null) {
                if (periodStartDate.isEqual(loanApplicationTerms.getExpectedDisbursementDate())
                        || loanApplicationTerms.getInterestChargedFromLocalDate().isAfter(periodStartDate)) {
                    periodStartDateApplicableForInterest = loanApplicationTerms.getInterestChargedFromLocalDate();
                }
            } else if (loanApplicationTerms.getInterestChargedFromLocalDate() == null
                    && isInterestChargedFromDateSameAsDisbursalDateEnabled) {
                periodStartDateApplicableForInterest = expectedDisbursementDate;
            } else if (periodStartDate.isEqual(loanApplicationTerms.getExpectedDisbursementDate())) {
                periodStartDateApplicableForInterest = idealDisbursementDate;
            }
        }
        return periodStartDateApplicableForInterest;
    }
}