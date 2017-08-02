/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.event;

import java.util.Date;
import java.util.List;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.fund.domain.FundMappingHistory;
import org.apache.fineract.portfolio.fund.domain.FundMappingHistoryRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundMappingHistoryEventListenerServiceImpl implements FundMappingHistoryEventListenerService {

    private final PlatformSecurityContext context;
    private final FundMappingHistoryRepository fundMappingHistoryRepository;

    @Autowired
    public FundMappingHistoryEventListenerServiceImpl(final PlatformSecurityContext context,
            final FundMappingHistoryRepository fundMappingHistoryRepository) {
        this.context = context;
        this.fundMappingHistoryRepository = fundMappingHistoryRepository;
    }

    @Override
    public void updateFundMappingHistory(Loan loan) {
        if (loan.getFund() != null) {
            Date currentDate = DateUtils.getLocalDateOfTenant().toDate();
            FundMappingHistory fundMappingHIstory = FundMappingHistory.instance(loan, loan.getFund(), this.context.authenticatedUser(),
                    currentDate, loan.getFund().getAssignmentEndDate());
            this.fundMappingHistoryRepository.save(fundMappingHIstory);
        }
    }

    @Override
    public void deleteFundMappingHistory(Loan loan) {
        List<FundMappingHistory> fundMappingHIstory = this.fundMappingHistoryRepository.findPreviousHistoryByLoan(loan.getId());
        if (fundMappingHIstory.size() > 0) {
            this.fundMappingHistoryRepository.delete(fundMappingHIstory);
        }
    }

}
