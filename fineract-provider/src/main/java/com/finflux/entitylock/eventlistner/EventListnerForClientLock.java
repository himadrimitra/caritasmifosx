package com.finflux.entitylock.eventlistner;

import java.util.Map;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.springframework.beans.factory.annotation.Autowired;

import com.finflux.entitylock.service.LockOrUnlockEntityService;

public class EventListnerForClientLock implements BusinessEventListner {

    private final LockOrUnlockEntityService service;

    @Autowired
    public EventListnerForClientLock(final LockOrUnlockEntityService service) {
        this.service = service;
    }

    @Override
    public void businessEventToBeExecuted(final Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub
    }

    @Override
    public void businessEventWasExecuted(final Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final BUSINESS_EVENTS businessEvent = (BUSINESS_EVENTS) businessEventEntity.get(BUSINESS_ENTITY.BUSINESS_EVENT);
        final Object object = businessEventEntity.get(BUSINESS_ENTITY.CLIENT);
        if (object != null) {
            final Client client = (Client) object;
            final Long entityId = client.getId();
            final EntityType entityType = EntityType.CLIENT;
            this.service.lockEntity(entityId, entityType, businessEvent.getValue());
        }
    }
}