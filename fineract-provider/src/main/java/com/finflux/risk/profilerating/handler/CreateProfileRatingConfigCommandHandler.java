package com.finflux.risk.profilerating.handler;

import javax.transaction.Transactional;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.risk.profilerating.service.ProfileRatingConfigWritePlatformService;

@Service
@CommandType(entity = "PROFILE_RATING_CONFIG", action = "CREATE")
public class CreateProfileRatingConfigCommandHandler implements NewCommandSourceHandler {

    private final ProfileRatingConfigWritePlatformService writePlatformService;

    @Autowired
    public CreateProfileRatingConfigCommandHandler(final ProfileRatingConfigWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.create(command);
    }
}