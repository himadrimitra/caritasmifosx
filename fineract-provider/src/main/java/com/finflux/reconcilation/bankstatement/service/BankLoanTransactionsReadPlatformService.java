package com.finflux.reconcilation.bankstatement.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;

public interface BankLoanTransactionsReadPlatformService {

    public Collection<LoanTransactionData> retrieveLoanTransactionsForGroup(final BigDecimal amount,
            final String groupExternalId, String transactionType);

    public Collection<LoanTransactionData> retrieveLoanTransactionsForBankDetails(Long loanTransactionId);
}
