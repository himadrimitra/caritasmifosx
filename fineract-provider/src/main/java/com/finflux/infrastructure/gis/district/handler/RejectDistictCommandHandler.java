/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.infrastructure.gis.district.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.infrastructure.gis.district.service.DistrictWritePlatformService;

@Service
@CommandType(entity = "DISTRICT", action = "REJECT")
public class RejectDistictCommandHandler implements NewCommandSourceHandler {

    private final DistrictWritePlatformService writePlatformService;

    @Autowired
    public RejectDistictCommandHandler(final DistrictWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.writePlatformService.rejectDistrict(command.entityId(), command);
    }
}