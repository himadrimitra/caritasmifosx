package org.apache.fineract.portfolio.client.service;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListnerForLimitLoanDisbursalAmount implements BusinessEventListner {

    private final CustomerAccountLimitEventListenerService customerAccountLimitEventListenerService;

    @Autowired
    public EventListnerForLimitLoanDisbursalAmount(final CustomerAccountLimitEventListenerService customerAccountLimitEventListenerService) {
        this.customerAccountLimitEventListenerService = customerAccountLimitEventListenerService;
    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("null")
    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final Object loanEntity = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
        if (loanEntity != null) {
            final Loan loan = (Loan) loanEntity;
            if (loan != null) {
                this.customerAccountLimitEventListenerService.validateLoanDisbursalAmountWithClientDisbursmentAmountLimit(
                        loan.getClientId(), loan.getPrincipalAmount());
                
                this.customerAccountLimitEventListenerService.validateLoanDisbursalAmountWithClientCurrentOutstandingAmountLimit(
                        loan.getClientId(), loan.getPrincipalAmount());
            }
        }

    }

}
