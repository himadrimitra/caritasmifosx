package com.finflux.pdcm.event;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.loanaccount.domain.LoanTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListnerOnPDCAdjustLoanTransaction implements BusinessEventListner {

    private final PDCEventListenerService pdcEventListenerService;

    @Autowired
    public EventListnerOnPDCAdjustLoanTransaction(final PDCEventListenerService pdcEventListenerService) {
        this.pdcEventListenerService = pdcEventListenerService;
    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("null")
    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final Object loanTransactionEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN_ADJUSTED_TRANSACTION);
        if (loanTransactionEntity != null) {
            final LoanTransaction loanTransaction = (LoanTransaction) loanTransactionEntity;
            if (loanTransaction != null) {
                this.pdcEventListenerService.adjustLoanTransaction(loanTransaction);
            }
        }
    }
}
