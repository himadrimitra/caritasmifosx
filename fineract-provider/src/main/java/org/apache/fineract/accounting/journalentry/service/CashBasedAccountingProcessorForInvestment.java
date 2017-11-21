package org.apache.fineract.accounting.journalentry.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.fineract.accounting.closure.domain.GLClosure;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_INVESTMENT;
import org.apache.fineract.accounting.common.AccountingConstants.CASH_ACCOUNTS_FOR_SAVINGS;
import org.apache.fineract.accounting.common.AccountingConstants.FINANCIAL_ACTIVITY;
import org.apache.fineract.accounting.journalentry.data.ChargePaymentDTO;
import org.apache.fineract.accounting.journalentry.data.InvestmentDTO;
import org.apache.fineract.accounting.journalentry.data.InvestmentTransactionDTO;
import org.apache.fineract.accounting.journalentry.data.SavingsTransactionDTO;
import org.apache.fineract.accounting.journalentry.domain.JournalEntry;
import org.apache.fineract.accounting.journalentry.domain.JournalEntryRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CashBasedAccountingProcessorForInvestment implements AccountingProcessorForInvestment{
    
    private final AccountingProcessorHelper helper;
    private final JournalEntryRepositoryWrapper journalEntryRepository;

    @Autowired
    public CashBasedAccountingProcessorForInvestment(final AccountingProcessorHelper accountingProcessorHelper,
            final JournalEntryRepositoryWrapper journalEntryRepository) {
        this.helper = accountingProcessorHelper;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void createJournalEntriesForInvestment(InvestmentDTO investmentDTO) {

        final GLClosure latestGLClosure = this.helper.getLatestClosureByBranch(investmentDTO.getOfficeId());
        final Long investmentProductId = investmentDTO.getInvestmentProductId();
        final Long investmentId = investmentDTO.getInvestmentId();
        final String currencyCode = investmentDTO.getCurrencyCode();
        final List<JournalEntry> journalEntryDetails = new ArrayList<>();
        for (final InvestmentTransactionDTO investmentTransactionDTO : investmentDTO.getNewInvestmentTransactions()) {
            final Date transactionDate = investmentTransactionDTO.getTransactionDate();
            final String transactionId = investmentTransactionDTO.getTransactionId();
            final Long officeId = investmentTransactionDTO.getOfficeId();
            final boolean isReversal = investmentTransactionDTO.isReversed();
            final BigDecimal amount = investmentTransactionDTO.getAmount();

            this.helper.checkForBranchClosures(latestGLClosure, transactionDate);
            final JournalEntry journalEntry = this.helper.createSavingsJournalEntry(currencyCode, transactionDate, transactionDate,
                    transactionDate, transactionId, officeId, investmentId);

            if (investmentTransactionDTO.getTransactionType().isDeposit()) {
                this.helper.createCashBasedJournalEntriesAndReversalsForInvestment(CASH_ACCOUNTS_FOR_INVESTMENT.FUND_SOURCE.getValue(), CASH_ACCOUNTS_FOR_INVESTMENT.FUND_SOURCE.getValue(), investmentProductId, amount, isReversal, journalEntry);
            }

            if (!journalEntry.getJournalEntryDetails().isEmpty()) {
                journalEntryDetails.add(journalEntry);
            }
        }

        if (!journalEntryDetails.isEmpty()) {
            this.journalEntryRepository.save(journalEntryDetails);
        }
    
        
    }

}
