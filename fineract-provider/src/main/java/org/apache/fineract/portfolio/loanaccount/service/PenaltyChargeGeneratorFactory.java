package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;


public class PenaltyChargeGeneratorFactory {
    
    public PenaltyChargeCalculator findPenaltyChargeCalculator(LoanRecurringCharge recurringCharge) {
        PenaltyChargeCalculator calculator = null;
        if (recurringCharge.getChargeCalculation().isFlat()) {
            calculator = new PenaltyChargeBasedOnFlatAmountCalculation();
        } else if (recurringCharge.getPercentageType().isFlat()) {
            calculator = new PenaltyChargeBasedOnFlatPercentageCalculation();
        } else {
            if (recurringCharge.getPercentagePeriodType().isDaily()) {
                calculator = new PenaltyChargeBasedOnDailyCalculation();
            } else {
                switch (recurringCharge.getFeeFrequency()) {
                    case DAYS:
                    case SAME_AS_REPAYMENT_PERIOD:
                        calculator = new PenaltyChargeBasedOnDailyCalculation();
                    break;
                    default:
                        calculator = new PenaltyChargeBasedOnPeriodCalculation();
                    break;
                }
            }
        }

        return calculator;
    }
    
    public PenaltyChargePeriodGenerator findPenaltyChargePeriodGenerator(LoanRecurringCharge recurringCharge, boolean isOnCurrentOutStanding){
        PenaltyChargePeriodGenerator periodGenerator = null;
        if(recurringCharge.getFeeFrequency().isInvalid()){
            periodGenerator = new PenaltyPeriodGeneratorForOneTimeCharge();
        }else if(isOnCurrentOutStanding){
            periodGenerator = new PenaltyPeriodGeneratorForCurrentOuststanding();
        }else{
            periodGenerator = new PenaltyPeriodGeneratorForAverageOuststanding();
        }
        return periodGenerator;
    }

}
