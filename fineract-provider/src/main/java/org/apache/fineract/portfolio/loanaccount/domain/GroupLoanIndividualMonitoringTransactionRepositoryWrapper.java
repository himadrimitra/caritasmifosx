/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Collection;

import org.apache.fineract.portfolio.loanaccount.exception.GroupLoanIndividualMonitoringTransactionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringTransactionRepositoryWrapper {

    public final GroupLoanIndividualMonitoringTransactionRepository groupLoanIndividualMonitoringTransactionRepository;

    @Autowired
    public GroupLoanIndividualMonitoringTransactionRepositoryWrapper(
            final GroupLoanIndividualMonitoringTransactionRepository groupLoanIndividualMonitoringTransactionRepository) {
        this.groupLoanIndividualMonitoringTransactionRepository = groupLoanIndividualMonitoringTransactionRepository;
    }

    public void saveAsList(final Collection<GroupLoanIndividualMonitoringTransaction> groupLoanIndividualMonitoringTransaction) {
        this.groupLoanIndividualMonitoringTransactionRepository.save(groupLoanIndividualMonitoringTransaction);
    }

    public void save(final GroupLoanIndividualMonitoringTransaction groupLoanIndividualMonitoringTransaction) {
        this.groupLoanIndividualMonitoringTransactionRepository.save(groupLoanIndividualMonitoringTransaction);
    }

    public GroupLoanIndividualMonitoringTransaction findOneWithNotFoundDetection(final Long id) {
        final GroupLoanIndividualMonitoringTransaction entity = this.groupLoanIndividualMonitoringTransactionRepository.findOne(id);
        if (entity == null) { throw new GroupLoanIndividualMonitoringTransactionNotFoundException(id); }
        return entity;
    }
}
