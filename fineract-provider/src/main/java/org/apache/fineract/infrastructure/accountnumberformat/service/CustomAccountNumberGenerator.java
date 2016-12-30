package org.apache.fineract.infrastructure.accountnumberformat.service;

import org.apache.fineract.infrastructure.accountnumberformat.domain.AccountNumberFormat;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;

public interface CustomAccountNumberGenerator {

	String generateAccountNumberForSavings(SavingsAccount savingsAccount, AccountNumberFormat accountNumberFormat);

	String generateAccountNumberForLoans(Loan loan, AccountNumberFormat accountNumberFormat);

}
