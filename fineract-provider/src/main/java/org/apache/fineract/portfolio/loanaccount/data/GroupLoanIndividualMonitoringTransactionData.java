/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.data;

import java.math.BigDecimal;
import java.util.Collection;

public class GroupLoanIndividualMonitoringTransactionData {

    private final BigDecimal transactionAmount;
    private final Collection<GroupLoanIndividualMonitoringData> clientMembers;

    public GroupLoanIndividualMonitoringTransactionData(final BigDecimal transactionAmount,
            final Collection<GroupLoanIndividualMonitoringData> groupMemberDetails) {
        this.transactionAmount = transactionAmount;
        this.clientMembers = groupMemberDetails;
    }

    public BigDecimal getTransactionAmount() {
        return this.transactionAmount;
    }

    public Collection<GroupLoanIndividualMonitoringData> getGroupMemberDetails() {
        return this.clientMembers;
    }
}
