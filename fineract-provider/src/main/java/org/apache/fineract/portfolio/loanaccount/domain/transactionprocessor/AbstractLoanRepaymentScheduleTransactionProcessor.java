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
package org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.organisation.monetary.domain.Money;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;
import org.apache.fineract.portfolio.charge.domain.GroupLoanIndividualMonitoringCharge;
import org.apache.fineract.portfolio.loanaccount.api.MathUtility;
import org.apache.fineract.portfolio.loanaccount.data.LoanChargePaidDetail;
import org.apache.fineract.portfolio.loanaccount.domain.ChangedTransactionDetail;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoring;
import org.apache.fineract.portfolio.loanaccount.domain.GroupLoanIndividualMonitoringTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanChargePaidBy;
import org.apache.fineract.portfolio.loanaccount.domain.LoanDisbursementDetails;
import org.apache.fineract.portfolio.loanaccount.domain.LoanInstallmentCharge;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleProcessingWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionToRepaymentScheduleMapping;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.impl.CreocoreLoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.impl.HeavensFamilyLoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.domain.transactionprocessor.impl.InterestPrincipalPenaltyFeesOrderLoanRepaymentScheduleTransactionProcessor;
import org.apache.fineract.portfolio.loanaccount.exception.ClientAlreadyWriteOffException;
import org.apache.fineract.portfolio.loanaccount.service.GroupLoanIndividualMonitoringTransactionAssembler;
import org.joda.time.LocalDate;

/**
 * Abstract implementation of {@link LoanRepaymentScheduleTransactionProcessor}
 * which is more convenient for concrete implementations to extend.
 *
 * @see InterestPrincipalPenaltyFeesOrderLoanRepaymentScheduleTransactionProcessor
 *
 * @see HeavensFamilyLoanRepaymentScheduleTransactionProcessor
 * @see CreocoreLoanRepaymentScheduleTransactionProcessor
 */
public abstract class AbstractLoanRepaymentScheduleTransactionProcessor implements LoanRepaymentScheduleTransactionProcessor {

    /**
     * Provides support for passing all {@link LoanTransaction}'s so it will
     * completely re-process the entire loan schedule. This is required in cases
     * where the {@link LoanTransaction} being processed is in the past and
     * falls before existing transactions or and adjustment is made to an
     * existing in which case the entire loan schedule needs to be re-processed.
     */

    @Override
    public ChangedTransactionDetail handleTransaction(final LocalDate disbursementDate,
            final List<LoanTransaction> transactionsPostDisbursement, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges,
            final List<LoanDisbursementDetails> disbursementDetails, final boolean isPeriodicAccrualEnabled) {

        final List<LoanTransaction> changedTransactions = new ArrayList<>();
        final List<LoanTransaction> accrualTransactions = new ArrayList<>();
        for (final LoanTransaction loanTransaction : transactionsPostDisbursement) {
            if (loanTransaction.isAccrualTransaction() && loanTransaction.isNotReversed()) {
                accrualTransactions.add(loanTransaction);
            }
        }
        changedTransactions.addAll(accrualTransactions);

        if (charges != null) {
            for (final LoanCharge loanCharge : charges) {
                if (!loanCharge.isDueAtDisbursement() && !loanCharge.getLoan().isGLIMLoan()) {
                    loanCharge.resetPaidAmount(currency);
                }
            }
        }

        for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
            if (currentInstallment.getLoan() != null && !currentInstallment.getLoan().isGLIMLoan()) {
                currentInstallment.resetDerivedComponents();
                currentInstallment.updateDerivedFields(currency, disbursementDate);
            }
        }

        // re-process loan charges over repayment periods (picking up on waived
        // loan charges)
        final LoanRepaymentScheduleProcessingWrapper wrapper = new LoanRepaymentScheduleProcessingWrapper();
        wrapper.reprocess(currency, disbursementDate, installments, charges, disbursementDetails);

        final ChangedTransactionDetail changedTransactionDetail = new ChangedTransactionDetail();
        final List<LoanTransaction> transactionstoBeProcessed = new ArrayList<>();
        for (final LoanTransaction loanTransaction : transactionsPostDisbursement) {

            if (!(loanTransaction.getId() != null && loanTransaction.getLoan().isGLIMLoan())) {

                if (loanTransaction.isChargePayment()) {
                    final List<LoanChargePaidDetail> chargePaidDetails = new ArrayList<>();
                    final Set<LoanChargePaidBy> chargePaidBies = loanTransaction.getLoanChargesPaid();
                    final Set<LoanCharge> transferCharges = new HashSet<>();
                    for (final LoanChargePaidBy chargePaidBy : chargePaidBies) {
                        final LoanCharge loanCharge = chargePaidBy.getLoanCharge();
                        transferCharges.add(loanCharge);
                        if (loanCharge.isInstalmentFee()) {
                            chargePaidDetails.addAll(loanCharge.fetchRepaymentInstallment(currency));
                        }
                    }
                    LocalDate startDate = disbursementDate;
                    for (final LoanRepaymentScheduleInstallment installment : installments) {
                        for (final LoanCharge loanCharge : transferCharges) {
                            if (loanCharge.isDueForCollectionFromAndUpToAndIncluding(startDate, installment.getDueDate())) {
                                Money amountForProcess = loanCharge.getAmount(currency);
                                if (amountForProcess.isGreaterThan(loanTransaction.getAmount(currency))) {
                                    amountForProcess = loanTransaction.getAmount(currency);
                                }
                                final LoanChargePaidDetail chargePaidDetail = new LoanChargePaidDetail(amountForProcess, installment,
                                        loanCharge.isFeeCharge());
                                chargePaidDetails.add(chargePaidDetail);
                                break;
                            }
                        }
                        startDate = installment.getDueDate();
                    }
                    loanTransaction.resetDerivedComponents();
                    Money unprocessed = loanTransaction.getAmount(currency);
                    for (final LoanChargePaidDetail chargePaidDetail : chargePaidDetails) {
                        final List<LoanRepaymentScheduleInstallment> processInstallments = new ArrayList<>(1);
                        processInstallments.add(chargePaidDetail.getInstallment());
                        Money processAmt = chargePaidDetail.getAmount();
                        if (processAmt.isGreaterThan(unprocessed)) {
                            processAmt = unprocessed;
                        }
                        unprocessed = handleTransactionAndCharges(loanTransaction, currency, processInstallments, transferCharges,
                                processAmt, chargePaidDetail.isFeeCharge());
                        if (!unprocessed.isGreaterThanZero()) {
                            break;
                        }
                    }

                    if (unprocessed.isGreaterThanZero()) {
                        onLoanOverpayment(loanTransaction, unprocessed);
                        loanTransaction.updateOverPayments(unprocessed);
                    }
                    changedTransactions.add(loanTransaction);
                } else {
                    transactionstoBeProcessed.add(loanTransaction);
                }

            }

        }

