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
package org.apache.fineract.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.glaccount.domain.GLAccount;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.LoanDTO;
import org.apache.fineract.accounting.journalentry.data.LoanTransactionDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccrualBasedAccountingProcessorForLoan implements AccountingProcessorForLoan {

    private final AccountingProcessorHelper helper;
    private final JournalEntryRepositoryWrapper journalEntryRepository;

    @Autowired
    public AccrualBasedAccountingProcessorForLoan(final AccountingProcessorHelper accountingProcessorHelper,
            final JournalEntryRepositoryWrapper journalEntryRepository) {
        this.helper = accountingProcessorHelper;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void createJournalEntriesForLoan(final LoanDTO loanDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(loanDTO.getOfficeId());
        final List<JournalEntry> journalEntryDetails = new ArrayList<>();
        for (final LoanTransactionDTO loanTransactionDTO : loanDTO.getNewLoanTransactions()) {
            final Long loanId = loanDTO.getLoanId();
            final String currencyCode = loanDTO.getCurrencyCode();

            // transaction properties
            final String transactionId = loanTransactionDTO.getTransactionId();
            final Date transactionDate = loanTransactionDTO.getTransactionDate();
            final Long officeId = loanTransactionDTO.getOfficeId();
            final JournalEntry journalEntry = this.helper.createLoanJournalEntry(currencyCode, transactionDate, transactionDate,
                    transactionDate, transactionId, officeId, loanId);
            if (!loanTransactionDTO.getTransactionType().isAccrualTransaction()) {
                this.helper.checkForBranchClosures(latestGLClosure, transactionDate);
            }

            /** Handle Disbursements **/
            if (loanTransactionDTO.getTransactionType().isDisbursement()) {
                createJournalEntriesForDisbursements(loanDTO, loanTransactionDTO, journalEntry);
            }

            /*** Handle Accruals ***/
            if (loanTransactionDTO.getTransactionType().isAccrual()) {
                createJournalEntriesForAccruals(loanDTO, loanTransactionDTO, journalEntry);
            } else if (loanTransactionDTO.getTransactionType().isAccrualSuspense()) {
                createJournalEntriesForAccrualSuspense(loanDTO, loanTransactionDTO, journalEntry);
            } else if (loanTransactionDTO.getTransactionType().isAccrualSuspenseReverse()) {
                createJournalEntriesForAccrualSuspenseReverse(loanDTO, loanTransactionDTO, journalEntry);
            } else if (loanTransactionDTO.getTransactionType().isAccrualWrittenOff()) {
                createJournalEntriesForAccrualWriteOff(loanDTO, loanTransactionDTO, journalEntry);
            }

            /*** Handle AddSubsidy ***/
            else if (loanTransactionDTO.getTransactionType().isAddSubsidy()) {
                createJournalEntriesForAddOrRevokeSubsidy(loanDTO, loanTransactionDTO, journalEntry,
                        LoanTransactionType.ADD_SUBSIDY.getValue());
            }

            /*** Handle RevokeSubsidy ***/
            else if (loanTransactionDTO.getTransactionType().isRevokeSubsidy()) {
                createJournalEntriesForAddOrRevokeSubsidy(loanDTO, loanTransactionDTO, journalEntry,
                        LoanTransactionType.REVOKE_SUBSIDY.getValue());
            }

            /***
             * Handle repayments, repayments at disbursement and reversal of
             * Repayments and Repayments at disbursement
             ***/
            else if (loanTransactionDTO.getTransactionType().isRepayment()
                    || loanTransactionDTO.getTransactionType().isRepaymentAtDisbursement()
                    || loanTransactionDTO.getTransactionType().isChargePayment()) {
                createJournalEntriesForRepaymentsAndWriteOffs(loanDTO, loanTransactionDTO, journalEntry, false,
                        loanTransactionDTO.getTransactionType().isRepaymentAtDisbursement());
            }

            /** Logic for handling recovery payments **/
            else if (loanTransactionDTO.getTransactionType().isRecoveryRepayment()) {
                createJournalEntriesForRecoveryRepayments(loanDTO, loanTransactionDTO, journalEntry);
            }

            /** Logic for Refunds of Overpayments **/
            else if (loanTransactionDTO.getTransactionType().isRefund()) {
                createJournalEntriesForRefund(loanDTO, loanTransactionDTO, journalEntry);
            }

            /** Handle Write Offs, waivers and their reversals **/
            else if ((loanTransactionDTO.getTransactionType().isWriteOff() || loanTransactionDTO.getTransactionType().isWaiveInterest()
                    || loanTransactionDTO.getTransactionType().isWaiveCharges())) {
                createJournalEntriesForRepaymentsAndWriteOffs(loanDTO, loanTransactionDTO, journalEntry, true, false);
            }

            else if (loanTransactionDTO.getTransactionType().isInitiateTransfer()
                    || loanTransactionDTO.getTransactionType().isApproveTransfer()
                    || loanTransactionDTO.getTransactionType().isWithdrawTransfer()) {
                createJournalEntriesForTransfers(loanDTO, loanTransactionDTO, journalEntry);
            }

            /** Logic for Refunds of Active Loans **/
            else if (loanTransactionDTO.getTransactionType().isRefundForActiveLoans()) {
                createJournalEntriesForRefundForActiveLoan(loanDTO, loanTransactionDTO, journalEntry);
            }
            /** Logic to Post Broken Period Interest **/
            else if (loanTransactionDTO.getTransactionType().isBrokenPeriodInterestPosting()) {
                createJournalEntriesForBrokenPeriodInterest(loanDTO, loanTransactionDTO, journalEntry);
            }

            if (!journalEntry.getJournalEntryDetails().isEmpty()) {
                journalEntryDetails.add(journalEntry);
            }
        }

        if (!journalEntryDetails.isEmpty()) {
            this.journalEntryRepository.save(journalEntryDetails);
        }
    }

    /**
     * Debit loan Portfolio and credit Fund source for Disbursement.
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param journalEntry
     */
    private void createJournalEntriesForDisbursements(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {

        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal disbursalAmount = loanTransactionDTO.getAmount();
        final boolean isReversed = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        // create journal entries for the disbursement (or disbursement
        // reversal)
        if (loanTransactionDTO.isLoanToLoanTransfer()) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    FINANCIAL_ACTIVITY.ASSET_TRANSFER.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }

    }

    /**
     *
     * Handles repayments using the following posting rules <br/>
     * <br/>
     * <br/>
     *
     * <b>Principal Repayment</b>: Debits "Fund Source" and Credits "Loan
     * Portfolio"<br/>
     *
     * <b>Interest Repayment</b>:Debits "Fund Source" and and Credits
     * "Receivable Interest" <br/>
     *
     * <b>Fee Repayment</b>:Debits "Fund Source" (or "Interest on Loans" in case
     * of repayment at disbursement) and and Credits "Receivable Fees" <br/>
     *
     * <b>Penalty Repayment</b>: Debits "Fund Source" and and Credits
     * "Receivable Penalties" <br/>
     * <br/>
     * Handles write offs using the following posting rules <br/>
     * <br/>
     * <b>Principal Write off</b>: Debits "Losses Written Off" and Credits "Loan
     * Portfolio"<br/>
     *
     * <b>Interest Write off</b>:Debits "Losses Written off" and and Credits
     * "Receivable Interest" <br/>
     *
     * <b>Fee Write off</b>:Debits "Losses Written off" and and Credits
     * "Receivable Fees" <br/>
     *
     * <b>Penalty Write off</b>: Debits "Losses Written off" and and Credits
     * "Receivable Penalties" <br/>
     * <br/>
     * <br/>
     * In case the loan transaction has been reversed, all debits are turned
     * into credits and vice versa
     *
     * @param loanTransactionDTO
     * @param loanDTO
     * @param office
     *
     */
    private void createJournalEntriesForRepaymentsAndWriteOffs(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry, final boolean writeOff, final boolean isIncomeFromFee) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final boolean ignoreAccountingForTax = true;

        BigDecimal totalDebitAmount = new BigDecimal(0);

        final Map<GLAccount, BigDecimal> accountMap = new HashMap<>();

        // handle principal payment or writeOff (and reversals)
        if (principalAmount != null && !(principalAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            final GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), paymentTypeId, loanDTO.getWriteOffReasonId());
            this.helper.addOrUpdateAccountMapWithAmount(accountMap, account, principalAmount);
        }

        // handle interest payment of writeOff (and reversals)
        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            final GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.getValue(), paymentTypeId, loanDTO.getWriteOffReasonId());
            this.helper.addOrUpdateAccountMapWithAmount(accountMap, account, interestAmount);
        }

        // handle fees payment of writeOff (and reversals)
        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            if (isIncomeFromFee) {
                if (loanTransactionDTO.getFeePayments() != null && !loanTransactionDTO.getFeePayments().isEmpty()) {
                    accountMap.putAll(this.helper.constructCreditJournalEntryOrReversalForLoanChargesAccountMap(
                            ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), loanProductId, feesAmount,
                            loanTransactionDTO.getFeePayments()));
                }
            } else {
                final GLAccount feesReceivableAccount = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.getValue(), paymentTypeId, loanDTO.getWriteOffReasonId());
                this.helper.constructCreditJournalEntryOrReversalForLoanChargesAccountMap(loanProductId, paymentTypeId,
                        loanDTO.getWriteOffReasonId(), feesAmount, accountMap, writeOff, feesReceivableAccount, ignoreAccountingForTax,
                        loanTransactionDTO.getFeePayments());
            }
        }

        // handle penalties payment of writeOff (and reversals)
        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            if (isIncomeFromFee) {
                if (loanTransactionDTO.getPenaltyPayments() != null && !loanTransactionDTO.getPenaltyPayments().isEmpty()) {
                    accountMap.putAll(this.helper.constructCreditJournalEntryOrReversalForLoanChargesAccountMap(
                            ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(), loanProductId, penaltiesAmount,
                            loanTransactionDTO.getPenaltyPayments()));
                }
            } else {
                final GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                        ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.getValue(), paymentTypeId, loanDTO.getWriteOffReasonId());
                this.helper.constructCreditJournalEntryOrReversalForLoanChargesAccountMap(loanProductId, paymentTypeId,
                        loanDTO.getWriteOffReasonId(), penaltiesAmount, accountMap, writeOff, account, ignoreAccountingForTax,
                        loanTransactionDTO.getPenaltyPayments());
            }
        }

        // handle over payment of writeOff (and reversals)
        if (overPaymentAmount != null && !(overPaymentAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            final GLAccount account = this.helper.getLinkedGLAccountForLoanProduct(loanProductId,
                    ACCRUAL_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(), paymentTypeId, loanDTO.getWriteOffReasonId());
            this.helper.addOrUpdateAccountMapWithAmount(accountMap, account, overPaymentAmount);
        }

        for (final Entry<GLAccount, BigDecimal> entry : accountMap.entrySet()) {
            this.helper.createCreditJournalEntryOrReversal(entry.getValue(), isReversal, entry.getKey(), journalEntry);
        }

        /**
         * Single DEBIT transaction for write-offs or Repayments (and their
         * reversals)
         ***/
        if (!(totalDebitAmount.compareTo(BigDecimal.ZERO) == 0)) {
            if (writeOff) {
                this.helper.createDebitJournalEntryOrReversalForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(), loanProductId,
                        paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
            } else {
                if (loanTransactionDTO.isLoanToLoanTransfer()) {
                    this.helper.createDebitJournalEntryOrReversalForLoan(FINANCIAL_ACTIVITY.ASSET_TRANSFER.getValue(), loanProductId,
                            paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
                } else if (loanTransactionDTO.isAccountTransfer()) {
                    this.helper.createDebitJournalEntryOrReversalForLoan(FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), loanProductId,
                            paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
                } else {
                    if (loanTransactionDTO.getTransactionSubType().isRealizationSubsidy()) {
                        this.helper.createDebitJournalEntryOrReversalForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.getValue(),
                                loanProductId, paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
                    } else {
                        this.helper.createDebitJournalEntryOrReversalForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(),
                                loanProductId, paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
                    }
                }
            }
        }
    }

    /**
     * Create a single Debit to fund source and a single credit to "Income from
     * Recovery"
     *
     * In case the loan transaction is a reversal, all debits are turned into
     * credits and vice versa
     */
    private void createJournalEntriesForRecoveryRepayments(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal amount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.getValue(), loanProductId, paymentTypeId, amount, isReversal,
                loanDTO.getWriteOffReasonId(), journalEntry);

    }

    /**
     * Recognize the receivable interest <br/>
     * Debit "Interest Receivable" and Credit "Income from Interest"
     *
     * <b>Fees:</b> Debit <i>Fees Receivable</i> and credit <i>Income from
     * Fees</i> <br/>
     *
     * <b>Penalties:</b> Debit <i>Penalties Receivable</i> and credit <i>Income
     * from Penalties</i>
     *
     * Also handles reversals for both fees and payment applications
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForAccruals(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {

        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final boolean isReversed = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        // create journal entries for recognizing interest (or reversal)
        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), loanProductId, paymentTypeId, interestAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
        // create journal entries for the fees application (or reversal)
        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoanCharges(ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), loanProductId, feesAmount, isReversed,
                    loanTransactionDTO.getFeePayments(), journalEntry);
        }
        // create journal entries for the penalties application (or reversal)
        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {

            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoanCharges(
                    ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, penaltiesAmount, isReversed, loanTransactionDTO.getPenaltyPayments(), journalEntry);
        }
    }

    /**
     * Moves Recognized income to suspense account <br/>
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param journalEntry
     */
    private void createJournalEntriesForAccrualSuspense(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {

        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final boolean isReversed = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        // create journal entries for recognizing interest (or reversal)
        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_INTEREST_SUSPENSE.getValue(), loanProductId, paymentTypeId, interestAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
        // create journal entries for the fees application (or reversal)
        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoanChargesWithNPA(
                    ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_FEES_SUSPENSE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(),
                    loanProductId, feesAmount, !isReversed, loanTransactionDTO.getFeePayments(), journalEntry);
        }
        // create journal entries for the penalties application (or reversal)
        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {

            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoanChargesWithNPA(
                    ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_PENALTIES_SUSPENSE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, penaltiesAmount, !isReversed, loanTransactionDTO.getPenaltyPayments(), journalEntry);
        }
    }

    /**
     * Moves income from suspense account to income account <br/>
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param journalEntry
     */
    private void createJournalEntriesForAccrualSuspenseReverse(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {

        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final boolean isReversed = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        // create journal entries for recognizing interest (or reversal)
        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_INTEREST_SUSPENSE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), loanProductId, paymentTypeId, interestAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
        // create journal entries for the fees application (or reversal)
        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoanChargesWithNPA(
                    ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_FEES_SUSPENSE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(),
                    loanProductId, feesAmount, isReversed, loanTransactionDTO.getFeePayments(), journalEntry);
        }
        // create journal entries for the penalties application (or reversal)
        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {

            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoanChargesWithNPA(
                    ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_PENALTIES_SUSPENSE.getValue(), ACCRUAL_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, penaltiesAmount, isReversed, loanTransactionDTO.getPenaltyPayments(), journalEntry);
        }
    }

    /**
     * reduces amount in suspense account and receivable account <br/>
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param journalEntry
     */
    private void createJournalEntriesForAccrualWriteOff(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {

        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final boolean isReversed = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        // create journal entries for recognizing interest (or reversal)
        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_INTEREST_SUSPENSE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_RECEIVABLE.getValue(), loanProductId, paymentTypeId, interestAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
        // create journal entries for the fees application (or reversal)
        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_FEES_SUSPENSE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.FEES_RECEIVABLE.getValue(), loanProductId, paymentTypeId, feesAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
        // create journal entries for the penalties application (or reversal)
        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {

            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.NPA_PENALTIES_SUSPENSE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.PENALTIES_RECEIVABLE.getValue(), loanProductId, paymentTypeId, penaltiesAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
    }

    private void createJournalEntriesForRefund(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal refundAmount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(),
                    FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), loanProductId, paymentTypeId, refundAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else {
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT.getValue(),
                    CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), loanProductId, paymentTypeId, refundAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
    }

    private void createJournalEntriesForRefundForActiveLoan(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // TODO Auto-generated method stub
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final BigDecimal interestAmount = loanTransactionDTO.getInterest();
        final BigDecimal feesAmount = loanTransactionDTO.getFees();
        final BigDecimal penaltiesAmount = loanTransactionDTO.getPenalties();
        final BigDecimal overPaymentAmount = loanTransactionDTO.getOverPayment();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        BigDecimal totalDebitAmount = new BigDecimal(0);

        if (principalAmount != null && !(principalAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(principalAmount);
            this.helper.createCreditJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO, loanProductId, paymentTypeId,
                    principalAmount, !isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        }

        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            this.helper.createCreditJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS, loanProductId, paymentTypeId,
                    interestAmount, !isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        }

        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);

            final List<ChargePaymentDTO> chargePaymentDTOs = new ArrayList<>();

            for (final ChargePaymentDTO chargePaymentDTO : loanTransactionDTO.getFeePayments()) {
                chargePaymentDTOs.add(new ChargePaymentDTO(chargePaymentDTO.getChargeId(), chargePaymentDTO.getLoanChargeId(),
                        chargePaymentDTO.getAmount().floatValue() < 0 ? chargePaymentDTO.getAmount().multiply(new BigDecimal(-1))
                                : chargePaymentDTO.getAmount(),
                        chargePaymentDTO.isCapitalized()));
            }
            this.helper.createCreditJournalEntryOrReversalForLoanCharges(CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), loanProductId,
                    feesAmount, !isReversal, chargePaymentDTOs, journalEntry);
        }

        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            final List<ChargePaymentDTO> chargePaymentDTOs = new ArrayList<>();

            for (final ChargePaymentDTO chargePaymentDTO : loanTransactionDTO.getPenaltyPayments()) {
                chargePaymentDTOs.add(new ChargePaymentDTO(chargePaymentDTO.getChargeId(), chargePaymentDTO.getLoanChargeId(),
                        chargePaymentDTO.getAmount().floatValue() < 0 ? chargePaymentDTO.getAmount().multiply(new BigDecimal(-1))
                                : chargePaymentDTO.getAmount(),
                        chargePaymentDTO.isCapitalized()));
            }

            this.helper.createCreditJournalEntryOrReversalForLoanCharges(CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, penaltiesAmount, !isReversal, chargePaymentDTOs, journalEntry);
        }

        if (overPaymentAmount != null && !(overPaymentAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            this.helper.createCreditJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT, loanProductId, paymentTypeId,
                    overPaymentAmount, !isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        }

        /*** create a single debit entry (or reversal) for the entire amount **/
        this.helper.createDebitJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), loanProductId, paymentTypeId,
                totalDebitAmount, !isReversal, loanDTO.getWriteOffReasonId(), journalEntry);

    }

    private void createJournalEntriesForAddOrRevokeSubsidy(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry, final Integer subsidyTransactionType) {

        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal transactionAmount = loanTransactionDTO.getAmount();
        final boolean isReversed = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        if (subsidyTransactionType.equals(LoanTransactionType.ADD_SUBSIDY.getValue())) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_FUND_SOURCE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.getValue(), loanProductId, paymentTypeId, transactionAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else if (subsidyTransactionType.equals(LoanTransactionType.REVOKE_SUBSIDY.getValue())) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_FUND_SOURCE.getValue(), loanProductId, paymentTypeId, transactionAmount, isReversed,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
    }

    /**
     * Credit loan Portfolio and Debit Suspense Account for a Transfer
     * Initiation. A Transfer acceptance would be treated the opposite i.e Debit
     * Loan Portfolio and Credit Suspense Account <br/>
     *
     * All debits are turned into credits and vice versa in case of Transfer
     * Initiation disbursals
     *
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param office
     */
    private void createJournalEntriesForTransfers(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
        final boolean isReversal = loanTransactionDTO.isReversed();
        // final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        if (loanTransactionDTO.getTransactionType().isInitiateTransfer()) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), loanProductId, null, principalAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else if (loanTransactionDTO.getTransactionType().isApproveTransfer()
                || loanTransactionDTO.getTransactionType().isWithdrawTransfer()) {
            this.helper.createAccrualBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    ACCRUAL_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(), loanProductId, null, principalAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }
    }

    private void createJournalEntriesForBrokenPeriodInterest(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal disbursalAmount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        this.helper.createCashBasedJournalEntriesAndReversalsForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                ACCRUAL_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversal,
                loanDTO.getWriteOffReasonId(), journalEntry);

    }

}
