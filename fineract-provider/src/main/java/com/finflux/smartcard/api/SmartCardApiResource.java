package com.finflux.smartcard.api;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
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

import com.finflux.smartcard.data.SmartCardData;
import com.finflux.smartcard.domain.SmartCard;
import com.finflux.smartcard.domain.SmartCardStatusTypeEnum;
import com.finflux.smartcard.exception.SmartCardEntityTypeNotSupportedException;
import com.finflux.smartcard.services.SmartCardReadPlatformServices;
import com.finflux.smartcard.services.SmartCardWritePlatformServices;

@Path("/clients/{clientId}/{entityType}/smartcard")
@Component
@Scope("singleton")

public class SmartCardApiResource {

	private final PlatformSecurityContext context;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<SmartCardData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final SmartCardReadPlatformServices smartCardReadPlatformServices;
	private final SmartCardWritePlatformServices SmartCardWritePlatformServices;

	@Autowired
	public SmartCardApiResource(final PlatformSecurityContext context,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final DefaultToApiJsonSerializer<SmartCardData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final SmartCardReadPlatformServices smartCardReadPlatformServices,
			final SmartCardWritePlatformServices SmartCardWritePlatformServices) {
		this.context = context;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.smartCardReadPlatformServices = smartCardReadPlatformServices;
		this.SmartCardWritePlatformServices = SmartCardWritePlatformServices;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createSmartCard(@PathParam("entityType") final String entityType,
			@PathParam("clientId") final Long clientId, @QueryParam("command") final String commandParam,
			final String apiRequestBodyAsJson) {

		final SmartCardStatusTypeEnum EntityType = SmartCardStatusTypeEnum.getEntityType(entityType);
		if (EntityType == null) {
			throw new SmartCardEntityTypeNotSupportedException(entityType);
		}

		final Long entityTypeId = EntityType.getValue().longValue();
		CommandProcessingResult result = null;
		final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
		if (is(commandParam, "activate")) {
			final CommandWrapper commandRequest = builder.activateSmartCard(entityTypeId, entityType, clientId).build();
			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		} else if (is(commandParam, "inactivate")) {
			final CommandWrapper commandRequest = builder.inActivateSmartCard(entityTypeId, entityType, clientId)
					.build();
			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		}

		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSmartCardData(@PathParam("entityType") final String entityType,
			@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(SmartCardApiConstants.SMARTCARD_RESOURCE_NAME);

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		final Collection<SmartCardData> smartcardData = this.smartCardReadPlatformServices
				.retriveSmartCardData(clientId);
		return this.toApiJsonSerializer.serialize(settings, smartcardData);
	}

	@GET
	@Path("{entityId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveOne(@PathParam("entityType") final String entityType,
			@PathParam("clientId") final Long clientId, @PathParam("entityId") final String entityId,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(SmartCardApiConstants.SMARTCARD_RESOURCE_NAME);
		SmartCardData smartcardData = null;
		SmartCard smartCard = null;

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		if (settings.isTemplate() && entityId != null) {
			smartCard = this.SmartCardWritePlatformServices.generateUniqueSmartCardNumber(clientId, entityType,
					entityId);
			if (smartCard != null) {
				smartcardData = this.smartCardReadPlatformServices.retrieveOne(smartCard.getCardNumber());
			}
		}
		return this.toApiJsonSerializer.serialize(settings, smartcardData);
	}

	private boolean is(final String commandParam, final String commandValue) {
		return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
	}
}
