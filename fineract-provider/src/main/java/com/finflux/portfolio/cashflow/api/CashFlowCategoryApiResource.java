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

import com.finflux.portfolio.cashflow.data.CashFlowCategoryData;
import com.finflux.portfolio.cashflow.data.CashFlowCategoryTemplateData;
import com.finflux.portfolio.cashflow.service.CashFlowCategoryReadPlatformService;

@Path("/cashflowcategories")
@Component
@Scope("singleton")
public class CashFlowCategoryApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final CashFlowCategoryReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public CashFlowCategoryApiResource(final PlatformSecurityContext context, final ApiRequestParameterHelper apiRequestParameterHelper,
            final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final CashFlowCategoryReadPlatformService readPlatformService) {
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
    public String retrieveTemplate(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(CashFlowCategoryApiConstants.CASH_FLOW_RESOURCE_NAME);

        final CashFlowCategoryTemplateData templateData = this.readPlatformService.retrieveCashFlowTemplate();

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, templateData);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCashFlow().withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@QueryParam("categoryEnumId") final Integer categoryEnumId,
            @QueryParam("typeEnumId") final Integer typeEnumId, @QueryParam("isActive") final Boolean isActive,
            @QueryParam("isFetchIncomeExpenseDatas") final Boolean isFetchIncomeExpenseDatas, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(CashFlowCategoryApiConstants.CASH_FLOW_RESOURCE_NAME);
        final Collection<CashFlowCategoryData> cashFlowCategoryDatas = this.readPlatformService.retrieveAll(categoryEnumId, typeEnumId,
                isActive, isFetchIncomeExpenseDatas);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, cashFlowCategoryDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{cashFlowCategoryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("cashFlowCategoryId") final Long cashFlowCategoryId,
            @QueryParam("isFetchIncomeExpenseDatas") final Boolean isFetchIncomeExpenseDatas, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(CashFlowCategoryApiConstants.CASH_FLOW_RESOURCE_NAME);
        final CashFlowCategoryData cashFlowCategoryData = this.readPlatformService.retrieveOne(cashFlowCategoryId,
                isFetchIncomeExpenseDatas);
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, cashFlowCategoryData);
    }

    @POST
    @Path("{cashFlowCategoryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("cashFlowCategoryId") final Long cashFlowCategoryId, @QueryParam("command") final String commandParam,
            final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        CommandProcessingResult result = null;
        CommandWrapper commandRequest = null;
        if (is(commandParam, "activate")) {
            commandRequest = builder.activateCashFlow(cashFlowCategoryId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        } else if (is(commandParam, "inactivate")) {
            commandRequest = builder.inActivateCashFlow(cashFlowCategoryId).build();
            result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
            return this.toApiJsonSerializer.serialize(result);
        }
        return null;
    }

    @PUT
    @Path("{cashFlowCategoryId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("cashFlowCategoryId") final Long cashFlowCategoryId, final String apiRequestBodyAsJson) {
        final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);
        final CommandWrapper commandRequest = builder.updateCashFlow(cashFlowCategoryId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }

    private boolean is(final String commandParam, final String commandValue) {
        return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
    }
}