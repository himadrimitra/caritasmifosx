package org.apache.fineract.portfolio.loanaccount.service;

import org.apache.fineract.portfolio.loanaccount.data.LoanOverdueCalculationDTO;

public interface LoanOutstandingCalculation {

    public void calculateAndUpdateCurrentOutstanding(final LoanOverdueCalculationDTO loanOverdueCalculationDetail);

}
