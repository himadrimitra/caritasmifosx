package com.finflux.pdcm.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.pdcm.constants.PostDatedChequeDetailApiConstants;
import com.finflux.pdcm.service.PostDatedChequeDetailWritePlatformService;

@Service
@CommandType(entity = PostDatedChequeDetailApiConstants.ENTITY_PDC, action = PostDatedChequeDetailApiConstants.ACTION_DELETE)
public class DeletePostDatedChequeDetailCommandHandler implements NewCommandSourceHandler {

    private final PostDatedChequeDetailWritePlatformService writePlatformService;

    @Autowired
    public DeletePostDatedChequeDetailCommandHandler(final PostDatedChequeDetailWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.delete(command.entityId(), command);
    }

}