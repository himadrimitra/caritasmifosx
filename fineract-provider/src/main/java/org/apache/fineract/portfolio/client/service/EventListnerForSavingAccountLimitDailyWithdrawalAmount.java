package org.apache.fineract.portfolio.client.service;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListnerForSavingAccountLimitDailyWithdrawalAmount implements BusinessEventListner {

    private final CustomerAccountLimitEventListenerService customerAccountLimitEventListenerService;

    @Autowired
    public EventListnerForSavingAccountLimitDailyWithdrawalAmount(
            final CustomerAccountLimitEventListenerService customerAccountLimitEventListenerService) {
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
        final Object savingEntity = businessEventEntity.get(BUSINESS_ENTITY.SAVING);
        final Object savingTransactionEntity = businessEventEntity.get(BUSINESS_ENTITY.SAVING_TRANSACTION);
        if (savingEntity != null && savingTransactionEntity != null) {
            final SavingsAccount savingsAccount = (SavingsAccount) savingEntity;
            final SavingsAccountTransaction withdrawal = (SavingsAccountTransaction) savingTransactionEntity;
            if (savingsAccount != null && withdrawal != null) {
                this.customerAccountLimitEventListenerService.validateSavingsAccountWithClientDailyWithdrawalAmountLimit(savingsAccount,
                        withdrawal);
            }
        }

    }

}
