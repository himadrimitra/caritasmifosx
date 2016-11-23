package org.apache.fineract.portfolio.loanaccount.service;

import java.util.List;

import org.apache.fineract.portfolio.loanaccount.data.GroupLoanIndividualMonitoringTransactionData;


public interface GroupLoanIndividualMonitoringTransactionReadPlatformService {
    
    List<GroupLoanIndividualMonitoringTransactionData> retriveGlimTransaction(Long transactionId);

}
