package org.apache.fineract.portfolio.loanaccount.service;

import java.util.Map;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.domain.ChargeCalculationType;
import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRecurringCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.loanschedule.domain.PenaltyPeriod;
import org.joda.time.LocalDate;

public abstract class PenaltyChargePeriodGenerator {

   abstract void createPeriods(LoanRecurringCharge recurringCharge, LoanOverdueCalculationDTO overdueCalculationDetail,
            Map<LocalDate,PenaltyPeriod> penaltyPeriods, LocalDate endDate, LocalDate recurrerDate, LocalDate chargeApplicableFromDate, LocalDate actualEndDate);

    public boolean occursOnDayFromAndUpToAndIncluding(final LocalDate fromNotInclusive, final LocalDate upToAndInclusive,
            final LocalDate target) {
        return target != null && target.isAfter(fromNotInclusive) && !target.isAfter(upToAndInclusive);
    }

    public Money findAmountBasedOnChargeCalculationType(ChargeCalculationType type, Money principalOutstingAsOnDate,
            Money interestOutstingAsOnDate, Money chargeOutstingAsOnDate) {
        Money amount = principalOutstingAsOnDate.zero();
        switch (type) {
            case PERCENT_OF_AMOUNT:
                amount = principalOutstingAsOnDate;
            break;
            case PERCENT_OF_AMOUNT_AND_INTEREST:
                amount = principalOutstingAsOnDate.plus(interestOutstingAsOnDate);
            break;
            case PERCENT_OF_AMOUNT_INTEREST_AND_FEES:
            case FLAT:
                amount = principalOutstingAsOnDate.plus(interestOutstingAsOnDate).plus(chargeOutstingAsOnDate);
            break;
            default:
            break;
        }

        return amount;
    }

    public void handleReversalOfTransactionForOverdueCalculation(final LoanOverdueCalculationDTO overdueCalculationDetail,
            LocalDate date) {
        MonetaryCurrency currency = overdueCalculationDetail.getCurrency();
        if (overdueCalculationDetail.getPaymentTransactions().containsKey(date)) {
            LoanTransaction transaction = overdueCalculationDetail.getPaymentTransactions().get(date);
            if(transaction.isRefund() || transaction.isRefundForActiveLoan()){
                overdueCalculationDetail.plusPrincipalPaidAfterOnDate(transaction.getPrincipalPortion());
                overdueCalculationDetail.plusInterestPaidAfterOnDate(transaction.getInterestPortion());
                overdueCalculationDetail.plusChargePaidAfterOnDate(transaction.getPenaltyChargesPortion(currency).plus(
                        transaction.getFeeChargesPortion()));
            } else {
                overdueCalculationDetail.minusPrincipalPaidAfterOnDate(transaction.getPrincipalPortion());
                overdueCalculationDetail.minusInterestPaidAfterOnDate(transaction.getInterestPortion());
                overdueCalculationDetail.minusChargePaidAfterOnDate(transaction.getPenaltyChargesPortion(currency).plus(
                        transaction.getFeeChargesPortion()));
                if (transaction.isInterestWaiver()) {
                    overdueCalculationDetail.minusInterestPaidAfterOnDate(transaction.getUnrecognizedIncomePortion(currency));
                } else if (transaction.isChargesWaiver()) {
                    overdueCalculationDetail.minusChargePaidAfterOnDate(transaction.getUnrecognizedIncomePortion(currency));
                }
            }
            
            
            if (overdueCalculationDetail.getPrincipalPaidAfterOnDate().isLessThanZero()) {
                overdueCalculationDetail.plusPrincipalOutstingAsOnDate(overdueCalculationDetail.getPrincipalPaidAfterOnDate().negated());
                overdueCalculationDetail.resetPrincipalPaidAfterOnDate();
            }

            if (overdueCalculationDetail.getInterestPaidAfterOnDate().isLessThanZero()) {
                overdueCalculationDetail.plusInterestOutstingAsOnDate(overdueCalculationDetail.getInterestPaidAfterOnDate().negated());
                overdueCalculationDetail.resetInterestPaidAfterOnDate();
            }
            if (overdueCalculationDetail.getChargePaidAfterOnDate().isLessThanZero()) {
                overdueCalculationDetail.plusChargeOutstingAsOnDate(overdueCalculationDetail.getChargePaidAfterOnDate().negated());
                overdueCalculationDetail.resetChargePaidAfterOnDate();
            }
            
        }
    }

    public void handleReversalOfOverdueInstallment(final LoanOverdueCalculationDTO overdueCalculationDetail, LocalDate date) {
        MonetaryCurrency currency = overdueCalculationDetail.getCurrency();
        if (overdueCalculationDetail.getOverdueInstallments().containsKey(date)) {
            LoanRepaymentScheduleInstallment overdueInstallment = overdueCalculationDetail.getOverdueInstallments().get(date);

            overdueCalculationDetail.minusPrincipalOutstingAsOnDate(overdueInstallment.getPrincipal(currency));
            overdueCalculationDetail.minusInterestOutstingAsOnDate(overdueInstallment.getInterestCharged(currency));
            overdueCalculationDetail.minusChargeOutstingAsOnDate(overdueInstallment.getPenaltyChargesCharged(currency).plus(
                    overdueInstallment.getFeeChargesCharged(currency)));
        }
    }

}
