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
package org.apache.fineract.portfolio.loanaccount.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.joda.time.LocalDate;

/**
 * A wrapper around loan schedule related data exposing needed behaviour by
 * loan.
 */
public class LoanRepaymentScheduleProcessingWrapper {

    public void reprocess(final MonetaryCurrency currency, final LocalDate disbursementDate,
            final List<LoanRepaymentScheduleInstallment> repaymentPeriods, final Set<LoanCharge> loanCharges) {

        Money totalInterest = Money.zero(currency);
        Money totalPrincipal = Money.zero(currency);
        for (final LoanRepaymentScheduleInstallment installment : repaymentPeriods) {
            totalInterest = totalInterest.plus(installment.getInterestCharged(currency));
            totalPrincipal = totalPrincipal.plus(installment.getPrincipal(currency));
        }
        LocalDate startDate = disbursementDate;
        for (final LoanRepaymentScheduleInstallment period : repaymentPeriods) {

            final Money feeChargesDueForRepaymentPeriod = cumulativeFeeChargesDueWithin(startDate, period.getDueDate(), loanCharges,
                    currency, period, totalPrincipal, totalInterest, !period.isRecalculatedInterestComponent(), period.getInstallmentNumber());
            final Money feeChargesWaivedForRepaymentPeriod = cumulativeFeeChargesWaivedWithin(startDate, period.getDueDate(), loanCharges,
                    currency, !period.isRecalculatedInterestComponent(), period.getInstallmentNumber());
            final Money feeChargesWrittenOffForRepaymentPeriod = cumulativeFeeChargesWrittenOffWithin(startDate, period.getDueDate(),
                    loanCharges, currency, !period.isRecalculatedInterestComponent(), period.getInstallmentNumber());

            final Money penaltyChargesDueForRepaymentPeriod = cumulativePenaltyChargesDueWithin(startDate, period.getDueDate(),
                    loanCharges, currency, period, totalPrincipal, totalInterest, !period.isRecalculatedInterestComponent(), period.getInstallmentNumber());
            final Money penaltyChargesWaivedForRepaymentPeriod = cumulativePenaltyChargesWaivedWithin(startDate, period.getDueDate(),
                    loanCharges, currency, !period.isRecalculatedInterestComponent(), period.getInstallmentNumber());
            final Money penaltyChargesWrittenOffForRepaymentPeriod = cumulativePenaltyChargesWrittenOffWithin(startDate,
                    period.getDueDate(), loanCharges, currency, !period.isRecalculatedInterestComponent(), period.getInstallmentNumber());

            period.updateChargePortion(feeChargesDueForRepaymentPeriod, feeChargesWaivedForRepaymentPeriod,
                    feeChargesWrittenOffForRepaymentPeriod, penaltyChargesDueForRepaymentPeriod, penaltyChargesWaivedForRepaymentPeriod,
                    penaltyChargesWrittenOffForRepaymentPeriod);

            startDate = period.getDueDate();
        }
    }

