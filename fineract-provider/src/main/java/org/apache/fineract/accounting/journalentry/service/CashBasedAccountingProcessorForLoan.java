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
import java.util.List;

import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.common.AccountingConstants.ACCRUAL_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_LOAN;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.LoanDTO;
import org.apache.fineract.accounting.journalentry.data.LoanTransactionDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForLoan implements AccountingProcessorForLoan {

    private final AccountingProcessorHelper helper;
    private final JournalEntryRepositoryWrapper journalEntryRepository;

    @Autowired
    public CashBasedAccountingProcessorForLoan(final AccountingProcessorHelper accountingProcessorHelper,
            final JournalEntryRepositoryWrapper journalEntryRepository) {
        this.helper = accountingProcessorHelper;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void createJournalEntriesForLoan(final LoanDTO loanDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(loanDTO.getOfficeId());
        // final Long officeId =
        // this.helper.getOfficeById(loanDTO.getOfficeId());
        final Long loanProductId = loanDTO.getLoanProductId();
        final String currencyCode = loanDTO.getCurrencyCode();
        final List<JournalEntry> journalEntryDetails = new ArrayList<>();
        for (final LoanTransactionDTO loanTransactionDTO : loanDTO.getNewLoanTransactions()) {
            final Date transactionDate = loanTransactionDTO.getTransactionDate();
            final String transactionId = loanTransactionDTO.getTransactionId();
            final Long officeId = loanTransactionDTO.getOfficeId();
            final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();
            final Long loanId = loanDTO.getLoanId();

            final JournalEntry journalEntry = this.helper.createLoanJournalEntry(currencyCode, transactionDate, transactionDate,
                    transactionDate, transactionId, officeId, loanId);

            if (!loanTransactionDTO.getTransactionType().isAccrual()) {
                this.helper.checkForBranchClosures(latestGLClosure, transactionDate);
            }

            /** Handle Disbursements and reversals of disbursements **/
            if (loanTransactionDTO.getTransactionType().isDisbursement()) {
                createJournalEntriesForDisbursements(loanDTO, loanTransactionDTO, journalEntry);
            }
            /*** Handle AddSubsidy ***/
            else if (loanTransactionDTO.getTransactionType().isAddSubsidy()) {
                createJournalEntriesForAddOrRevokeSubsidy(loanDTO, loanTransactionDTO, LoanTransactionType.ADD_SUBSIDY.getValue(),
                        journalEntry);
            }

            /*** Handle RevokeSubsidy ***/
            else if (loanTransactionDTO.getTransactionType().isRevokeSubsidy()) {
                createJournalEntriesForAddOrRevokeSubsidy(loanDTO, loanTransactionDTO, LoanTransactionType.REVOKE_SUBSIDY.getValue(),
                        journalEntry);
            }
            /***
             * Logic for repayments, repayments at disbursement and reversal of
             * Repayments and Repayments at disbursement
             ***/
            else if (loanTransactionDTO.getTransactionType().isRepayment()
                    || loanTransactionDTO.getTransactionType().isRepaymentAtDisbursement()
                    || loanTransactionDTO.getTransactionType().isChargePayment()) {
                createJournalEntriesForRepayments(loanDTO, loanTransactionDTO, journalEntry);
            }

            /** Logic for handling recovery payments **/
            else if (loanTransactionDTO.getTransactionType().isRecoveryRepayment()) {
                createJournalEntriesForRecoveryRepayments(loanDTO, loanTransactionDTO, journalEntry);
            }

            /** Logic for Refunds of Overpayments **/
            else if (loanTransactionDTO.getTransactionType().isRefund()) {
                createJournalEntriesForRefund(loanDTO, loanTransactionDTO, journalEntry);
            }
            /***
             * Only principal write off affects cash based accounting (interest
             * and fee write off need not be considered). Debit losses written
             * off and credit Loan Portfolio
             **/
            else if (loanTransactionDTO.getTransactionType().isWriteOff()) {
                final BigDecimal principalAmount = loanTransactionDTO.getPrincipal();
                if (principalAmount != null && !(principalAmount.compareTo(BigDecimal.ZERO) == 0)) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.LOSSES_WRITTEN_OFF.getValue(),
                            CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), loanProductId, paymentTypeId, principalAmount,
                            loanTransactionDTO.isReversed(), loanDTO.getWriteOffReasonId(), journalEntry);
                }
            } else if (loanTransactionDTO.getTransactionType().isInitiateTransfer()
                    || loanTransactionDTO.getTransactionType().isApproveTransfer()
                    || loanTransactionDTO.getTransactionType().isWithdrawTransfer()) {
                createJournalEntriesForTransfers(loanDTO, loanTransactionDTO, journalEntry);
            }
            /** Logic for Refunds of Active Loans **/
            else if (loanTransactionDTO.getTransactionType().isRefundForActiveLoans()) {
                createJournalEntriesForRefundForActiveLoan(loanDTO, loanTransactionDTO, journalEntry);
            } else if (loanTransactionDTO.getTransactionType().isBrokenPeriodInterestPosting()) {
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
     * Debit loan Portfolio and credit Fund source for a Disbursement <br/>
     *
     * All debits are turned into credits and vice versa in case of disbursement
     * reversals
     *
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param journalEntryDetails
     *            TODO
     */
    private void createJournalEntriesForDisbursements(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal disbursalAmount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();
        if (loanTransactionDTO.isLoanToLoanTransfer()) {
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    FINANCIAL_ACTIVITY.ASSET_TRANSFER.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else {
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        }

    }

    /**
     * Debit loan Portfolio and credit Fund source for a Disbursement <br/>
     *
     * All debits are turned into credits and vice versa in case of disbursement
     * reversals
     *
     *
     * @param loanDTO
     * @param loanTransactionDTO
     * @param journalEntryDetails
     *            TODO
     */
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

    /**
     * Create a single Debit to fund source and multiple credits if applicable
     * (loan portfolio for principal repayments, Interest on loans for interest
     * repayments, Income from fees for fees payment and Income from penalties
     * for penalty payment)
     *
     * In case the loan transaction is a reversal, all debits are turned into
     * credits and vice versa
     *
     * @param journalEntryDetails
     *            TODO
     */
    private void createJournalEntriesForRepayments(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
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
                    principalAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        }

        if (interestAmount != null && !(interestAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(interestAmount);
            this.helper.createCreditJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS, loanProductId, paymentTypeId,
                    interestAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        }

        if (feesAmount != null && !(feesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(feesAmount);
            this.helper.createCreditJournalEntryOrReversalForLoanCharges(CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_FEES.getValue(), loanProductId,
                    feesAmount, isReversal, loanTransactionDTO.getFeePayments(), journalEntry);
        }

        if (penaltiesAmount != null && !(penaltiesAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(penaltiesAmount);
            this.helper.createCreditJournalEntryOrReversalForLoanCharges(CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_PENALTIES.getValue(),
                    loanProductId, penaltiesAmount, isReversal, loanTransactionDTO.getPenaltyPayments(), journalEntry);
        }

        if (overPaymentAmount != null && !(overPaymentAmount.compareTo(BigDecimal.ZERO) == 0)) {
            totalDebitAmount = totalDebitAmount.add(overPaymentAmount);
            this.helper.createCreditJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.OVERPAYMENT, loanProductId, paymentTypeId,
                    overPaymentAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        }

        /*** create a single debit entry (or reversal) for the entire amount **/
        if (loanTransactionDTO.isLoanToLoanTransfer()) {
            this.helper.createDebitJournalEntryOrReversalForLoan(FINANCIAL_ACTIVITY.ASSET_TRANSFER.getValue(), loanProductId, paymentTypeId,
                    totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        } else if (loanTransactionDTO.isAccountTransfer()) {
            this.helper.createDebitJournalEntryOrReversalForLoan(FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), loanProductId,
                    paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
        } else {
            if (loanTransactionDTO.getTransactionSubType().isRealizationSubsidy()) {
                this.helper.createDebitJournalEntryOrReversalForLoan(ACCRUAL_ACCOUNTS_FOR_LOAN.SUBSIDY_ACCOUNT.getValue(), loanProductId,
                        paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
            } else {
                this.helper.createDebitJournalEntryOrReversalForLoan(CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(), loanProductId,
                        paymentTypeId, totalDebitAmount, isReversal, loanDTO.getWriteOffReasonId(), journalEntry);
            }

        }
    }

    /**
     * Create a single Debit to fund source and a single credit to "Income from
     * Recovery"
     *
     * In case the loan transaction is a reversal, all debits are turned into
     * credits and vice versa
     *
     * @param journalEntryDetails
     *            TODO
     */
    private void createJournalEntriesForRecoveryRepayments(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal amount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.FUND_SOURCE.getValue(),
                CASH_ACCOUNTS_FOR_LOAN.INCOME_FROM_RECOVERY.getValue(), loanProductId, paymentTypeId, amount, isReversal,
                loanDTO.getWriteOffReasonId(), journalEntry);

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
     * @param journalEntryDetails
     *            TODO
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
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(),
                    CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(), loanProductId, null, principalAmount, isReversal,
                    loanDTO.getWriteOffReasonId(), journalEntry);
        } else if (loanTransactionDTO.getTransactionType().isApproveTransfer()
                || loanTransactionDTO.getTransactionType().isWithdrawTransfer()) {
            this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                    CASH_ACCOUNTS_FOR_LOAN.TRANSFERS_SUSPENSE.getValue(), loanProductId, null, principalAmount, isReversal,
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
            final Integer subsidyTransactionType, final JournalEntry journalEntry) {

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

    private void createJournalEntriesForBrokenPeriodInterest(final LoanDTO loanDTO, final LoanTransactionDTO loanTransactionDTO,
            final JournalEntry journalEntry) {
        // loan properties
        final Long loanProductId = loanDTO.getLoanProductId();

        // transaction properties
        final BigDecimal disbursalAmount = loanTransactionDTO.getAmount();
        final boolean isReversal = loanTransactionDTO.isReversed();
        final Long paymentTypeId = loanTransactionDTO.getPaymentTypeId();

        this.helper.createCashBasedJournalEntriesAndReversalsForLoan(CASH_ACCOUNTS_FOR_LOAN.LOAN_PORTFOLIO.getValue(),
                CASH_ACCOUNTS_FOR_LOAN.INTEREST_ON_LOANS.getValue(), loanProductId, paymentTypeId, disbursalAmount, isReversal,
                loanDTO.getWriteOffReasonId(), journalEntry);

    }
}
