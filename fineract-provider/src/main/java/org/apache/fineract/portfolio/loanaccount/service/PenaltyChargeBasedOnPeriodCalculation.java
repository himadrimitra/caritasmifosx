package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.charge.service.ChargeUtils;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.DefaultPaymentPeriodsInOneYearCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PaymentPeriodsInOneYearCalculator;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.Days;

public class PenaltyChargeBasedOnPeriodCalculation implements PenaltyChargeCalculator {

    final PaymentPeriodsInOneYearCalculator paymentPeriodsInOneYearCalculator = new DefaultPaymentPeriodsInOneYearCalculator();

    @Override
    public BigDecimal calculateCharge(PenaltyPeriod penaltyPeriod) {
        double percentage = ChargeUtils.retriveChargePercentage(penaltyPeriod.getFrequencyType().getValue(), penaltyPeriod.getPercentageOrAmount())/ PenaltyChargeCalculator.PERCENTAGE_DIVIDEND;
        int numberOfDays = Days.daysBetween(penaltyPeriod.getStartDate(), penaltyPeriod.getPostingDate()).getDays();
        int actualDays = Days.daysBetween(penaltyPeriod.getActualStartDate(), penaltyPeriod.getActualEndDate()).getDays();
        double amount = percentage * penaltyPeriod.getOutstanding().getAmount().doubleValue();
        amount = amount * numberOfDays / actualDays;
        return new BigDecimal(amount);
    }

}