        for (final LoanTransaction loanTransaction : transactionstoBeProcessed) {

            if (!loanTransaction.getTypeOf().equals(LoanTransactionType.REFUND_FOR_ACTIVE_LOAN)) {
                final Comparator<LoanRepaymentScheduleInstallment> byDate = new Comparator<LoanRepaymentScheduleInstallment>() {

                    @Override
                    public int compare(final LoanRepaymentScheduleInstallment ord1, final LoanRepaymentScheduleInstallment ord2) {
                        return ord1.getDueDate().compareTo(ord2.getDueDate());
                    }
                };
                Collections.sort(installments, byDate);
            }

            if (loanTransaction.isRepayment() || loanTransaction.isInterestWaiver() || loanTransaction.isRecoveryRepayment()) {
                // pass through for new transactions
                if (loanTransaction.getId() == null) {
                    handleTransaction(loanTransaction, currency, installments, charges);
                    loanTransaction.adjustInterestComponent(currency);
                    changedTransactions.add(loanTransaction);
                } else {
                    /**
                     * For existing transactions, check if the re-payment
                     * breakup (principal, interest, fees, penalties) has
                     * changed.<br>
                     **/

                    final LoanTransaction newLoanTransaction = LoanTransaction.copyTransactionProperties(loanTransaction);

                    // Reset derived component of new loan transaction and
                    // re-process transaction
                    handleTransaction(newLoanTransaction, currency, installments, charges);
                    newLoanTransaction.adjustInterestComponent(currency);
                    /**
                     * Check if the transaction amounts have changed. If so,
                     * reverse the original transaction and update
                     * changedTransactionDetail accordingly
                     **/
                    if (LoanTransaction.transactionAmountsMatch(currency, loanTransaction, newLoanTransaction)
                            || loanTransaction.getLoan().isGLIMLoan()) {
                        loanTransaction.updateLoanTransactionToRepaymentScheduleMappings(
                                newLoanTransaction.getLoanTransactionToRepaymentScheduleMappings());
                        changedTransactions.add(loanTransaction);
                    } else {
                        loanTransaction.reverse();
                        loanTransaction.updateExternalId(null);
                        changedTransactionDetail.getNewTransactionMappings().put(loanTransaction.getId(), newLoanTransaction);
                        changedTransactions.add(newLoanTransaction);
                    }
                }

            } else if (loanTransaction.isWriteOff()) {
                final LoanTransaction newLoanTransaction = LoanTransaction.copyTransactionProperties(loanTransaction);
                newLoanTransaction.resetDerivedComponents(true);
                final Money[] receivableIncomes = getReceivableIncome(changedTransactions, loanTransaction.getTransactionDate(), currency);
                handleWriteOff(newLoanTransaction, currency, installments, receivableIncomes, charges, accrualTransactions);
                if (!LoanTransaction.transactionAmountsMatch(currency, loanTransaction, newLoanTransaction)) {
                    loanTransaction.reverse();
                    loanTransaction.updateExternalId(null);
                    changedTransactionDetail.getNewTransactionMappings().put(loanTransaction.getId(), newLoanTransaction);
                }
            } else if (loanTransaction.isRefundForActiveLoan()) {
                loanTransaction.resetDerivedComponents();

                handleRefund(loanTransaction, currency, installments, charges);
            }
        }
        return changedTransactionDetail;
    }

    public Money[] getReceivableIncome(final List<LoanTransaction> loanTransactions, final LocalDate tillDate,
            final MonetaryCurrency currency) {
        Money receivableInterest = Money.zero(currency);
        Money receivableFee = Money.zero(currency);
        Money receivablePenalty = Money.zero(currency);
        final Money[] receivables = new Money[3];
        for (final LoanTransaction transaction : loanTransactions) {
            if (transaction.isNotReversed() && !transaction.isRepaymentAtDisbursement() && !transaction.isDisbursement()
                    && !transaction.getTransactionDate().isAfter(tillDate)) {
                if (transaction.isAccrual()) {
                    receivableInterest = receivableInterest.plus(transaction.getInterestPortion(currency));
                    receivableFee = receivableFee.plus(transaction.getFeeChargesPortion(currency));
                    receivablePenalty = receivablePenalty.plus(transaction.getPenaltyChargesPortion(currency));
                } else if (transaction.isRepayment() || transaction.isAccrualWrittenOff() || transaction.isInterestWaiver()
                        || transaction.isChargesWaiver() || transaction.isChargePayment()) {
                    receivableInterest = receivableInterest.minus(transaction.getInterestPortion(currency));
                    receivableFee = receivableFee.minus(transaction.getFeeChargesPortion(currency));
                    receivablePenalty = receivablePenalty.minus(transaction.getPenaltyChargesPortion(currency));
                }
            }
            if (receivableInterest.isLessThanZero()) {
                receivableInterest = receivableInterest.zero();
            }
            if (receivableFee.isLessThanZero()) {
                receivableFee = receivableFee.zero();
            }
            if (receivablePenalty.isLessThanZero()) {
                receivablePenalty = receivablePenalty.zero();
            }
        }
        receivables[0] = receivableInterest;
        receivables[1] = receivableFee;
        receivables[2] = receivablePenalty;
        return receivables;
    }

    /**
     * Provides support for processing the latest transaction (which should be
     * latest transaction) against the loan schedule.
     */
    @Override
    public void handleTransaction(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges) {

        final Money amountToProcess = null;
        final boolean isChargeAmount = false;
        handleTransaction(loanTransaction, currency, installments, charges, amountToProcess, isChargeAmount);

    }

    private void handleTransaction(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges, final Money chargeAmountToProcess,
            final boolean isFeeCharge) {

        final Money transactionAmountUnprocessed = handleTransactionAndCharges(loanTransaction, currency, installments, charges,
                chargeAmountToProcess, isFeeCharge);

        if (transactionAmountUnprocessed.isGreaterThanZero()) {
            if (loanTransaction.isWaiver()) {
                loanTransaction.updateComponentsAndTotal(transactionAmountUnprocessed.zero(), transactionAmountUnprocessed.zero(),
                        transactionAmountUnprocessed.zero(), transactionAmountUnprocessed.zero());
            } else if (!loanTransaction.getLoan().isGLIMLoan()) {
                onLoanOverpayment(loanTransaction, transactionAmountUnprocessed);
                loanTransaction.updateOverPayments(transactionAmountUnprocessed);
            }
        }
    }

    private Money handleTransactionAndCharges(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges, final Money chargeAmountToProcess,
            final boolean isFeeCharge) {
        // to.
        if (loanTransaction.isRepayment() || loanTransaction.isInterestWaiver() || loanTransaction.isRecoveryRepayment()) {
            loanTransaction.resetDerivedComponents();
        }
        final Money transactionAmountUnprocessed = processTransaction(loanTransaction, currency, installments, chargeAmountToProcess);

        final Set<LoanCharge> loanFees = extractFeeCharges(charges);
        final Set<LoanCharge> loanPenalties = extractPenaltyCharges(charges);
        Integer installmentNumber = null;
        if (loanTransaction.isChargePayment() && installments.size() == 1) {
            installmentNumber = installments.get(0).getInstallmentNumber();
        }

        if (loanTransaction.isNotWaiver()) {
            Money feeCharges = loanTransaction.getFeeChargesPortion(currency);
            Money penaltyCharges = loanTransaction.getPenaltyChargesPortion(currency);
            if (chargeAmountToProcess != null && feeCharges.isGreaterThan(chargeAmountToProcess)) {
                if (isFeeCharge) {
                    feeCharges = chargeAmountToProcess;
                } else {
                    penaltyCharges = chargeAmountToProcess;
                }
            }
            if (feeCharges.isGreaterThanZero() && !loanTransaction.getLoan().isGLIMLoan()) {
                updateChargesPaidAmountBy(loanTransaction, feeCharges, loanFees, installmentNumber);
            }

            if (penaltyCharges.isGreaterThanZero()) {
                updateChargesPaidAmountBy(loanTransaction, penaltyCharges, loanPenalties, installmentNumber);
            }
        }
        return transactionAmountUnprocessed;
    }

    private Money processTransaction(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Money amountToProcess) {
        int installmentIndex = 0;

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        Money transactionAmountUnprocessed = loanTransaction.getAmount(currency);
        final boolean isPrePayment = loanTransaction.getTransactionSubTye().isPrePayment();
        if (amountToProcess != null) {
            transactionAmountUnprocessed = amountToProcess;
        }
        final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings = new ArrayList<>();
        final Loan loan = loanTransaction.getLoan();
        final Boolean isGLIMLoan = (loan == null) ? false : loan.isGLIMLoan();
        if (isGLIMLoan) {
            transactionAmountUnprocessed = processGlimTransactions(loanTransaction, currency, installments, transactionAmountUnprocessed,
                    transactionMappings, loan);

        } else {
            for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
                if (transactionAmountUnprocessed.isGreaterThanZero()) {
                    if (currentInstallment.isNotFullyPaidOff()) {

                        if (isPrePayment) {
                            transactionAmountUnprocessed = handleTransactionThatIsPrePaymentInstallment(currentInstallment, installments,
                                    loanTransaction, transactionDate, transactionAmountUnprocessed, transactionMappings);
                        }
                        // is this transaction early/late/on-time with respect
                        // to
                        // the
                        // current installment?
                        else if (isTransactionInAdvanceOfInstallment(installmentIndex, installments, transactionDate)) {
                            transactionAmountUnprocessed = handleTransactionThatIsPaymentInAdvanceOfInstallment(currentInstallment,
                                    installments, loanTransaction, transactionDate, transactionAmountUnprocessed, transactionMappings);
                        } else if (isTransactionALateRepaymentOnInstallment(installmentIndex, installments,
                                loanTransaction.getTransactionDate())) {
                            // does this result in a late payment of existing
                            // installment?
                            transactionAmountUnprocessed = handleTransactionThatIsALateRepaymentOfInstallment(currentInstallment,
                                    installments, loanTransaction, transactionAmountUnprocessed, transactionMappings);
                        } else {
                            // standard transaction
                            transactionAmountUnprocessed = handleTransactionThatIsOnTimePaymentOfInstallment(currentInstallment,
                                    loanTransaction, transactionAmountUnprocessed, transactionMappings);
                        }
                    }
                }

                installmentIndex++;
            }
        }

        loanTransaction.updateLoanTransactionToRepaymentScheduleMappings(transactionMappings);
        return transactionAmountUnprocessed;
    }

    /*
     * process glim transaction amount for installment its recalculate each
     * installment which is already paid and create new installment portions
     * along with each installment charge amount
     */
    private Money processGlimTransactions(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, Money transactionAmountUnprocessed,
            final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings, final Loan loan) {
        final List<GroupLoanIndividualMonitoring> glimMembers = loan.getGroupLoanIndividualMonitoringList();
        final Set<LoanCharge> charges = loan.charges();
        // to get charge amount for each charge initialized with zero amount
        final Map<Long, BigDecimal> chargeAmountMap = new HashMap<>();
        for (final LoanCharge loanCharge : charges) {
            chargeAmountMap.put(loanCharge.getCharge().getId(), BigDecimal.ZERO);
        }

        /*
         * creates installments for each glim member
         *
         */
        for (final GroupLoanIndividualMonitoring glimMember : glimMembers) {
            if (glimMember.getTransactionAmount().compareTo(BigDecimal.ZERO) > 0) {
                Money transactionAmountPerClient = Money.of(currency, glimMember.getTransactionAmount());
                final Money penaltyPortion = Money.zero(currency);
                // earlier paid installment amounts for transactions
                final Map<String, BigDecimal> processedTransactionMap = new HashMap<>();
                processedTransactionMap.put("processedCharge", BigDecimal.ZERO);
                processedTransactionMap.put("processedInterest", BigDecimal.ZERO);
                processedTransactionMap.put("processedPrincipal", BigDecimal.ZERO);
                processedTransactionMap.put("processedOverPaidAmount", BigDecimal.ZERO);
                processedTransactionMap.put("processedinstallmentTransactionAmount", BigDecimal.ZERO);
                // installment portion for current transactions
                final Map<String, BigDecimal> installmentPaidMap = new HashMap<>();
                installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
                installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
                installmentPaidMap.put("unprocessedOverPaidAmount", BigDecimal.ZERO);
                installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);
                final Set<GroupLoanIndividualMonitoringCharge> glimCharges = glimMember.getGroupLoanIndividualMonitoringCharges();
                // charge amount for each glim charges
                final Map<Long, BigDecimal> glimChargeAmountMap = new HashMap<>();
                for (final GroupLoanIndividualMonitoringCharge glimloanCharge : glimCharges) {
                    glimChargeAmountMap.put(glimloanCharge.getCharge().getId(), MathUtility.zeroIfNull(glimloanCharge.getPaidCharge()));
                }
                // get installment portions for each installment
                for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
                    if (transactionAmountPerClient.isGreaterThanZero()) {
                        // get installment portions

                        final Map<String, BigDecimal> splitMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                                transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(), installmentPaidMap,
                                loanTransaction, null);

                        if (!(MathUtility.isZero(splitMap.get("installmentTransactionAmount")))) {

                            // installment portions in current installment
                            final Money feePortion = Money.of(currency, splitMap.get("unpaidCharge"));
                            final Money interestPortion = Money.of(currency, splitMap.get("unpaidInterest"));
                            final Money principalPortion = Money.of(currency, splitMap.get("unpaidPrincipal"));
                            final Money overPaidAmount = Money.of(currency, splitMap.get("unprocessedOverPaidAmount"));
                            final Money totalAmountForCurrentInstallment = Money.of(currency, splitMap.get("installmentTransactionAmount"));
                            // add portions to processed installments amount
                            processedTransactionMap.put("processedCharge",
                                    processedTransactionMap.get("processedCharge").add(feePortion.getAmount()));
                            processedTransactionMap.put("processedInterest",
                                    processedTransactionMap.get("processedInterest").add(interestPortion.getAmount()));
                            processedTransactionMap.put("processedPrincipal",
                                    processedTransactionMap.get("processedPrincipal").add(principalPortion.getAmount()));
                            processedTransactionMap.put("processedOverPaidAmount",
                                    processedTransactionMap.get("processedOverPaidAmount").add(overPaidAmount.getAmount()));
                            processedTransactionMap.put("processedinstallmentTransactionAmount", processedTransactionMap
                                    .get("processedinstallmentTransactionAmount").add(totalAmountForCurrentInstallment.getAmount()));
                            // subtract current installment processed amount
                            // from transaction amount
                            transactionAmountPerClient = transactionAmountPerClient.minus(totalAmountForCurrentInstallment);
                            // get installment charge amount for each glim
                            // charge
                            loanTransaction.updateOverPayments(Money.of(currency, processedTransactionMap.get("processedOverPaidAmount")));
                            if (!loanTransaction.isRecoveryRepayment()) {
                                handleGlimInstallmentAmount(loanTransaction, currency, transactionMappings, loan, charges, penaltyPortion,
                                        glimCharges, glimChargeAmountMap, currentInstallment, feePortion, interestPortion, principalPortion,
                                        totalAmountForCurrentInstallment);
                            }

                            // installmentPaidMap = splitMap;
                            installmentPaidMap.put("unpaidCharge", processedTransactionMap.get("processedCharge"));
                            installmentPaidMap.put("unpaidInterest", processedTransactionMap.get("processedInterest"));
                            installmentPaidMap.put("unpaidPrincipal", processedTransactionMap.get("processedPrincipal"));
                            installmentPaidMap.put("unprocessedOverPaidAmount", processedTransactionMap.get("processedOverPaidAmount"));
                            installmentPaidMap.put("installmentTransactionAmount",
                                    processedTransactionMap.get("processedinstallmentTransactionAmount"));

                            transactionAmountUnprocessed = transactionAmountPerClient;

                            if (transactionAmountPerClient.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                                break;
                            }

                        }

                    }
                }
                getGlimTotalChargesAmount(chargeAmountMap, processedTransactionMap, glimCharges, glimChargeAmountMap);
                glimMember.updateProcessedTransactionMap(processedTransactionMap);
            }
        }
        updateGlimChargesPaidAmountBy(loanTransaction, chargeAmountMap, charges, null);
        return transactionAmountUnprocessed;
    }

    /*
     * update glim installment amount
     */
    private void handleGlimInstallmentAmount(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings, final Loan loan, final Set<LoanCharge> charges,
            final Money penaltyPortion, final Set<GroupLoanIndividualMonitoringCharge> glimCharges,
            final Map<Long, BigDecimal> glimChargeAmountMap, final LoanRepaymentScheduleInstallment currentInstallment,
            final Money feePortion, final Money interestPortion, final Money principalPortion, Money totalAmountForCurrentInstallment) {
        BigDecimal currentInstallmentFee = feePortion.getAmount();

        currentInstallmentFee = getGlimInstallmentChargeAmount(currency, loan, charges, glimCharges, glimChargeAmountMap,
                currentInstallment, currentInstallmentFee);

        totalAmountForCurrentInstallment = handleTransactionThatIsOnTimePaymentOfInstallmentForGlim(currentInstallment, loanTransaction,
                totalAmountForCurrentInstallment, transactionMappings, principalPortion, interestPortion, feePortion, penaltyPortion);
    }

    /*
     *
     * get installment charge amount for each glim charge
     */
    private BigDecimal getGlimInstallmentChargeAmount(final MonetaryCurrency currency, final Loan loan, final Set<LoanCharge> charges,
            final Set<GroupLoanIndividualMonitoringCharge> glimCharges, final Map<Long, BigDecimal> glimChargeAmountMap,
            final LoanRepaymentScheduleInstallment currentInstallment, BigDecimal currentInstallmentFee) {
        for (final LoanCharge loanCharge : charges) {
            for (final GroupLoanIndividualMonitoringCharge glimCharge : glimCharges) {
                if (loanCharge.getCharge().getId().equals(glimCharge.getCharge().getId())) {
                    final int currentInstallmentNumber = currentInstallment.getInstallmentNumber();
                    final BigDecimal amount = glimCharge.getCharge().isEmiRoundingGoalSeek() ? glimCharge.getRevisedFeeAmount()
                            : glimCharge.getFeeAmount();
                    BigDecimal amountToBePaidInCurrentInstallment = BigDecimal.ZERO;
                    // get charge amount for upfront charge
                    if (ChargeTimeType.fromInt(glimCharge.getCharge().getChargeTimeType().intValue()).isUpfrontFee()
                            && currentInstallmentNumber == ChargesApiConstants.applyUpfrontFeeOnFirstInstallment) {
                        amountToBePaidInCurrentInstallment = amount;
                    } else {
                        // get charge amount for installment charge
                        final BigDecimal installmentAmount = MathUtility.getInstallmentAmount(amount,
                                loan.fetchNumberOfInstallmensAfterExceptions(), currency, currentInstallmentNumber);
                        // for last installment amount
                        if (currentInstallmentNumber != loan.fetchNumberOfInstallmensAfterExceptions()) {
                            final BigDecimal perChargeamount = glimChargeAmountMap.get(glimCharge.getCharge().getId());
                            amountToBePaidInCurrentInstallment = MathUtility
                                    .subtract(MathUtility.multiply(installmentAmount, currentInstallmentNumber), perChargeamount);
                        } else {
                            // if it not last installment amount
                            final BigDecimal defaultInstallmentAmount = MathUtility.getInstallmentAmount(amount,
                                    loan.fetchNumberOfInstallmensAfterExceptions(), currency, 1);
                            amountToBePaidInCurrentInstallment = MathUtility.subtract(
                                    MathUtility.multiply(defaultInstallmentAmount, currentInstallmentNumber - 1).add(installmentAmount),
                                    glimChargeAmountMap.get(glimCharge.getCharge().getId()));
                        }
                    }
                    /**
                     * if amount paid in current installment is greater than
                     * zero then add to installment fee
                     */
                    if (MathUtility.isGreaterThanZero(amountToBePaidInCurrentInstallment)) {
                        if (MathUtility.isEqualOrGreater(currentInstallmentFee, amountToBePaidInCurrentInstallment)) {
                            glimChargeAmountMap.put(glimCharge.getCharge().getId(), MathUtility
                                    .add(glimChargeAmountMap.get(glimCharge.getCharge().getId()), amountToBePaidInCurrentInstallment));
                            currentInstallmentFee = currentInstallmentFee.subtract(amountToBePaidInCurrentInstallment);
                        } else {
                            glimChargeAmountMap.put(glimCharge.getCharge().getId(),
                                    MathUtility.add(glimChargeAmountMap.get(glimCharge.getCharge().getId()), currentInstallmentFee));
                            currentInstallmentFee = currentInstallmentFee.subtract(currentInstallmentFee);
                        }
                    }

                }

            }
        }
        return currentInstallmentFee;
    }

    /*
     * get total charge amount for each charge processed
     */
    private void getGlimTotalChargesAmount(final Map<Long, BigDecimal> chargeAmountMap,
            final Map<String, BigDecimal> processedTransactionMap, final Set<GroupLoanIndividualMonitoringCharge> glimCharges,
            final Map<Long, BigDecimal> glimChargeAmountMap) {
        for (final Long chargeId : glimChargeAmountMap.keySet()) {
            processedTransactionMap.put(chargeId.toString(), MathUtility.zeroIfNull(glimChargeAmountMap.get(chargeId)));
            for (final GroupLoanIndividualMonitoringCharge glimloanCharge : glimCharges) {
                if (chargeId.equals(glimloanCharge.getCharge().getId())) {
                    final BigDecimal amount = MathUtility.subtract(
                            MathUtility.add(chargeAmountMap.get(chargeId), glimChargeAmountMap.get(chargeId)),
                            glimloanCharge.getPaidCharge());
                    chargeAmountMap.put(chargeId, amount);
                }
            }
        }
    }

    private Set<LoanCharge> extractFeeCharges(final Set<LoanCharge> loanCharges) {
        final Set<LoanCharge> feeCharges = new HashSet<>();
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isFeeCharge()) {
                feeCharges.add(loanCharge);
            }
        }
        return feeCharges;
    }

    private Set<LoanCharge> extractPenaltyCharges(final Set<LoanCharge> loanCharges) {
        final Set<LoanCharge> penaltyCharges = new HashSet<>();
        for (final LoanCharge loanCharge : loanCharges) {
            if (loanCharge.isPenaltyCharge()) {
                penaltyCharges.add(loanCharge);
            }
        }
        return penaltyCharges;
    }

    private void updateChargesPaidAmountBy(final LoanTransaction loanTransaction, final Money feeCharges, final Set<LoanCharge> charges,
            final Integer installmentNumber) {

        Money amountRemaining = feeCharges;
        while (amountRemaining.isGreaterThanZero()) {
            final LoanCharge unpaidCharge = findEarliestUnpaidChargeFromUnOrderedSet(charges, feeCharges.getCurrency());
            Money feeAmount = feeCharges.zero();
            if (loanTransaction.isChargePayment()) {
                feeAmount = feeCharges;
            }
            if (unpaidCharge == null) {
                break; // All are trache charges
            }
            final Money amountPaidTowardsCharge = unpaidCharge.updatePaidAmountBy(amountRemaining, installmentNumber, feeAmount);

            if (!amountPaidTowardsCharge.isZero()) {
                final Set<LoanChargePaidBy> chargesPaidBies = loanTransaction.getLoanChargesPaid();
                if (loanTransaction.isChargePayment()) {
                    for (final LoanChargePaidBy chargePaidBy : chargesPaidBies) {
                        final LoanCharge loanCharge = chargePaidBy.getLoanCharge();
                        if (loanCharge.getId().equals(unpaidCharge.getId())) {
                            chargePaidBy.setAmount(amountPaidTowardsCharge.getAmount());
                        }
                    }
                } else {
                    final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(loanTransaction, unpaidCharge,
                            amountPaidTowardsCharge.getAmount(), installmentNumber);
                    chargesPaidBies.add(loanChargePaidBy);
                }
                amountRemaining = amountRemaining.minus(amountPaidTowardsCharge);
            }
        }

    }

    private void updateGlimChargesPaidAmountBy(final LoanTransaction loanTransaction, final Map<Long, BigDecimal> chargeAmountMap,
            final Set<LoanCharge> charges, final Integer installmentNumber) {
        final MonetaryCurrency currency = loanTransaction.getLoan().getCurrency();
        final Set<LoanChargePaidBy> chargesPaidBies = loanTransaction.getLoanChargesPaid();
        for (final LoanCharge loanCharge : charges) {
            for (final Long chargeId : chargeAmountMap.keySet()) {
                if (chargeId == loanCharge.getCharge().getId()) {
                    Money amountRemaining = Money.of(currency, chargeAmountMap.get(chargeId));
                    final Set<LoanCharge> chargeSet = new HashSet<>();
                    chargeSet.add(loanCharge);
                    while (amountRemaining.isGreaterThanZero()) {
                        final LoanCharge unpaidCharge = findEarliestUnpaidChargeFromUnOrderedSet(chargeSet, currency);
                        Money feeAmount = Money.zero(currency);
                        if (loanTransaction.isChargePayment()) {
                            feeAmount = Money.of(currency, chargeAmountMap.get(chargeId));
                        }
                        if (unpaidCharge == null) {
                            break;
                        }
                        final Money amountPaidTowardsCharge = unpaidCharge.updatePaidAmountBy(amountRemaining, installmentNumber,
                                feeAmount);

                        if (!amountPaidTowardsCharge.isZero()) {
                            final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(loanTransaction, unpaidCharge,
                                    amountPaidTowardsCharge.getAmount(), installmentNumber);
                            chargesPaidBies.add(loanChargePaidBy);
                            amountRemaining = amountRemaining.minus(amountPaidTowardsCharge);
                        }
                    }

                }
            }
        }

    }

    private LoanCharge findEarliestUnpaidChargeFromUnOrderedSet(final Set<LoanCharge> charges, final MonetaryCurrency currency) {
        LoanCharge earliestUnpaidCharge = null;
        LoanCharge installemntCharge = null;
        LoanInstallmentCharge chargePerInstallment = null;
        for (final LoanCharge loanCharge : charges) {
            if (loanCharge.getAmountOutstanding(currency).isGreaterThanZero() && !loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isInstalmentFee()) {
                    final LoanInstallmentCharge unpaidLoanChargePerInstallment = loanCharge.getUnpaidInstallmentLoanCharge();
                    if (chargePerInstallment == null || chargePerInstallment.getRepaymentInstallment().getDueDate()
                            .isAfter(unpaidLoanChargePerInstallment.getRepaymentInstallment().getDueDate())) {
                        installemntCharge = loanCharge;
                        chargePerInstallment = unpaidLoanChargePerInstallment;
                    }
                } else if (earliestUnpaidCharge == null || (loanCharge.isDueDateCharge()
                        && loanCharge.getDueLocalDate().isBefore(earliestUnpaidCharge.getDueLocalDate()))) {
                    earliestUnpaidCharge = loanCharge;
                }
            }
        }
        if (earliestUnpaidCharge == null || (chargePerInstallment != null && earliestUnpaidCharge.getDueLocalDate() != null
                && earliestUnpaidCharge.getDueLocalDate().isAfter(chargePerInstallment.getRepaymentInstallment().getDueDate()))) {
            earliestUnpaidCharge = installemntCharge;
        }

        return earliestUnpaidCharge;
    }

    @Override
    public void handleWriteOff(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Money[] receivableIncomes, final Set<LoanCharge> charges,
            final List<LoanTransaction> transactions) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        Money principalPortion = Money.zero(currency);
        Money interestPortion = Money.zero(currency);
        Money feeChargesPortion = Money.zero(currency);
        Money penaltychargesPortion = Money.zero(currency);
        final Money interestAccruedPortion = receivableIncomes[0];
        final Money feeChargesAccruedPortion = receivableIncomes[1];
        final Money penaltychargesAccruedPortion = receivableIncomes[2];

        // determine how much is written off in total and breakdown for
        // principal, interest and charges
        for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {

            if (currentInstallment.isNotFullyPaidOff()) {
                principalPortion = principalPortion.plus(currentInstallment.writeOffOutstandingPrincipal(transactionDate, currency));
                interestPortion = interestPortion.plus(currentInstallment.writeOffOutstandingInterest(transactionDate, currency));
                feeChargesPortion = feeChargesPortion.plus(currentInstallment.writeOffOutstandingFeeCharges(transactionDate, currency));
                penaltychargesPortion = penaltychargesPortion
                        .plus(currentInstallment.writeOffOutstandingPenaltyCharges(transactionDate, currency));
            }
        }
        final Money unrecognizedIncomePortion = interestPortion.plus(feeChargesPortion).plus(penaltychargesPortion)
                .minus(interestAccruedPortion).minus(feeChargesAccruedPortion).minus(penaltychargesAccruedPortion);
        loanTransaction.updateComponentsAndTotal(principalPortion, interestAccruedPortion, feeChargesAccruedPortion,
                penaltychargesAccruedPortion, unrecognizedIncomePortion);

        final Set<LoanChargePaidBy> chargesPaidBies = loanTransaction.getLoanChargesPaid();

        // will pass transactions only for non NPA loans
        for (final LoanTransaction accrualTransaction : transactions) {
            if (accrualTransaction.isAccrual()) {
                final Set<LoanChargePaidBy> chargePaidBies = accrualTransaction.getLoanChargesPaid();
                for (final LoanChargePaidBy chargePaidBy : chargePaidBies) {
                    final Money outstanding = chargePaidBy.getLoanCharge().getAmountOutstanding(currency,
                            chargePaidBy.getInstallmentNumber());
                    if (outstanding.isGreaterThanZero()) {
                        final Money writtenOffAmount = chargePaidBy.getLoanCharge().writtenOff(currency,
                                chargePaidBy.getInstallmentNumber());
                        final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(loanTransaction, chargePaidBy.getLoanCharge(),
                                writtenOffAmount.getAmount(), chargePaidBy.getInstallmentNumber());
                        chargesPaidBies.add(loanChargePaidBy);

                    }
                }
            }
        }

        for (final LoanCharge loanCharge : charges) {
            if (loanCharge.getAmountOutstanding(currency).isGreaterThanZero()) {
                loanCharge.writtenOff(currency);
            }
        }
    }

    @Override
    public void handleWriteOffForGlimLoan(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final GroupLoanIndividualMonitoring glimMember) {

        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        Money principalPortion = Money.zero(currency);
        Money interestPortion = Money.zero(currency);
        Money feeChargesPortion = Money.zero(currency);
        final Money penaltychargesPortion = Money.zero(currency);
        Money transactionAmountPerClient = Money.of(currency, glimMember.getTransactionAmount());
        final Loan loan = loanTransaction.getLoan();

        final Map<String, BigDecimal> installmentPaidMap = new HashMap<>();
        installmentPaidMap.put("unpaidCharge", BigDecimal.ZERO);
        installmentPaidMap.put("unpaidInterest", BigDecimal.ZERO);
        installmentPaidMap.put("unpaidPrincipal", BigDecimal.ZERO);
        installmentPaidMap.put("unprocessedOverPaidAmount", BigDecimal.ZERO);
        installmentPaidMap.put("installmentTransactionAmount", BigDecimal.ZERO);

        // determine how much is written off in total and breakdown for
        // principal, interest and charges
        for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
            if (transactionAmountPerClient.isGreaterThanZero()) {
                final Map<String, BigDecimal> paidInstallmentMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                        transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(), installmentPaidMap,
                        loanTransaction, null);

                if (!(paidInstallmentMap.get("installmentTransactionAmount").compareTo(BigDecimal.ZERO) == 0
                        && glimMember.getTotalPaidAmount().compareTo(BigDecimal.ZERO) > 0)) {
                    if (currentInstallment.isNotFullyPaidOff()) {

                        final Map<String, BigDecimal> splitMap = GroupLoanIndividualMonitoringTransactionAssembler.getSplit(glimMember,
                                transactionAmountPerClient.getAmount(), loan, currentInstallment.getInstallmentNumber(), installmentPaidMap,
                                loanTransaction, null);
                        final Money feePortionForCurrentInstallment = Money.of(currency, splitMap.get("unpaidCharge"));
                        final Money interestPortionForCurrentInstallment = Money.of(currency, splitMap.get("unpaidInterest"));
                        final Money principalPortionForCurrentInstallment = Money.of(currency, splitMap.get("unpaidPrincipal"));
                        final Money totalAmountForCurrentInstallment = Money.of(currency, splitMap.get("installmentTransactionAmount"));

                        principalPortion = principalPortion.plus(currentInstallment.getPrincipalWrittenOff(currency).plus(currentInstallment
                                .writeOffOutstandingPrincipalForGlim(transactionDate, currency, principalPortionForCurrentInstallment)));
                        interestPortion = interestPortion.plus(currentInstallment.getInterestWrittenOff(currency).plus(currentInstallment
                                .writeOffOutstandingInterestForGlim(transactionDate, currency, interestPortionForCurrentInstallment)));
                        feeChargesPortion = feeChargesPortion
                                .plus(currentInstallment.getFeeChargesWrittenOff(currency).plus(currentInstallment
                                        .writeOffOutstandingFeeChargeForGlim(transactionDate, currency, feePortionForCurrentInstallment)));

                        installmentPaidMap.put("unpaidCharge",
                                installmentPaidMap.get("unpaidCharge").add(feePortionForCurrentInstallment.getAmount()));
                        installmentPaidMap.put("unpaidInterest",
                                installmentPaidMap.get("unpaidInterest").add(interestPortionForCurrentInstallment.getAmount()));
                        installmentPaidMap.put("unpaidPrincipal",
                                installmentPaidMap.get("unpaidPrincipal").add(principalPortionForCurrentInstallment.getAmount()));
                        installmentPaidMap.put("installmentTransactionAmount",
                                installmentPaidMap.get("installmentTransactionAmount").add(totalAmountForCurrentInstallment.getAmount()));
                        installmentPaidMap.put("unprocessedOverPaidAmount", BigDecimal.ZERO);
                        transactionAmountPerClient = transactionAmountPerClient.minus(totalAmountForCurrentInstallment);

                    }
                }
            }
        }

        loanTransaction.updateComponentsAndTotal(principalPortion, interestPortion, feeChargesPortion, penaltychargesPortion);
    }

    // abstract interface
    /**
     * This method is responsible for checking if the current transaction is 'an
     * advance/early payment' based on the details passed through.
     *
     * Default implementation simply processes transactions as 'Late' if the
     * transaction date is after the installment due date.
     */
    protected boolean isTransactionALateRepaymentOnInstallment(final int installmentIndex,
            final List<LoanRepaymentScheduleInstallment> installments, final LocalDate transactionDate) {

        final LoanRepaymentScheduleInstallment currentInstallment = installments.get(installmentIndex);

        return transactionDate.isAfter(currentInstallment.getDueDate());
    }

    /**
     * For late repayments, how should components of installment be paid off
     *
     * @param transactionMappings
     *            TODO
     */
    protected abstract Money handleTransactionThatIsALateRepaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final Money transactionAmountUnprocessed, final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings);

    /**
     * This method is responsible for checking if the current transaction is 'an
     * advance/early payment' based on the details passed through.
     *
     * Default implementation is check transaction date is before installment
     * due date.
     */
    protected boolean isTransactionInAdvanceOfInstallment(final int currentInstallmentIndex,
            final List<LoanRepaymentScheduleInstallment> installments, final LocalDate transactionDate) {

        final LoanRepaymentScheduleInstallment currentInstallment = installments.get(currentInstallmentIndex);

        return transactionDate.isBefore(currentInstallment.getDueDate());
    }

    /**
     * For early/'in advance' repayments.
     *
     * @param transactionMappings
     *            TODO
     */
    protected abstract Money handleTransactionThatIsPaymentInAdvanceOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final LocalDate transactionDate, final Money paymentInAdvance,
            final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings);

    /**
     * For normal on-time repayments.
     *
     * @param transactionMappings
     *            TODO
     */
    protected abstract Money handleTransactionThatIsOnTimePaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final LoanTransaction loanTransaction, final Money transactionAmountUnprocessed,
            final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings);

    protected Money handleTransactionThatIsPrePaymentInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            @SuppressWarnings("unused") final List<LoanRepaymentScheduleInstallment> installments, final LoanTransaction loanTransaction,
            final LocalDate transactionDate, final Money amount,
            final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings) {
        Money transactionAmountRemaining = amount;
        final Money principalPortion = currentInstallment.payPrincipalComponent(transactionDate, transactionAmountRemaining);
        transactionAmountRemaining = transactionAmountRemaining.minus(principalPortion);
        loanTransaction.updateComponents(principalPortion, principalPortion.zero(), principalPortion.zero(), principalPortion.zero());
        transactionMappings.add(LoanTransactionToRepaymentScheduleMapping.createFrom(currentInstallment, principalPortion,
                principalPortion.zero(), principalPortion.zero(), principalPortion.zero()));
        return transactionAmountRemaining;
    }

    /**
     * Invoked when a transaction results in an over-payment of the full loan.
     *
     * transaction amount is greater than the total expected principal and
     * interest of the loan.
     */
    protected void onLoanOverpayment(final LoanTransaction loanTransaction, final Money loanOverPaymentAmount) {
        // empty implementation by default.
    }

    @Override
    public Money handleRepaymentSchedule(final List<LoanTransaction> transactionsPostDisbursement, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments) {
        Money unProcessed = Money.zero(currency);
        for (final LoanTransaction loanTransaction : transactionsPostDisbursement) {
            final Money amountToProcess = null;
            if (loanTransaction.isRepayment() || loanTransaction.isInterestWaiver() || loanTransaction.isRecoveryRepayment()) {
                loanTransaction.resetDerivedComponents();
            }
            if (loanTransaction.isInterestWaiver()) {
                processTransaction(loanTransaction, currency, installments, amountToProcess);
            } else {
                unProcessed = processTransaction(loanTransaction, currency, installments, amountToProcess);
            }
        }
        return unProcessed;
    }

    @Override
    public boolean isInterestFirstRepaymentScheduleTransactionProcessor() {
        return false;
    }

    @Override
    public abstract boolean isFullPeriodInterestToBeCollectedForLatePaymentsAfterLastInstallment();

    @Override
    public void handleRefund(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges) {
        // TODO Auto-generated method stub
        final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings = new ArrayList<>();
        final Comparator<LoanRepaymentScheduleInstallment> byDate = new Comparator<LoanRepaymentScheduleInstallment>() {

            @Override
            public int compare(final LoanRepaymentScheduleInstallment ord1, final LoanRepaymentScheduleInstallment ord2) {
                return ord1.getDueDate().compareTo(ord2.getDueDate());
            }
        };
        Collections.sort(installments, Collections.reverseOrder(byDate));
        Money transactionAmountUnprocessed = loanTransaction.getAmount(currency);

        for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
            final Money outstanding = currentInstallment.getTotalOutstanding(currency);
            final Money due = currentInstallment.getDue(currency);

            if (outstanding.isLessThan(due)) {
                transactionAmountUnprocessed = handleRefundTransactionPaymentOfInstallment(currentInstallment, loanTransaction,
                        transactionAmountUnprocessed, transactionMappings);

            }

            if (transactionAmountUnprocessed.isZero()) {
                break;
            }

        }

        final Set<LoanCharge> loanFees = extractFeeCharges(charges);
        final Set<LoanCharge> loanPenalties = extractPenaltyCharges(charges);
        final Integer installmentNumber = null;

        final Money feeCharges = loanTransaction.getFeeChargesPortion(currency);
        if (feeCharges.isGreaterThanZero()) {
            undoChargesPaidAmountBy(loanTransaction, feeCharges, loanFees, installmentNumber);
        }

        final Money penaltyCharges = loanTransaction.getPenaltyChargesPortion(currency);
        if (penaltyCharges.isGreaterThanZero()) {
            undoChargesPaidAmountBy(loanTransaction, penaltyCharges, loanPenalties, installmentNumber);
        }

        loanTransaction.updateLoanTransactionToRepaymentScheduleMappings(transactionMappings);
    }

    /**
     * Invoked when a there is a refund of an active loan or undo of an active
     * loan
     *
     * Undoes principal, interest, fees and charges of this transaction based on
     * the repayment strategy
     *
     * @param transactionMappings
     *            TODO
     *
     */
    protected abstract Money handleRefundTransactionPaymentOfInstallment(final LoanRepaymentScheduleInstallment currentInstallment,
            final LoanTransaction loanTransaction, final Money transactionAmountUnprocessed,
            final List<LoanTransactionToRepaymentScheduleMapping> transactionMappings);

    private void undoChargesPaidAmountBy(final LoanTransaction loanTransaction, final Money feeCharges, final Set<LoanCharge> charges,
            final Integer installmentNumber) {

        Money amountRemaining = feeCharges;
        while (amountRemaining.isGreaterThanZero()) {
            final LoanCharge paidCharge = findLatestPaidChargeFromUnOrderedSet(charges, feeCharges.getCurrency());

            if (paidCharge != null) {
                final Money feeAmount = feeCharges.zero();

                final Money amountDeductedTowardsCharge = paidCharge.undoPaidOrPartiallyAmountBy(amountRemaining, installmentNumber,
                        feeAmount);
                if (amountDeductedTowardsCharge.isGreaterThanZero()) {

                    final LoanChargePaidBy loanChargePaidBy = new LoanChargePaidBy(loanTransaction, paidCharge,
                            amountDeductedTowardsCharge.getAmount().multiply(new BigDecimal(-1)), null);
                    loanTransaction.getLoanChargesPaid().add(loanChargePaidBy);

                    amountRemaining = amountRemaining.minus(amountDeductedTowardsCharge);
                }
            }
        }

    }

    private LoanCharge findLatestPaidChargeFromUnOrderedSet(final Set<LoanCharge> charges, final MonetaryCurrency currency) {
        LoanCharge latestPaidCharge = null;
        LoanCharge installemntCharge = null;
        LoanInstallmentCharge chargePerInstallment = null;
        for (final LoanCharge loanCharge : charges) {
            final boolean isPaidOrPartiallyPaid = loanCharge.isPaidOrPartiallyPaid(currency);
            if (isPaidOrPartiallyPaid && !loanCharge.isDueAtDisbursement()) {
                if (loanCharge.isInstalmentFee()) {
                    final LoanInstallmentCharge paidLoanChargePerInstallment = loanCharge
                            .getLastPaidOrPartiallyPaidInstallmentLoanCharge(currency);
                    if (chargePerInstallment == null
                            || (paidLoanChargePerInstallment != null && chargePerInstallment.getRepaymentInstallment().getDueDate()
                                    .isBefore(paidLoanChargePerInstallment.getRepaymentInstallment().getDueDate()))) {
                        installemntCharge = loanCharge;
                        chargePerInstallment = paidLoanChargePerInstallment;
                    }
                } else if (latestPaidCharge == null || (loanCharge.isPaidOrPartiallyPaid(currency))
                        && loanCharge.getDueLocalDate().isAfter(latestPaidCharge.getDueLocalDate())) {
                    latestPaidCharge = loanCharge;
                }
            }
        }
        if (latestPaidCharge == null || (chargePerInstallment != null
                && latestPaidCharge.getDueLocalDate().isAfter(chargePerInstallment.getRepaymentInstallment().getDueDate()))) {
            latestPaidCharge = installemntCharge;
        }

        return latestPaidCharge;
    }

    @Override
    public void processTransactionsFromDerivedFields(final List<LoanTransaction> transactionsPostDisbursement,
            final MonetaryCurrency currency, final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges) {
        for (final LoanTransaction loanTransaction : transactionsPostDisbursement) {
            if (!loanTransaction.isAccrualTransaction()) {
                processTransactionFromDerivedFields(loanTransaction, currency, installments, charges);
            }
        }
    }

    private void processTransactionFromDerivedFields(final LoanTransaction loanTransaction, final MonetaryCurrency currency,
            final List<LoanRepaymentScheduleInstallment> installments, final Set<LoanCharge> charges) {
        Money principal = loanTransaction.getPrincipalPortion(currency);
        Money interest = loanTransaction.getInterestPortion(currency);
        if (loanTransaction.isInterestWaiver()) {
            interest = loanTransaction.getAmount(currency);
        }
        Money feeCharges = loanTransaction.getFeeChargesPortion(currency);
        Money penaltyCharges = loanTransaction.getPenaltyChargesPortion(currency);
        final LocalDate transactionDate = loanTransaction.getTransactionDate();
        if (principal.isGreaterThanZero() || interest.isGreaterThanZero() || feeCharges.isGreaterThanZero()
                || penaltyCharges.isGreaterThanZero()) {
            for (final LoanRepaymentScheduleInstallment currentInstallment : installments) {
                if (currentInstallment.isNotFullyPaidOff()) {
                    if (penaltyCharges.isGreaterThanZero()) {
                        Money penaltyChargesPortion = Money.zero(currency);
                        if (loanTransaction.isWaiver()) {
                            penaltyChargesPortion = currentInstallment.waivePenaltyChargesComponent(transactionDate, penaltyCharges);
                        } else {
                            penaltyChargesPortion = currentInstallment.payPenaltyChargesComponent(transactionDate, penaltyCharges);
                        }
                        penaltyCharges = penaltyCharges.minus(penaltyChargesPortion);
                    }

                    if (feeCharges.isGreaterThanZero()) {
                        Money feeChargesPortion = Money.zero(currency);
                        if (loanTransaction.isWaiver()) {
                            feeChargesPortion = currentInstallment.waiveFeeChargesComponent(transactionDate, feeCharges);
                        } else {
                            feeChargesPortion = currentInstallment.payFeeChargesComponent(transactionDate, feeCharges);
                        }
                        feeCharges = feeCharges.minus(feeChargesPortion);
                    }

                    if (interest.isGreaterThanZero()) {
                        Money interestPortion = Money.zero(currency);
                        if (loanTransaction.isWaiver()) {
                            interestPortion = currentInstallment.waiveInterestComponent(transactionDate, interest);
                        } else {
                            interestPortion = currentInstallment.payInterestComponent(transactionDate, interest);
                        }
                        interest = interest.minus(interestPortion);
                    }

                    if (principal.isGreaterThanZero()) {
                        final Money principalPortion = currentInstallment.payPrincipalComponent(transactionDate, principal);
                        principal = principal.minus(principalPortion);
                    }
                }
                if (!(principal.isGreaterThanZero() || interest.isGreaterThanZero() || feeCharges.isGreaterThanZero()
                        || penaltyCharges.isGreaterThanZero())) {
                    break;
                }
            }
        }

        final Set<LoanCharge> loanFees = extractFeeCharges(charges);
        final Set<LoanCharge> loanPenalties = extractPenaltyCharges(charges);
        Integer installmentNumber = null;
        if (loanTransaction.isChargePayment() && installments.size() == 1) {
            installmentNumber = installments.get(0).getInstallmentNumber();
        }

        if (loanTransaction.isNotWaiver()) {
            feeCharges = loanTransaction.getFeeChargesPortion(currency);
            penaltyCharges = loanTransaction.getPenaltyChargesPortion(currency);
            if (feeCharges.isGreaterThanZero()) {
                updateChargesPaidAmountBy(loanTransaction, feeCharges, loanFees, installmentNumber);
            }

            if (penaltyCharges.isGreaterThanZero()) {
                updateChargesPaidAmountBy(loanTransaction, penaltyCharges, loanPenalties, installmentNumber);
            }
        }
    }

    /**
     * Invoked when a there is a glim repayment for an active loan
     *
     * Splits the total transaction amount of an individual into principal,
     * interest, fees and penalties of this transaction based on the repayment
     * strategy
     *
     */
    @Override
    public void handleGLIMRepayment(final GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction,
            final BigDecimal individualTransactionAmount) {
        // final MathContext mc = new MathContext(0, RoundingMode.HALF_EVEN);
        final LoanTransaction loanTransaction = groupLoanIndividualMonitoringTransaction.getLoanTransaction();
        final GroupLoanIndividualMonitoring glim = groupLoanIndividualMonitoringTransaction.getGroupLoanIndividualMonitoring();
        final BigDecimal writeOfAmount = zeroIfNull(glim.getPrincipalWrittenOffAmount()).add(zeroIfNull(glim.getInterestWrittenOffAmount()))
                .add(zeroIfNull(glim.getChargeWrittenOffAmount()));
        if (writeOfAmount.compareTo(BigDecimal.ZERO) > 0 && individualTransactionAmount.compareTo(BigDecimal.ZERO) > 0
                && !loanTransaction.getTypeOf().isRecoveryRepayment()) { throw new ClientAlreadyWriteOffException(); }
        final Loan loan = loanTransaction.getLoan();
        final MonetaryCurrency currency = loan.getCurrency();

        final Map<String, BigDecimal> processedTransactionMap = glim.getProcessedTransactionMap();

        final Money feePortion = Money.of(currency, processedTransactionMap.get("processedCharge"));

        final Money penaltyPortion = Money.zero(currency);

        final Money interestPortion = Money.of(currency, processedTransactionMap.get("processedInterest"));

        final Money principalPortion = Money.of(currency, processedTransactionMap.get("processedPrincipal"));

        final Money processedinstallmentTransactionAmount = Money.of(currency,
                processedTransactionMap.get("processedinstallmentTransactionAmount"));

        handleGLIMRepaymentInstallment(groupLoanIndividualMonitoringTransaction, processedinstallmentTransactionAmount, principalPortion,
                interestPortion, feePortion, penaltyPortion);
    }

    public static BigDecimal zeroIfNull(final BigDecimal amount) {
        return (amount == null) ? BigDecimal.ZERO : amount;
    }

    protected abstract void handleGLIMRepaymentInstallment(
            GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction, Money installmentAmount,
            Money principalPortion, Money interestPortion, Money feePortion, Money penaltyPortion);

    protected abstract Money handleTransactionThatIsOnTimePaymentOfInstallmentForGlim(
            final LoanRepaymentScheduleInstallment currentInstallment, final LoanTransaction loanTransaction,
            final Money transactionAmountUnprocessed, List<LoanTransactionToRepaymentScheduleMapping> transactionMappings,
            Money principalPortion, Money interestPortion, Money feePortion, Money penaltyPortion);

}