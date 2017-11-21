package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.data.InvestmentDTO;


public interface AccountingProcessorForInvestment {
    
    void createJournalEntriesForInvestment(InvestmentDTO investmentDTO);
}
