package com.finflux.risk.profilerating.handler;

import javax.transaction.Transactional;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.service.ComputeProfileRatingWritePlatformService;

@Service
@CommandType(entity = "PROFILE_RATING", action = "COMPUTE")
public final class ComputeProfileRatingCommandHandler implements NewCommandSourceHandler {

    private final ComputeProfileRatingWritePlatformService writePlatformService;

    @Autowired
    public ComputeProfileRatingCommandHandler(final ComputeProfileRatingWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.computeProfileRating(command);
    }
}