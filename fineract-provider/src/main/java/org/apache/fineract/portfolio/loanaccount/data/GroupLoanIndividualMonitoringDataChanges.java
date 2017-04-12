package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;


public class GroupLoanIndividualMonitoringDataChanges {
    
    @SuppressWarnings("unused")
    private final Long glimId;
    @SuppressWarnings("unused")
    private final BigDecimal transactionAmount;
    
    public GroupLoanIndividualMonitoringDataChanges(Long glimId, BigDecimal transactionAmount) {
        this.glimId = glimId;
        this.transactionAmount = transactionAmount;
    }
    
    public static GroupLoanIndividualMonitoringDataChanges createNew(Long glimId, BigDecimal transactionAmount) {
        return new GroupLoanIndividualMonitoringDataChanges(glimId, transactionAmount);
    }
    
}
