/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.fund.event;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundEventListnerOnUndoLoanDisbursal implements BusinessEventListner {

    private final FundMappingHistoryEventListenerService fundMappingHistoryEventListenerService;

    @Autowired
    public FundEventListnerOnUndoLoanDisbursal(FundMappingHistoryEventListenerService fundMappingHistoryEventListenerService) {
        this.fundMappingHistoryEventListenerService = fundMappingHistoryEventListenerService;
    }

    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
        if (loanEntity != null) {
            final Loan loan = (Loan) loanEntity;
            if (loan != null) {
                this.fundMappingHistoryEventListenerService.deleteFundMappingHistory(loan);
            }
        }
    }

}
