package org.apache.fineract.portfolio.loanaccount.service;

import java.util.Collection;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.LocalDate;

public class PenaltyPeriodGeneratorForOneTimeCharge extends PenaltyChargePeriodGenerator {

    @Override
    public void createPeriods(LoanRecurringCharge recurringCharge, LoanOverdueCalculationDTO overdueCalculationDetail,
            Collection<PenaltyPeriod> penaltyPeriods, LocalDate endDate, LocalDate recurrerDate, LocalDate chargeApplicableFromDate) {

        MonetaryCurrency currency = overdueCalculationDetail.getCurrency();
        Money principalAmount = Money.zero(currency);
        Money interestAmount = Money.zero(currency);
        Money chargeAmount = Money.zero(currency);
        if (recurringCharge.getChargeCalculation().isPercentageBased()) {
            for (LocalDate date : overdueCalculationDetail.getDatesForOverdueAmountChange()) {
                if (date.isAfter(endDate)) {
                    if (overdueCalculationDetail.getPaymentTransactions().containsKey(date)) {
                        LoanTransaction transaction = overdueCalculationDetail.getPaymentTransactions().get(date);
                        principalAmount = principalAmount.plus(transaction.getPrincipalPortion());
                        interestAmount = interestAmount.plus(transaction.getInterestPortion());
                        chargeAmount = chargeAmount.plus(transaction.getFeeChargesPortion()).plus(transaction.getPenaltyChargesPortion());
                    }
                    if (overdueCalculationDetail.getOverdueInstallments().containsKey(date)) {
                        LoanRepaymentScheduleInstallment installment = overdueCalculationDetail.getOverdueInstallments().get(date);
                        principalAmount = principalAmount.minus(installment.getPrincipalCompleted(currency).plus(
                                installment.getPrincipalWrittenOff(currency)));
                        interestAmount = interestAmount.minus(installment.getInterestPaid(currency)
                                .plus(installment.getInterestWaived(currency)).plus(installment.getInterestWrittenOff(currency)));
                        chargeAmount = chargeAmount.minus((installment.getPenaltyChargesPaid(currency).plus(
                                installment.getPenaltyChargesWaived(currency)).plus(installment.getPenaltyChargesWrittenOff(currency)))
                                .plus(installment.getFeeChargesPaid(currency).plus(installment.getFeeChargesWaived(currency))
                                        .plus(installment.getFeeChargesWrittenOff(currency))));
                    }

                }
                if (!date.isAfter(endDate)) {
                    break;
                }

            }
            if (principalAmount.isLessThanZero()) {
                principalAmount = Money.zero(currency);
            }
            if (interestAmount.isLessThanZero()) {
                interestAmount = Money.zero(currency);
            }
            if (chargeAmount.isLessThanZero()) {
                chargeAmount = Money.zero(currency);
            }
        }
        LoanRepaymentScheduleInstallment overduInstallment = overdueCalculationDetail.getOverdueInstallments().get(endDate);

        Money principalPaidInInstallment = overduInstallment.getPrincipalCompleted(currency).plus(
                overduInstallment.getPrincipalWrittenOff(currency));
        Money interestPaidInInstallment = overduInstallment.getInterestPaid(currency).plus(overduInstallment.getInterestWaived(currency))
                .plus(overduInstallment.getInterestWrittenOff(currency));
        Money chargePaidInInstallment = (overduInstallment.getPenaltyChargesPaid(currency).plus(
                overduInstallment.getPenaltyChargesWaived(currency)).plus(overduInstallment.getPenaltyChargesWrittenOff(currency)))
                .plus(overduInstallment.getFeeChargesPaid(currency).plus(overduInstallment.getFeeChargesWaived(currency))
                        .plus(overduInstallment.getFeeChargesWrittenOff(currency)));

        principalAmount = principalAmount.isGreaterThan(principalPaidInInstallment) ? principalPaidInInstallment : principalAmount;
        interestAmount = interestAmount.isGreaterThan(interestPaidInInstallment) ? interestPaidInInstallment : interestAmount;
        chargeAmount = chargeAmount.isGreaterThan(chargePaidInInstallment) ? chargePaidInInstallment : chargeAmount;
        
        Money amountForChargeCalculation = findAmountBasedOnChargeCalculationType(recurringCharge.getChargeCalculation(),
                overduInstallment.getPrincipalOutstanding(currency).plus(principalAmount),
                overduInstallment.getInterestOutstanding(currency).plus(interestAmount),
                overduInstallment.getFeeChargesOutstanding(currency).plus(overduInstallment.getPenaltyChargesOutstanding(currency))).plus(
                chargeAmount);
        if (amountForChargeCalculation.isGreaterThanOrEqualTo(recurringCharge.getChargeOverueDetail().getMinOverdueAmountRequired(
                overdueCalculationDetail.getCurrency()))
                && chargeApplicableFromDate.isEqual(recurrerDate)) {
            PenaltyPeriod penaltyPeriod = new PenaltyPeriod(recurringCharge.getAmount().doubleValue(), recurrerDate, endDate,
                    amountForChargeCalculation, chargeApplicableFromDate, endDate, recurringCharge.getFeeFrequency());
            penaltyPeriods.add(penaltyPeriod);
        }

    }

}
