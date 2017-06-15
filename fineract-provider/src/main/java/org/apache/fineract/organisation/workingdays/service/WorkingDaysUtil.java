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
package org.apache.fineract.organisation.workingdays.service;

import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.apache.fineract.organisation.workingdays.domain.NonWorkingDayRescheduleDetail;
import org.apache.fineract.organisation.workingdays.domain.RepaymentRescheduleType;
import org.apache.fineract.organisation.workingdays.domain.WorkingDays;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.joda.time.LocalDate;

public class WorkingDaysUtil {

    public static LocalDate getOffSetDateIfNonWorkingDay(final LocalDate date, final LocalDate nextMeetingDate,
            final WorkingDays workingDays) {

        // If date is a working day then return date.
        if (isWorkingDay(workingDays, date)) { return date; }

        
        RepaymentRescheduleType rescheduleType = RepaymentRescheduleType.fromInt(workingDays.getRepaymentReschedulingType());
        NonWorkingDayRescheduleDetail nonWorkingDayRescheduleDetail = workingDays.getNonWorkingDayRescheduleDetails().get(date.getDayOfWeek());
        if(nonWorkingDayRescheduleDetail != null){
            rescheduleType =  RepaymentRescheduleType.fromInt(nonWorkingDayRescheduleDetail.getRepaymentReschedulingType());
        }
        

        switch (rescheduleType) {
            case INVALID:
                return date;
            case SAME_DAY:
                return date;
            case MOVE_TO_NEXT_WORKING_DAY:
                return getOffSetDateIfNonWorkingDay(date.plusDays(1), nextMeetingDate, workingDays);
            case MOVE_TO_NEXT_REPAYMENT_DAY:
                return nextMeetingDate;
            case MOVE_TO_PREVIOUS_WORKING_DAY:
                return getOffSetDateIfNonWorkingDay(date.minusDays(1), nextMeetingDate, workingDays);
            case MOVE_TO_NEXT_WORKING_WEEK_DAY:
                @SuppressWarnings("null")
                int fromDayForNext = CalendarUtils.getWeekDayAsInt(nonWorkingDayRescheduleDetail.getFromWeekDay());
                int toDayForNext = CalendarUtils.getWeekDayAsInt(nonWorkingDayRescheduleDetail.getToWeekDay());
                int addDays = CalendarUtils.getWeekDayDifference(fromDayForNext, toDayForNext);
                return date.plusDays(addDays);
            case MOVE_TO_PREVIOUS_WORKING_WEEK_DAY:
                @SuppressWarnings("null")
                int fromDayPrevious = CalendarUtils.getWeekDayAsInt(nonWorkingDayRescheduleDetail.getToWeekDay());
                int toDayPrevious = CalendarUtils.getWeekDayAsInt(nonWorkingDayRescheduleDetail.getFromWeekDay());
                int minusDays = CalendarUtils.getWeekDayDifference(fromDayPrevious, toDayPrevious);
                return date.minusDays(minusDays);
            default:
                return date;
        }
    }

    public static boolean isWorkingDay(final WorkingDays workingDays, final LocalDate date) {
        return CalendarUtils.isValidRedurringDate(workingDays.getRecurrence(), date, date) || isRepaymentRescheduleTypeIsSameDay(workingDays);
    }

    public static boolean isRepaymentRescheduleTypeIsSameDay(final WorkingDays workingDays) {
        return RepaymentRescheduleType.fromInt(workingDays.getRepaymentReschedulingType()).isSameDay();
    }
    
    public static boolean isNonWorkingDay(final WorkingDays workingDays, final LocalDate date) {
        return !isWorkingDay(workingDays, date);
    }

    public static void updateWorkingDayIfRepaymentDateIsNonWorkingDay(final AdjustedDateDetailsDTO adjustedDateDetailsDTO, final WorkingDays workingDays) {
        final LocalDate changedScheduleDate = getOffSetDateIfNonWorkingDay(adjustedDateDetailsDTO.getChangedScheduleDate(),
                adjustedDateDetailsDTO.getNextRepaymentPeriodDueDate(), workingDays);
        adjustedDateDetailsDTO.setChangedScheduleDate(changedScheduleDate);
    }
}
