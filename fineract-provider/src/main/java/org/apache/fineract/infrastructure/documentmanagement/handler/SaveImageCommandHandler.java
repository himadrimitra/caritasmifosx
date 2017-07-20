package org.apache.fineract.infrastructure.documentmanagement.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.documentmanagement.service.ImageWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "IMAGE", action = "SAVE")
public class SaveImageCommandHandler implements NewCommandSourceHandler {
    
    private final ImageWritePlatformService imageWritePlatformService;
    @Autowired
    public SaveImageCommandHandler(final ImageWritePlatformService imageWritePlatformService) {
        this.imageWritePlatformService = imageWritePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.imageWritePlatformService.saveOrUpdateImage(command);
    }

}
