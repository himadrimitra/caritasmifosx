package com.finflux.entitylock.service;

import org.apache.fineract.portfolio.common.domain.EntityType;

public interface LockOrUnlockEntityService {

    void lockEntity(final Long entityId, final EntityType entityType, final String businessEventName);

    void unlockEntity(final Long entityId, final EntityType entityType, final String businessEventName);

    void validateEntityRecordLockedOrNot(final EntityType entityType, final boolean isLocked);
}