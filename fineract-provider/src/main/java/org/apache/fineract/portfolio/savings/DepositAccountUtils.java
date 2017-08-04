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
package org.apache.fineract.portfolio.savings;

import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.service.HolidayUtil;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.joda.time.LocalDate;

public class DepositAccountUtils {

    public static final int GENERATE_MINIMUM_NUMBER_OF_FUTURE_INSTALMENTS = 5;

    public static LocalDate calculateNextDepositDate(final LocalDate lastDepositDate, final PeriodFrequencyType frequency,
            final int recurringEvery) {
        LocalDate nextDepositDate = lastDepositDate;

        switch (frequency) {
            case DAYS:
                nextDepositDate = lastDepositDate.plusDays(recurringEvery);
            break;
            case WEEKS:
                nextDepositDate = lastDepositDate.plusWeeks(recurringEvery);
            break;
            case MONTHS:
                nextDepositDate = lastDepositDate.plusMonths(recurringEvery);
            break;
            case YEARS:
                nextDepositDate = lastDepositDate.plusYears(recurringEvery);
            break;
            case INVALID:
            break;
        }
        return nextDepositDate;
    }

    public static LocalDate calculateNextDepositDate(final LocalDate lastDepositDate, final String recurrence) {
        final PeriodFrequencyType frequencyType = CalendarFrequencyType.from(CalendarUtils.getFrequency(recurrence));
        Integer frequency = CalendarUtils.getInterval(recurrence);
        frequency = frequency == -1 ? 1 : frequency;
        return calculateNextDepositDate(lastDepositDate, frequencyType, frequency);
    }
    
    public static AdjustedDateDetailsDTO adjustInstallmentDateBasedOnWorkingDaysAndHolidays(
            final AdjustedDateDetailsDTO adjustedDateDetailsDTO, final HolidayDetailDTO holidayDetailDTO,
            final PeriodFrequencyType frequency, final Integer recurringEvery) {
        while ((holidayDetailDTO.isHolidayEnabled() && HolidayUtil.getApplicableHoliday(adjustedDateDetailsDTO.getChangedScheduleDate(),
                holidayDetailDTO.getHolidays()) != null)
                || WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), adjustedDateDetailsDTO.getChangedScheduleDate())) {
            checkAndUpdateWorkingDayInCaseInstallmentDateIsNonWorkingDay(adjustedDateDetailsDTO, holidayDetailDTO, frequency,
                    recurringEvery);
            checkAndUpdateNotHolidayInCaseInstallmentDateIsHoliday(adjustedDateDetailsDTO, holidayDetailDTO, frequency, recurringEvery);
        }
        return adjustedDateDetailsDTO;
    }

    private static void checkAndUpdateWorkingDayInCaseInstallmentDateIsNonWorkingDay(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final PeriodFrequencyType frequency, final Integer recurringEvery) {
        while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), adjustedDateDetailsDTO.getChangedScheduleDate())) {
            if (WorkingDaysUtil.getRepaymentRescheduleType(holidayDetailDTO.getWorkingDays(),
                    adjustedDateDetailsDTO.getChangedScheduleDate()).isMoveToNextRepaymentDay()) {
                while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(),
                        adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate())
                        || adjustedDateDetailsDTO.getChangedScheduleDate().isAfter(adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate())) {
                    final LocalDate nextRepaymentPeriodDueDate = DepositAccountUtils.calculateNextDepositDate(
                            adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate(), frequency, recurringEvery);
                    adjustedDateDetailsDTO.setNextRepaymentPeriodDueDate(nextRepaymentPeriodDueDate);
                }
            }
            WorkingDaysUtil.updateWorkingDayIfRepaymentDateIsNonWorkingDay(adjustedDateDetailsDTO, holidayDetailDTO.getWorkingDays());
        }
    }

    private static void checkAndUpdateNotHolidayInCaseInstallmentDateIsHoliday(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final PeriodFrequencyType frequency, final Integer recurringEvery) {
        if (holidayDetailDTO.isHolidayEnabled()) {
            Holiday applicableHolidayForNewAdjustedDate = null;
            while ((applicableHolidayForNewAdjustedDate = HolidayUtil.getApplicableHoliday(adjustedDateDetailsDTO.getChangedScheduleDate(),
                    holidayDetailDTO.getHolidays())) != null) {
                if (applicableHolidayForNewAdjustedDate.getReScheduleType().isResheduleToNextRepaymentDate()) {
                    LocalDate nextRepaymentPeriodDueDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
                    while (!nextRepaymentPeriodDueDate.isAfter(adjustedDateDetailsDTO.getChangedScheduleDate())) {
                        nextRepaymentPeriodDueDate = DepositAccountUtils.calculateNextDepositDate(
                                adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate(), frequency, recurringEvery);
                        adjustedDateDetailsDTO.setChangedScheduleDate(nextRepaymentPeriodDueDate);
                        adjustedDateDetailsDTO.setNextRepaymentPeriodDueDate(nextRepaymentPeriodDueDate);
                    }
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

}
