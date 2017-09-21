package com.finflux.entitylock.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.common.domain.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.finflux.common.domain.ActionType;

@Service
public class LockEntityWritePlatformServiceImpl implements LockEntityWritePlatformService {

    private final PlatformSecurityContext context;
    private final LockOrUnlockEntityService service;

    @Autowired
    public LockEntityWritePlatformServiceImpl(final PlatformSecurityContext context, final LockOrUnlockEntityService service) {
        this.context = context;
        this.service = service;
    }

    @Override
    public CommandProcessingResult lock(final Integer entityTypeEnum, final Long entityId, final JsonCommand command) {
        try {
            final EntityType entityType = EntityType.fromInt(entityTypeEnum);
            validateUserHasPermissionOrNot(entityType, ActionType.LOCK);
            final String businessEventName = null;
            this.service.lockEntity(entityId, entityType, businessEventName);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(entityId)//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }

    private void validateUserHasPermissionOrNot(final EntityType entityType, final ActionType actionType) {
        final String function = actionType.getSystemName().toUpperCase() + "_" + entityType.name();
        this.context.authenticatedUser().validateHasPermissionTo(function);
    }

    @Override
    public CommandProcessingResult unlock(final Integer entityTypeEnum, final Long entityId, final JsonCommand command) {
        try {
            final EntityType entityType = EntityType.fromInt(entityTypeEnum);
            validateUserHasPermissionOrNot(entityType, ActionType.UNLOCK);
            final String businessEventName = null;
            this.service.unlockEntity(entityId, entityType, businessEventName);
            return new CommandProcessingResultBuilder()//
                    .withCommandId(command.commandId())//
                    .withEntityId(entityId)//
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            return CommandProcessingResult.empty();
        }
    }
}
