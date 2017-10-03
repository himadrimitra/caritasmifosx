package org.apache.fineract.portfolio.loanaccount.service;

import java.math.BigDecimal;

import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;

public interface PenaltyChargeCalculator {

    public static final double PERCENTAGE_DIVIDEND = 100;
    public BigDecimal calculateCharge(PenaltyPeriod penaltyPeriod);

}
