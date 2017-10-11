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
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SAVINGS;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.SavingsDTO;
import org.apache.fineract.accounting.journalentry.data.SavingsTransactionDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForSavings implements AccountingProcessorForSavings {

    private final AccountingProcessorHelper helper;
    private final JournalEntryRepositoryWrapper journalEntryRepository;

    @Autowired
    public CashBasedAccountingProcessorForSavings(final AccountingProcessorHelper accountingProcessorHelper,
            final JournalEntryRepositoryWrapper journalEntryRepository) {
        this.helper = accountingProcessorHelper;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void createJournalEntriesForSavings(final SavingsDTO savingsDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(savingsDTO.getOfficeId());
        final Long savingsProductId = savingsDTO.getSavingsProductId();
        final Long savingsId = savingsDTO.getSavingsId();
        final String currencyCode = savingsDTO.getCurrencyCode();
        final List<JournalEntry> journalEntryDetails = new ArrayList<>();
        for (final SavingsTransactionDTO savingsTransactionDTO : savingsDTO.getNewSavingsTransactions()) {
            final Date transactionDate = savingsTransactionDTO.getTransactionDate();
            final String transactionId = savingsTransactionDTO.getTransactionId();
            final Long officeId = savingsTransactionDTO.getOfficeId();
            final Long paymentTypeId = savingsTransactionDTO.getPaymentTypeId();
            final boolean isReversal = savingsTransactionDTO.isReversed();
            final BigDecimal amount = savingsTransactionDTO.getAmount();
            final BigDecimal overdraftAmount = savingsTransactionDTO.getOverdraftAmount();
            final List<ChargePaymentDTO> feePayments = savingsTransactionDTO.getFeePayments();
            final List<ChargePaymentDTO> penaltyPayments = savingsTransactionDTO.getPenaltyPayments();

            this.helper.checkForBranchClosures(latestGLClosure, transactionDate);
            final JournalEntry journalEntry = this.helper.createSavingsJournalEntry(currencyCode, transactionDate, transactionDate,
                    transactionDate, transactionId, officeId, savingsId);

            if (savingsTransactionDTO.getTransactionType().isWithdrawal() && savingsTransactionDTO.isOverdraftTransaction()) {
                if (savingsTransactionDTO.isAccountTransfer()) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(),
                            FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), savingsProductId, paymentTypeId, overdraftAmount, isReversal,
                            journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(),
                                savingsProductId, paymentTypeId, amount.subtract(overdraftAmount), isReversal, journalEntry);
                    }
                } else {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue(), savingsProductId, paymentTypeId, overdraftAmount,
                            isReversal, journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(),
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue(), savingsProductId, paymentTypeId,
                                amount.subtract(overdraftAmount), isReversal, journalEntry);
                    }
                }
            } else if (savingsTransactionDTO.getTransactionType().isDeposit() && savingsTransactionDTO.isOverdraftTransaction()) {
                if (savingsTransactionDTO.isAccountTransfer()) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(), savingsProductId, paymentTypeId,
                            overdraftAmount, isReversal, journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavings(FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(),
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId,
                                amount.subtract(overdraftAmount), isReversal, journalEntry);
                    }
                } else {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(), savingsProductId, paymentTypeId,
                            overdraftAmount, isReversal, journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue(),
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId,
                                amount.subtract(overdraftAmount), isReversal, journalEntry);
                    }
                }
            }

            /** Handle Deposits and reversals of deposits **/
            else if (savingsTransactionDTO.getTransactionType().isDeposit()) {
                if (savingsTransactionDTO.isAccountTransfer()) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                            journalEntry);
                } else {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                            journalEntry);
                }
            }

            /** Handle Deposits and reversals of Dividend pay outs **/
            else if (savingsTransactionDTO.getTransactionType().isDividendPayout()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavings(FINANCIAL_ACTIVITY.PAYABLE_DIVIDENDS.getValue(),
                        CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                        journalEntry);
            }
            /** Handle withdrawals and reversals of withdrawals **/
            else if (savingsTransactionDTO.getTransactionType().isWithdrawal()) {
                if (savingsTransactionDTO.isAccountTransfer()) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(),
                            FINANCIAL_ACTIVITY.LIABILITY_TRANSFER.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                            journalEntry);
                } else {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                            journalEntry);
                }
            }

            else if (savingsTransactionDTO.getTransactionType().isEscheat()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(),
                        CASH_ACCOUNTS_FOR_SAVINGS.ESCHEAT_LIABILITY.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                        journalEntry);
            }
            /**
             * Handle Interest Applications and reversals of Interest
             * Applications
             **/
            /**
             * @TODO : Vishwas, this block of code is never entered as a
             *       separate transaction type is used for Overdraft Interest
             **/
            else if (savingsTransactionDTO.getTransactionType().isInterestPosting() && savingsTransactionDTO.isOverdraftTransaction()) {
                // Post journal entry if earned interest amount is greater than
                // zero
                if (savingsTransactionDTO.getAmount().compareTo(BigDecimal.ZERO) == 1) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                            CASH_ACCOUNTS_FOR_SAVINGS.INTEREST_ON_SAVINGS.getValue(),
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(), savingsProductId, paymentTypeId,
                            overdraftAmount, isReversal, journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                                CASH_ACCOUNTS_FOR_SAVINGS.INTEREST_ON_SAVINGS.getValue(),
                                CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId,
                                amount.subtract(overdraftAmount), isReversal, journalEntry);
                    }
                }
            }

            else if (savingsTransactionDTO.getTransactionType().isInterestPosting()) {
                // Post journal entry if earned interest amount is greater than
                // zero
                if (savingsTransactionDTO.getAmount().compareTo(BigDecimal.ZERO) == 1) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                            CASH_ACCOUNTS_FOR_SAVINGS.INTEREST_ON_SAVINGS.getValue(), CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(),
                            savingsProductId, paymentTypeId, amount, isReversal, journalEntry);
                }
            }

            else if (savingsTransactionDTO.getTransactionType().isWithholdTax()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavingsTax(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                        CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_REFERENCE, savingsProductId, paymentTypeId, amount, isReversal,
                        savingsTransactionDTO.getTaxPayments(), journalEntry);
            }

            /** Handle Fees Deductions and reversals of Fees Deductions **/
            /**
             * @TODO : Vishwas, this block of code is never entered as a
             *       separate transaction type is used for Overdraft Fees
             **/
            else if (savingsTransactionDTO.getTransactionType().isFeeDeduction() && savingsTransactionDTO.isOverdraftTransaction()) {
                // Is the Charge a penalty?
                if (penaltyPayments.size() > 0) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavingsCharges(
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL, CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_PENALTIES,
                            savingsProductId, paymentTypeId, overdraftAmount, isReversal, penaltyPayments, journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavingsCharges(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                                CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_PENALTIES, savingsProductId, paymentTypeId,
                                amount.subtract(overdraftAmount), isReversal, penaltyPayments, journalEntry);
                    }
                } else {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavingsCharges(
                            CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL, CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_FEES,
                            savingsProductId, paymentTypeId, overdraftAmount, isReversal, feePayments, journalEntry);
                    if (amount.subtract(overdraftAmount).compareTo(BigDecimal.ZERO) == 1) {
                        this.helper.createCashBasedJournalEntriesAndReversalsForSavingsCharges(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                                CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_FEES, savingsProductId, paymentTypeId,
                                amount.subtract(overdraftAmount), isReversal, feePayments, journalEntry);
                    }
                }
            }

            else if (savingsTransactionDTO.getTransactionType().isFeeDeduction()) {
                // Is the Charge a penalty?
                if (penaltyPayments.size() > 0) {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavingsCharges(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                            CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_PENALTIES, savingsProductId, paymentTypeId, amount, isReversal,
                            penaltyPayments, journalEntry);
                } else {
                    this.helper.createCashBasedJournalEntriesAndReversalsForSavingsCharges(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL,
                            CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_FEES, savingsProductId, paymentTypeId, amount, isReversal, feePayments,
                            journalEntry);
                }

            }

            /** Handle Transfers proposal **/
            else if (savingsTransactionDTO.getTransactionType().isInitiateTransfer()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(),
                        CASH_ACCOUNTS_FOR_SAVINGS.TRANSFERS_SUSPENSE.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                        journalEntry);
            }

            /** Handle Transfer Withdrawal or Acceptance **/
            else if (savingsTransactionDTO.getTransactionType().isWithdrawTransfer()
                    || savingsTransactionDTO.getTransactionType().isApproveTransfer()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.TRANSFERS_SUSPENSE.getValue(),
                        CASH_ACCOUNTS_FOR_SAVINGS.SAVINGS_CONTROL.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                        journalEntry);
            }

            /** overdraft Interest posting, write-off and fees **/
            else if (savingsTransactionDTO.getTransactionType().isOverdraftInterest()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavings(
                        CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(),
                        CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_INTEREST.getValue(), savingsProductId, paymentTypeId, amount, isReversal,
                        journalEntry);
            } else if (savingsTransactionDTO.getTransactionType().isWrittenoff()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForSavings(CASH_ACCOUNTS_FOR_SAVINGS.LOSSES_WRITTEN_OFF.getValue(),
                        CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL.getValue(), savingsProductId, paymentTypeId, amount,
                        isReversal, journalEntry);
            }
            /*
             * else if
             * (savingsTransactionDTO.getTransactionType().isOverdraftFee()) {
             * this.helper.
             * createCashBasedJournalEntriesAndReversalsForSavingsCharges(
             * CASH_ACCOUNTS_FOR_SAVINGS.OVERDRAFT_PORTFOLIO_CONTROL,
             * CASH_ACCOUNTS_FOR_SAVINGS.INCOME_FROM_FEES, savingsProductId,
             * paymentTypeId, amount, isReversal, feePayments, journalEntry); }
             */

            if (!journalEntry.getJournalEntryDetails().isEmpty()) {
                journalEntryDetails.add(journalEntry);
            }
        }

        if (!journalEntryDetails.isEmpty()) {
            this.journalEntryRepository.save(journalEntryDetails);
        }
    }
}
