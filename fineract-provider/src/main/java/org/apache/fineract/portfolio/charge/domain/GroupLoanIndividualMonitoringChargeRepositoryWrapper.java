/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.charge.domain;

import java.util.Collection;

import org.apache.fineract.portfolio.charge.exception.GroupLoanIndividualMonitoringChargeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupLoanIndividualMonitoringChargeRepositoryWrapper {

    private final GroupLoanIndividualMonitoringChargeRepository groupLoanIndividualMonitoringChargeRepository;

    @Autowired
    public GroupLoanIndividualMonitoringChargeRepositoryWrapper(
            GroupLoanIndividualMonitoringChargeRepository groupLoanIndividualMonitoringChargeRepository) {
        this.groupLoanIndividualMonitoringChargeRepository = groupLoanIndividualMonitoringChargeRepository;
    }

    public void save(final GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge) {
        this.groupLoanIndividualMonitoringChargeRepository.save(groupLoanIndividualMonitoringCharge);
    }

    public void save(final Collection<GroupLoanIndividualMonitoringCharge> groupLoanIndividualMonitoringCharges) {
        this.groupLoanIndividualMonitoringChargeRepository.save(groupLoanIndividualMonitoringCharges);
    }

    public void delete(final GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge) {
        this.groupLoanIndividualMonitoringChargeRepository.delete(groupLoanIndividualMonitoringCharge);
    }

    public GroupLoanIndividualMonitoringCharge findOneWithNotFoundDetection(final Long id) {
        final GroupLoanIndividualMonitoringCharge groupLoanIndividualMonitoringCharge = this.groupLoanIndividualMonitoringChargeRepository
                .findOne(id);
        if (groupLoanIndividualMonitoringCharge == null) { throw new GroupLoanIndividualMonitoringChargeNotFoundException(id); }
        return groupLoanIndividualMonitoringCharge;
    }
}
