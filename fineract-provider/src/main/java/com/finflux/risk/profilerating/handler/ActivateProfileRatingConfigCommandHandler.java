package com.finflux.risk.profilerating.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.service.ProfileRatingConfigWritePlatformService;

@Service
@CommandType(entity = "PROFILERATINGCONFIG", action = "ACTIVATE")
public class ActivateProfileRatingConfigCommandHandler implements NewCommandSourceHandler {

    private final ProfileRatingConfigWritePlatformService writePlatformService;

    @Autowired
    public ActivateProfileRatingConfigCommandHandler(final ProfileRatingConfigWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.activate(command.entityId(), command);
    }
}