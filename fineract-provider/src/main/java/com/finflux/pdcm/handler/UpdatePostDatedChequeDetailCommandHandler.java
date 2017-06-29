package com.finflux.pdcm.handler;

import javax.transaction.Transactional;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.pdcm.constants.PostDatedChequeDetailApiConstants;
import com.finflux.pdcm.service.PostDatedChequeDetailWritePlatformService;

@Service
@CommandType(entity = PostDatedChequeDetailApiConstants.ENTITY_PDC, action = PostDatedChequeDetailApiConstants.ACTION_UPDATE)
public class UpdatePostDatedChequeDetailCommandHandler implements NewCommandSourceHandler {

    private final PostDatedChequeDetailWritePlatformService writePlatformService;

    @Autowired
    public UpdatePostDatedChequeDetailCommandHandler(final PostDatedChequeDetailWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.update(command.entityId(), command);
    }
}
