package com.finflux.pdcm.event;

import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;

public interface PDCEventListenerService {

    void adjustLoanTransaction(final LoanTransaction loanTransaction);

    void undoLoanDisbursal(final Loan loan);

}