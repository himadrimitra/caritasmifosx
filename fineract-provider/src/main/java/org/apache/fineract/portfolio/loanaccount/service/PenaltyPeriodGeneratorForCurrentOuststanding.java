package org.apache.fineract.portfolio.loanaccount.service;

import java.util.Collection;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.LocalDate;

public class PenaltyPeriodGeneratorForCurrentOuststanding extends PenaltyChargePeriodGenerator {

    @Override
    public void createPeriods(LoanRecurringCharge recurringCharge, LoanOverdueCalculationDTO overdueCalculationDetail,
            Collection<PenaltyPeriod> penaltyPeriods, LocalDate endDate, LocalDate recurrerDate, LocalDate chargeApplicableFromDate) {
        Money amountForChargeCalculation = findAmountBasedOnChargeCalculationType(recurringCharge.getChargeCalculation(),
                overdueCalculationDetail.getPrincipalOutstingAsOnDate(), overdueCalculationDetail.getInterestOutstingAsOnDate(),
                overdueCalculationDetail.getChargeOutstingAsOnDate());
        if (amountForChargeCalculation.isGreaterThanOrEqualTo(recurringCharge.getChargeOverueDetail().getMinOverdueAmountRequired(
                overdueCalculationDetail.getCurrency()))) {
            PenaltyPeriod penaltyPeriod = new PenaltyPeriod(recurringCharge.getAmount().doubleValue(), recurrerDate, endDate,
                    amountForChargeCalculation, chargeApplicableFromDate, endDate, recurringCharge.getFeeFrequency());
            penaltyPeriods.add(penaltyPeriod);
        }

        for (LocalDate date : overdueCalculationDetail.getDatesForOverdueAmountChange()) {
            if (occursOnDayFromAndUpToAndIncluding(recurrerDate, endDate, date)) {
                handleReversalOfTransactionForOverdueCalculation(overdueCalculationDetail, date);
                handleReversalOfOverdueInstallment(overdueCalculationDetail, date);
            }
            if (!date.isAfter(chargeApplicableFromDate)) {
                break;
            }

        }
    }

}
