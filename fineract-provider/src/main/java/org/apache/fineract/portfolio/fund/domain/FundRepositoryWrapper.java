/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.domain;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.fund.exception.FundNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundRepositoryWrapper {

    private final PlatformSecurityContext context;
    private final FundRepository fundRepository;

    @Autowired
    public FundRepositoryWrapper(PlatformSecurityContext context, FundRepository fundRepository) {
        this.context = context;
        this.fundRepository = fundRepository;
    }

    public void save(final Fund fund) {
        this.fundRepository.save(fund);
    }

    public Fund findOneWithNotFoundDetection(final Long id) {
        this.context.authenticatedUser();
        final Fund fund = this.fundRepository.findOne(id);
        if (fund == null) { throw new FundNotFoundException(id); }
        return fund;
    }

    public void saveAndFlush(Fund fund) {
        this.fundRepository.saveAndFlush(fund);
    }
}
