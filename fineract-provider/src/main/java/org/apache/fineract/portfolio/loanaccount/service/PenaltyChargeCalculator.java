package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;

public interface PenaltyChargeCalculator {

    public static final double PERCENTAGE_DIVIDEND = 100;
    public Money calculateCharge(PenaltyPeriod penaltyPeriod);

}
