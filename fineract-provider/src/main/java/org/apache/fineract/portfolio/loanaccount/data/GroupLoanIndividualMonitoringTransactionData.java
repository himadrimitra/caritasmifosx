/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Collection;

public class GroupLoanIndividualMonitoringTransactionData {

    private final Long id;
    private final Long glimId;
    private final Long clientId;
    private final String clientName;
    private final Long loanTransactionId;
    private final LoanTransactionEnumData transactionType;
    private final BigDecimal principalPortion;
    private final BigDecimal interestPortion;
    private final BigDecimal feePortion;
    private final BigDecimal penaltyPortion;
    private final BigDecimal transactionAmount;
    private final Collection<GroupLoanIndividualMonitoringData> clientMembers;
    

    public GroupLoanIndividualMonitoringTransactionData(final BigDecimal transactionAmount,
            final Collection<GroupLoanIndividualMonitoringData> groupMemberDetails) {
        this.id = null;
        this.glimId = null;
        this.clientId = null;
        this.clientName = null;
        this.loanTransactionId = null;
        this.transactionType = null;
        this.principalPortion = null;
        this.interestPortion = null;
        this.feePortion = null;
        this.penaltyPortion = null;
        this.transactionAmount = transactionAmount;
        this.clientMembers = groupMemberDetails;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public Collection<GroupLoanIndividualMonitoringData> getGroupMemberDetails() {
        return this.clientMembers;
    }
    
    public static GroupLoanIndividualMonitoringTransactionData createNew(final Long id, final Long glimId, Long clientId, String clientName,
            Long loanTransactionId, final LoanTransactionEnumData transactionType, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feePortion, final BigDecimal penaltyPortion, final BigDecimal transactionAmount) {
        return new GroupLoanIndividualMonitoringTransactionData(id, glimId, clientId, clientName, loanTransactionId, transactionType, principalPortion,
                interestPortion, feePortion, penaltyPortion, transactionAmount);
    }
    
    public GroupLoanIndividualMonitoringTransactionData(final Long id, final Long glimId, Long clientId, String clientName, Long loanTransactionId,
            final LoanTransactionEnumData transactionType, final BigDecimal principalPortion, final BigDecimal interestPortion,
            final BigDecimal feePortion, final BigDecimal penaltyPortion, final BigDecimal transactionAmount) {
        this.id = id;
        this.glimId = glimId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.loanTransactionId = loanTransactionId;
        this.transactionType = transactionType;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.feePortion = feePortion;
        this.penaltyPortion = penaltyPortion;
        this.transactionAmount = transactionAmount;
        this.clientMembers = null;
    }
}
