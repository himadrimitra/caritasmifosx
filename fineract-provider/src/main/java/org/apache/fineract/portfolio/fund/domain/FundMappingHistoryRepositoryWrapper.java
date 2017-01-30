/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.domain;

import java.util.Collection;

import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundMappingHistoryRepositoryWrapper {

    private final PlatformSecurityContext context;
    private final FundMappingHistoryRepository fundMappingHistoryRepository;

    @Autowired
    public FundMappingHistoryRepositoryWrapper(PlatformSecurityContext context, FundMappingHistoryRepository fundMappingHistoryRepository) {
        this.context = context;
        this.fundMappingHistoryRepository = fundMappingHistoryRepository;
    }

    public void save(final Collection<FundMappingHistory> FundMappingHistories) {
        this.context.authenticatedUser();
        this.fundMappingHistoryRepository.save(FundMappingHistories);
    }
}
