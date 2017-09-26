package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;

public class PenaltyChargeBasedOnFlatAmountCalculation implements PenaltyChargeCalculator {

    @Override
    public BigDecimal calculateCharge(PenaltyPeriod penaltyPeriod) {
        return  new BigDecimal(penaltyPeriod.getPercentageOrAmount() * penaltyPeriod.getNumberofTimes());
    }

}
