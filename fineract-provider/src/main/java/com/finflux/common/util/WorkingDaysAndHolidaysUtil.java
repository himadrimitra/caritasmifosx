package com.finflux.common.util;

import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.holiday.service.HolidayUtil;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.service.WorkingDaysUtil;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.data.HolidayDetailDTO;
import org.joda.time.LocalDate;

public class WorkingDaysAndHolidaysUtil {

    public static AdjustedDateDetailsDTO adjustInstallmentDateBasedOnWorkingDaysAndHolidays(
            final AdjustedDateDetailsDTO adjustedDateDetailsDTO, final HolidayDetailDTO holidayDetailDTO,
            final PeriodFrequencyType frequency, final Integer recurringEvery, final CalendarData calendarData) {
        while ((holidayDetailDTO.isHolidayEnabled() && HolidayUtil.getApplicableHoliday(adjustedDateDetailsDTO.getChangedScheduleDate(),
                holidayDetailDTO.getHolidays()) != null)
                || WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), adjustedDateDetailsDTO.getChangedScheduleDate())) {
            checkAndUpdateWorkingDayInCaseInstallmentDateIsNonWorkingDay(adjustedDateDetailsDTO, holidayDetailDTO, frequency,
                    recurringEvery, calendarData);
            checkAndUpdateNotHolidayInCaseInstallmentDateIsHoliday(adjustedDateDetailsDTO, holidayDetailDTO, frequency, recurringEvery,
                    calendarData);
        }
        return adjustedDateDetailsDTO;
    }

    private static void checkAndUpdateWorkingDayInCaseInstallmentDateIsNonWorkingDay(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final PeriodFrequencyType frequency, final Integer recurringEvery,
            final CalendarData calendarData) {
        while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(), adjustedDateDetailsDTO.getChangedScheduleDate())) {
            if (WorkingDaysUtil.getRepaymentRescheduleType(holidayDetailDTO.getWorkingDays(),
                    adjustedDateDetailsDTO.getChangedScheduleDate()).isMoveToNextRepaymentDay()) {
                while (WorkingDaysUtil.isNonWorkingDay(holidayDetailDTO.getWorkingDays(),
                        adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate())
                        || adjustedDateDetailsDTO.getChangedScheduleDate().isAfter(adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate())) {
                    final LocalDate nextRepaymentPeriodDueDate = ScheduleDateGeneratorUtil.generateNextScheduleDate(calendarData,
                            adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate(), frequency, recurringEvery);
                    adjustedDateDetailsDTO.setNextRepaymentPeriodDueDate(nextRepaymentPeriodDueDate);
                }
            }
            WorkingDaysUtil.updateWorkingDayIfRepaymentDateIsNonWorkingDay(adjustedDateDetailsDTO, holidayDetailDTO.getWorkingDays());
        }
    }

    private static void checkAndUpdateNotHolidayInCaseInstallmentDateIsHoliday(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final HolidayDetailDTO holidayDetailDTO, final PeriodFrequencyType frequency, final Integer recurringEvery,
            final CalendarData calendarData) {
        if (holidayDetailDTO.isHolidayEnabled()) {
            Holiday applicableHolidayForNewAdjustedDate = null;
            while ((applicableHolidayForNewAdjustedDate = HolidayUtil.getApplicableHoliday(adjustedDateDetailsDTO.getChangedScheduleDate(),
                    holidayDetailDTO.getHolidays())) != null) {
                if (applicableHolidayForNewAdjustedDate.getReScheduleType().isResheduleToNextRepaymentDate()) {
                    LocalDate nextRepaymentPeriodDueDate = adjustedDateDetailsDTO.getChangedActualRepaymentDate();
                    while (!nextRepaymentPeriodDueDate.isAfter(adjustedDateDetailsDTO.getChangedScheduleDate())) {
                        nextRepaymentPeriodDueDate = ScheduleDateGeneratorUtil.generateNextScheduleDate(calendarData,
                                nextRepaymentPeriodDueDate, frequency, recurringEvery);
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
}
