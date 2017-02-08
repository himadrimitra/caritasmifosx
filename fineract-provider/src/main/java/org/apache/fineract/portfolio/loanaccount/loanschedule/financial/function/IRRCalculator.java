package org.apache.fineract.portfolio.loanaccount.loanschedule.financial.function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.calendar.domain.CalendarFrequencyType;
import org.apache.fineract.portfolio.calendar.service.CalendarUtils;
import org.apache.fineract.portfolio.loanaccount.data.DisbursementData;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.LoanApplicationTerms;
import org.joda.time.LocalDate;

public class IRRCalculator {
    
    public static double calculateIrr(final LoanApplicationTerms terms){
        BigDecimal flatInterestRate = terms.getFlatInterestRate();
        int numberOfRepayments = terms.getNumberOfRepayments();
        Map<LocalDate, Money> disbursements = new HashMap<>();
        MonetaryCurrency currency = terms.getPrincipal().getCurrency();
        if(terms.isMultiDisburseLoan()){
            for (DisbursementData disbursementData : terms.getDisbursementDatas()) {
                disbursements.put(disbursementData.disbursementDate(),Money.of(currency,disbursementData.getPrincipal()));
            }
        }else{
            disbursements.put(terms.getExpectedDisbursementDate(), terms.getPrincipal());
        }
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
        return calculateIrr(flatInterestRate, numberOfRepayments, disbursements, currency, mc, calendarStartDate, recurrence, firstRepaymentDate);
        
        
    }
    

    public static double calculateIrr(final BigDecimal flatInterestRate, final int numberOfRepayments, Map<LocalDate, Money> disbursements,
            final MonetaryCurrency currency, final MathContext mc, final LocalDate seedDate, final String recurrence,
            final LocalDate firstRepaymentDate) {
        final BigDecimal divisor = BigDecimal.valueOf(Double.valueOf("100.0"));
        final Map<LocalDate, Money> values = new TreeMap<>();
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        for (Map.Entry<LocalDate, Money> disbursement : disbursements.entrySet()) {
            values.put(disbursement.getKey(), disbursement.getValue().negated());
            totalPrincipal = totalPrincipal.add(disbursement.getValue().getAmount());
        }

        BigDecimal totalInterest = totalPrincipal.multiply(flatInterestRate).divide(divisor, mc);
        BigDecimal emi = totalPrincipal.add(totalInterest).divide(BigDecimal.valueOf(numberOfRepayments), mc);
        Money emiMoney = Money.of(currency, emi);
        addToMap(values, firstRepaymentDate, emiMoney);
        LocalDate dueDate = firstRepaymentDate;
        for (int i = 1; i < numberOfRepayments; i++) {
            dueDate = CalendarUtils.getNextRecurringDate(recurrence, seedDate, dueDate);
            addToMap(values, dueDate, emiMoney);
        }

        double[] dates = new double[values.size()];
        double[] amounts = new double[values.size()];
        int i = 0;
        for (Map.Entry<LocalDate, Money> entry : values.entrySet()) {
            dates[i] = XIRRData.getExcelDateValue( new GregorianCalendar(entry.getKey().getYear(), entry.getKey().getMonthOfYear()-1, entry.getKey().getDayOfMonth()) );
            amounts[i] = entry.getValue().getAmount().doubleValue();
            i++;
        }

        XIRRData data = new XIRRData(values.size(), 0.1, amounts, dates);
        double xirrValue = XIRR.xirr( data );
        double dividend = (double)1/12;
        double XIRRpow = Math.pow(xirrValue,dividend);
        double monthlyRate =  XIRRpow - 1;
        double annumRate = monthlyRate * 12 ;
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
