/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.domain;

import java.util.Collection;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bankstatement.exception.BankStatementDetailNotFoundException;

@Service
public class BankStatementDetailsRepositoryWrapper {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final BankStatementDetailsRepository bankStatementDetailsRepository;

    @Autowired
    private BankStatementDetailsRepositoryWrapper(final PlatformSecurityContext context,
            final BankStatementDetailsRepository bankStatementDetailsRepository) {
        this.context = context;
        this.bankStatementDetailsRepository = bankStatementDetailsRepository;
    }

    public void save(final BankStatementDetails bankStatementDetails) {
        this.bankStatementDetailsRepository.save(bankStatementDetails);
    }

    public void save(final Collection<BankStatementDetails> bankStatementDetails) {
        this.bankStatementDetailsRepository.save(bankStatementDetails);
    }

    public void delete(final BankStatementDetails bankStatementDetails) {
        this.bankStatementDetailsRepository.delete(bankStatementDetails);
    }

    public BankStatementDetails findOneWithNotFoundDetection(final Long id){
        final BankStatementDetails bankStatementDetails = this.bankStatementDetailsRepository.findOne(id);
        if (bankStatementDetails == null) { throw new BankStatementDetailNotFoundException(id); }
        return bankStatementDetails;
    }
}
