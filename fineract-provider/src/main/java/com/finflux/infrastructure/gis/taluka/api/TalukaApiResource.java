package com.finflux.infrastructure.gis.taluka.api;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.infrastructure.gis.taluka.data.TalukaData;
import com.finflux.infrastructure.gis.taluka.services.TalukaReadPlatformServices;

@Path("/districts/{districtId}/talukas")
@Component
@Scope("singleton")
public class TalukaApiResource {
    
    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    @SuppressWarnings("unused")
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final TalukaReadPlatformServices talukaReadPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public TalukaApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final TalukaReadPlatformServices talukaReadPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.talukaReadPlatformService = talukaReadPlatformService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllDistrictDataByStateId(@PathParam("districtId") final Long districtId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(TalukaApiConstants.TALUKA_RESOURCE_NAME);

        final Collection<TalukaData> talukaData = this.talukaReadPlatformService.retrieveAllTalukaDataByDistrictId(districtId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, talukaData);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{talukaId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("talukaId") final Long talukaId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(TalukaApiConstants.TALUKA_RESOURCE_NAME);

        final TalukaData talukaData = this.talukaReadPlatformService.retrieveOne(talukaId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, talukaData);
    }
    
    @SuppressWarnings("unchecked")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createTaluka(@PathParam("talukas") final String entityType, @PathParam("districtId") final Long entityId,final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createTaluka(entityType,entityId) //
                .withJson(apiRequestBodyAsJson) //
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

         return this.toApiJsonSerializer.serialize(result);
    }
}
