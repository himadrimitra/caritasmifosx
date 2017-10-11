package com.finflux.entitylock.eventlistner;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.entitylock.service.LockOrUnlockEntityService;

@Service
public class EventListnerForLoanLock implements BusinessEventListner {

    private final LockOrUnlockEntityService service;

    @Autowired
    public EventListnerForLoanLock(final LockOrUnlockEntityService service) {
        this.service = service;
    }

    @Override
    public void businessEventToBeExecuted(final Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void businessEventWasExecuted(final Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final BUSINESS_EVENTS businessEvent = (BUSINESS_EVENTS) businessEventEntity.get(BUSINESS_ENTITY.BUSINESS_EVENT);
        final Object object = businessEventEntity.get(BUSINESS_ENTITY.LOAN);
        if (object != null) {
            final Loan loan = (Loan) object;
            final Long entityId = loan.getId();
            final EntityType entityType = EntityType.LOAN;
            this.service.lockEntity(entityId, entityType, businessEvent.getValue());
        }
    }

}