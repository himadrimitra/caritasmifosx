package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.data.LoanTransactionData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.joda.time.LocalDate;

public interface LoanCalculationReadService {

    LoanTransactionData retrieveLoanPrePaymentTemplate(LocalDate onDate, boolean calcualteInterestTillDate, Loan loan);

    LoanTransactionData retrieveLoanPrePaymentTemplate(Long loanId, LocalDate onDate, boolean calcualteInterestTillDate);

    LoanTransactionData retrieveLoanForeclosureTemplate(Long loanId, LocalDate transactionDate);

    LoanOverdueChargeData retrieveLoanOverdueChargeDetailAsOnDate(Long loanId, LocalDate date);

}
