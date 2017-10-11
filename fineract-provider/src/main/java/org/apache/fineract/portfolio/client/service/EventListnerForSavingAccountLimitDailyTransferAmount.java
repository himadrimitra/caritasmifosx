package org.apache.fineract.portfolio.client.service;

import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListnerForSavingAccountLimitDailyTransferAmount implements BusinessEventListner {

    private final CustomerAccountLimitEventListenerService customerAccountLimitEventListenerService;

    @Autowired
    public EventListnerForSavingAccountLimitDailyTransferAmount(
            final CustomerAccountLimitEventListenerService customerAccountLimitEventListenerService) {
        this.customerAccountLimitEventListenerService = customerAccountLimitEventListenerService;
    }

    @SuppressWarnings("unused")
    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final SavingsAccount savingsAccount = (SavingsAccount) businessEventEntity.get(BUSINESS_ENTITY.SAVING);
        final JsonCommand jsonCommand = (JsonCommand) businessEventEntity.get(BUSINESS_ENTITY.JSON_COMMAND);
        if (savingsAccount != null && jsonCommand != null) {
            this.customerAccountLimitEventListenerService.validateSavingsAccountWithClientDailyTransferAmountLimit(savingsAccount,
                    jsonCommand);
        }

    }

}
