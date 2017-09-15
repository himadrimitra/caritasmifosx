package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.joda.time.LocalDate;


public interface LoanOverdueChargeService {

    boolean applyOverdueChargesForLoan(Loan loan, LocalDate runOndate);

    boolean updateOverdueChargesOnPayment(Loan loan, LocalDate transactionDate);

}
