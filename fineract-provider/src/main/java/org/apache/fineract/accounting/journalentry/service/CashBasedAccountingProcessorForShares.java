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
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SHARES;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.SharesDTO;
import org.apache.fineract.accounting.journalentry.data.SharesTransactionDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForShares implements AccountingProcessorForShares {

    private final AccountingProcessorHelper helper;
    private final JournalEntryRepositoryWrapper journalEntryRepository;

    @Autowired
    public CashBasedAccountingProcessorForShares(final AccountingProcessorHelper helper,
            final JournalEntryRepositoryWrapper journalEntryRepository) {
        this.helper = helper;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void createJournalEntriesForShares(SharesDTO sharesDTO) {
        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(sharesDTO.getOfficeId());
        final Long shareAccountId = sharesDTO.getShareAccountId();
        final Long shareProductId = sharesDTO.getShareProductId();
        final String currencyCode = sharesDTO.getCurrencyCode();
        List<JournalEntry> journalEntryDetails = new ArrayList<>();
        for (SharesTransactionDTO transactionDTO : sharesDTO.getNewTransactions()) {
            final Date transactionDate = transactionDTO.getTransactionDate();
            final String transactionId = transactionDTO.getTransactionId();
            final Long officeId = transactionDTO.getOfficeId();
            final Long paymentTypeId = transactionDTO.getPaymentTypeId();
            final BigDecimal amount = transactionDTO.getAmount();
            final BigDecimal chargeAmount = transactionDTO.getChargeAmount();
            final List<ChargePaymentDTO> feePayments = transactionDTO.getFeePayments();

            this.helper.checkForBranchClosures(latestGLClosure, transactionDate);

            JournalEntry journalEntry = this.helper.createShareJournalEntry(currencyCode, transactionDate, transactionDate,
                    transactionDate, transactionId, officeId, shareAccountId);

            if (transactionDTO.getTransactionType().isPurchased()) {
                createJournalEntriesForPurchase(shareProductId, transactionDTO, journalEntry, paymentTypeId, amount, chargeAmount,
                        feePayments);
            } else if (transactionDTO.getTransactionType().isRedeemed() && transactionDTO.getTransactionStatus().isApproved()) {
                createJournalEntriesForRedeem(shareProductId, journalEntry, paymentTypeId, amount, chargeAmount, feePayments);

            } else if (transactionDTO.getTransactionType().isChargePayment()) {
                this.helper.createCashBasedJournalEntriesForSharesCharges(CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE,
                        CASH_ACCOUNTS_FOR_SHARES.INCOME_FROM_FEES, shareProductId, paymentTypeId, amount, feePayments, journalEntry);
            }
            if (!journalEntry.getJournalEntryDetails().isEmpty()) {
                journalEntryDetails.add(journalEntry);
            }
        }

        if (!journalEntryDetails.isEmpty()) {
            this.journalEntryRepository.save(journalEntryDetails);
        }

    }

    public void createJournalEntriesForRedeem(final Long shareProductId, final JournalEntry journalEntry, final Long paymentTypeId,
            final BigDecimal amount, final BigDecimal chargeAmount, final List<ChargePaymentDTO> feePayments) {
        if (chargeAmount == null || chargeAmount.compareTo(BigDecimal.ZERO) != 1) {
            this.helper.createJournalEntriesForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_EQUITY.getValue(),
                    CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE.getValue(), shareProductId, paymentTypeId, amount, journalEntry);
        } else {
            this.helper.createDebitJournalEntryForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_EQUITY.getValue(), shareProductId, paymentTypeId,
                    amount.add(chargeAmount), journalEntry);
            this.helper.createCreditJournalEntryForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE.getValue(), shareProductId,
                    paymentTypeId, amount, journalEntry);
            this.helper.createCashBasedJournalEntryForSharesCharges(CASH_ACCOUNTS_FOR_SHARES.INCOME_FROM_FEES, shareProductId,
                    chargeAmount, feePayments, journalEntry);
        }
    }

    public void createJournalEntriesForPurchase(final Long shareProductId, SharesTransactionDTO transactionDTO,
            final JournalEntry journalEntry, final Long paymentTypeId, final BigDecimal amount, final BigDecimal chargeAmount,
            final List<ChargePaymentDTO> feePayments) {
        if (transactionDTO.getTransactionStatus().isApplied()) {
            if (chargeAmount == null || chargeAmount.compareTo(BigDecimal.ZERO) != 1) {
                this.helper.createJournalEntriesForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE.getValue(),
                        CASH_ACCOUNTS_FOR_SHARES.SHARES_SUSPENSE.getValue(), shareProductId, paymentTypeId, amount, journalEntry);
            } else {
                this.helper.createDebitJournalEntryForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE.getValue(), shareProductId,
                        paymentTypeId, amount, journalEntry);
                this.helper.createCreditJournalEntryForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_SUSPENSE.getValue(), shareProductId,
                        paymentTypeId, amount.subtract(chargeAmount), journalEntry);
                this.helper.createCashBasedJournalEntryForSharesCharges(CASH_ACCOUNTS_FOR_SHARES.INCOME_FROM_FEES, shareProductId,
                        chargeAmount, feePayments, journalEntry);
            }
        } else if (transactionDTO.getTransactionStatus().isApproved()) {
            BigDecimal amountForJE = amount;
            if (chargeAmount != null && chargeAmount.compareTo(BigDecimal.ZERO) == 1) {
                amountForJE = amount.subtract(chargeAmount);
            }
            this.helper.createJournalEntriesForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_SUSPENSE.getValue(),
                    CASH_ACCOUNTS_FOR_SHARES.SHARES_EQUITY.getValue(), shareProductId, paymentTypeId, amountForJE, journalEntry);
        } else if (transactionDTO.getTransactionStatus().isRejected()) {
            BigDecimal amountForJE = amount;
            if (chargeAmount != null && chargeAmount.compareTo(BigDecimal.ZERO) == 1) {
                amountForJE = amount.subtract(chargeAmount);
                /*
                 * this.helper.revertCashBasedJournalEntryForSharesCharges(office
                 * , currencyCode, CASH_ACCOUNTS_FOR_SHARES.INCOME_FROM_FEES,
                 * shareProductId, shareAccountId, transactionId,
                 * transactionDate, chargeAmount, feePayments);
                 */
            }
            this.helper.createJournalEntriesForShares(CASH_ACCOUNTS_FOR_SHARES.SHARES_SUSPENSE.getValue(),
                    CASH_ACCOUNTS_FOR_SHARES.SHARES_REFERENCE.getValue(), shareProductId, paymentTypeId, amountForJE, journalEntry);
        }
    }

}
