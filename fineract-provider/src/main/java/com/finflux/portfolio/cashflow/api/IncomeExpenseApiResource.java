package com.finflux.portfolio.cashflow.api;

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

import com.finflux.portfolio.cashflow.data.IncomeExpenseData;
import com.finflux.portfolio.cashflow.data.IncomeExpenseTemplateData;
import com.finflux.portfolio.cashflow.service.IncomeExpenseReadPlatformService;

@Path("/incomesorexpenses")
@Component
@Scope("singleton")
public class IncomeExpenseApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final IncomeExpenseReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public IncomeExpenseApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final IncomeExpenseReadPlatformService readPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readPlatformService = readPlatformService;
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@QueryParam("isActive") final Boolean isActive, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(IncomeExpenseApiConstants.INCOME_EXPENSE_RESOURCE_NAME);

        final IncomeExpenseTemplateData templateData = this.readPlatformService.retrieveIncomeExpenseTemplate(isActive);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createIncomeExpense().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@QueryParam("cashFlowCategoryId") final Long cashFlowCategoryId,
            @QueryParam("isActive") final Boolean isActive,
            @QueryParam("isFetchCashflowCategoryData") final Boolean isFetchCashflowCategoryData, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(IncomeExpenseApiConstants.INCOME_EXPENSE_RESOURCE_NAME);
        final Collection<IncomeExpenseData> incomeExpenseDatas = this.readPlatformService.retrieveAll(cashFlowCategoryId, isActive,
                isFetchCashflowCategoryData);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, incomeExpenseDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{incomeExpenseId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("incomeExpenseId") final Long incomeExpenseId,
            @QueryParam("isFetchCashflowCategoryData") final Boolean isFetchCashflowCategoryData, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(IncomeExpenseApiConstants.INCOME_EXPENSE_RESOURCE_NAME);
        final IncomeExpenseData incomeExpenseData = this.readPlatformService.retrieveOne(incomeExpenseId, isFetchCashflowCategoryData);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, incomeExpenseData);
    }

    @POST
    @Path("{incomeExpenseId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("incomeExpenseId") final Long incomeExpenseId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateIncomeExpense(incomeExpenseId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "inactivate")) {
            commandRequest = builder.inActivateIncomeExpense(incomeExpenseId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        return null;
    }

    @PUT
    @Path("{incomeExpenseId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createLoanPurpose(@PathParam("incomeExpenseId") final Long incomeExpenseId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateIncomeExpense(incomeExpenseId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}