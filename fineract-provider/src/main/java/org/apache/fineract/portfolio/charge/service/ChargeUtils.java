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

    public static List<LocalDate> retriveRecurrencePeriods(LocalDate startDate, LocalDate toDate,
            List<LocalDate> repaymentScheduledates, int gracePeriod, LoanPeriodFrequencyType feeFrequency, Integer feeInterval, LocalDate fromDate) {
        List<LocalDate> recurrerDates = new ArrayList<>();
        LocalDate seedDate = startDate.plusDays(gracePeriod);
        switch (feeFrequency) {
            case DAYS:
                recurrerDates.add(fromDate);
                recurrerDates.add(toDate);
                break;
            case WEEKS:
                String recurringRule = "FREQ=WEEKLY;INTERVAL=" + feeInterval;
                recurrerDates.addAll(CalendarUtils.getRecurringDates(recurringRule, seedDate, seedDate, toDate));
            break;
            case MONTHS:
                String recurringRuleMonthly = "FREQ=MONTHLY;INTERVAL=" + feeInterval;
                recurrerDates.addAll(CalendarUtils.getRecurringDates(recurringRuleMonthly, seedDate, seedDate, toDate));
            break;
            case YEARS:
                String recurringRuleYearly = "FREQ=YEARLY;INTERVAL=" + feeInterval;
                recurrerDates.addAll(CalendarUtils.getRecurringDates(recurringRuleYearly, seedDate, seedDate, toDate));
            break;
            case SAME_AS_REPAYMENT_PERIOD:
                for (LocalDate date : repaymentScheduledates) {
                    if(!date.isAfter(toDate)){
                        recurrerDates.add(date);    
                    }
                }
            break;

            default:
            break;
        }
        return recurrerDates;
    }
    
    
    public static LocalDate retrivePreviosRecurringDate(LocalDate date,LoanPeriodFrequencyType feeFrequency, Integer feeInterval){
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
            default:
            break;
        }
        return previousDate;
    }

}
