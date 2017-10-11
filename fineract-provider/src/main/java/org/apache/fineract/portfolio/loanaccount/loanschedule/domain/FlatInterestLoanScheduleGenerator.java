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
package org.apache.fineract.portfolio.loanaccount.loanschedule.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.loanaccount.data.LoanTermVariationsData;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTermVariationType;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringAssembler;
import org.joda.time.LocalDate;

public class FlatInterestLoanScheduleGenerator extends AbstractLoanScheduleGenerator {

    @Override
    public PrincipalInterest calculatePrincipalInterestComponentsForPeriod(final PaymentPeriodsInOneYearCalculator calculator,
            final double interestCalculationGraceOnRepaymentPeriodFraction, final Money totalCumulativePrincipal,
            Money totalCumulativeInterest, Money totalInterestDueForLoan, final Money cumulatingInterestPaymentDueToGrace,
            final Money outstandingBalance, final LoanApplicationTerms loanApplicationTerms, final int periodNumber, final MathContext mc,
            @SuppressWarnings("unused") TreeMap<LocalDate, Money> principalVariation,
            @SuppressWarnings("unused") Map<LocalDate, Money> compoundingMap, LocalDate periodStartDate, LocalDate periodEndDate,
            @SuppressWarnings("unused") Collection<LoanTermVariationsData> termVariations) {
        
        Money principalForThisInstallment = loanApplicationTerms.calculateTotalPrincipalForPeriod(calculator, outstandingBalance,
                periodNumber, mc, null);
        
        final PrincipalInterest result = loanApplicationTerms.calculateTotalInterestForPeriod(calculator,
                interestCalculationGraceOnRepaymentPeriodFraction, periodNumber, mc, cumulatingInterestPaymentDueToGrace,
                outstandingBalance, periodStartDate, periodEndDate);
        Money interestForThisInstallment = result.interest();

        if (loanApplicationTerms.getTotalInterestDue() == null) {
            loanApplicationTerms.updateTotalInterestDue(totalInterestDueForLoan);
        }

        // EMI rounding logic
        Collection<LoanTermVariationType> variations = new ArrayList<>();
        variations.add(LoanTermVariationType.EMI_AMOUNT);
        variations.add(LoanTermVariationType.PRINCIPAL_AMOUNT);
        if (!loanApplicationTerms.getLoanTermVariations().hasVariations(variations)) {
            Money emiAmount = principalForThisInstallment.plus(interestForThisInstallment);
            double roundedEmiAmount = loanApplicationTerms.roundInstallmentInMultiplesOf(emiAmount.getAmount().doubleValue());
            if(loanApplicationTerms.isGlim()){
                principalForThisInstallment = Money.of(loanApplicationTerms.getCurrency(), loanApplicationTerms.getFixedEmiAmount().subtract(interestForThisInstallment.getAmount())); 
            }else{
                if (!loanApplicationTerms.isPrincipalGraceApplicableForThisPeriod(periodNumber)
                        && !loanApplicationTerms.isInterestPaymentGraceApplicableForThisPeriod(periodNumber)
                        && !(periodNumber == 1 && loanApplicationTerms.getBrokenPeriodMethod().isAdjustmentInFirstEMI())) {
                    loanApplicationTerms.setFixedEmiAmount(BigDecimal.valueOf(roundedEmiAmount));
                }
                if (loanApplicationTerms.isPrincipalGraceApplicableForThisPeriod(periodNumber)) {
                    interestForThisInstallment = interestForThisInstallment.minus(emiAmount.minus(BigDecimal.valueOf(roundedEmiAmount)));
                } else {
                    principalForThisInstallment = principalForThisInstallment.minus(emiAmount.minus(BigDecimal.valueOf(roundedEmiAmount)));
                }
            }
            

            // First EMI Rounding logic
            if (loanApplicationTerms.isAdjustFirstEMIAmount() && periodNumber == 1) {

                Money interestForInstallmentBeforeRounding = interestForThisInstallment;
                Money principalBeforeRounding = principalForThisInstallment;
                Money roundingDiff = principalBeforeRounding.zero();
                // counter to quit the loop
                // for now breaking the loop after 2 times.
                int counter = 0;
                do {
                    interestForInstallmentBeforeRounding = interestForInstallmentBeforeRounding.minus(roundingDiff);
                    principalBeforeRounding = principalBeforeRounding.plus(roundingDiff);
                    principalForThisInstallment = principalBeforeRounding;
                    interestForThisInstallment = interestForInstallmentBeforeRounding;
                    Money interestDiff = loanApplicationTerms.getTotalInterestDue().minus(
                            interestForThisInstallment.multipliedBy(loanApplicationTerms.fetchNumberOfRepaymentsAfterExceptions()));
                    Money principalDiff = loanApplicationTerms.getPrincipal()
                            .minus(principalForThisInstallment.multipliedBy(loanApplicationTerms
                                    .calculateNumberOfRepaymentsWithPrincipalPayment()));

                    int numberOfPrincipalPayments = loanApplicationTerms.calculateNumberOfRepaymentsWithPrincipalPayment();
                    int numberOfRepayments = loanApplicationTerms.fetchNumberOfRepaymentsAfterExceptions();
                    int numberOfInteretPaymentGrace = loanApplicationTerms.getInterestPaymentGrace();
                    if (numberOfRepayments != numberOfPrincipalPayments) {
                        double roundedInterest = loanApplicationTerms.roundInstallmentInMultiplesOf(interestForThisInstallment.getAmount()
                                .doubleValue());
                        Money diff = interestForThisInstallment.minus(BigDecimal.valueOf(roundedInterest));
                        interestDiff = interestDiff.plus(diff.multipliedBy(numberOfRepayments - numberOfPrincipalPayments));
                    }
                    if (numberOfInteretPaymentGrace > 0) {
                        double roundedPrincipal = loanApplicationTerms.roundInstallmentInMultiplesOf(principalForThisInstallment
                                .getAmount().doubleValue());
                        Money diff = principalForThisInstallment.minus(BigDecimal.valueOf(roundedPrincipal));
                        principalDiff = principalDiff.plus(diff.multipliedBy(numberOfInteretPaymentGrace));
                    }

                    interestForThisInstallment = interestForThisInstallment.plus(interestDiff);
                    principalForThisInstallment = principalForThisInstallment.plus(principalDiff);
                    emiAmount = principalForThisInstallment.plus(interestForThisInstallment);
                    BigDecimal firstEmiAmount = loanApplicationTerms.roundAdjustedEmiAmount(emiAmount.getAmount());
                    Money adjustInterest = emiAmount.minus(firstEmiAmount);
                    interestForThisInstallment = interestForThisInstallment.minus(adjustInterest);
                    loanApplicationTerms.updateTotalInterestDue(loanApplicationTerms.getTotalInterestDue().minus(adjustInterest));
                    final PrincipalInterest interestWithInterest = loanApplicationTerms.calculateTotalInterestForPeriod(calculator,
                            interestCalculationGraceOnRepaymentPeriodFraction, periodNumber, mc, cumulatingInterestPaymentDueToGrace,
                            outstandingBalance, periodStartDate, periodEndDate);
                    roundingDiff = interestForInstallmentBeforeRounding.minus(interestWithInterest.interest());
                    counter++;
                } while (!roundingDiff.isZero() && counter < 2);
            }
        }

        // update cumulative fields for principal & interest
        final Money interestBroughtForwardDueToGrace = result.interestPaymentDueToGrace();
        final Money totalCumulativePrincipalToDate = totalCumulativePrincipal.plus(principalForThisInstallment);
        final Money totalCumulativeInterestToDate = totalCumulativeInterest.plus(interestForThisInstallment);

        // adjust if needed
        principalForThisInstallment = loanApplicationTerms.adjustPrincipalIfLastRepaymentPeriod(principalForThisInstallment,
                totalCumulativePrincipalToDate, periodNumber);

        // totalCumulativeInterest from partial schedule generation for multi rescheduling
        /*if (loanApplicationTerms.getPartialTotalCumulativeInterest() != null && loanApplicationTerms.getTotalInterestDue() != null) {
            totalInterestDueForLoan = loanApplicationTerms.getTotalInterestDue();
            totalInterestDueForLoan = totalInterestDueForLoan.plus(loanApplicationTerms.getPartialTotalCumulativeInterest());
        }*/
        interestForThisInstallment = loanApplicationTerms.adjustInterestIfLastRepaymentPeriod(interestForThisInstallment,
                totalCumulativeInterestToDate, loanApplicationTerms.getTotalInterestDue(), periodNumber);
        if(loanApplicationTerms.getFirstFixedEmiAmount() != null){
        		principalForThisInstallment = GroupLoanIndividualMonitoringAssembler.calculatePrincipalAmount(loanApplicationTerms, periodNumber, interestForThisInstallment);
        }
        return new PrincipalInterest(principalForThisInstallment, interestForThisInstallment, interestBroughtForwardDueToGrace);
    }
}