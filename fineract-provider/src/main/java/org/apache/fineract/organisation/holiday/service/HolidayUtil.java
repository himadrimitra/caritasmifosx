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
package org.apache.fineract.organisation.holiday.service;

import java.util.List;

import org.apache.fineract.organisation.holiday.domain.Holiday;
import org.apache.fineract.organisation.workingdays.data.AdjustedDateDetailsDTO;
import org.joda.time.LocalDate;

public class HolidayUtil {

    public static void updateRepaymentRescheduleDateToWorkingDayIfItIsHoliday(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final List<Holiday> holidays) {
        for (final Holiday holiday : holidays) {
            if (!adjustedDateDetailsDTO.getChangedScheduleDate().isBefore(holiday.getFromDateLocalDate())
                    && !adjustedDateDetailsDTO.getChangedScheduleDate().isAfter(holiday.getToDateLocalDate())) {
                updateRepaymentRescheduleDateToWorkingDayIfItIsHoliday(adjustedDateDetailsDTO, holiday);
            }
        }
    }
    
    public static void updateRepaymentRescheduleDateToWorkingDayIfItIsHoliday(final AdjustedDateDetailsDTO adjustedDateDetailsDTO,
            final Holiday holiday) {
        if (holiday.getReScheduleType().isRescheduleToSpecificDate()) {
            adjustedDateDetailsDTO.setChangedScheduleDate(holiday.getRepaymentsRescheduledToLocalDate());
            if (holiday.isExtendRepaymentReschedule()) {
                adjustedDateDetailsDTO.setChangedActualRepaymentDate(adjustedDateDetailsDTO.getChangedScheduleDate());
            }
        }
    }

    public static boolean isHoliday(final LocalDate date, final List<Holiday> holidays) {
        for (final Holiday holiday : holidays) {
            if (!date.isBefore(holiday.getFromDateLocalDate()) && !date.isAfter(holiday.getToDateLocalDate())) { return true; }
        }
        return false;
    }

    public static Holiday getApplicableHoliday(final LocalDate repaymentDate, final List<Holiday> holidays) {
        Holiday referedHoliday = null;
        for (final Holiday holiday : holidays) {
            if (!repaymentDate.isBefore(holiday.getFromDateLocalDate()) && !repaymentDate.isAfter(holiday.getToDateLocalDate())) {
                referedHoliday = holiday;
            }
        }
        return referedHoliday;
    }
}
