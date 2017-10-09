package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;


public class PenaltyChargeBasedOnFlatPercentageCalculation implements PenaltyChargeCalculator {
    
    @Override
    public BigDecimal calculateCharge(PenaltyPeriod penaltyPeriod) {
        double amount = (penaltyPeriod.getPercentageOrAmount() / PenaltyChargeCalculator.PERCENTAGE_DIVIDEND ) * penaltyPeriod.getOutstanding().getAmount().doubleValue();
        return new BigDecimal(amount);
    }

}
