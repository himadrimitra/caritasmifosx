/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.organisation.monetary.domain.MoneyHelper;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargePaidBy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionToRepaymentScheduleMapping;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.AbstractLoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.LoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.service.LoanUtilService;
import org.joda.time.LocalDate;

/**
 * Old style {@link LoanRepaymentScheduleTransactionProcessor}.
 * 
 * For ALL types of transactions, pays off components in order of interest, then
 * principal.
 * 
 * Other formulas exist on fineract where you can choose 'Declining-Balance
 * Interest Recalculation' which simply means, recalculate the interest
 * component based on the how much principal is outstanding at a point in time;
 * but this isnt trying to model that option only the basic one for now.
 */
@SuppressWarnings("unused")
public class FineractStyleLoanRepaymentScheduleTransactionProcessor extends AbstractLoanRepaymentScheduleTransactionProcessor {

    @Override
    protected boolean isTransactionInAdvanceOfInstallment(final int currentInstallmentIndex,
            final List<LoanRepaymentScheduleInstallment> installments, final LocalDate transactionDate) {

        final LoanRepaymentScheduleInstallment currentInstallment = installments.get(currentInstallmentIndex);

        return transactionDate.isBefore(currentInstallment.getDueDate());
    }

    /**
     * For early/'in advance' repayments, pay off in the same way as on-time
     * payments, interest first then principal.
     */
    @Override
    protected Money handleTransactionThatIsPaymentInAdvanceOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final LocalDate transactionDate, final Money paymentInAdvance,
            List<LoanTransactionToRepaymentScheduleMapping> transactionMappings) {

        return handleTransactionThatIsOnTimePaymentOfInstallment(currentInstallment, loanTransaction, paymentInAdvance, transactionMappings);
    }

    /**
     * For late repayments, pay off in the same way as on-time payments,
     * interest first then principal.
     */
    @Override
    protected Money handleTransactionThatIsALateRepaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final Money transactionAmountUnprocessed, List<LoanTransactionToRepaymentScheduleMapping> transactionMappings) {

        return handleTransactionThatIsOnTimePaymentOfInstallment(currentInstallment, loanTransaction, transactionAmountUnprocessed,
                transactionMappings);
    }

    /**
     * For normal on-time repayments, pays off interest first, then principal.
     */
    @Override
    protected Money handleTransactionThatIsOnTimePaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final LoanTransaction loanTransaction, final Money transactionAmountUnprocessed,
            List<LoanTransactionToRepaymentScheduleMapping> transactionMappings) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
        Money transactionAmountRemaining = transactionAmountUnprocessed;
        Money principalPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money interestPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money feeChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money penaltyChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());

        if (loanTransaction.isChargesWaiver()) {
            Money penaltyPortion = loanTransaction.getPenaltyChargesPortion(currency);
            Money feePortion = loanTransaction.getFeeChargesPortion(currency);
            for (LoanChargePaidBy loanChargePaidBy : loanTransaction.getLoanChargesPaidForProcessing()) {
                if (loanChargePaidBy.getLoanCharge().isPenaltyCharge()) {
                    penaltyPortion = loanTransaction.getAmount(currency);
                } else {
                    feePortion = loanTransaction.getAmount(currency);
                }
                break;
            }
            penaltyChargesPortion = currentInstallment.waivePenaltyChargesComponent(transactionDate, penaltyPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);

            feeChargesPortion = currentInstallment.waiveFeeChargesComponent(transactionDate, feePortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);

        } else if (loanTransaction.isInterestWaiver()) {
            interestPortion = currentInstallment.waiveInterestComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        } else if (loanTransaction.isChargePayment()) {
            if (loanTransaction.isPenaltyPayment()) {
                penaltyChargesPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
                transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);
            } else {
                feeChargesPortion = currentInstallment.payFeeChargesComponent(transactionDate, transactionAmountRemaining);
                transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);
            }
            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        } else {
            penaltyChargesPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);

            feeChargesPortion = currentInstallment.payFeeChargesComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);

            interestPortion = currentInstallment.payInterestComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            principalPortion = currentInstallment.payPrincipalComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        }
        if (principalPortion.plus(interestPortion).plus(feeChargesPortion).plus(penaltyChargesPortion).isGreaterThanZero()) {
            transactionMappings.add(LoanTransactionToRepaymentScheduleMapping.createFrom(currentInstallment, principalPortion,
                    interestPortion, feeChargesPortion, penaltyChargesPortion));
        }
        return transactionAmountRemaining;
    }
    
    @Override
    protected Money handleTransactionThatIsOnTimePaymentOfInstallmentForGlim(final LoanRepaymentScheduleInstallment currentInstallment,
            final LoanTransaction loanTransaction, final Money transactionAmountUnprocessed,
            List<LoanTransactionToRepaymentScheduleMapping> transactionMappings, Money principalPortion, Money interestPortion,
            Money feePortion, Money penaltyPortion) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
        Money transactionAmountRemaining = transactionAmountUnprocessed;

        if (loanTransaction.isChargesWaiver()) {
            penaltyPortion = currentInstallment.waivePenaltyChargesComponent(transactionDate,
                    penaltyPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyPortion);

            feePortion = currentInstallment
                    .waiveFeeChargesComponent(transactionDate, feePortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(feePortion);

        } else if (loanTransaction.isInterestWaiver()) {
            interestPortion = currentInstallment.waiveInterestComponent(transactionDate, interestPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feePortion, penaltyPortion);
        } else if (loanTransaction.isChargePayment()) {
            if (loanTransaction.isPenaltyPayment()) {
                penaltyPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, penaltyPortion);
                transactionAmountRemaining = transactionAmountRemaining.minus(penaltyPortion);
            } else {
                feePortion = currentInstallment.payFeeChargesComponent(transactionDate, feePortion);
                transactionAmountRemaining = transactionAmountRemaining.minus(feePortion);
            }
            loanTransaction.updateComponents(principalPortion, interestPortion, feePortion, penaltyPortion);
        } else {
            penaltyPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, penaltyPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyPortion);

            feePortion = currentInstallment.payFeeChargesComponent(transactionDate, feePortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(feePortion);

            interestPortion = currentInstallment.payInterestComponent(transactionDate, interestPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);

            principalPortion = currentInstallment.payPrincipalComponent(transactionDate, principalPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);

            loanTransaction.updateComponents(principalPortion, interestPortion, feePortion, penaltyPortion);
        }
        if (principalPortion.plus(interestPortion).plus(feePortion).plus(penaltyPortion).isGreaterThanZero()) {
            transactionMappings.add(LoanTransactionToRepaymentScheduleMapping.createFrom(currentInstallment, principalPortion,
                    interestPortion, feePortion, penaltyPortion));
        }
        return transactionAmountRemaining;
    }

    @Override
    protected void onLoanOverpayment(final LoanTransaction loanTransaction, final Money loanOverPaymentAmount) {
        // TODO - KW - dont do anything with loan over-payment for now
    }

    @Override
    protected Money handleRefundTransactionPaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final LoanTransaction loanTransaction, final Money transactionAmountUnprocessed,
            List<LoanTransactionToRepaymentScheduleMapping> transactionMappings) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
        Money transactionAmountRemaining = transactionAmountUnprocessed;
        Money principalPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money interestPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money feeChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());
        Money penaltyChargesPortion = Money.zero(transactionAmountRemaining.getCurrency());

        principalPortion = currentInstallment.unpayPrincipalComponent(transactionDate, transactionAmountRemaining);
        transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);

        if (transactionAmountRemaining.isGreaterThanZero()) {
            interestPortion = currentInstallment.unpayInterestComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);
        }

        if (transactionAmountRemaining.isGreaterThanZero()) {
            feeChargesPortion = currentInstallment.unpayFeeChargesComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);
        }

        if (transactionAmountRemaining.isGreaterThanZero()) {
            penaltyChargesPortion = currentInstallment.unpayPenaltyChargesComponent(transactionDate, transactionAmountRemaining);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);
        }
        loanTransaction.updateComponents(principalPortion, interestPortion, feeChargesPortion, penaltyChargesPortion);
        if (principalPortion.plus(interestPortion).plus(feeChargesPortion).plus(penaltyChargesPortion).isGreaterThanZero()) {
            transactionMappings.add(LoanTransactionToRepaymentScheduleMapping.createFrom(currentInstallment, principalPortion,
                    interestPortion, feeChargesPortion, penaltyChargesPortion));
        }
        return transactionAmountRemaining;
    }

    @Override
    protected void handleGLIMRepaymentInstallment(GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction,
            Money installmentAmount, Money principalPortion, Money interestPortion, Money feeChargesPortion, Money penaltyChargesPortion) {

        Money transactionAmountUnprocessed = installmentAmount;
        Money transactionAmountRemaining = transactionAmountUnprocessed;
        final MonetaryCurrency currency = transactionAmountUnprocessed.getCurrency();
        Money tempPrincipalPortion = Money.zero(currency);
        Money tempInterestPortion = Money.zero(currency);
        Money tempFeeChargesPortion = Money.zero(currency);
        Money tempPenaltyChargesPortion = Money.zero(currency);

        if (transactionAmountRemaining.isGreaterThanZero()) {
            tempPenaltyChargesPortion = LoanUtilService.deductGivenComponent(transactionAmountRemaining, penaltyChargesPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(penaltyChargesPortion);
        }

        if (transactionAmountRemaining.isGreaterThanZero()) {
            tempFeeChargesPortion = LoanUtilService.deductGivenComponent(transactionAmountRemaining, feeChargesPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(feeChargesPortion);
        }

        if (transactionAmountRemaining.isGreaterThanZero()) {
            tempInterestPortion = LoanUtilService.deductGivenComponent(transactionAmountRemaining, interestPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(interestPortion);
        }

        if (transactionAmountRemaining.isGreaterThanZero()) {
            tempPrincipalPortion = LoanUtilService.deductGivenComponent(transactionAmountRemaining, principalPortion);
            transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);
        }
        groupLoanIndividualMonitoringTransaction.updateComponents(tempPrincipalPortion.getAmount(), tempInterestPortion.getAmount(),
                tempFeeChargesPortion.getAmount(), tempPenaltyChargesPortion.getAmount(), installmentAmount.getAmount());
    }

    @Override
    public boolean isFullPeriodInterestToBeCollectedForLatePaymentsAfterLastInstallment() {
        // TODO Auto-generated method stub
        return false;
    }
}