/* Copyright (C) Conflux Technologies Pvt Ltd - All Rights Reserved
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is proprietary and confidential software; you can't redistribute it and/or modify it unless agreed to in writing.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */
package com.finflux.reconcilation.bankstatement.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.finflux.reconcilation.bankstatement.data.BankStatementDetailsData;
import com.finflux.reconcilation.bankstatement.service.BankStatementDetailsReadPlatformService;

@Path("/bankstatements/{bankStatementId}/details")
@Component
@Scope("singleton")
public class BankStatementDetailsReconciliationApiResource {

    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BankStatementDetailsData> bankStatementDetailsApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    @SuppressWarnings("rawtypes")
    private final ToApiJsonSerializer apiJsonSerializer;
    private final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService;

    @Autowired
    public BankStatementDetailsReconciliationApiResource(final ApiRequestParameterHelper apiRequestParameterHelper,
            final PlatformSecurityContext context,
            final ToApiJsonSerializer<BankStatementDetailsData> bankStatementDetailsApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            @SuppressWarnings("rawtypes") final ToApiJsonSerializer apiJsonSerializer,
            final BankStatementDetailsReadPlatformService bankStatementDetailsReadPlatformService) {

        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.context = context;
        this.bankStatementDetailsApiJsonSerializer = bankStatementDetailsApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializer = apiJsonSerializer;
        this.bankStatementDetailsReadPlatformService = bankStatementDetailsReadPlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getBankStatementDetails(@PathParam("bankStatementId") final Long bankStatementId, @Context final UriInfo uriInfo,
            @QueryParam("command") final String command) {

        this.context.authenticatedUser().validateHasReadPermission(ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESOURCE_NAME);

        List<BankStatementDetailsData> bankStatementDetailsData = null;

        if (command.equalsIgnoreCase(ReconciliationApiConstants.RECONCILED)) {
            bankStatementDetailsData = this.bankStatementDetailsReadPlatformService
                    .retrieveBankStatementDetailsReconciledData(bankStatementId);
        } else if (command.equalsIgnoreCase(ReconciliationApiConstants.JOURNAL_ENTRY)) {
            bankStatementDetailsData = this.bankStatementDetailsReadPlatformService.retrieveBankStatementNonPortfolioData(bankStatementId);
        } else if (command.equalsIgnoreCase(ReconciliationApiConstants.MISCELLANEOUS)) {
            bankStatementDetailsData = this.bankStatementDetailsReadPlatformService.retrieveBankStatementMiscellaneousData(bankStatementId);
        } else if (command.equalsIgnoreCase(ReconciliationApiConstants.GENERATE_TRANSACTIONS)) {
            bankStatementDetailsData = this.bankStatementDetailsReadPlatformService.retrieveGeneratePortfolioData(bankStatementId, "");
        } else {
            bankStatementDetailsData = this.bankStatementDetailsReadPlatformService
                    .retrieveBankStatementDetailsDataForReconcile(bankStatementId);
        }
        
        return this.bankStatementDetailsReadPlatformService.getBankStatementDetails(bankStatementDetailsData, bankStatementId);        
        
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String reconcileUpdate(@PathParam("bankStatementId") final Long bankStatementId, final String apiRequestBodyAsJson,
            @Context final UriInfo uriInfo, @QueryParam("command") String command) {
    	
    	CommandWrapper commandRequest = null;
        if(command.equalsIgnoreCase(ReconciliationApiConstants.RECONCILE_ACTION)){
        	commandRequest = new CommandWrapperBuilder().reconcileBankStatementDetails(bankStatementId)
                    .withJson(apiRequestBodyAsJson).build();
        }else{
        	commandRequest = new CommandWrapperBuilder().undoReconcileBankStatementDetails(bankStatementId)
                    .withJson(apiRequestBodyAsJson).build();
        }

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializer.serialize(result);
    }

}
