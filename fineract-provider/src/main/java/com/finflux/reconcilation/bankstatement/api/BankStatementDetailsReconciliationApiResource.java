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
import com.finflux.reconcilation.bankstatement.service.BankStatementReadPlatformService;

@Path("/bankstatements/{bankStatementId}/details")
@Component
@Scope("singleton")
public class BankStatementDetailsReconciliationApiResource {

    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PlatformSecurityContext context;
    private final ToApiJsonSerializer<BankStatementDetailsData> bankStatementDetailsApiJsonSerializer;
    private final BankStatementReadPlatformService bankStatementReadPlatformService;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    @SuppressWarnings("rawtypes")
    private final ToApiJsonSerializer apiJsonSerializer;

    @Autowired
    public BankStatementDetailsReconciliationApiResource(final ApiRequestParameterHelper apiRequestParameterHelper,
            final PlatformSecurityContext context, final BankStatementReadPlatformService bankStatementReadPlatformService,
            final ToApiJsonSerializer<BankStatementDetailsData> bankStatementDetailsApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            @SuppressWarnings("rawtypes") final ToApiJsonSerializer apiJsonSerializer) {

        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.context = context;
        this.bankStatementReadPlatformService = bankStatementReadPlatformService;
        this.bankStatementDetailsApiJsonSerializer = bankStatementDetailsApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.apiJsonSerializer = apiJsonSerializer;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getBankStatementDetails(@PathParam("bankStatementId") final Long bankStatementId, @Context final UriInfo uriInfo,
            @QueryParam("command") final String command) {

        this.context.authenticatedUser();

        List<BankStatementDetailsData> bankStatementDetailsData = null;

        bankStatementDetailsData = this.bankStatementReadPlatformService.retrieveBankStatementDetailsData(bankStatementId, command);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.bankStatementDetailsApiJsonSerializer.serialize(settings, bankStatementDetailsData,
                ReconciliationApiConstants.BANK_STATEMENT_DETAILS_RESPONSE_DATA_PARAMETERS);
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String makeReconcile(@PathParam("bankStatementId") final Long bankStatementId, final String apiRequestBodyAsJson,
            @Context final UriInfo uriInfo) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().reconcileBankStatementDetails(bankStatementId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.apiJsonSerializer.serialize(result);
    }

}
