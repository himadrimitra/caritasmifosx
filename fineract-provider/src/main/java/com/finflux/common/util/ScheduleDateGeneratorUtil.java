package com.finflux.common.util;

import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.joda.time.LocalDate;

public class ScheduleDateGeneratorUtil {

    public static final int GENERATE_MINIMUM_NUMBER_OF_FUTURE_INSTALMENTS = 5;

    public static LocalDate generateNextScheduleDate(final CalendarData calendarData, final LocalDate lastScheduledDate,
            final PeriodFrequencyType frequency, final int recurringEvery) {
        LocalDate nextScheduledDate = lastScheduledDate;
        if (calendarData == null) {
            nextScheduledDate = ScheduleDateGeneratorUtil.generateNextScheduleDate(nextScheduledDate, frequency, recurringEvery);
        } else {
            nextScheduledDate = CalendarUtils.getNextRecurringDate(calendarData.getRecurrence(), calendarData.getStartDate(),
                    nextScheduledDate);
        }
        return nextScheduledDate;
    }
    
    public static LocalDate generateNextScheduleDate(final LocalDate lastScheduledDate, final PeriodFrequencyType frequency,
            final int recurringEvery) {
        LocalDate nextScheduledDate = lastScheduledDate;
        switch (frequency) {
            case DAYS:
                nextScheduledDate = lastScheduledDate.plusDays(recurringEvery);
            break;
            case WEEKS:
                nextScheduledDate = lastScheduledDate.plusWeeks(recurringEvery);
            break;
            case MONTHS:
                nextScheduledDate = lastScheduledDate.plusMonths(recurringEvery);
            break;
            case YEARS:
                nextScheduledDate = lastScheduledDate.plusYears(recurringEvery);
            break;
            case INVALID:
            break;
        }
        return nextScheduledDate;
    }

    public static LocalDate generateNextScheduleDate(final LocalDate lastScheduledDate, final String recurrence) {
        final PeriodFrequencyType frequencyType = CalendarFrequencyType.from(CalendarUtils.getFrequency(recurrence));
        Integer frequency = CalendarUtils.getInterval(recurrence);
        frequency = frequency == -1 ? 1 : frequency;
        return generateNextScheduleDate(lastScheduledDate, frequencyType, frequency);
    }
}
