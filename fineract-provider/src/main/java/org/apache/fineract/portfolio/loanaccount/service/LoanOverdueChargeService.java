package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueChargeData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.joda.time.LocalDate;


public interface LoanOverdueChargeService {

    boolean updateAndApplyOverdueChargesForLoan(Loan loan, LocalDate runOndate, LocalDate brokenPeriodOnDate);

    boolean updateOverdueChargesOnPayment(Loan loan, LocalDate transactionDate);

    void applyOverdueChargesForNonInterestRecalculationLoans(Long loanId, LocalDate runOndate, LocalDate brokenPeriodOnDate);

    boolean updateOverdueChargesOnAdjustPayment(Loan loan, LoanTransaction previousTransaction, LoanTransaction newTransaction);

    LoanOverdueChargeData calculateOverdueChargesAsOnDate(Loan loan, LocalDate runOnDate, LocalDate reverseLoanChargesBefore);

}
