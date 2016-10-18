/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.domain;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.reconcilation.bank.exception.BankNotFoundException;

@Service
public class BankRepositoryWrapper {

    @SuppressWarnings("unused")
    private final PlatformSecurityContext context;
    private final BankRepository bankRepository;

    @Autowired
    public BankRepositoryWrapper(final PlatformSecurityContext context, final BankRepository bankRepository) {
        this.context = context;
        this.bankRepository = bankRepository;
    }

    public void save(final Bank bank) {
        this.bankRepository.save(bank);
    }

    public void delete(final Bank bank) {
        this.bankRepository.delete(bank);
    }

    public Bank findOneWithNotFoundDetection(final Long id){
        final Bank bank = this.bankRepository.findOne(id);
        if (bank == null) { throw new BankNotFoundException(id); }
        return bank;
    }
}
