package com.finflux.fingerprint.api;

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
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.fingerprint.data.FingerPrintData;
import com.finflux.fingerprint.data.FingerPrintDataForAuthentication;
import com.finflux.fingerprint.services.FingerPrintReadPlatformServices;

@Path("/clients/{clientId}/fingerprint")
@Component
@Scope("singleton")

public class FingerPrintApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<FingerPrintDataForAuthentication> defaultToApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final FingerPrintReadPlatformServices fingerPrintReadPlatformServices;
    private final ToApiJsonSerializer<EnumOptionData> toApiJsonSerializer;
    private final ClientRepositoryWrapper clientRepositoryWrapper;

    @Autowired
    public FingerPrintApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer<FingerPrintDataForAuthentication> defaultToApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final FingerPrintReadPlatformServices fingerPrintReadPlatformServices,
            final ToApiJsonSerializer<EnumOptionData> toApiJsonSerializer,
            final ClientRepositoryWrapper clientRepositoryWrapper) {

        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.fingerPrintReadPlatformServices = fingerPrintReadPlatformServices;
        this.defaultToApiJsonSerializer = defaultToApiJsonSerializer;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
    }

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveFingerPrintTemplate(@PathParam("clientId") final Long clientId,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(FingerPrintApiConstants.FINGER_PRINT_RESOURCE_NAME);
		this.clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
		final Collection<EnumOptionData> fingerOptions = this.fingerPrintReadPlatformServices
				.retriveFingerPrintTemplate();

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());

		return this.toApiJsonSerializer.serialize(settings, fingerOptions);
	}

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createFingerPrint(@PathParam("clients") final String entityType, @PathParam("clientId") final Long clientId,
            final String apiRequestBodyAsJson) {

        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

        final CommandWrapper commandRequest = builder.createFingerPrint(entityType, clientId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.defaultToApiJsonSerializer.serialize(result);
    }

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveFingerPrintData(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(FingerPrintApiConstants.FINGER_PRINT_RESOURCE_NAME);
		Collection<EnumOptionData> fingerOptions = null;
		final Collection<FingerPrintData> fingerPrintData = this.fingerPrintReadPlatformServices
				.retriveFingerPrintData(clientId);

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		if (settings.isTemplate()) {
			fingerOptions = this.fingerPrintReadPlatformServices.retriveFingerPrintTemplate();
		}
		final FingerPrintDataForAuthentication fingerData = FingerPrintDataForAuthentication.instance(fingerOptions, fingerPrintData);

		return this.defaultToApiJsonSerializer.serialize(settings, fingerData);
	}

}
