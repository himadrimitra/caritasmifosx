package org.apache.fineract.portfolio.loanaccount.loanschedule.financial.function;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.joda.time.LocalDate;

public class IRRCalculator {

    public static double calculateIrr(final LoanApplicationTerms terms) {
        int numberOfRepayments = terms.getNumberOfRepayments();
        Map<LocalDate, Money> disbursements = terms.getDisbursementsAsMap();
        LocalDate disburseDate = terms.getExpectedDisbursementDate();
        LocalDate firstRepaymentDate = terms.getCalculatedRepaymentsStartingFromLocalDate();
        LocalDate calendarStartDate = firstRepaymentDate;
        if (calendarStartDate == null) {
            calendarStartDate = disburseDate;
        }
        String recurrence = null;
        if (terms.getLoanCalendar() == null) {
            CalendarFrequencyType frequencyType = CalendarFrequencyType.from(terms.getRepaymentPeriodFrequencyType());
            Integer interval = terms.getRepaymentEvery();
            final Integer repeatsOnDay = null;
            final Integer repeatsOnNthDayOfMonth = null;
            final Collection<Integer> repeatsOnDayOfMonth = null;
            recurrence = org.apache.fineract.portfolio.calendar.domain.Calendar.constructRecurrence(frequencyType, interval, repeatsOnDay,
                    repeatsOnNthDayOfMonth, repeatsOnDayOfMonth);
        } else {
            recurrence = terms.getLoanCalendar().getRecurrence();
            calendarStartDate = terms.getLoanCalendar().getStartDateLocalDate();
        }
        final RoundingMode roundingMode = MoneyHelper.getRoundingMode();
        final MathContext mc = new MathContext(8, roundingMode);
        Money emi = terms.calculateEmiWithFlatInterestRate(mc);
        if(terms.collectInterestUpfront()){
            Money totalInterest = terms.calculateInterestWithFlatInterestRate(mc);
            totalInterest = totalInterest.minus(terms.getDiscountOnDisbursalAmount());
            addToMap(disbursements, terms.getExpectedDisbursementDate(), totalInterest.negated());
        }
        return calculateIrr(numberOfRepayments, disbursements, calendarStartDate, recurrence, firstRepaymentDate, emi);

    }

    public static double calculateIrr(final int numberOfRepayments, Map<LocalDate, Money> disbursements, final LocalDate seedDate,
            final String recurrence, final LocalDate firstRepaymentDate, final Money emi) {
        final Map<LocalDate, Money> values = new TreeMap<>();
        for (Map.Entry<LocalDate, Money> disbursement : disbursements.entrySet()) {
            values.put(disbursement.getKey(), disbursement.getValue().negated());
        }

        addToMap(values, firstRepaymentDate, emi);
        LocalDate dueDate = firstRepaymentDate;
        for (int i = 1; i < numberOfRepayments; i++) {
            dueDate = CalendarUtils.getNextRecurringDate(recurrence, seedDate, dueDate);
            addToMap(values, dueDate, emi);
        }

        double[] dates = new double[values.size()];
        double[] amounts = new double[values.size()];
        int i = 0;
        for (Map.Entry<LocalDate, Money> entry : values.entrySet()) {
            dates[i] = XIRRData.getExcelDateValue(new GregorianCalendar(entry.getKey().getYear(), entry.getKey().getMonthOfYear() - 1,
                    entry.getKey().getDayOfMonth()));
            amounts[i] = entry.getValue().getAmount().doubleValue();
            i++;
        }

        XIRRData data = new XIRRData(values.size(), 1.1, amounts, dates);
        double xirrValue = XIRR.xirr(data);
        double dividend = (double) 1 / 12;
        double XIRRpow = Math.pow(xirrValue, dividend);
        double monthlyRate = XIRRpow - 1;
        double annumRate = monthlyRate * 12;
        return annumRate * 100;
    }

    private static void addToMap(final Map<LocalDate, Money> map, final LocalDate date, final Money amount) {
        Money value = amount;
        if (map.containsKey(date)) {
            value = value.plus(map.get(date));
        }
        map.put(date, value);
    }

}
