package org.apache.fineract.accounting.journalentry.service;

import org.apache.fineract.accounting.journalentry.data.InvestmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AccountingProcessorForInvestmentFactory {
    
    private final ApplicationContext applicationContext;

    @Autowired
    public AccountingProcessorForInvestmentFactory(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /***
     * Looks like overkill for now, but wanted to keep the savings side of
     * accounting identical to that of Loans 
     ***/
    public AccountingProcessorForInvestment determineProcessor(final InvestmentDTO investmentDTO) {

        AccountingProcessorForInvestment accountingProcessorForSavings = null;

        if (investmentDTO.isCashBasedAccountingEnabled()) {
            accountingProcessorForSavings = this.applicationContext.getBean("cashBasedAccountingProcessorForInvestment",
                    AccountingProcessorForInvestment.class);
        }

        return accountingProcessorForSavings;
    }

}
