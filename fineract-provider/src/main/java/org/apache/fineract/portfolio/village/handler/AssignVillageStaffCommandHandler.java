/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package org.apache.fineract.portfolio.village.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.village.service.VillageWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "VILLAGE", action = "ASSIGNSTAFF")
public class AssignVillageStaffCommandHandler implements NewCommandSourceHandler {

    private final VillageWritePlatformService villageWritePlatformService;

    @Autowired
    public AssignVillageStaffCommandHandler(final VillageWritePlatformService villageWritePlatformService) {
        this.villageWritePlatformService = villageWritePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(JsonCommand command) {
        // TODO Auto-generated method stub
        return this.villageWritePlatformService.assignVillageStaffWorkflow(command.entityId(), command);
    }
}
