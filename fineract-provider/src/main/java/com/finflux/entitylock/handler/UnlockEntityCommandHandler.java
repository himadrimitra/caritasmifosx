package com.finflux.entitylock.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.common.constant.CommonConstants;
import com.finflux.entitylock.api.EntityLockApiConstants;
import com.finflux.entitylock.service.LockEntityWritePlatformService;

@Service
@CommandType(entity = CommonConstants.ENTITY, action = EntityLockApiConstants.ACTION_UNLOCK)
public class UnlockEntityCommandHandler implements NewCommandSourceHandler {

    private final LockEntityWritePlatformService writePlatformService;

    @Autowired
    public UnlockEntityCommandHandler(final LockEntityWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.unlock(command.entityId().intValue(), command.subentityId(), command);
    }
}