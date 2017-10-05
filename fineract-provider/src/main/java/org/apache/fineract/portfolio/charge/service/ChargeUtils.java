package org.apache.fineract.portfolio.charge.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.common.domain.LoanPeriodFrequencyType;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultPaymentPeriodsInOneYearCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PaymentPeriodsInOneYearCalculator;
import org.joda.time.LocalDate;

public class ChargeUtils {

    final static PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();
    final static Integer weeksInYear = 52;

    public static double retriveChargePercentage(Integer type, double amount) {
        double percentage = amount;
        percentage = percentage / paymentPeriodsInOneYearCalculator.calculate(PeriodFrequencyType.fromInt(type), weeksInYear);
        return percentage;
    }

    public static List<LocalDate> retriveRecurrencePeriods(LocalDate startDate, LocalDate toDate, List<LocalDate> repaymentScheduledates,
            int gracePeriod, LoanPeriodFrequencyType feeFrequency, Integer feeInterval, LocalDate fromDate, boolean isFlat) {
        List<LocalDate> recurrerDates = new ArrayList<>();
        LocalDate seedDate = startDate.plusDays(gracePeriod);
        switch (feeFrequency) {
            case DAYS:
                LocalDate date = fromDate;
                if(isFlat){
                    date = date.plusDays(1);
                }

                while (date.isBefore(toDate)) {
                    if (isFlat) {
                        recurrerDates.add(date);
                    }
                    date = date.plusDays(feeInterval);
                }
                if (isFlat && date.isEqual(toDate)) {
                    recurrerDates.add(date);
                } else if (!isFlat) {
                    recurrerDates.add(fromDate);
                    recurrerDates.add(date);
                }
            break;
            case WEEKS:
                String recurringRule = "FREQ=WEEKLY;INTERVAL=" + feeInterval;
                recurrerDates.addAll(CalendarUtils.getRecurringDatesWithNoLimit(recurringRule, seedDate, seedDate, toDate.plusDays(1)));
            break;
            case MONTHS:
                String recurringRuleMonthly = "FREQ=MONTHLY;INTERVAL=" + feeInterval;
                recurrerDates.addAll(CalendarUtils.getRecurringDatesWithNoLimit(recurringRuleMonthly, seedDate, seedDate, toDate.plusDays(1)));
            break;
            case YEARS:
                String recurringRuleYearly = "FREQ=YEARLY;INTERVAL=" + feeInterval;
                recurrerDates.addAll(CalendarUtils.getRecurringDatesWithNoLimit(recurringRuleYearly, seedDate, seedDate, toDate.plusDays(1)));
            break;
            case SAME_AS_REPAYMENT_PERIOD:
                for (LocalDate scheduleDate : repaymentScheduledates) {
                    if (!scheduleDate.isAfter(toDate)) {
                        recurrerDates.add(scheduleDate);
                    }
                }
            break;

            default:
            break;
        }
        return recurrerDates;
    }
    
    public static LocalDate retriveNextRecurrenceDate(LocalDate startDate, LocalDate date, LocalDate nextScheduleDate, int gracePeriod,
            LoanPeriodFrequencyType feeFrequency, Integer feeInterval) {
        LocalDate recurrerDates = date;
        LocalDate seedDate = startDate.plusDays(gracePeriod);
        switch (feeFrequency) {
            case DAYS:
                recurrerDates = date;
            break;
            case WEEKS:
                String recurringRule = "FREQ=WEEKLY;INTERVAL=" + feeInterval;
                if (!CalendarUtils.isValidRecurringDate(recurringRule, seedDate, seedDate)) {
                    recurrerDates = CalendarUtils.getNextRecurringDate(recurringRule, seedDate, date);
                }
            break;
            case MONTHS:
                String recurringRuleMonthly = "FREQ=MONTHLY;INTERVAL=" + feeInterval;
                if (!CalendarUtils.isValidRecurringDate(recurringRuleMonthly, seedDate, seedDate)) {
                    recurrerDates = CalendarUtils.getNextRecurringDate(recurringRuleMonthly, seedDate, date);
                }
            break;
            case YEARS:
                String recurringRuleYearly = "FREQ=YEARLY;INTERVAL=" + feeInterval;
                if (!CalendarUtils.isValidRecurringDate(recurringRuleYearly, seedDate, seedDate)) {
                    recurrerDates = CalendarUtils.getNextRecurringDate(recurringRuleYearly, seedDate, date);
                }
            break;
            case SAME_AS_REPAYMENT_PERIOD:
                recurrerDates = nextScheduleDate;
            break;

            default:
            break;
        }
        return recurrerDates;
    }

    public static LocalDate retrivePreviosRecurringDate(LocalDate date, LoanPeriodFrequencyType feeFrequency, Integer feeInterval) {
        LocalDate previousDate = date;
        switch (feeFrequency) {
            case WEEKS:
                previousDate = date.minusWeeks(feeInterval);
            break;
            case MONTHS:
                previousDate = date.minusMonths(feeInterval);
            break;
            case YEARS:
                previousDate = date.minusYears(feeInterval);
            break;
            default:
            break;
        }
        return previousDate;
    }

}
