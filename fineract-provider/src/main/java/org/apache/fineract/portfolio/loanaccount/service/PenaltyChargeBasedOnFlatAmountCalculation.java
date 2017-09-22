package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;

public class PenaltyChargeBasedOnFlatAmountCalculation implements PenaltyChargeCalculator {

    @Override
    public Money calculateCharge(PenaltyPeriod penaltyPeriod) {
        return Money.of(penaltyPeriod.getOutstanding().getCurrency(), new BigDecimal(penaltyPeriod.getPercentageOrAmount()));
    }

}
