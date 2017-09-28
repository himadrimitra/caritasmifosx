package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.charge.service.ChargeUtils;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultPaymentPeriodsInOneYearCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PaymentPeriodsInOneYearCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.Days;

public class PenaltyChargeBasedOnDailyCalculation implements PenaltyChargeCalculator {

    final PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();

    @Override
    public BigDecimal calculateCharge(PenaltyPeriod penaltyPeriod) {
        double percentage = ChargeUtils.retriveChargePercentage(PeriodFrequencyType.DAYS.getValue(), penaltyPeriod.getPercentageOrAmount())
                / PenaltyChargeCalculator.PERCENTAGE_DIVIDEND;
        int numberOfDays = Days.daysBetween(penaltyPeriod.getStartDate(), penaltyPeriod.getPostingDate()).getDays();
        double amount = percentage * penaltyPeriod.getOutstanding().getAmount().doubleValue() * numberOfDays;
        return new BigDecimal(amount);
    }

}
