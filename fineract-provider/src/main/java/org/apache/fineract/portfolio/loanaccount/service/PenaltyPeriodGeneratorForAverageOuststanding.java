package org.apache.fineract.portfolio.loanaccount.service;

import java.util.Map;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.LocalDate;

public class PenaltyPeriodGeneratorForAverageOuststanding extends PenaltyChargePeriodGenerator {

    @Override
    public void createPeriods(LoanRecurringCharge recurringCharge, LoanOverdueCalculationDTO overdueCalculationDetail,
            Map<LocalDate,PenaltyPeriod> penaltyPeriods, LocalDate endDate, LocalDate recurrerDate, LocalDate chargeApplicableFromDate) {
        LocalDate currentPeriodEndDate = endDate;
        for (LocalDate date : overdueCalculationDetail.getDatesForOverdueAmountChange()) {
            if (occursOnDayFromAndUpToAndIncluding(recurrerDate, endDate, date)) {
                if(!date.isEqual(currentPeriodEndDate)){
                Money amountForChargeCalculation = findAmountBasedOnChargeCalculationType(recurringCharge.getChargeCalculation(),
                        overdueCalculationDetail.getPrincipalOutstingAsOnDate(), overdueCalculationDetail.getInterestOutstingAsOnDate(),
                        overdueCalculationDetail.getChargeOutstingAsOnDate());
                PenaltyPeriod penaltyPeriod = new PenaltyPeriod(recurringCharge.getAmount().doubleValue(), recurrerDate, endDate,
                        amountForChargeCalculation, date, currentPeriodEndDate, recurringCharge.getFeeFrequency());
                penaltyPeriods.put(currentPeriodEndDate,penaltyPeriod);
                }
                handleReversalOfTransactionForOverdueCalculation(overdueCalculationDetail, date);
                handleReversalOfOverdueInstallment(overdueCalculationDetail, date);
                currentPeriodEndDate = date;
            }
            if (!date.isAfter(chargeApplicableFromDate)) {
                break;
            }
        }
        if (!chargeApplicableFromDate.isEqual(currentPeriodEndDate)) {
            Money amountForChargeCalculation = findAmountBasedOnChargeCalculationType(recurringCharge.getChargeCalculation(),
                    overdueCalculationDetail.getPrincipalOutstingAsOnDate(), overdueCalculationDetail.getInterestOutstingAsOnDate(),
                    overdueCalculationDetail.getChargeOutstingAsOnDate());
            if (amountForChargeCalculation.isGreaterThanOrEqualTo(recurringCharge.getChargeOverueDetail().getMinOverdueAmountRequired(
                    overdueCalculationDetail.getCurrency()))) {
                PenaltyPeriod penaltyPeriod = new PenaltyPeriod(recurringCharge.getAmount().doubleValue(), recurrerDate, endDate,
                        amountForChargeCalculation, chargeApplicableFromDate, currentPeriodEndDate, recurringCharge.getFeeFrequency());
                penaltyPeriods.put(currentPeriodEndDate,penaltyPeriod);
            }
        }
        
        LocalDate lastAppliedOnDate = recurringCharge.getChargeOverueDetail().getLastAppliedOnDate();
        if(lastAppliedOnDate != null && lastAppliedOnDate.isEqual(chargeApplicableFromDate)){
            Integer penaltyFreePeriod = recurringCharge.getChargeOverueDetail().getPenaltyFreePeriod();
            boolean graceApplicableForEachOverdue = recurringCharge.getChargeOverueDetail().getGraceType().isApplyGraceForEachInstallment();
            Integer gracePeriod = recurringCharge.getChargeOverueDetail().getGracePeriod();
            if(graceApplicableForEachOverdue && gracePeriod > penaltyFreePeriod){
                MonetaryCurrency currency = overdueCalculationDetail.getCurrency();
                int diff = gracePeriod - penaltyFreePeriod;
                for (Map.Entry<LocalDate, LoanRepaymentScheduleInstallment> mapEntry : overdueCalculationDetail.getOverdueInstallments().entrySet()) {
                    LocalDate dateAsPerGrace =  mapEntry.getKey().plusDays(diff);
                    if(dateAsPerGrace.isAfter(lastAppliedOnDate) &&  mapEntry.getKey().isBefore(lastAppliedOnDate)){
                        Money amountForChargeCalculation = findAmountBasedOnChargeCalculationType(recurringCharge.getChargeCalculation(),
                                mapEntry.getValue().getPrincipalOutstanding(currency), mapEntry.getValue().getInterestOutstanding(currency),
                                mapEntry.getValue().getFeeChargesOutstanding(currency).plus(mapEntry.getValue().getPenaltyChargesOutstanding(currency)));
                        PenaltyPeriod penaltyPeriod = new PenaltyPeriod(recurringCharge.getAmount().doubleValue(), recurrerDate, endDate,
                                amountForChargeCalculation, mapEntry.getKey(), lastAppliedOnDate, recurringCharge.getFeeFrequency());
                        penaltyPeriods.put(lastAppliedOnDate,penaltyPeriod);
                    }
                    
                }
            }
            
        }
    }

}
