package org.apache.fineract.portfolio.loanaccount.service;

import java.util.Map;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.LocalDate;

public class PenaltyPeriodGeneratorForCurrentOuststanding extends PenaltyChargePeriodGenerator {

    @Override
    public void createPeriods(LoanRecurringCharge recurringCharge, LoanOverdueCalculationDTO overdueCalculationDetail,
            Map<LocalDate, PenaltyPeriod> penaltyPeriods, LocalDate endDate, LocalDate recurrerDate, LocalDate chargeApplicableFromDate,
            LocalDate actualEndDate) {
        Money amountForChargeCalculation = findAmountBasedOnChargeCalculationType(recurringCharge.getChargeCalculation(),
                overdueCalculationDetail.getPrincipalOutstingAsOnDate(), overdueCalculationDetail.getInterestOutstingAsOnDate(),
                overdueCalculationDetail.getChargeOutstingAsOnDate());
        if (amountForChargeCalculation.isGreaterThanOrEqualTo(recurringCharge.getChargeOverueDetail().getMinOverdueAmountRequired(
                overdueCalculationDetail.getCurrency()))) {
            if(penaltyPeriods.containsKey(endDate)){
                penaltyPeriods.get(endDate).setNumberofTimes(penaltyPeriods.get(endDate).getNumberofTimes() + 1);
            } else {
                PenaltyPeriod penaltyPeriod = new PenaltyPeriod(recurringCharge.getAmount().doubleValue(), recurrerDate, actualEndDate,
                        amountForChargeCalculation, chargeApplicableFromDate, endDate, recurringCharge.getFeeFrequency());
                penaltyPeriods.put(endDate, penaltyPeriod);
            }
        }

        for (LocalDate date : overdueCalculationDetail.getDatesForOverdueAmountChange()) {
            if (occursOnDayFromAndUpToAndIncluding(chargeApplicableFromDate, endDate, date)) {
                handleReversalOfTransactionForOverdueCalculation(overdueCalculationDetail, date);
                handleReversalOfOverdueInstallment(overdueCalculationDetail, date);
            }
            if (!date.isAfter(chargeApplicableFromDate)) {
                break;
            }

        }
    }

}
