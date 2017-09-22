package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;


public class PenaltyChargeBasedOnFlatPercentageCalculation implements PenaltyChargeCalculator {
    
    @Override
    public Money calculateCharge(PenaltyPeriod penaltyPeriod) {
        double amount = (penaltyPeriod.getPercentageOrAmount() / PenaltyChargeCalculator.PERCENTAGE_DIVIDEND ) * penaltyPeriod.getOutstanding().getAmount().doubleValue();
        return Money.of(penaltyPeriod.getOutstanding().getCurrency(), new BigDecimal(amount));
    }

}