    private Money cumulativeFeeChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd, final Set<LoanCharge> loanCharges,
            final MonetaryCurrency monetaryCurrency, LoanRepaymentScheduleInstallment period, final Money totalPrincipal,
            final Money totalInterest, boolean isInstallmentChargeApplicable, int installmentNumber) {

        Money cumulative = Money.zero(monetaryCurrency);
        Money totalLoanCharges = Money.zero(monetaryCurrency);
        Integer numberOfRepayments = null;

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge() && !loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        BigDecimal amount = BigDecimal.ZERO;
                        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                            amount = amount.add(period.getPrincipal(monetaryCurrency).getAmount()).add(
                                    period.getInterestCharged(monetaryCurrency).getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                            amount = amount.add(period.getInterestCharged(monetaryCurrency).getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()) {
                            numberOfRepayments = loanCharge.getLoan().fetchNumberOfInstallmensAfterExceptions();
                            totalLoanCharges = totalLoanCharges.plus(loanCharge.amount()); 
                        } else {
                            amount = amount.add(period.getPrincipal(monetaryCurrency).getAmount());
                        }
                        if (loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()) {
                            if(loanCharge.getLoan().isGlimPaymentAsGroup()){                                
                                LoanInstallmentCharge loanInstallmentCharge = loanCharge.getInstallmentLoanCharge(installmentNumber);
                                cumulative = cumulative.plus(loanInstallmentCharge.getAmount());
                            }else{                                
                                BigDecimal installmentChargePerClient = BigDecimal.ZERO;
                                installmentChargePerClient = glimInstallmentChargePerClient(period, numberOfRepayments, loanCharge,
                                        installmentChargePerClient);
                                cumulative = cumulative.plus(installmentChargePerClient);
                            }
                        } else {
                            BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                            cumulative = cumulative.plus(loanChargeAmt);
                        }
                    } else if(loanCharge.getChargeCalculation().isSlabBased()){
                    	BigDecimal amount = MathUtility.getInstallmentAmount(loanCharge.amountOrPercentage(), loanCharge.getLoan().fetchNumberOfInstallmensAfterExceptions(), loanCharge.getLoan().getCurrency(), installmentNumber);
                    	cumulative = cumulative.plus(amount);
                    }else {
                        cumulative = cumulative.plus(loanCharge.amountOrPercentage());
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = calculateChargesWithPercentage(totalPrincipal, totalInterest, cumulative, loanCharge);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                } else if (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        cumulative = calculateChargesWithPercentage(totalPrincipal, totalInterest, cumulative, loanCharge);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount());
                    }
                }
            }
        }

        return cumulative;
    }
    
    private BigDecimal glimInstallmentChargePerClient(LoanRepaymentScheduleInstallment period, Integer numberOfRepayments,
            final LoanCharge loanCharge, BigDecimal installmentChargePerClient) {
        for (GroupLoanIndividualMonitoring glim : loanCharge.getLoan().getDefautGlimMembers()) {
            Set<GroupLoanIndividualMonitoringCharge> charges = glim.getGroupLoanIndividualMonitoringCharges();
            for (GroupLoanIndividualMonitoringCharge glimCharge : charges) {
                if (loanCharge.getCharge().getId() == glimCharge.getCharge().getId()) {
                    BigDecimal chargeAmount = glimCharge.getRevisedFeeAmount() == null ? glimCharge.getFeeAmount()
                            : glimCharge.getRevisedFeeAmount();
                    BigDecimal perInstallmentCharge = MathUtility.divide(chargeAmount, numberOfRepayments, loanCharge
                            .getLoan().getCurrency());
                    if (period.getInstallmentNumber() == numberOfRepayments) {
                        installmentChargePerClient = MathUtility.add(
                                installmentChargePerClient,
                                MathUtility.subtract(chargeAmount,
                                        MathUtility.multiply(perInstallmentCharge, numberOfRepayments - 1)));
                    } else {
                        installmentChargePerClient = MathUtility.add(installmentChargePerClient, perInstallmentCharge);
                    }

                }
            }
        }
        return installmentChargePerClient;
    }

    private Money calculateChargesWithPercentage(final Money totalPrincipal, final Money totalInterest, Money cumulative,
            final LoanCharge loanCharge) {
        BigDecimal amount = BigDecimal.ZERO;
        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
            amount = amount.add(totalPrincipal.getAmount()).add(totalInterest.getAmount());
        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
            amount = amount.add(totalInterest.getAmount());
        } else {
            amount = amount.add(totalPrincipal.getAmount());
        }
        BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
        cumulative = cumulative.plus(loanChargeAmt);
        return cumulative;
    }

    private Money cumulativeFeeChargesWaivedWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency currency, boolean isInstallmentChargeApplicable, 
            int installmentNumber) {

        Money cumulative = Money.zero(currency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge() && !loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    LoanInstallmentCharge loanChargePerInstallment = loanCharge.getInstallmentLoanCharge(periodEnd);
                    if (loanChargePerInstallment != null) {
                        cumulative = cumulative.plus(loanChargePerInstallment.getAmountWaived(currency));
                    }
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd) || 
                        (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                    cumulative = cumulative.plus(loanCharge.getAmountWaived(currency));
                } 
            }
        }

        return cumulative;
    }

    private Money cumulativeFeeChargesWrittenOffWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency currency, boolean isInstallmentChargeApplicable, int installmentNumber) {

        Money cumulative = Money.zero(currency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge() && !loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    LoanInstallmentCharge loanChargePerInstallment = loanCharge.getInstallmentLoanCharge(periodEnd);
                    if (loanChargePerInstallment != null) {
                        cumulative = cumulative.plus(loanChargePerInstallment.getAmountWrittenOff(currency));
                    }
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd) || 
                        (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                    cumulative = cumulative.plus(loanCharge.getAmountWrittenOff(currency));
                }
            }
        }

        return cumulative;
    }

    private Money cumulativePenaltyChargesDueWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency currency, LoanRepaymentScheduleInstallment period,
            final Money totalPrincipal, final Money totalInterest, boolean isInstallmentChargeApplicable, int installmentNumber) {

        Money cumulative = Money.zero(currency);
        Money totalLoanCharges = Money.zero(currency);
        Integer numberOfRepayments = null;

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        BigDecimal amount = BigDecimal.ZERO;
                        if (loanCharge.getChargeCalculation().isPercentageOfAmountAndInterest()) {
                            amount = amount.add(period.getPrincipal(currency).getAmount()).add(
                                    period.getInterestCharged(currency).getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfInterest()) {
                            amount = amount.add(period.getInterestCharged(currency).getAmount());
                        } else if (loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()) {
                            numberOfRepayments = loanCharge.getLoan().fetchNumberOfInstallmensAfterExceptions();
                            totalLoanCharges = totalLoanCharges.plus(loanCharge.amount()); 
                        } else {
                            amount = amount.add(period.getPrincipal(currency).getAmount());
                        }
                        if (loanCharge.getChargeCalculation().isPercentageOfDisbursementAmount()) {
                            BigDecimal loanChargeAmount = BigDecimal.valueOf(loanCharge.amount().doubleValue() / numberOfRepayments);
                            cumulative = cumulative.plus(loanChargeAmount);
                        } else {
                            BigDecimal loanChargeAmt = amount.multiply(loanCharge.getPercentage()).divide(BigDecimal.valueOf(100));
                            cumulative = cumulative.plus(loanChargeAmt);
                        }
                    } else {
                        cumulative = cumulative.plus(loanCharge.amountOrPercentage());
                    }
                } else if (loanCharge.isOverdueInstallmentCharge()
                        && loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = cumulative.plus(loanCharge.chargeAmount());
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)
                        && loanCharge.getChargeCalculation().isPercentageBased()) {
                    cumulative = calculateChargesWithPercentage(totalPrincipal, totalInterest, cumulative, loanCharge);
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd)) {
                    cumulative = cumulative.plus(loanCharge.amount());
                } else if (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment) {
                    if (loanCharge.getChargeCalculation().isPercentageBased()) {
                        cumulative = calculateChargesWithPercentage(totalPrincipal, totalInterest, cumulative, loanCharge);
                    } else {
                        cumulative = cumulative.plus(loanCharge.amount());
                    }
                }
            }
        }
        
        if (numberOfRepayments != null && isLastRepaymentPeriod(numberOfRepayments, period.getInstallmentNumber())
                && totalLoanCharges.compareTo(Money.zero(cumulative.getCurrency())) == 1) {
            Money totalGlimCharges = cumulative.multipliedBy(BigDecimal.valueOf(numberOfRepayments.doubleValue()));
            if (totalGlimCharges.compareTo(totalLoanCharges) != BigDecimal.ZERO.intValue()) {
                cumulative = cumulative.minus((totalGlimCharges.minus(totalLoanCharges)));
            }
        }

        return cumulative;
    }

    private Money cumulativePenaltyChargesWaivedWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency currency, boolean isInstallmentChargeApplicable, int installmentNumber) {

        Money cumulative = Money.zero(currency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    LoanInstallmentCharge loanChargePerInstallment = loanCharge.getInstallmentLoanCharge(periodEnd);
                    if (loanChargePerInstallment != null) {
                        cumulative = cumulative.plus(loanChargePerInstallment.getAmountWaived(currency));
                    }
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd) || 
                        (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                    cumulative = cumulative.plus(loanCharge.getAmountWaived(currency));
                }
            }
        }

        return cumulative;
    }

    private Money cumulativePenaltyChargesWrittenOffWithin(final LocalDate periodStart, final LocalDate periodEnd,
            final Set<LoanCharge> loanCharges, final MonetaryCurrency currency, boolean isInstallmentChargeApplicable, int installmentNumber) {

        Money cumulative = Money.zero(currency);

        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                if (loanCharge.isInstalmentFee() && isInstallmentChargeApplicable) {
                    LoanInstallmentCharge loanChargePerInstallment = loanCharge.getInstallmentLoanCharge(periodEnd);
                    if (loanChargePerInstallment != null) {
                        cumulative = cumulative.plus(loanChargePerInstallment.getAmountWrittenOff(currency));
                    }
                } else if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(periodStart, periodEnd) || 
                        (loanCharge.isUpfrontFee() && installmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment)) {
                    cumulative = cumulative.plus(loanCharge.getAmountWrittenOff(currency));
                }
            }
        }

        return cumulative;
    }
    
    protected final boolean isLastRepaymentPeriod(final int numberOfRepayments, final int periodNumber) {
        return periodNumber == numberOfRepayments;
    }
}