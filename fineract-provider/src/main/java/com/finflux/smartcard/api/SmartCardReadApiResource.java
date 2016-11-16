package com.finflux.smartcard.api;

/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.smartcard.data.SmartCardData;
import com.finflux.smartcard.services.SmartCardReadPlatformServices;

@Path("/clients/smartcard/{cardNumber}")
@Component
@Scope("singleton")
public class SmartCardReadApiResource {

	private final PlatformSecurityContext context;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final SmartCardReadPlatformServices smartCardReadPlatformServices;
	private final ClientReadPlatformService clientReadPlatformService;
	
	@Autowired
	public SmartCardReadApiResource(final PlatformSecurityContext context,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final DefaultToApiJsonSerializer<ClientData> toApiJsonSerializer,
			final SmartCardReadPlatformServices smartCardReadPlatformServices,final ClientReadPlatformService clientReadPlatformService){
		
		this.context = context;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.smartCardReadPlatformServices = smartCardReadPlatformServices;
		this.clientReadPlatformService = clientReadPlatformService;
		
	}
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClient(@PathParam("cardNumber") final String cardNumber, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(SmartCardApiConstants.SMARTCARD_RESOURCE_NAME);

		final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		ClientData clientData = null;
		final SmartCardData smartCardData = this.smartCardReadPlatformServices.retrieveOne(cardNumber);
		if(smartCardData != null){
		clientData = this.clientReadPlatformService.retrieveOne(smartCardData.getClientId());
		}
		return this.toApiJsonSerializer.serialize(settings, clientData);
	}
}
