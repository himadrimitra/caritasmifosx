package com.finflux.entitylock.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface LockEntityWritePlatformService {

    CommandProcessingResult lock(final Integer entityTypeEnum, final Long entityId, final JsonCommand command);

    CommandProcessingResult unlock(final Integer entityTypeEnum, final Long entityId, final JsonCommand command);

}