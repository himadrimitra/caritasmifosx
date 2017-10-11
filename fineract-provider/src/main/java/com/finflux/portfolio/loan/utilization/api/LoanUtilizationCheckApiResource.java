package com.finflux.portfolio.loan.utilization.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
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

import com.finflux.portfolio.loan.utilization.data.LoanUtilizationCheckData;
import com.finflux.portfolio.loan.utilization.service.LoanUtilizationCheckReadPlatformService;

@Path("/loans/{loanId}/utilizationchecks")
@Component
@Scope("singleton")
public class LoanUtilizationCheckApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    @SuppressWarnings("rawtypes")
    private final DefaultToApiJsonSerializer toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final LoanUtilizationCheckReadPlatformService readPlatformService;

    @SuppressWarnings("rawtypes")
    @Autowired
    public LoanUtilizationCheckApiResource(final PlatformSecurityContext context,
            final ApiRequestParameterHelper apiRequestParameterHelper, final DefaultToApiJsonSerializer toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final LoanUtilizationCheckReadPlatformService readPlatformService) {
        this.context = context;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.readPlatformService = readPlatformService;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(@PathParam("loanId") final Long loanId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createLoanUtilizationCheck(loanId).withJson(apiRequestBodyAsJson)
                .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@PathParam("loanId") final Long loanId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(LoanUtilizationCheckApiConstants.LOAN_UTILIZATION_CHECK_RESOURCE_NAME);

        final Collection<LoanUtilizationCheckData> loanUtilizationCheckDatas = this.readPlatformService.retrieveAll(loanId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanUtilizationCheckDatas);
    }

    @SuppressWarnings("unchecked")
    @GET
    @Path("{utilizationCheckId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("loanId") final Long loanId, @PathParam("utilizationCheckId") final Long utilizationCheckId,
            @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(LoanUtilizationCheckApiConstants.LOAN_UTILIZATION_CHECK_RESOURCE_NAME);

        final LoanUtilizationCheckData loanUtilizationCheckData = this.readPlatformService.retrieveOne(loanId, utilizationCheckId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        return this.toApiJsonSerializer.serialize(settings, loanUtilizationCheckData);
    }

    @PUT
    @Path("{utilizationCheckId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(@PathParam("loanId") final Long loanId, @PathParam("utilizationCheckId") final Long utilizationCheckId,
            final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLoanUtilizationCheck(loanId, utilizationCheckId)
                .withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}