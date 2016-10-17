package com.finflux.portfolio.client.cashflow.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.finflux.portfolio.client.cashflow.data.ClientIncomeExpenseData;
import com.finflux.portfolio.client.cashflow.service.ClientIncomeExpenseReadPlatformService;

@Path("/clients/{clientId}/incomesandexpenses")
@Component
@Scope("singleton")
public class ClientIncomeExpenseApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final ClientIncomeExpenseReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public ClientIncomeExpenseApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final ClientIncomeExpenseReadPlatformService readPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readPlatformService = readPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientIncomeExpense(clientId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@PathParam("clientId") final Long clientId,
            @QueryParam("isFetchFamilyDeatilsIncomeAndExpense") final Boolean isFetchFamilyDeatilsIncomeAndExpense,
            @QueryParam("isActive") final Boolean isActive, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ClientIncomeExpenseApiConstants.CLIENT_INCOME_EXPENSE);
        final Collection<ClientIncomeExpenseData> clientIncomeExpenseDatas = this.readPlatformService.retrieveAll(clientId,
                isFetchFamilyDeatilsIncomeAndExpense, isActive);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientIncomeExpenseDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{clientIncomeExpenseId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("clientIncomeExpenseId") final Long clientIncomeExpenseId, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(ClientIncomeExpenseApiConstants.CLIENT_INCOME_EXPENSE);
        final ClientIncomeExpenseData clientIncomeExpenseData = this.readPlatformService.retrieveOne(clientIncomeExpenseId);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientIncomeExpenseData);
    }

    @PUT
    @Path("{clientIncomeExpenseId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("clientId") final Long clientId, @PathParam("clientIncomeExpenseId") final Long clientIncomeExpenseId,
            final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        commandRequest = builder.updateClientIncomeExpense(clientId, clientIncomeExpenseId).build();
        result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}