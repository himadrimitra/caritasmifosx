package com.finflux.entitylock.eventlistner;

import java.util.Map;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.apache.fineract.portfolio.common.service.BusinessEventListner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.entitylock.service.LockOrUnlockEntityService;

@Service
public class EventListnerForValidateEntityLockedOrNot implements BusinessEventListner {

    private final LockOrUnlockEntityService service;

    @Autowired
    public EventListnerForValidateEntityLockedOrNot(final LockOrUnlockEntityService service) {
        this.service = service;
    }

    @Override
    public void businessEventToBeExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        final BUSINESS_EVENTS businessEvent = (BUSINESS_EVENTS) businessEventEntity.get(BUSINESS_ENTITY.BUSINESS_EVENT);
        final boolean isLocked = (boolean) businessEventEntity.get(BUSINESS_ENTITY.ENTITY_LOCK_STATUS);
        EntityType entityType = null;
        switch (businessEvent) {
            case CLIENT_UPDATE:
                entityType = EntityType.CLIENT;
            break;
            case LOAN_MODIFY:
            case LOAN_UNDO_DISBURSAL:
                entityType = EntityType.LOAN;
            break;
            case ADDRESS_UPDATE:
                entityType = EntityType.ADDRESS;
            break;
            case DOCUMENT_UPDATE:
                entityType = EntityType.DOCUMENT;
            break;
            case FAMILY_DETAILS_UPDATE:
                entityType = EntityType.FAMILY_DETAILS;
            break;
            case CLIENT_IDENTIFIER_UPDATE:
                entityType = EntityType.CLIENT_IDENTIFIER;
            break;
            case CLIENT_INCOME_EXPENSE_UPDATE:
                entityType = EntityType.CLIENT_INCOME_EXPENSE;
            break;
            case EXISTING_LOAN_UPDATE:
                entityType = EntityType.EXISTING_LOAN;
            break;
            default:
            break;
        }
        this.service.validateEntityRecordLockedOrNot(entityType, isLocked);
    }

    @Override
    public void businessEventWasExecuted(Map<BUSINESS_ENTITY, Object> businessEventEntity) {
        // TODO Auto-generated method stub

    }

}
