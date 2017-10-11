/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import java.util.Collection;
import java.util.List;

import org.apache.fineract.portfolio.loanaccount.exception.GroupLoanIndividualMonitoringNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringRepositoryWrapper {

    public final GroupLoanIndividualMonitoringRepository groupLoanIndividualMonitoringRepository;

    @Autowired
    public GroupLoanIndividualMonitoringRepositoryWrapper(GroupLoanIndividualMonitoringRepository groupLoanIndividualMonitoringRepository) {
        this.groupLoanIndividualMonitoringRepository = groupLoanIndividualMonitoringRepository;
    }

    public void save(final GroupLoanIndividualMonitoring entity) {
        this.groupLoanIndividualMonitoringRepository.save(entity);
    }

    public void save(final Collection<GroupLoanIndividualMonitoring> entity) {
        this.groupLoanIndividualMonitoringRepository.save(entity);
    }

    public void delete(final GroupLoanIndividualMonitoring entity) {
        this.groupLoanIndividualMonitoringRepository.delete(entity);
    }

    public GroupLoanIndividualMonitoring findOneWithNotFoundDetection(final Long id) {
        final GroupLoanIndividualMonitoring entity = this.groupLoanIndividualMonitoringRepository.findOne(id);
        if (entity == null) { throw new GroupLoanIndividualMonitoringNotFoundException(id); }
        return entity;
    }
    
    public List<GroupLoanIndividualMonitoring> findByClientId(final Long clientId) {
        final List<GroupLoanIndividualMonitoring> entity = this.groupLoanIndividualMonitoringRepository.findByClientId(clientId);
        if (entity == null) { throw new GroupLoanIndividualMonitoringNotFoundException(clientId); }
        return entity;
    }
    
    public List<GroupLoanIndividualMonitoring> findByLoanId(final Long loanId) {
        final List<GroupLoanIndividualMonitoring> entity = this.groupLoanIndividualMonitoringRepository.findByLoanId(loanId);
        if (entity == null) { throw new GroupLoanIndividualMonitoringNotFoundException(loanId); }
        return entity;
    }
    
}
