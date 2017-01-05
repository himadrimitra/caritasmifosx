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

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.service.HolidayUtil;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.data.WorkingDayExemptionsData;
import org.apache.fineract.organisation.workingdays.domain.ApplicableProperty;
import org.apache.fineract.organisation.workingdays.domain.RepaymentRescheduleType;
import org.apache.fineract.organisation.workingdays.domain.RepaymentScheduleUpdationType;
import org.apache.fineract.organisation.workingdays.service.EpressionUtilsServiceImpl;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.calendar.data.CalendarHistoryDataWrapper;
import org.apache.fineract.portfolio.calendar.domain.Calendar;
import org.apache.fineract.portfolio.calendar.domain.CalendarHistory;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.DayOfWeekType;
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
            lastRepaymentDate = generateNextRepaymentDate(lastRepaymentDate, loanApplicationTerms, isFirstRepayment, holidayDetailDTO);
            isFirstRepayment = false;
        }
        lastRepaymentDate = adjustRepaymentDate(lastRepaymentDate, loanApplicationTerms, holidayDetailDTO).getChangedScheduleDate();
        return lastRepaymentDate;
    }

    @Override
    public LocalDate generateNextRepaymentDate(final LocalDate lastRepaymentDate, final LoanApplicationTerms loanApplicationTerms,
            boolean isFirstRepayment, final HolidayDetailDTO holidayDetailDTO) {
        final LocalDate firstRepaymentPeriodDate = loanApplicationTerms.getCalculatedRepaymentsStartingFromLocalDate();
        LocalDate dueRepaymentPeriodDate = null;
        if (isFirstRepayment && firstRepaymentPeriodDate != null) {
            dueRepaymentPeriodDate = firstRepaymentPeriodDate;
        } else {
        	 LocalDate seedDate = null;
             String reccuringString = null;
            Calendar currentCalendar = loanApplicationTerms.getLoanCalendar();
            dueRepaymentPeriodDate = getRepaymentPeriodDate(loanApplicationTerms.getRepaymentPeriodFrequencyType(),
                    loanApplicationTerms.getRepaymentEvery(), lastRepaymentDate, null,
                    null);
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
                        holidayDetailDTO.getWorkingDays(), loanApplicationTerms.isSkipRepaymentOnFirstDayofMonth(),
                        loanApplicationTerms.getNumberOfdays());
            }
        }
        
        return dueRepaymentPeriodDate;
    }
    
    private boolean useCalendar(final LocalDate lastRepaymentDate, final LoanApplicationTerms loanApplicationTerms,
            final Calendar currentCalendar) {
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
    public AdjustedDateDetailsDTO adjustRepaymentDate(final LocalDate dueRepaymentPeriodDate, final LoanApplicationTerms loanApplicationTerms,
            final HolidayDetailDTO holidayDetailDTO) {
    	
    	AdjustedDateDetailsDTO newAdjustedDateDetailsDTO = null;
    	AdjustedDateDetailsDTO adjustedDateDetailsDTO = null;
    	LocalDate nextRepaymentDateAsPerSchedule = null;
        LocalDate adjustedDate = dueRepaymentPeriodDate;
        adjustedDateDetailsDTO = getadjustedDate(dueRepaymentPeriodDate, loanApplicationTerms, holidayDetailDTO, adjustedDate);
        LocalDate originalAdjustDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();

        if (loanApplicationTerms.getLoanCalendar() != null) {
            do {
                LocalDate seedDate = null;
                String reccuringString = null;
                CalendarHistory calendarHistory = null;

                Calendar currentCalendar = loanApplicationTerms.getLoanCalendar();

                CalendarHistoryDataWrapper calendarHistoryDataWrapper = loanApplicationTerms.getCalendarHistoryDataWrapper();
                if (calendarHistoryDataWrapper != null) {
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

                adjustedDateDetailsDTO = adjustNextRepaymentDateBasedOnWorkingDaysExemption(
                        adjustedDateDetailsDTO.getChangedScheduleDate(), holidayDetailDTO, reccuringString, seedDate, loanApplicationTerms);
                adjustedDate = adjustedDateDetailsDTO.getChangedScheduleDate();
                newAdjustedDateDetailsDTO = adjustedDateDetailsDTO;
                adjustedDateDetailsDTO = getadjustedDate(dueRepaymentPeriodDate, loanApplicationTerms, holidayDetailDTO, adjustedDate);
            } while (!adjustedDate.isEqual(adjustedDateDetailsDTO.getChangedScheduleDate()));

            if (newAdjustedDateDetailsDTO.getChangeactualMeetingday()) {
                newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(adjustedDate, adjustedDate);
            } else {
                boolean isNextRepaymentDateLessThanAdjuestedDate = false;
                do {
                    isNextRepaymentDateLessThanAdjuestedDate = false;
                    nextRepaymentDateAsPerSchedule = generateNextRepaymentDate(originalAdjustDate, loanApplicationTerms, false, holidayDetailDTO);
                    if (nextRepaymentDateAsPerSchedule.isBefore(adjustedDate)) {
                        originalAdjustDate = nextRepaymentDateAsPerSchedule;
                        isNextRepaymentDateLessThanAdjuestedDate = true;
                    }
                } while (isNextRepaymentDateLessThanAdjuestedDate);
                newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(adjustedDate, originalAdjustDate);
            }
        } else {
            newAdjustedDateDetailsDTO = adjustedDateDetailsDTO;
        }
        return newAdjustedDateDetailsDTO;
    }

	private AdjustedDateDetailsDTO getadjustedDate(final LocalDate dueRepaymentPeriodDate, final LoanApplicationTerms loanApplicationTerms,
			final HolidayDetailDTO holidayDetailDTO, LocalDate adjustedDate) {
		LocalDate nextDueRepaymentPeriodDate = getRepaymentPeriodDate(loanApplicationTerms.getRepaymentPeriodFrequencyType(),
                loanApplicationTerms.getRepaymentEvery(), adjustedDate, loanApplicationTerms.getNthDay(),
                loanApplicationTerms.getWeekDayType());

        final RepaymentRescheduleType rescheduleType = RepaymentRescheduleType.fromInt(holidayDetailDTO.getWorkingDays()
                .getRepaymentReschedulingType());

        /**
         * Fix for https://mifosforge.jira.com/browse/MIFOSX-1357
         */
        // recursively check for the next working meeting day.
        while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), nextDueRepaymentPeriodDate)
                && rescheduleType == RepaymentRescheduleType.MOVE_TO_NEXT_REPAYMENT_MEETING_DAY) {

            nextDueRepaymentPeriodDate = getRepaymentPeriodDate(loanApplicationTerms.getRepaymentPeriodFrequencyType(),
                    loanApplicationTerms.getRepaymentEvery(), nextDueRepaymentPeriodDate, loanApplicationTerms.getNthDay(),
                    loanApplicationTerms.getWeekDayType());
            nextDueRepaymentPeriodDate = CalendarUtils.adjustDate(nextDueRepaymentPeriodDate, loanApplicationTerms.getSeedDate(),
                    loanApplicationTerms.getRepaymentPeriodFrequencyType());

        }
        adjustedDate = WorkingDaysUtil.getOffSetDateIfNonWorkingDay(adjustedDate, nextDueRepaymentPeriodDate,
                holidayDetailDTO.getWorkingDays());
        AdjustedDateDetailsDTO newAdjustedDateDetailsDTO = null;
        if (holidayDetailDTO.isHolidayEnabled()) {
        	newAdjustedDateDetailsDTO = HolidayUtil.getRepaymentRescheduleDateToIfHoliday(adjustedDate, holidayDetailDTO.getHolidays());
        }
        
        Holiday applicableHolidayForAjustedDate = HolidayUtil.getApplicableHoliday(adjustedDate, holidayDetailDTO.getHolidays());
		if (applicableHolidayForAjustedDate != null
				&& applicableHolidayForAjustedDate.getReScheduleType().isResheduleToNextRepaymentDate()) {
        	LocalDate newAdjustedDate = dueRepaymentPeriodDate;
        	Holiday applicableHolidayForNewAdjustedDate = HolidayUtil.getApplicableHoliday(newAdjustedDate, holidayDetailDTO.getHolidays());
            do{
            	
				if (applicableHolidayForNewAdjustedDate != null
						&& applicableHolidayForNewAdjustedDate.isExtendRepaymentReschedule()) {
					newAdjustedDateDetailsDTO = HolidayUtil.getRepaymentRescheduleDateToIfHoliday(newAdjustedDate, holidayDetailDTO.getHolidays());
            	}
            	newAdjustedDate = generateNextRepaymentDate(newAdjustedDate, loanApplicationTerms, false, holidayDetailDTO);
            	applicableHolidayForNewAdjustedDate = HolidayUtil.getApplicableHoliday(newAdjustedDate, holidayDetailDTO.getHolidays());
            }while(applicableHolidayForNewAdjustedDate != null);

            if(newAdjustedDateDetailsDTO == null){
            	newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(newAdjustedDate, newAdjustedDate);
            }
            	newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(newAdjustedDate, dueRepaymentPeriodDate);
        } else {
        	newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(adjustedDate, dueRepaymentPeriodDate);
        }
		if (applicableHolidayForAjustedDate != null && applicableHolidayForAjustedDate.isExtendRepaymentReschedule()) {
        	newAdjustedDateDetailsDTO = new AdjustedDateDetailsDTO(newAdjustedDateDetailsDTO.getChangedScheduleDate(), newAdjustedDateDetailsDTO.getChangedScheduleDate());
        }
        return newAdjustedDateDetailsDTO;
	}

    @Override
    public LocalDate getRepaymentPeriodDate(final PeriodFrequencyType frequency, final int repaidEvery, final LocalDate startDate,
            Integer nthDay, DayOfWeekType dayOfWeek) {
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
                if (!(nthDay == null || dayOfWeek == null || dayOfWeek == DayOfWeekType.INVALID)) {
                    dueRepaymentPeriodDate = adjustToNthWeekDay(dueRepaymentPeriodDate, nthDay, dayOfWeek.getValue());
                }
            break;
            case YEARS:
                dueRepaymentPeriodDate = startDate.plusYears(repaidEvery);
            break;
            case INVALID:
            break;
        }
        return dueRepaymentPeriodDate;
    }

    private LocalDate adjustToNthWeekDay(LocalDate dueRepaymentPeriodDate, int nthDay, int dayOfWeek) {
        // adjust date to start of month
        dueRepaymentPeriodDate = dueRepaymentPeriodDate.withDayOfMonth(1);
        // adjust date to next week if current day is past specified day of
        // week.
        if (dueRepaymentPeriodDate.getDayOfWeek() > dayOfWeek) {
            dueRepaymentPeriodDate = dueRepaymentPeriodDate.plusWeeks(1);
        }
        // adjust date to specified date of week
        dueRepaymentPeriodDate = dueRepaymentPeriodDate.withDayOfWeek(dayOfWeek);
        // adjust to specified nth week day
        dueRepaymentPeriodDate = dueRepaymentPeriodDate.plusWeeks(nthDay - 1);
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
                    idealDisbursementDate = CalendarUtils.getNewRepaymentMeetingDate(loanCalendar.getRecurrence(),
                            firstRepaymentDate.minusMonths(repaidEvery), firstRepaymentDate.minusMonths(repaidEvery), repaidEvery,
                            CalendarUtils.getMeetingFrequencyFromPeriodFrequencyType(repaymentPeriodFrequencyType),
                            holidayDetailDTO.getWorkingDays(), loanApplicationTerms.isSkipRepaymentOnFirstDayofMonth(),
                            loanApplicationTerms.getNumberOfdays());
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
    public LocalDate generateNextScheduleDateStartingFromDisburseDate(LocalDate lastRepaymentDate,
            LoanApplicationTerms loanApplicationTerms, final HolidayDetailDTO holidayDetailDTO) {

        LocalDate generatedDate = loanApplicationTerms.getExpectedDisbursementDate();
        boolean isFirstRepayment = true;
        while (!generatedDate.isAfter(lastRepaymentDate)) {
            generatedDate = generateNextRepaymentDate(generatedDate, loanApplicationTerms, isFirstRepayment, holidayDetailDTO);
            isFirstRepayment = false;
        }
        generatedDate = adjustRepaymentDate(generatedDate, loanApplicationTerms, holidayDetailDTO).getChangedScheduleDate();
        return generatedDate;
    }
    
    @Override
    public AdjustedDateDetailsDTO adjustNextRepaymentDateBasedOnWorkingDaysExemption(final LocalDate dueRepaymentPeriodDate,
            final HolidayDetailDTO holidayDetailDTO, final String reccuringString, final LocalDate seedDate,
            final LoanApplicationTerms loanApplicationTerms) {
        boolean isRepaymentSheduleExtended = false;
        LocalDate scheduleDate = dueRepaymentPeriodDate;
        LocalDate actualDate = dueRepaymentPeriodDate;
        if (seedDate != null) {
            final ArrayList<WorkingDayExemptionsData> workingDayExemptions = (ArrayList<WorkingDayExemptionsData>) holidayDetailDTO
                    .getWorkingDayExemptions();
            for (WorkingDayExemptionsData workingDayExcemption : workingDayExemptions) {
                if (EpressionUtilsServiceImpl
                        .isGivenDateisWorkingDayExemption(dueRepaymentPeriodDate, workingDayExcemption.getExpression())) {
                    do {
                        if (ApplicableProperty.INSTALLMENTDATE.getValue().equals(workingDayExcemption.getApplicableProperty())) {

                            switch (workingDayExcemption.getActionToBePerformed()) {
                                case 3:
                                    scheduleDate = CalendarUtils.getNextRecurringDate(reccuringString, seedDate, dueRepaymentPeriodDate);
                                break;
                                case 5:
                                    scheduleDate = getRepaymentPeriodDate(loanApplicationTerms.getRepaymentPeriodFrequencyType(),
                                            loanApplicationTerms.getRepaymentEvery(), dueRepaymentPeriodDate, null, null);
                                    scheduleDate = CalendarUtils.adjustDate(dueRepaymentPeriodDate, loanApplicationTerms.getSeedDate(),
                                            loanApplicationTerms.getRepaymentPeriodFrequencyType());
                                break;
                                default:
                                break;
                            }
                        }
                    } while (EpressionUtilsServiceImpl.isGivenDateisWorkingDayExemption(dueRepaymentPeriodDate,
                            workingDayExcemption.getExpression()));
                }

                if (workingDayExcemption.getupdateType() == RepaymentScheduleUpdationType.EXTENDREPAYMENTSCHEDULE.getValue()) {
                    isRepaymentSheduleExtended = true;
                    actualDate = scheduleDate;
                } else {
                    isRepaymentSheduleExtended = false;
                    actualDate = scheduleDate;
                }

            }
            return new AdjustedDateDetailsDTO(scheduleDate, actualDate, isRepaymentSheduleExtended);
        }
        return new AdjustedDateDetailsDTO(scheduleDate, actualDate, isRepaymentSheduleExtended);
    }
}