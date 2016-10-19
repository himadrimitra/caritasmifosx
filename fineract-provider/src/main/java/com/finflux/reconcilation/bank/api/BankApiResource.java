/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bank.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.fineract.commands.domain.CommandWrapper;

import com.finflux.commands.service.CommandWrapperBuilder;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.api.ApiRequestParameterHelper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.finflux.reconcilation.ReconciliationApiConstants;
import com.finflux.reconcilation.bank.data.BankData;
import com.finflux.reconcilation.bank.service.BankReadPlatformService;

@Path("/banks")
@Component
@Scope("singleton")
public class BankApiResource {

    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BankData> bankDataToApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final BankReadPlatformService bankReadPlatformService;

    @Autowired
    public BankApiResource(final PlatformSecurityContext context, final ToApiJsonSerializer<BankData> bankDataToApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper, final BankReadPlatformService bankReadPlatformService) {
        this.context = context;
        this.bankDataToApiJsonSerializer = bankDataToApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.bankReadPlatformService = bankReadPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createBank(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createBank().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.bankDataToApiJsonSerializer.serialize(result);

    }

    @PUT
    @Path("{bankId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateBank(@PathParam("bankId") final Long bankId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateBank(bankId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.bankDataToApiJsonSerializer.serialize(result);

    }

    @DELETE
    @Path("{bankId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteBank(@PathParam("bankId") final Long bankId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .deleteBank(bankId) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.bankDataToApiJsonSerializer.serialize(result);

    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveAllBanks(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        final Collection<BankData> bankData = this.bankReadPlatformService.retrieveAllBanks();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.bankDataToApiJsonSerializer.serialize(settings, bankData, ReconciliationApiConstants.BANK_RESPONSE_DATA_PARAMETERS);
    }

    @GET
    @Path("{bankId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retreiveBankWithId(@PathParam("bankId") final Long bankId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser();

        final BankData bankData = this.bankReadPlatformService.getBank(bankId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.bankDataToApiJsonSerializer.serialize(settings, bankData, ReconciliationApiConstants.BANK_RESPONSE_DATA_PARAMETERS);
    }

}
