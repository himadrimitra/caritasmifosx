package org.apache.fineract.portfolio.client.service;

import java.math.BigDecimal;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

public interface CustomerAccountLimitEventListenerService {

    void validateLoanDisbursalAmountWithClientDisbursmentAmountLimit(final Long clientId, final BigDecimal principalAmount);

    void validateLoanDisbursalAmountWithClientCurrentOutstandingAmountLimit(final Loan loan);

    void validateSavingsAccountWithClientDailyWithdrawalAmountLimit(final SavingsAccount savingsAccount,
            final SavingsAccountTransaction withdrawal);

    void validateSavingsAccountWithClientDailyTransferAmountLimit(final SavingsAccount savingsAccount, final JsonCommand jsonCommand);

    void validateSavingsAccountWithClientTotalOverdraftAmountLimit(final SavingsAccount savingsAccount);
}