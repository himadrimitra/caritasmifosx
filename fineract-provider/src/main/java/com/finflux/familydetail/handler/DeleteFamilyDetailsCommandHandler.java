package com.finflux.familydetail.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.familydetail.service.FamilyDetailWritePlatromService;

@Service
@CommandType(entity = "FAMILYDETAIL", action = "DELETE")
public class DeleteFamilyDetailsCommandHandler implements NewCommandSourceHandler {

    private final FamilyDetailWritePlatromService familyDetailWritePlatromService;

    @Autowired
    public DeleteFamilyDetailsCommandHandler(FamilyDetailWritePlatromService familyDetailWritePlatromService) {
        this.familyDetailWritePlatromService = familyDetailWritePlatromService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.familyDetailWritePlatromService.deleteFamilyDeatails(command.getClientId(), command.entityId());
    }
}