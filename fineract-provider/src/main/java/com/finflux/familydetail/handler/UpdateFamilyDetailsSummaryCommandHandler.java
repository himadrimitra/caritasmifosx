package com.finflux.familydetail.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.familydetail.service.FamilyDetailsSummaryWritePlatromService;

@Service
@CommandType(entity = "FAMILYDETAILSSUMMARY", action = "UPDATE")
public class UpdateFamilyDetailsSummaryCommandHandler implements NewCommandSourceHandler {

    private final FamilyDetailsSummaryWritePlatromService familyDetailsSummaryWritePlatromService;

    @Autowired
    public UpdateFamilyDetailsSummaryCommandHandler(FamilyDetailsSummaryWritePlatromService familyDetailsSummaryWritePlatromService) {
        this.familyDetailsSummaryWritePlatromService = familyDetailsSummaryWritePlatromService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.familyDetailsSummaryWritePlatromService.update(command.getClientId(), command.entityId(), command);
    }
}